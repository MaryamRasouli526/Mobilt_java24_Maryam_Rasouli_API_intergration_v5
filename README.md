# WeatherApp – Android Kotlin

WeatherApp är en Androidapplikation utvecklad med **Android Studio 16 “Bakalava”**, **Kotlin** och **Gradle**. Appen hämtar realtidsväderdata från:

- [Open-Meteo API](https://open-meteo.com/en/docs)  
- [SMHI API](https://www.smhi.se/data/sok-oppna-data-i-utforskaren/se-acmf-meteorologiska-observationer-vindhastighet-timvarde)

---

## Funktioner

- **Fragment 1 / MainFragment:** Startskärm med navigering genom knapptryck vidare till de andra fragments.  
- **Fragment 2:** Skapa listor med temperatur- eller vinddata baserat på användarens plats. Data sparas i **Firestore** och visas i UI.  
- **Fragment 3:** Visar listor med tidigare poster och ändrar bild beroende på vädret
- Navigering mellan fragment är implementerad med **Android Navigation Component** med även stöd för telefonens egna backstack och knapptryck.

---

## Tekniska detaljer
- **Fake GPS** App som går att ladda ner i Play Butik och som används för att testa att hämta vädret från olika platser
- **Retrofit + Gson** används för API-anrop.  
- **Coroutines** används för asynkrona nätverksoperationer.  
- **Firebase Firestore** används för lagring av väderdata.  
- Appen har notifikationskanaler för vädervarningar.  
- Kräver internet-, notifikation- och platspermission:  
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  <uses-permission android:name="android.permission.INTERNET" />

## Appens utseende

1.Börja med att ansluta telefonen till wifi.  Här visas UI för MainFragment för att starta väderappen:

![IMG_0910](https://github.com/user-attachments/assets/01f7efc7-74bd-4fdd-93f5-24a20c8c7d80)



2. I Fragment1 finns knappar att gå till Fragment2 och Fragment3:


4. I Fragment2 kan du skapa väderlistor beroende på vilken län du befinner dig i som sparas i firestore. Här syns vädret också:
5. I Fragment3  visas aktuella vädret(tempratur och vind) för platsen man befinner sig i och beroende på tempraturen ändras bilden (sol, mol, snö och så vidare). listas väderdata 


 6. Visar notifkationen om tempratur mindre är 10 grader celsius
  7. Du kan testa gå tillbaka
  8. Landscape
  



  


