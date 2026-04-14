# Beispiel-Prompt: JavaDoc prüfen

> Dieser Prompt nutzt den **doc-validator** Skill, um die JavaDoc-Dokumentation im Projekt **ki-cli-poc** zu prüfen.

---

## Variante 1: Einzelne Datei prüfen

### Eingabe

```
Prüfe die JavaDoc-Dokumentation der Datei
src/main/java/net/tkinline/kiclipoc/controller/PromptController.java
mit dem doc-validator Skill.

Stelle sicher, dass:
- Die Klasse einen JavaDoc-Kommentar mit @author hat
- Alle public Methoden dokumentiert sind
- Alle Parameter mit @param beschrieben sind
- Rückgabewerte mit @return dokumentiert sind
- Exceptions mit @throws dokumentiert sind
```

### Erwartete Ausgabe

```markdown
## JavaDoc-Prüfung: PromptController.java

| Regel                        | Status     | Details                                           |
|------------------------------|------------|---------------------------------------------------|
| Klassen-JavaDoc vorhanden    | ✅ OK      | Beschreibung und @author vorhanden                |
| Methode `handlePrompt`       | ❌ FEHLER  | Fehlende JavaDoc für public Methode               |
| @param `prompt`              | ⚠️ WARNUNG | Parameter nicht mit @param dokumentiert            |
| @return                      | ⚠️ WARNUNG | Rückgabewert nicht mit @return dokumentiert        |
| @throws RuntimeException     | ⚠️ WARNUNG | Exception nicht mit @throws dokumentiert           |

### Empfohlene Ergänzung

/**
 * Verarbeitet einen Prompt und gibt die Copilot-Antwort zurück.
 *
 * @param prompt der vom Benutzer eingegebene Prompt-Text (darf nicht null sein)
 * @return die Antwort des GitHub Copilot CLI als Klartext
 * @throws RuntimeException wenn der CLI-Aufruf fehlschlägt oder ein Timeout auftritt
 */
@PostMapping("/prompt")
public String handlePrompt(@RequestBody String prompt) { ... }
```

---

## Variante 2: Ganzes Paket prüfen

### Eingabe

```
Prüfe die JavaDoc-Dokumentation aller Java-Dateien im Paket
de.tk.it.tm.kiclipoc.service mit dem doc-validator Skill.

Erstelle einen Bericht mit allen Fehlern und Warnungen.
```

### Erwartete Ausgabe

```markdown
## JavaDoc-Prüfung: Paket de.tk.it.tm.kiclipoc.service

### Zusammenfassung

| Datei                  | FEHLER | WARNUNG | OK |
|------------------------|--------|---------|----|
| CopilotService.java    |   0    |    2    |  3 |
| **Gesamt**             | **0**  |  **2**  | **3** |

### Detaillierte Ergebnisse

#### CopilotService.java

| Regel                        | Status     | Details                                      |
|------------------------------|------------|----------------------------------------------|
| Klassen-JavaDoc vorhanden    | ✅ OK      | Vollständig dokumentiert                     |
| @author vorhanden            | ✅ OK      | Autor angegeben                              |
| Methode `executePrompt`      | ✅ OK      | JavaDoc vorhanden                            |
| @param `prompt`              | ⚠️ WARNUNG | Beschreibung könnte detaillierter sein       |
| @throws dokumentiert         | ⚠️ WARNUNG | IOException nicht mit @throws dokumentiert   |

#### package-info.java

| Regel                        | Status     | Details                                      |
|------------------------------|------------|----------------------------------------------|
| Datei vorhanden              | ⚠️ WARNUNG | Keine package-info.java im Paket vorhanden   |
```

---

## Variante 3: Nur public API prüfen

### Eingabe

```
Prüfe mit dem doc-validator Skill ausschließlich die öffentliche API
des Projekts ki-cli-poc. Ignoriere:
- private und package-private Methoden
- Interne Hilfsklassen
- Test-Klassen (außer Fakes und Stubs)

Fokussiere auf:
- Alle @RestController Klassen
- Alle @Service Klassen
- Alle @Configuration Klassen
- Alle Fake-/Stub-Klassen unter src/test/java/.../fake/

Erstelle einen kompakten Bericht.
```

### Erwartete Ausgabe

```markdown
## JavaDoc-Prüfung: Öffentliche API

### Geprüfte Klassen

| Klasse                    | Typ              | Paket                              |
|---------------------------|------------------|-------------------------------------|
| PromptController          | @RestController  | de.tk.it.tm.kiclipoc.controller   |
| CopilotService            | @Service         | de.tk.it.tm.kiclipoc.service      |
| CopilotConfig             | @Configuration   | de.tk.it.tm.kiclipoc.config       |
| FakeCopilotService        | Fake (Test)      | de.tk.it.tm.kiclipoc.fake         |
| StubPromptController      | Stub (Test)      | de.tk.it.tm.kiclipoc.fake         |

### Ergebnis

| Klasse                 | Status     | Fehlende Elemente                          |
|------------------------|------------|--------------------------------------------|
| PromptController       | ⚠️ WARNUNG | @param, @return bei handlePrompt           |
| CopilotService         | ⚠️ WARNUNG | @throws bei executePrompt                  |
| CopilotConfig          | ✅ OK      | Vollständig dokumentiert                   |
| FakeCopilotService     | ❌ FEHLER  | Klassen-JavaDoc fehlt komplett             |
| StubPromptController   | ❌ FEHLER  | Klassen-JavaDoc fehlt komplett             |

> **Hinweis:** Fake- und Stub-Klassen sind gemäß der Teststrategie (Fakes & Stubs statt
> Mocking-Frameworks) vollwertige Implementierungen und sollten entsprechend dokumentiert
> sein. Sie ersetzen die echten Klassen in Tests und müssen daher verständlich beschreiben,
> welches Verhalten sie simulieren.
```

---

## Variante 4: JavaDoc automatisch generieren

### Eingabe

```
Prüfe die JavaDoc im gesamten Projekt mit dem doc-validator Skill.
Für alle gefundenen FEHLER: Generiere die fehlende JavaDoc direkt
in den entsprechenden Dateien.

Verwende dabei:
- Deutsche Beschreibungstexte
- @author mit "ki-cli-poc Team"
- @since mit der aktuellen Projektversion aus der pom.xml
- Verweise (@see, @link) auf verwandte Klassen wo sinnvoll
```

### Erwartete Ausgabe

Der Skill erstellt zunächst den Validierungsbericht und ergänzt dann die fehlende JavaDoc direkt in den Quelldateien. Beispiel:

```markdown
## Durchgeführte Änderungen

| Datei                      | Aktion                                    |
|----------------------------|-------------------------------------------|
| PromptController.java      | Klassen-JavaDoc ergänzt, @param/@return bei handlePrompt hinzugefügt |
| FakeCopilotService.java    | Klassen-JavaDoc ergänzt, Setter dokumentiert |
| StubPromptController.java  | Klassen-JavaDoc ergänzt                   |

### Zusammenfassung

- **3 Dateien** geändert
- **5 JavaDoc-Blöcke** hinzugefügt
- **0 FEHLER** verbleibend (vorher: 3)
```

---

> **Tipp:** Kombiniere die JavaDoc-Prüfung mit einer Konsistenzprüfung, um sicherzustellen, dass bestehende JavaDoc-Kommentare noch zum aktuellen Code passen:
>
> ```
> Prüfe mit dem doc-validator Skill die JavaDoc UND die Konsistenz
> zwischen Code und Dokumentation im gesamten Projekt.
> ```

