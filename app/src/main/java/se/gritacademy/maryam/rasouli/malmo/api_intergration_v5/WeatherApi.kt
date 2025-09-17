package se.gritacademy.maryam.rasouli.malmo.api_intergration_v5

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Open-Meteo API för temperatur

/**
 * Retrofit interface för Open-Meteo API
 * Hämtar temperaturdata baserat på latitud och longitud
 * API: https://open-meteo.com/en/docs
 */
interface WeatherApi {

    /**
     * Hämtar prognos med timvis temperatur
     * @param lat latitud
     * @param lon longitud
     * @param hourly parametrar som ska hämtas. Standarden är "temperature_2m"
     */
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "temperature_2m"
    ): Response<OpenMeteoResponse>
}

/**
 * Responseklass för Open-Meteo API
 */
data class OpenMeteoResponse(
    val hourly: Hourly? = Hourly()
)

/**
 * Timvis data
 */
data class Hourly(
    @SerializedName("time")
    val time: List<String> = emptyList(),

    @SerializedName("temperature_2m")
    val temperature_2m: List<Double> = emptyList()
)

// SMHI API - Vind
/**
 * Retrofit interface för SMHI API
 * Hämtar vinddata från stationer
 *
 */
interface WeatherApiWind {

    /**
     * Hämtar lista på alla väderstationer
     * API: https://www.smhi.se/data/sok-oppna-data-i-utforskaren/se-acmf-meteorologiska-observationer-vindhastighet-timvarde
     */
    @GET("version/latest/parameter/4.json")
    suspend fun getStations(): Response<StationListResponse>

    /**
     * Hämtar vinddata för en specifik station
     * @param stationId ID för stationen
     */
    @GET("version/latest/parameter/4/station/{stationId}/period/latest-hour/data.json")
    suspend fun getCurrentWind(
        @Path("stationId") stationId: Int
    ): Response<WindResponse>
}

/**
 * Lista på stationer
 */
data class StationListResponse(
    val station: List<StationData> = emptyList()
)

/**
 * Data för en station
 */
data class StationData(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Response med vinddata
 */
data class WindResponse(
    val station: Station? = null,
    val value: List<WindValue> = emptyList()
)

/**
 * Station info
 */
data class Station(
    val id: Int,
    val name: String
)

/**
 * Vindvärde vid ett specifikt datum
 */
data class WindValue(
    val date: Long,
    val value: Double? = 0.0,
    val quality: String? = ""
)
