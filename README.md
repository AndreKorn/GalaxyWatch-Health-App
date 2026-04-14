# GalaxyWatch-Health-App

Wear-OS-Projekt fuer die **Samsung Galaxy Watch 7** auf Basis des offiziellen Compose-Starter-Templates.

---

## Beschreibung

Dieses Repository enthaelt aktuell eine lauffaehige **Wear OS Template-App** mit:

- einer Compose-basierten Hauptansicht (`Hello World`)
- einem Beispiel-Tile (`Example tile`)
- einer Beispiel-Complication (`Example complication`)

Die in `## Funktionsumfang` beschriebenen Features (Uhrzeit als Text, Datum, Schritte) sind als Zielbild dokumentiert,
im aktuellen Code-Stand aber noch nicht umgesetzt.

---

## Anforderungen

| Anforderung            | Details                                                                                                               |
|------------------------|-----------------------------------------------------------------------------------------------------------------------|
| **IDE**                | [Android Studio](https://developer.android.com/studio) (aktuelle stabile Version) **oder** IntelliJ mit Android-Plugin |
| **Programmiersprache** | [Kotlin](https://kotlinlang.org/)                                                                                     |
| **UI-Framework**       | [Jetpack Compose for Wear OS](https://developer.android.com/training/wearables/compose)                               |
| **Min. SDK**           | API 30 (Wear OS 3.0)                                                                                                  |
| **Compile SDK**        | API 36                                                                                                                |
| **Target SDK**         | API 36                                                                                                                |

### IntelliJ IDEA – Plugin installieren

1. Gehe zu *File → Settings → Plugins*.
2. Suche nach **Android** und installiere das offizielle Android-Plugin.
3. Starte IntelliJ IDEA neu.

---

## Installation & Einrichtung

### 1. Repository klonen

```bash
git clone https://github.com/AndreKorn/GalaxyWatch-Health-App.git
cd GalaxyWatch-Health-App
```

### 2. Projekt öffnen

Öffne den geklonten Ordner in **Android Studio** oder **IntelliJ IDEA**.  
Die IDE lädt automatisch alle Gradle-Abhängigkeiten herunter.

### 3. ADB-Debugging auf der Galaxy Watch aktivieren

1. Öffne auf der Galaxy Watch 7 **Einstellungen → Info zur Uhr → Softwareinfo**.
2. Tippe **7-mal** auf „Software-Version", um die **Entwickleroptionen** freizuschalten.
3. Gehe zu **Einstellungen → Entwickleroptionen** und aktiviere:
    - **ADB-Debugging**
    - **Debuggen über WLAN**

### 4. Über WLAN verbinden

1. Notiere die angezeigte **IP-Adresse und den Port** direkt von der Uhr (z. B. `192.168.1.42:5555`). Beides wird im
   Menü „Debuggen über WLAN" angezeigt – Port und IP können je nach Netzwerk abweichen.
2. Verbinde deinen PC mit **demselben WLAN-Netzwerk** wie die Uhr.
3. Ersetze in folgendem Befehl `<IP>` und `<Port>` durch die auf der Uhr angezeigten Werte und führe ihn in einem
   Terminal aus:

```bash
adb connect <IP>:<Port>
# Beispiel: adb connect 192.168.1.42:5555
```

4. Wähle in Android Studio / IntelliJ deine Uhr als Zielgerät aus und starte die App über den **Run**-Button (▶).

### 5. Build pruefen (optional)

```bash
./gradlew :app:assembleDebug
```

### 6. Deployment auf den Watch Emulator

#### 6.1 Wear OS Emulator erstellen

1. Öffne den **Device Manager** in Android Studio (über *Tools → Device Manager*).
2. Klicke auf **Create Device** (Gerät erstellen).
3. Wähle unter **Wear OS** ein Gerät aus (z. B. *Wear OS Small Round* oder *Wear OS Large Round*).
4. Wähle ein System Image:
    - **Empfohlen**: API 30 oder höher (Wear OS 3.0+)
    - Falls nicht vorhanden, klicke auf **Download** neben dem System Image
5. Konfiguriere das AVD (Android Virtual Device):
    - Name: z. B. „Galaxy Watch 7 Emulator"
    - Optional: Passe RAM und interne Speichergröße an
6. Klicke auf **Finish**, um das virtuelle Gerät zu erstellen.

#### 6.2 App auf dem Emulator starten

1. Starte den Wear OS Emulator über den Device Manager.
2. Warte, bis der Emulator vollständig gebootet ist (dies kann beim ersten Start etwas dauern).
3. Wähle im oberen Toolbar von Android Studio den Emulator als Zielgerät aus.
4. Klicke auf den **Run**-Button (▶) oder drücke `Shift + F10`.
5. Android Studio baut die App und installiert sie automatisch auf dem Emulator.

#### 6.3 App testen

Nach der Installation öffnet sich die App automatisch. Du kannst folgendes testen:

- **Uhrzeit-Anzeige**: Die aktuelle Uhrzeit sollte im Format `HH:mm` angezeigt werden und sich jede Minute aktualisieren.
- **Uhrzeit als Text**: Die Uhrzeit sollte als deutscher Text dargestellt werden (z. B. „halb neun").
- **Datum**: Das aktuelle Datum sollte im Format `dd.MM.yyyy` angezeigt werden.
- **Schrittzähler**:
  - Bei der ersten Nutzung wird nach der `ACTIVITY_RECOGNITION`-Berechtigung gefragt.
  - Nach Erteilung der Berechtigung wird die Schrittzahl angezeigt.
  - **Hinweis**: Im Emulator ist oft kein echter Schrittzähler-Sensor verfügbar. In diesem Fall wird „Sensor nicht verfügbar" angezeigt. Zum Testen der Schrittzähler-Funktion ist ein echtes Gerät erforderlich.

#### 6.4 Logs anzeigen

Um Logs der App im Emulator anzuzeigen:

```bash
# Logcat für die App filtern
adb logcat -s "com.example.myapplication"

# Oder in Android Studio: View → Tool Windows → Logcat
```

#### 6.5 Debugging im Emulator

- **Breakpoints setzen**: Setze Breakpoints im Code und nutze den Debug-Modus (🐞-Button statt ▶).
- **Layout Inspector**: *Tools → Layout Inspector* zeigt die Compose-Hierarchie live an.
- **Sensor-Simulation**: Für fehlende Sensoren (Schritte, Herzfrequenz) muss ein echtes Gerät verwendet werden.

---

## Funktionsumfang

| Funktion               | Beschreibung                                                                                     |
|------------------------|--------------------------------------------------------------------------------------------------|
| **Uhrzeit**            | Echtzeit-Anzeige im Format `HH:mm` (aktualisiert sich jede Minute)                               |
| **Uhrzeit als Text** | Die Uhrzeit wird zusätzlich als Text (z. B. „Dreizehn Uhr Fünfundvierzig“) angezeigt             |
| **Datum**              | Anzeige des aktuellen Datums im Format `dd.MM.yyyy`                                              |
| **Schrittzähler**      | Tägliche Schritte werden über den `SensorManager` (`TYPE_STEP_COUNTER`) ausgelesen und angezeigt |

### Uhrzeit als Text

Text für die Uhrzeit (Start- und Endminute jeweils eingeschlossen):

Beispiele von 8:00 bis 9:00 Uhr:

| Uhrzeit       | Text                          |
|---------------|-------------------------------|
| 8:00          | acht Uhr                      |
| 8:01 – 8:05   | kurz nach acht   |
| 8:06 – 8:10   | nach acht                 |
| 8:11 – 8:14   | gleich viertel nach acht      |
| 8:15 – 8:19   | viertel nach acht             |
| 8:20 – 8:29   | gleich halb neun              |
| 8:30 – 8:32   | halb neun                     |
| 8:33 – 8:40   | nach halb neun                |
| 8:41 – 8:44   | gleich viertel vor neun       |
| 8:45          | viertel vor neun              |
| 8:46 – 8:50   | kurz nach viertel vor neun    |
| 8:51 – 8:59   | kurz vor neun                 |
| 9:00          | neun Uhr                      |

---

## Tests

Das Projekt verfügt über umfassende Unit-Tests für alle kritischen Komponenten.

### Test-Struktur

```
app/src/test/java/com/example/myapplication/
├── domain/
│   └── TimeTextFormatterTest.kt       # Tests für Uhrzeit-zu-Text-Konvertierung
├── data/
│   └── StepCounterManagerTest.kt      # Tests für Schrittzähler-Logik
└── presentation/
    └── HealthViewModelTest.kt         # Tests für ViewModel
```

### Tests ausführen

#### Alle Unit-Tests ausführen

```bash
# Windows
.\gradlew :app:testDebugUnitTest

# Linux/Mac
./gradlew :app:testDebugUnitTest
```

#### Einzelne Test-Klasse ausführen

```bash
# TimeTextFormatter Tests
.\gradlew :app:testDebugUnitTest --tests "com.example.myapplication.domain.TimeTextFormatterTest"

# StepCounterManager Tests
.\gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.StepCounterManagerTest"

# HealthViewModel Tests
.\gradlew :app:testDebugUnitTest --tests "com.example.myapplication.presentation.HealthViewModelTest"
```

#### Test-Report anzeigen

Nach dem Test-Lauf wird ein HTML-Report generiert:

```
app/build/reports/tests/testDebugUnitTest/index.html
```

Öffne diese Datei im Browser, um eine detaillierte Übersicht aller Tests zu sehen.

### Test-Abdeckung

Die Tests decken folgende Bereiche ab:

#### TimeTextFormatterTest
- Alle Zeitbereiche gemäß README-Spezifikation (`:00`, `:01-:05`, `:06-:10`, etc.)
- Grenzwerte aller Minutenbereiche
- Alle 24 Stunden (12h- und 24h-Format)
- Übergänge zwischen Stunden (Mitternacht, Mittag)

#### StepCounterManagerTest
- Sensor-Verfügbarkeit
- Tägliche Schrittzählung
- Baseline-Reset bei Tag-Wechsel
- SharedPreferences-Speicherung
- Negative Werte-Prävention
- Lifecycle-Management (start/stop)

#### HealthViewModelTest
- UI State Initialisierung
- Uhrzeit-, Datums- und Text-Formatierung
- Berechtigungs-Handling
- Lifecycle-Management (Resume/Pause)
- StateFlow-Updates

### Instrumentierte Tests (Android-Gerät erforderlich)

Für UI-Tests (Compose) wird ein Wear OS Emulator oder echtes Gerät benötigt:

```bash
.\gradlew :app:connectedDebugAndroidTest
```

**Hinweis**: Aktuell sind noch keine instrumentierten Tests implementiert.

### Test-Abhängigkeiten

Die folgenden Test-Bibliotheken werden verwendet:

- **JUnit 4.13.2**: Unit-Testing-Framework
- **MockK 1.13.8**: Mocking-Framework für Kotlin
- **Coroutines-Test 1.7.3**: Test-Utilities für Kotlin Coroutines
- **Arch Core Testing 2.2.0**: Test-Utilities für Android Architecture Components
- **Robolectric 4.11.1**: Android-Framework für JVM-Tests

---

## Aktueller Implementierungsstand

**✅ Vollständig implementiert:**

- ✅ **MainActivity**: Zeigt alle Health-Daten in Compose UI
  - Uhrzeit (HH:mm Format, aktualisiert sich jede Minute)
  - Uhrzeit als deutscher Text
  - Datum (dd.MM.yyyy Format)
  - Schrittzähler mit Berechtigungs-Handling
- ✅ **HealthViewModel**: State Management mit StateFlow, Lifecycle-Management
- ✅ **TimeTextFormatter**: Deutsche Uhrzeit-zu-Text-Konvertierung (alle Zeitbereiche)
- ✅ **StepCounterManager**: Schrittzählung via `TYPE_STEP_COUNTER` mit täglichem Reset
- ✅ **Berechtigungen**: Laufzeit-Abfrage für `ACTIVITY_RECOGNITION`
- ✅ **Unit-Tests**: Vollständige Test-Abdeckung für Domain-, Data- und Presentation-Layer

**⏳ Noch nicht implementiert:**

- ⏳ **MainTileService**: Zeigt noch Beispiel-Daten statt Live-Health-Daten
- ⏳ **MainComplicationService**: Zeigt noch Wochentag statt Schrittzahl/Health-Daten
- ⏳ **Instrumentierte Tests**: Compose-UI-Tests für Wear OS fehlen noch

Die Kern-Funktionalität (Health-Dashboard mit Uhrzeit, Datum und Schrittzähler) ist vollständig funktionsfähig und durch Tests abgesichert.

---

## Projektstruktur (Überblick)

```
GalaxyWatch-Health-App/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/myapplication/
│   │   │   ├── presentation/MainActivity.kt
│   │   │   ├── tile/MainTileService.kt
│   │   │   └── complication/MainComplicationService.kt
│   │   └── res/values/strings.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── gradle/libs.versions.toml
└── README.md
```

---

## Benoetigte Berechtigungen (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.WAKE_LOCK"/>
```

Hinweis:

- `ACTIVITY_RECOGNITION` ist aktuell **nicht** im Manifest eingetragen.
- Fuer den spaeteren Schrittzaehler (`TYPE_STEP_COUNTER`) muss diese Berechtigung bei der Implementierung
  hinzugefuegt und zur Laufzeit abgefragt werden.

---

## Lizenz

Copyright (c) 2026 Andre Korn

Dieses Projekt steht unter der [MIT-Lizenz](LICENSE).
