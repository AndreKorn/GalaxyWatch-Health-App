# Copilot-Anweisungen – GalaxyWatch Health App (Wear OS)

> Dieses Dokument beschreibt Architektur, Konventionen und Arbeitsabläufe für das
> **GalaxyWatch-Health-App**-Projekt. Es richtet sich an alle Entwickler und an KI-Assistenten,
> die im Kontext dieses Repositorys arbeiten.

---

## 1  Projektübersicht

| Eigenschaft            | Wert                                                         |
|------------------------|--------------------------------------------------------------|
| **Plattform**          | Wear OS (Samsung Galaxy Watch 7)                             |
| **Sprache**            | Kotlin 2.0                                                   |
| **UI-Framework**       | Jetpack Compose for Wear OS                                  |
| **Build-System**       | Gradle (Kotlin DSL), Version-Catalog (`libs.versions.toml`) |
| **Min SDK**            | 30 (Wear OS 3.0)                                             |
| **Compile / Target SDK** | 36                                                        |
| **JVM-Target**         | 11                                                           |
| **Namespace / App-ID** | `com.example.myapplication`                                  |
| **Standalone**         | Ja – die App benötigt **kein** Smartphone-Pendant            |

---

## 2  Build-, Test- und Lint-Befehle

Alle Befehle gehen von der **Projekt-Root** aus.
Unter **Windows PowerShell** wird `.\gradlew` statt `./gradlew` verwendet.

### 2.1  Build

```powershell
# Debug-APK bauen
.\gradlew :app:assembleDebug

# Release-APK bauen
.\gradlew :app:assembleRelease

# Projekt vollständig bereinigen
.\gradlew clean
```

### 2.2  Tests

```powershell
# Unit-Tests ausfuehren
.\gradlew :app:testDebugUnitTest

# Instrumentierte Tests (Android-Geraet/Emulator erforderlich)
.\gradlew :app:connectedDebugAndroidTest

# Einen einzelnen Test ausfuehren (Beispiel)
.\gradlew :app:testDebugUnitTest --tests "com.example.myapplication.domain.TimeTextFormatterTest"
```

### 2.3  Lint

```powershell
# Lint-Pruefung fuer das gesamte Modul
.\gradlew :app:lintDebug

# Nur den kritischen „Vital"-Lint-Check ausfuehren (wird auch beim Release-Build geprueft)
.\gradlew :app:lintVitalRelease
```

### 2.4  Installation auf der Uhr

```powershell
# Debug-APK bauen und direkt auf das verbundene Geraet installieren
.\gradlew :app:installDebug

# Alternativ ueber ADB (nach assembleDebug)
adb install .\app\build\outputs\apk\debug\app-debug.apk
```

---

## 3  Architektur und Projektstruktur

### 3.1  Paketstruktur (abgeleitet aus Manifest & Quellcode)

```
com.example.myapplication/
├── presentation/          # UI-Schicht (Compose, Activities, ViewModels, Theme)
│   ├── MainActivity.kt           # Einzige Activity – LAUNCHER-Einstiegspunkt
│   ├── HealthViewModel.kt        # ViewModel fuer Gesundheitsdaten
│   ├── HealthUiState.kt          # UI-State-Datenklasse
│   └── theme/                    # Compose-Theme-Definitionen
├── domain/                # Geschaeftslogik / Use Cases
│   └── TimeTextFormatter.kt      # Uhrzeit-zu-Text-Konvertierung
├── data/                  # Datenschicht (Sensoren, Repositories)
│   └── StepCounterManager.kt     # Zugriff auf TYPE_STEP_COUNTER
├── tile/                  # Wear-OS-Tile-Service
│   └── MainTileService.kt
└── complication/          # Wear-OS-Complication-Service
    └── MainComplicationService.kt
```

### 3.2  Architektur-Schichtung

Das Projekt folgt einer **dreischichtigen Architektur** (Presentation → Domain → Data):

