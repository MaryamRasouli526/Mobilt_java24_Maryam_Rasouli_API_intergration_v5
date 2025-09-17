# WeatherApp – Android Kotlin

WeatherApp är en Androidapplikation utvecklad med **Android Studio 16 “Bakalava”**, **Kotlin** och **Gradle**. Appen hämtar realtidsväderdata från:

- [Open-Meteo API](https://open-meteo.com/en/docs)  
- [SMHI API](https://www.smhi.se/data/sok-oppna-data-i-utforskaren/se-acmf-meteorologiska-observationer-vindhastighet-timvarde)

---

## Funktioner

- **Fragment 1 / MainFragment:** Startskärm med navigering.  
- **Fragment 2:** Skapa listor med temperatur- eller vinddata baserat på användarens plats. Data sparas i **Firestore** och visas i UI.  
- **Fragment 3:** Visar listor med tidigare poster och ändrar bild beroende på vädret
- Navigering mellan fragment är implementerad med **Android Navigation Component** med även stöd för telefonens egna backstack och knapptryck.

---

## Tekniska detaljer
- **Fake GPS** App som går att ladda ner i Play Butik och som används för att testa olika platser
- **Retrofit + Gson** används för API-anrop.  
- **Coroutines** används för asynkrona nätverksoperationer.  
- **Firebase Firestore** används för lagring av väderdata.
- **Apk-fil** För att sedan installera appen. Finns i min repository.  
- Appen har notifikationskanaler för vädervarningar.  
- Kräver internet-, notifikation- och platspermission:  
 

## Appens utseende

1.Anslut telefonen till wifi. Här visas UI för MainFragment för att starta väderappen:

![IMG_0909](https://github.com/user-attachments/assets/073b316a-d144-44ff-9cb5-3ce96c2f6cbd)






2. I Fragment1 (Menu)  till Fragment2(Create temp/vind) eller Fragment3 (Current weather/list):
  ![IMG_0910](https://github.com/user-attachments/assets/0aa7a82f-cef8-4dc7-94bd-ca9a97c9602b)



4. I Fragment2 kan du skapa väderlistor beroende på vilken län du befinner dig i som sparas i firestore. Här syns vädret också:

  ![IMG_0912](https://github.com/user-attachments/assets/14048e47-001b-4471-a69b-43229b2c904f)

6. I Fragment3 visas bland annat aktuella vädret(tempratur och vind) för platsen man befinner sig i. Men beroende på tempraturen ändras bilden (sol, mol, snö och osv):

- Det är låg temperatur i Naimakka därför visas snö:

![IMG_0913 (1)](https://github.com/user-attachments/assets/499d62f1-c212-4798-8751-29f6105a3349)


- Det är medeltempratur i Malmö därför visas moln:

  ![IMG_0918](https://github.com/user-attachments/assets/30e5038f-256c-43f9-8079-89746bd80bcd)

  

- Genom appen Fake GPS kunde jag testa olika platser:

![IMG_0916](https://github.com/user-attachments/assets/7e69b335-921b-43a5-958e-35476cafc0e5)

![IMG_0917](https://github.com/user-attachments/assets/edf0974b-42fd-47ad-b9c6-0d0dce865edd)



8. Här visas notifkationen om tempratur mindre är 10 grader - Det är kallt:

 ![IMG_0915](https://github.com/user-attachments/assets/a80e9c64-8a8b-4473-8726-71a647c2dbb7)

    
10. Gå tillbaka i appen genom knapptryck (Backstack med navsystem - one way back)
      
12. Landscape för fragments:

Fragment1:

![IMG_0911](https://github.com/user-attachments/assets/cad978e1-d006-4052-8d1a-ed5fc0f951af)

Fragment2:

![IMG_0919](https://github.com/user-attachments/assets/8a925f58-9e67-4aeb-8254-919f2f15a409)


Fragment3:

![IMG_0914](https://github.com/user-attachments/assets/0e562f19-4192-47c8-a39e-aa6195520bef)

  



  


