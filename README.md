# Reperibilita

Questo repository contiene una semplice app Android (Kotlin) per programmare l'inoltro di chiamata sul proprio telefono.

La funzionalità principale prevede l'attivazione tramite il codice `**21*+39[numerodidestinazione]#` e la disattivazione con `#21#`. Dall'app è possibile scegliere il numero dalla rubrica e programmare l'inoltro definendo intervalli con data e ora di inizio e fine. Ogni fascia salvata mostra il nome del contatto scelto così da sapere a chi è indirizzata. Più fasce possono essere impostate nello stesso giorno e il servizio resta attivo in background. L'interfaccia utilizza componenti Material per una presentazione chiara degli intervalli programmati.

## Build dell'APK
È necessario Android Studio (o il comando `gradlew`) con un'installazione dell'SDK. Compilare in questo modo:

```bash
cd Reperibilita
./gradlew assembleDebug
```

Il file APK verrà generato in `app/build/outputs/apk/debug/`. Copiatelo sul dispositivo e installatelo per effettuare i test.

L'interfaccia permette di selezionare un contatto e aggiungere più intervalli. Per ogni intervallo vengono scelti una data e un'orario di inizio e di fine tramite i relativi selettori. Una volta programmati, gli allarmi vengono ripristinati automaticamente al riavvio del telefono.

È presente inoltre un pulsante "Annulla Programmazione" che rimuove tutte le fasce orarie salvate. Per evitare che Android chiuda l'applicazione in background è consigliato disattivare le ottimizzazioni batteria per "Reperibilità" dalle impostazioni del dispositivo.
