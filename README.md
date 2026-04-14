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

## Aktueller Implementierungsstand

Der aktuelle Code-Stand entspricht noch weitgehend dem Starter-Template:

- `MainActivity` zeigt einen statischen `Hello World`-Text in Compose.
- `MainTileService` liefert ein Beispiel-Tile.
- `MainComplicationService` liefert eine Beispiel-Complication (Wochentag).

Damit ist die Basis fuer die in `## Funktionsumfang` beschriebenen Features vorhanden, die konkrete
Health-Logik (z. B. `TYPE_STEP_COUNTER`) fehlt jedoch noch.

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

Dieses Projekt steht unter der [MIT-Lizenz](LICENSE).
