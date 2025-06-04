# Reperibilita

Questo repository contiene una semplice app Android (Kotlin) per programmare l'inoltro di chiamata sul proprio telefono.

La funzionalità principale prevede l'attivazione tramite il codice `**21*+39[numerodidestinazione]#` e la disattivazione con `#21#`. Dall'app è possibile scegliere il numero dalla rubrica e programmare l'inoltro definendo intervalli con data e ora di inizio e fine. Il nome del contatto selezionato viene mostrato nella schermata principale. Più fasce possono essere impostate nello stesso giorno e il servizio resta attivo in background.

## Build dell'APK
È necessario Android Studio (o il comando `gradlew`) con un'installazione dell'SDK. Compilare in questo modo:

```bash
cd Reperibilita
./gradlew assembleDebug
```

Il file APK verrà generato in `app/build/outputs/apk/debug/`. Copiatelo sul dispositivo e installatelo per effettuare i test.

L'interfaccia permette di selezionare un contatto e aggiungere più intervalli. Per ogni intervallo vengono scelti una data e un'orario di inizio e di fine tramite i relativi selettori. Una volta programmati, gli allarmi vengono ripristinati automaticamente al riavvio del telefono.