- **presentation/** – Activities, Compose-Screens, ViewModels, UI-State.
  Hier liegt die gesamte Benutzeroberfläche.
- **domain/** – Reine Kotlin-Klassen ohne Android-Abhängigkeiten (soweit möglich).
  Geschäftsregeln wie die Uhrzeit-als-Text-Logik gehören hierher.
- **data/** – Android-spezifische Datenquellen (Sensoren, APIs, Datenbanken).
  Der `StepCounterManager` kapselt den Zugriff auf `SensorManager`.

> **Regel:** `domain` darf niemals von `presentation` oder `data` abhängen.
> Abhängigkeiten zeigen immer nach innen: `presentation → domain ← data`.

### 3.3  Manifest-Komponenten im Detail

#### Activity

| Komponente                    | `exported` | Beschreibung                                          |
|-------------------------------|------------|-------------------------------------------------------|
| `.presentation.MainActivity`  | `true`     | Haupt-Activity mit LAUNCHER-Intent-Filter, Splash-Theme |

- Verwendet `taskAffinity=""`, um eine eigene Task-Zuordnung zu vermeiden (typisch für Wear OS).
- Start-Theme: `@style/MainActivityTheme.Starting` (Splash Screen via `core-splashscreen`).

#### Services

| Komponente                          | `exported` | Permission                                                              | Beschreibung                          |
|-------------------------------------|------------|-------------------------------------------------------------------------|---------------------------------------|
| `.complication.MainComplicationService` | `true`   | `com.google.android.wearable.permission.BIND_COMPLICATION_PROVIDER`    | Stellt Complication-Daten bereit (SHORT_TEXT) |
| `.tile.MainTileService`               | `true`   | `com.google.android.wearable.permission.BIND_TILE_PROVIDER`           | Stellt ein Wear-OS-Tile bereit         |

- **MainComplicationService**: Reagiert auf `ACTION_COMPLICATION_UPDATE_REQUEST`.
  Unterstuetzter Typ: `SHORT_TEXT`. Update-Periode: `0` (nur on-demand, kein periodisches Update).
- **MainTileService**: Reagiert auf `androidx.wear.tiles.action.BIND_TILE_PROVIDER`.
  Vorschaubild: `@drawable/tile_preview`.

#### Bibliotheken

| Library                              | `required` | Zweck                                |
|--------------------------------------|------------|--------------------------------------|
| `com.google.android.wearable`        | `true`     | Kern-Wear-OS-Funktionen             |
| `wear-sdk`                           | `false`    | Erweiterte Wear-SDK-Features         |

#### Meta-Daten

- `com.google.android.wearable.standalone = true` → Die App ist **eigenstaendig** und benoetigt keine
  Companion-App auf dem Smartphone.

---

## 4  Berechtigungen und Sicherheit

### 4.1  Deklarierte Berechtigungen

```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
```

| Berechtigung            | Typ          | Laufzeit-Abfrage noetig? | Verwendungszweck                          |
|-------------------------|--------------|---------------------------|-------------------------------------------|
| `WAKE_LOCK`             | Normal       | Nein                      | Bildschirm/CPU wach halten (Tiles, Services) |
| `ACTIVITY_RECOGNITION`  | Dangerous    | **Ja** (ab API 29)        | Schrittzaehler / Aktivitaetserkennung     |

> **Wichtig:** `ACTIVITY_RECOGNITION` ist eine **Dangerous Permission** und muss zur Laufzeit ueber
> `ActivityCompat.requestPermissions()` oder die Activity Result API abgefragt werden.
> Der entsprechende UI-Flow (Berechtigungs-Dialog) muss in `presentation/` implementiert sein.

### 4.2  Hardware-Feature

```xml
<uses-feature android:name="android.hardware.type.watch" />
```

Diese Deklaration stellt sicher, dass die App **ausschliesslich auf Wear-OS-Geraeten** im
Google Play Store angezeigt wird. Auf Smartphones oder Tablets ist die App nicht installierbar.

### 4.3  `exported`-Attribute

Alle drei Manifest-Komponenten sind mit `android:exported="true"` deklariert.
Das ist **korrekt und erforderlich**, weil:

- **MainActivity**: Muss vom System-Launcher gestartet werden koennen (LAUNCHER-Kategorie).
- **MainComplicationService**: Muss vom Wear-OS-System gebunden werden (geschuetzt durch
  `BIND_COMPLICATION_PROVIDER`-Permission).
- **MainTileService**: Muss vom Wear-OS-System gebunden werden (geschuetzt durch
  `BIND_TILE_PROVIDER`-Permission).

> **Sicherheitsregel:** Jede exportierte Komponente, die nicht fuer beliebige Apps zugaenglich
> sein soll, **muss** durch eine `android:permission` geschuetzt werden. Bei den Services ist
> das hier korrekt umgesetzt. Neue exportierte Services oder Receiver muessen immer eine
> passende Permission deklarieren.

### 4.4  Weitere Sicherheitshinweise

- `android:allowBackup="true"` ist gesetzt. Fuer sensible Gesundheitsdaten sollte man pruefen,
  ob `allowBackup="false"` oder eine `BackupAgent`-Konfiguration sinnvoller waere.
- ProGuard/R8 ist im Release-Build aktuell **deaktiviert** (`isMinifyEnabled = false`).
  Vor einer Veroeffentlichung im Play Store sollte Minifizierung und Obfuscation aktiviert werden.

---

## 5  Entwicklungskonventionen

### 5.1  Sprache und Stil

- **Programmiersprache:** Ausschliesslich Kotlin. Kein Java-Code.
- **UI:** Jetpack Compose for Wear OS. Kein XML-basiertes Layout.
- **Namenskonventionen:**
  - Packages: `lowercase` (z.B. `presentation`, `domain`, `data`)
  - Klassen/Interfaces: `PascalCase` (z.B. `HealthViewModel`, `StepCounterManager`)
  - Funktionen/Properties: `camelCase` (z.B. `formatTimeAsText()`)
  - Compose-Funktionen: `PascalCase` (z.B. `@Composable fun HealthScreen()`)
  - Konstanten: `UPPER_SNAKE_CASE`
- **String-Ressourcen:** Alle nutzersichtbaren Texte gehoeren in `res/values/strings.xml`,
  nicht als Hardcoded-Strings im Code.

### 5.2  Dependency Management

- Alle Abhaengigkeiten und Versionen werden zentral in `gradle/libs.versions.toml` verwaltet.
- In `build.gradle.kts` werden Abhaengigkeiten ueber `libs.*`-Aliase referenziert.
- Neue Abhaengigkeiten **immer** zuerst im Version-Catalog eintragen.

### 5.3  Compose-Besonderheiten fuer Wear OS

- Verwende `androidx.wear.compose:compose-material` (nicht das regulaere Material 3).
- Verwende `androidx.wear.compose:compose-foundation` fuer Wear-spezifische Layouts
  (z.B. `ScalingLazyColumn`).
- Bildschirmgroesse ist sehr begrenzt (~1,47 Zoll) – Layouts muessen kompakt sein.
- Runde Displays beachten: Inhalte duerfen nicht am Rand abgeschnitten werden
  (`values-round/` fuer runde Display-Ressourcen).

### 5.4  ViewModel und State

- ViewModels verwenden `StateFlow` oder `State<T>` (Compose) fuer reaktive UI-Updates.
- UI-State wird als `data class` modelliert (z.B. `HealthUiState`).
- Seiteneffekte (Sensor-Listener, Timer) in ViewModels oder Use Cases kapseln, nicht in Composables.

---

## 6  Testkonventionen

### 6.1  Teststruktur

```
app/src/
├── test/          # Unit-Tests (JVM, kein Android-Geraet noetig)
└── androidTest/   # Instrumentierte Tests (Geraet/Emulator noetig)
```

### 6.2  Richtlinien

- **Domain-Logik** (z.B. `TimeTextFormatter`) mit reinen JUnit-Unit-Tests testen.
- **Compose-UI** mit `ui-test-junit4` (instrumentierte Tests) testen.
- **Tiles und Complications** mit den `tiles-tooling`-Bibliotheken in der Vorschau pruefen.
- Testklassen benennen: `<KlassenName>Test.kt` (z.B. `TimeTextFormatterTest.kt`).
- Testmethoden benennen: Beschreibend mit Backticks oder `snake_case`
  (z.B. `` `gibt halb neun zurueck fuer 8 Uhr 30` `` oder `returns_halbNeun_for_0830`).

### 6.3  Debugging auf der Galaxy Watch

```powershell
# Uhr ueber WLAN verbinden (IP und Port von der Uhr ablesen)
adb connect <IP>:<PORT>

# Logs der App filtern
adb logcat -s "com.example.myapplication"

# Complication-Update manuell ausloesen
adb shell am broadcast -a android.support.wearable.complications.ACTION_COMPLICATION_UPDATE_REQUEST
```

---

## 7  Anpassung und Erweiterung

### 7.1  Neue Activity hinzufuegen

1. Kotlin-Klasse in `presentation/` erstellen, die von `ComponentActivity` erbt.
2. Im `AndroidManifest.xml` registrieren:
   ```xml
   <activity
       android:name=".presentation.NeueActivity"
       android:exported="false"
       android:theme="@android:style/Theme.DeviceDefault" />
   ```
3. `exported="false"` setzen, wenn die Activity nur app-intern aufgerufen wird.
4. Navigation per `Intent` oder Compose-Navigation einrichten.

### 7.2  Neuen Service hinzufuegen

1. Kotlin-Klasse im passenden Package erstellen (z.B. `tile/`, `complication/` oder neues Package).
2. Im Manifest registrieren – **immer mit Permission**, wenn `exported="true"`:
   ```xml
   <service
       android:name=".meinpackage.MeinService"
       android:exported="true"
       android:permission="com.example.MEINE_PERMISSION">
       <intent-filter>
           <action android:name="..." />
       </intent-filter>
   </service>
   ```
3. Fuer Tiles: `BIND_TILE_PROVIDER`-Permission und passenden Intent-Filter verwenden.
4. Fuer Complications: `BIND_COMPLICATION_PROVIDER`-Permission und `SUPPORTED_TYPES` Meta-Daten setzen.

### 7.3  Neue Berechtigung hinzufuegen

1. In `AndroidManifest.xml` deklarieren:
   ```xml
   <uses-permission android:name="android.permission.BODY_SENSORS" />
   ```
2. Pruefen, ob es sich um eine **Dangerous Permission** handelt (z.B. `BODY_SENSORS`, `ACTIVITY_RECOGNITION`).
3. Falls ja: Laufzeit-Abfrage in `presentation/` implementieren **vor** dem ersten Sensor-Zugriff.
4. Fehlende Berechtigung graceful behandeln (Hinweistext anzeigen, Feature deaktivieren).

### 7.4  Neues Hardware-Feature deklarieren

```xml
<!-- Pflicht-Feature: App wird auf Geraeten ohne dieses Feature nicht angezeigt -->
<uses-feature android:name="android.hardware.sensor.stepcounter" android:required="true" />

<!-- Optionales Feature: App funktioniert auch ohne, nutzt es aber, wenn vorhanden -->
<uses-feature android:name="android.hardware.sensor.heartrate" android:required="false" />
```

> `required="true"` schraenkt die Geraetekompatibilitaet ein.
> Fuer optionale Features immer `required="false"` verwenden und im Code pruefen, ob der Sensor vorhanden ist.

### 7.5  Complication-Typen erweitern

Um weitere Complication-Typen zu unterstuetzen (z.B. `LONG_TEXT`, `RANGED_VALUE`):

```xml
<meta-data
    android:name="android.support.wearable.complications.SUPPORTED_TYPES"
    android:value="SHORT_TEXT,LONG_TEXT,RANGED_VALUE" />
```

Gleichzeitig muss in `MainComplicationService.kt` die Logik fuer den jeweiligen Typ implementiert werden.

### 7.6  Periodisches Complication-Update aktivieren

Aktuell steht `UPDATE_PERIOD_SECONDS` auf `0` (nur on-demand).
Fuer periodische Updates (z.B. alle 15 Minuten):

```xml
<meta-data
    android:name="android.support.wearable.complications.UPDATE_PERIOD_SECONDS"
    android:value="900" />
```

> **Achtung:** Haeufige Updates verbrauchen viel Akku. Werte unter 300 Sekunden (5 Minuten) werden
> vom System auf 300 angehoben.

---

## 8  Wichtige Bibliotheken

| Bibliothek                                    | Zweck                                              |
|-----------------------------------------------|-----------------------------------------------------|
| `androidx.wear.compose:compose-material`      | Material-Komponenten fuer Wear OS                   |
| `androidx.wear.compose:compose-foundation`    | Wear-spezifische Layouts (ScalingLazyColumn etc.)   |
| `androidx.wear.tiles:tiles`                   | Tile-API                                            |
| `androidx.wear.tiles:tiles-material`          | Material-Komponenten fuer Tiles                     |
| `com.google.android.horologist:*`             | Hilfsbibliotheken fuer Wear OS (Compose, Tiles)     |
| `androidx.wear.watchface:watchface-complications-data-source-ktx` | Complication-Datenquellen-API |
| `com.google.android.gms:play-services-wearable` | Google Play Services fuer Wearables              |
| `androidx.core:core-splashscreen`             | Splash Screen API                                   |
| `androidx.activity:activity-compose`          | Compose-Integration fuer Activities                 |

---

## 9  Checkliste vor einem Pull Request

- [ ] `.\gradlew :app:assembleDebug` laeuft fehlerfrei.
- [ ] `.\gradlew :app:testDebugUnitTest` laeuft fehlerfrei.
- [ ] `.\gradlew :app:lintDebug` zeigt keine neuen Warnungen/Fehler.
- [ ] Alle neuen Strings in `res/values/strings.xml` eingetragen (keine Hardcoded-Strings).
- [ ] Neue Manifest-Komponenten korrekt registriert (`exported`, `permission`).
- [ ] Dangerous Permissions werden zur Laufzeit abgefragt.
- [ ] Code ist auf Deutsch kommentiert (Inline-Kommentare) oder selbsterklaerend benannt.
- [ ] Getestet auf einem echten Wear-OS-Geraet oder im Wear-OS-Emulator.

