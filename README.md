# Reperibilita

Questo repository contiene una semplice app Android (Kotlin) per programmare l'inoltro di chiamata sul proprio telefono.

La funzionalità principale prevede l'attivazione tramite il codice `**21*+39[numerodidestinazione]#` e la disattivazione con `##002#`. Dall'app è possibile scegliere il numero dalla rubrica e programmare l'inoltro definendo intervalli con data e ora di inizio e fine.
Quando arriva l'ora programmata viene avviato un servizio in foreground con notifica permanente che esegue il codice USSD. A causa delle limitazioni di Android 11 l'operazione richiede l'apertura automatica del dialer tramite `ACTION_CALL` e può necessitare la conferma dell'utente.
Ogni fascia salvata mostra il nome del contatto scelto così da sapere a chi è indirizzata. È possibile inserire più contatti e numeri durante la giornata, programmando diverse fasce orarie.
Gli allarmi vengono impostati con `AlarmManager.setExactAndAllowWhileIdle` per funzionare anche con il telefono bloccato e in modalità doze.
Il servizio resta in esecuzione per tutta la durata dell'inoltro mostrando la notifica, così da non essere terminato dal sistema, e viene ripristinato automaticamente al riavvio.
Se due fasce sono consecutive (l'ora di fine della prima coincide con quella di inizio della successiva) la prima non invia il comando di disattivazione `##002#`, ma l'app passa direttamente al nuovo numero programmato.
L'interfaccia utilizza componenti Material per una presentazione chiara degli intervalli programmati.

## Build dell'APK
Per compilare è necessario avere installato l'SDK Android.
Se Android Studio non individua automaticamente il percorso, create nella radice
della cartella `Reperibilita` un file `local.properties` con il seguente
contenuto indicando il percorso del vostro SDK:

```
sdk.dir=C:\\Percorso\\Android\\sdk
```

Una volta configurato l'SDK potete compilare usando:

```bash
cd Reperibilita
./gradlew assembleDebug
```

Il file APK verrà generato in `app/build/outputs/apk/debug/`. Copiatelo sul dispositivo e installatelo per effettuare i test.

L'interfaccia permette di selezionare un contatto e aggiungere più intervalli. Per ogni intervallo vengono scelti una data e un'orario di inizio e di fine tramite i relativi selettori. Una volta programmati, gli allarmi vengono ripristinati automaticamente al riavvio del telefono.

È presente inoltre un pulsante "Annulla Programmazione" che rimuove tutte le fasce orarie salvate. Al primo avvio l'app richiede di essere esclusa dalle ottimizzazioni batteria tramite l'apposita schermata di sistema; in questo modo il servizio in foreground potrà continuare a funzionare anche con lo schermo bloccato.
