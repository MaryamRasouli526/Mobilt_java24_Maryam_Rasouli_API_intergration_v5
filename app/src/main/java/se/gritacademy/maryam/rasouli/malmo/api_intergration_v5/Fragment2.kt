package se.gritacademy.maryam.rasouli.malmo.api_intergration_v5

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.gritacademy.maryam.rasouli.malmo.api_intergration_v5.databinding.Fragment2Binding
import java.util.*
import kotlin.math.*

/**
 * Fragment2 hanterar väderdata. Det vill säga temperatur och vind
 * Hämtar data från API:er och Firestore
 * Visar notifikationer om det är kallt
 */
class Fragment2 : Fragment() {

    private var _binding: Fragment2Binding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val retrofitTemp = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val weatherApiTemp = retrofitTemp.create(WeatherApi::class.java)

    private val retrofitWind = Retrofit.Builder()
        .baseUrl("https://opendata-download-metobs.smhi.se/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val weatherApiWind = retrofitWind.create(WeatherApiWind::class.java)

    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0

    private val notificationId = 1001
    private val channelId = "weather_channel"

    /**
     * Denna kallas när fragmentets vy ska skapas
     * @return Root View för fragmentet
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = Fragment2Binding.inflate(inflater, container, false)

        // Här navigeras tillbaka till Fragment1
        binding.buttonToFragment1?.setOnClickListener {
            findNavController().navigate(R.id.action_fragment2_to_fragment1)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        createNotificationChannel()

        fetchApiData()
        fetchFirestoreList()

        binding.buttonSaveToFirestore.setOnClickListener {
            val tempInput = binding.etTemp.text.toString().toDoubleOrNull()
            val windInput = binding.etWind.text.toString().toDoubleOrNull()
            if (tempInput != null && windInput != null) {
                saveEntryToFirestore(tempInput, windInput, currentLat, currentLon)
            } else {
                Toast.makeText(context, "Write the correct temperature and Wind speed", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    /**
     * Skapar en notifikationskanal för kallt väder
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alerts"
            val descriptionText = "Notifications for cold weather"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Skickar en notifikation om det är kallt
     */
    private fun sendColdNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 300)
                return
            }
        }

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_snow)
            .setContentTitle("Cold weather: ")
            .setContentText("It’s cold outside. Wear a coat!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Här hämtas API data om väder baserat på användarens position.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchApiData() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 200)
            return
        }

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            currentLat = location?.latitude ?: 55.6050
            currentLon = location?.longitude ?: 13.0038
            fetchWeatherFromApi(currentLat, currentLon)
        }
    }

    /**
     * Här hämtas temperatur och vind från API och uppdaterar UI
     */
    private fun fetchWeatherFromApi(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val responseTemp = weatherApiTemp.getForecast(lat, lon)
                val apiTemp = if (responseTemp.isSuccessful) {
                    responseTemp.body()?.hourly?.temperature_2m?.firstOrNull() ?: 0.0
                } else 0.0

                val stationsResponse = weatherApiWind.getStations()
                val sortedStations = stationsResponse.body()?.station?.sortedBy { distance(lat, lon, it.latitude, it.longitude) } ?: emptyList()

                val nearestStation = sortedStations.firstOrNull()
                val nearestStationName = nearestStation?.name ?: "Unknown"

                var windSpeed = 0.0
                var windStationName = nearestStationName

                if (nearestStation != null) {
                    val windResponse = weatherApiWind.getCurrentWind(nearestStation.id)
                    val latestWind = if (windResponse.isSuccessful) windResponse.body()?.value?.lastOrNull()?.value ?: 0.0 else 0.0

                    if (latestWind > 0) {
                        windSpeed = latestWind
                    } else {
                        val nextStation = sortedStations.drop(1).firstOrNull { station ->
                            val resp = weatherApiWind.getCurrentWind(station.id)
                            val valWind = if (resp.isSuccessful) resp.body()?.value?.lastOrNull()?.value ?: 0.0 else 0.0
                            if (valWind > 0) {
                                windSpeed = valWind
                                windStationName = station.name
                                true
                            } else false
                        }
                        if (nextStation == null) {
                            windStationName = nearestStationName
                            windSpeed = 0.0
                        }
                    }
                }

                binding.tvFragment2.text = """
                Temperature (from location): $apiTemp°C
                Nearest station: $nearestStationName
                Wind (from $windStationName): %.1f m/s
            """.trimIndent().format(windSpeed)

                if (apiTemp < 10) sendColdNotification()

            } catch (e: Exception) {
                binding.tvFragment2.text = "API: ${e.message}"
            }
        }
    }

    /**
     * Sparar en temperatur och vindpost till Firestore
     */
    private fun saveEntryToFirestore(temp: Double, wind: Double, lat: Double, lon: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var locationName = "Unknown location"
        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                locationName = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea ?: "Unknown location"
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Failed to get location name: ${e.message}")
        }

        val entry = mapOf(
            "temperature" to temp,
            "wind" to wind,
            "timestamp" to Date(),
            "location" to locationName
        )
        db.collection("weather_entries")
            .add(entry)
            .addOnSuccessListener {
                Toast.makeText(context, "Data saved!", Toast.LENGTH_SHORT).show()
                binding.etTemp.text.clear()
                binding.etWind.text.clear()
                if (temp < 10) sendColdNotification()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Wrong: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Hämtar och visar listan från Firestore
     */
    private fun fetchFirestoreList() {
        db.collection("weather_entries")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    binding.tvFirestoreList.text = "Fel vid hämtning: ${e.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val listText = snapshot.documents.joinToString("\n") { doc ->
                        val t = doc.getDouble("temperature") ?: 0.0
                        val w = doc.getDouble("wind") ?: 0.0
                        val loc = doc.getString("location") ?: "Unknown location"
                        "Temp: $t°C, Wind: $w m/s, Location: $loc"
                    }
                    binding.tvFirestoreList.text = listText
                }
            }
    }

    /**
     * Beräknar avstånd mellan två koordinater i meter
     */
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)
        val a = sin(deltaPhi/2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda/2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return R * c
    }

    /**
     * Hanterar resultat av permissionsförfrågningar
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchApiData()
        }
        if (requestCode == 300 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission för notifications beviljad
        }
    }

    /**
     * Rensar binding när fragmentets vy förstörs
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

