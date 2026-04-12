# GalaxyWatch-Health-App

Eine einfache Wear OS App für die **Samsung Galaxy Watch 7**, die Uhrzeit, Datum und den aktuellen Schrittzähler in einer textbasierten Oberfläche anzeigt.

---

## Beschreibung

Diese App läuft nativ auf der Galaxy Watch 7 (Wear OS) und zeigt auf dem Zifferblatt in Echtzeit:

- 🕐 die aktuelle **Uhrzeit** im Format `HH:mm`
- 📅 das aktuelle **Datum** im Format `dd.MM.yyyy`
- 👟 die **täglichen Schritte** über den integrierten Schrittzähler-Sensor

Die Benutzeroberfläche wird mit **Jetpack Compose for Wear OS** entwickelt und passt sich an das runde Display der Galaxy Watch 7 an.

---

## Anforderungen

| Anforderung | Details |
|---|---|
| **IDE** | [Android Studio](https://developer.android.com/studio) (Koala oder neuer) **oder** IntelliJ IDEA mit Android-Plugin |
| **Programmiersprache** | [Kotlin](https://kotlinlang.org/) |
| **UI-Framework** | [Jetpack Compose for Wear OS](https://developer.android.com/training/wearables/compose) |
| **API** | SensorManager API (`TYPE_STEP_COUNTER`) |
| **Berechtigung** | `ACTIVITY_RECOGNITION` (wird zur Laufzeit abgefragt) |
| **Min. SDK** | API 30 (Wear OS 3.0) |
| **Target SDK** | API 34 |

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

1. Notiere die angezeigte **IP-Adresse und den Port** direkt von der Uhr (z. B. `192.168.1.42:5555`). Beides wird im Menü „Debuggen über WLAN" angezeigt – Port und IP können je nach Netzwerk abweichen.
2. Verbinde deinen PC mit **demselben WLAN-Netzwerk** wie die Uhr.
3. Ersetze in folgendem Befehl `<IP>` und `<Port>` durch die auf der Uhr angezeigten Werte und führe ihn in einem Terminal aus:

```bash
adb connect <IP>:<Port>
# Beispiel: adb connect 192.168.1.42:5555
```

4. Wähle in Android Studio / IntelliJ deine Uhr als Zielgerät aus und starte die App über den **Run**-Button (▶).

---

## Funktionsumfang

| Funktion | Beschreibung |
|---|---|
| **Uhrzeit** | Echtzeit-Anzeige im Format `HH:mm` (aktualisiert sich jede Minute) |
| **Datum** | Anzeige des aktuellen Datums im Format `dd.MM.yyyy` |
| **Schrittzähler** | Tägliche Schritte werden über den `SensorManager` (`TYPE_STEP_COUNTER`) ausgelesen und angezeigt |

---

## Projektstruktur (Überblick)

```
GalaxyWatch-Health-App/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml       # Berechtigungen & App-Konfiguration
│   │   ├── java/.../MainActivity.kt  # Einstiegspunkt der App
│   │   └── java/.../WearApp.kt       # Composable UI (Uhrzeit, Datum, Schritte)
│   └── build.gradle.kts
├── build.gradle.kts
└── README.md
```

---

## Benötigte Berechtigungen (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
```

Diese Berechtigung ist ab Android 10 (API 29) erforderlich, um auf den Schrittzähler-Sensor zugreifen zu dürfen, und wird zur Laufzeit vom Nutzer bestätigt.

---

## Lizenz

Dieses Projekt steht unter der [MIT-Lizenz](LICENSE).
