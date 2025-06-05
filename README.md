# Reperibilita

Questo repository contiene una semplice app Android (Kotlin) per programmare l'inoltro di chiamata sul proprio telefono.

La funzionalità principale prevede l'attivazione tramite il codice
`**21*+39[NUMERO_DESTINAZIONE]#` e la disattivazione con `#21#`.
Per eseguire il codice MMI l'app, quando possibile (Android 8+), utilizza
`TelephonyManager.sendUssdRequest` così da inviare il comando in
background senza aprire il dialer. Sui dispositivi più vecchi il codice
viene invece composto tramite `ACTION_CALL` usando
`Uri.fromParts("tel", code, null)`.
Dall'interfaccia è possibile scegliere il numero dalla rubrica e
programmare l'inoltro definendo intervalli con data e ora di inizio e
fine.
Il numero selezionato viene ripulito da spazi o simboli extra prima di essere utilizzato nel codice MMI.
Ogni fascia salvata mostra il nome del contatto scelto così da sapere a chi è indirizzata. È possibile inserire più contatti e numeri durante la giornata, programmando diverse fasce orarie.
Gli allarmi vengono impostati con `AlarmManager.setExactAndAllowWhileIdle` per funzionare anche con il telefono bloccato e in modalità doze.
Il servizio in foreground garantisce l'esecuzione in background e viene ripristinato automaticamente al riavvio.
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

È presente inoltre un pulsante "Annulla Programmazione" che rimuove tutte le fasce orarie salvate. Per evitare che Android chiuda l'applicazione in background è consigliato disattivare le ottimizzazioni batteria per "Reperibilità" dalle impostazioni del dispositivo.
