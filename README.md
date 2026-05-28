# Bible

Offline Bible reader for Android with on-device verse translation.

- **Read offline** — full King James Version bundled in the APK (~5 MB).
- **Switch versions** — KJV ships built-in; the data layer is structured so more translations can be added by dropping a JSON file into `app/src/main/assets/` and registering it in `BibleRepository.versions`.
- **Translate verses to any supported language** — uses Google ML Kit on-device translation. First use of a language pair downloads a small model (~30 MB) over the network; after that, translation runs entirely on device.

## Tech

- Kotlin + Jetpack Compose
- Material 3
- AndroidX Navigation Compose
- kotlinx.serialization for the Bible JSON
- ML Kit Translation (`com.google.mlkit:translate`)
- minSdk 24, targetSdk 34

## Run

1. Open the project root (`BibleApp/`) in Android Studio (Hedgehog or newer).
2. Let Gradle sync — the wrapper pins Gradle 8.9 + AGP 8.5.2.
3. Run `app` on a device or emulator with Google Play services (required by ML Kit).

Or from the command line:

```
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # device/emulator must be connected
```

## Adding another Bible version

1. Drop a JSON file at `app/src/main/assets/yourversion.json` shaped like:

   ```json
   {
     "version": "WEB",
     "language": "en",
     "books": [
       {
         "name": "Genesis",
         "testament": "OT",
         "chapters": [
           { "number": 1, "verses": [{ "number": 1, "text": "..." }] }
         ]
       }
     ]
   }
   ```

2. Register it in [BibleRepository](app/src/main/java/com/saiapps/bibleapp/data/BibleRepository.kt):

   ```kotlin
   BibleVersion(id = "web", displayName = "World English Bible", sourceLanguage = "en", assetFile = "web.json")
   ```

The version dropdown in the top bar will pick it up automatically.

## Notes on translation

- ML Kit's on-device translator is free, runs locally, and supports ~60 languages.
- The first translation into a new target language triggers a model download. After that it works offline.
- Translations are computed per chapter and shown beneath each verse in italics.
- Quality is good for common languages but not theologian-grade — for liturgical use, prefer a curated Bible version.
