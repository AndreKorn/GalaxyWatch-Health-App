# doc-validator – Skill-Dokumentation

> **Dokumentations-Validierungs- und Generierungsagent** für das ki-cli-poc Projekt (Java 17 / Maven / Spring Boot 3.3.x).

Dieser Skill prüft systematisch die Dokumentationsqualität im Projekt, identifiziert Lücken und kann fehlende Dokumentation automatisch generieren.

---

## Inhaltsverzeichnis

1. [Zweck und Motivation](#zweck-und-motivation)
2. [Voraussetzungen](#voraussetzungen)
3. [Verwendung](#verwendung)
4. [Konfiguration / Anpassung](#konfiguration--anpassung)
5. [Prüfbereiche im Detail](#prüfbereiche-im-detail)
6. [Best Practices für Skill-Erstellung](#best-practices-für-skill-erstellung)
7. [Bekannte Einschränkungen](#bekannte-einschränkungen)
8. [Lizenz](#lizenz)

---

## Zweck und Motivation

In einem Proof of Concept wie **ki-cli-poc** ist gute Dokumentation entscheidend, damit das Team und zukünftige Entwickler den Code schnell verstehen. Manuelle Dokumentationsprüfung ist fehleranfällig und zeitaufwändig.

Der **doc-validator** Skill automatisiert diese Prüfung und bietet:

| Fähigkeit                         | Nutzen                                                             |
|-----------------------------------|--------------------------------------------------------------------|
| **JavaDoc-Prüfung**              | Stellt sicher, dass alle public Klassen und Methoden dokumentiert sind |
| **README-Vollständigkeit**       | Prüft ob alle Pflicht-Abschnitte vorhanden sind                    |
| **Changelog-Validierung**        | Validiert Format und Konsistenz der Versionshistorie               |
| **Konfigurationsdokumentation**  | Prüft ob alle Spring Boot Properties dokumentiert sind             |
| **Code-Doku-Konsistenz**         | Erkennt veraltete oder widersprüchliche Dokumentation              |

---

## Voraussetzungen

| Voraussetzung                    | Beschreibung                                                        |
|----------------------------------|---------------------------------------------------------------------|
| **GitHub Copilot**               | Aktive Copilot-Lizenz mit Zugriff auf Custom Agents/Skills          |
| **Workspace**                    | Das ki-cli-poc Projekt muss als Workspace geöffnet sein             |
| **Java-Quelldateien**            | Der Skill analysiert Dateien unter `src/main/java/` und `src/test/java/` |
| **IDE-Integration**              | JetBrains IDE oder VS Code mit Copilot-Extension                    |

> **Hinweis:** Der Skill benötigt Lesezugriff auf alle Projektdateien. Schreibzugriff ist nur erforderlich, wenn fehlende Dokumentation automatisch generiert werden soll.

---

## Verwendung

### Vollständige Dokumentationsprüfung

Öffne den Copilot-Chat und gib folgenden Prompt ein:

```
Prüfe die gesamte Dokumentation des Projekts mit dem doc-validator Skill
und erstelle einen vollständigen Validierungsbericht.
```

### JavaDoc einer bestimmten Datei prüfen

```
Prüfe die JavaDoc-Dokumentation der Datei PromptController.java
und zeige alle fehlenden oder unvollständigen Einträge.
```

### README-Abschnitt generieren

```
Generiere einen "API-Endpunkte"-Abschnitt für die README.md
basierend auf den vorhandenen Controller-Klassen.
```

### Konsistenzprüfung

```
Prüfe ob die Projektstruktur in der README.md mit der tatsächlichen
Verzeichnisstruktur übereinstimmt und zeige Abweichungen.
```

### Erwartetes Ausgabeformat

Der Skill liefert einen strukturierten Report mit drei Kategorien:

| Symbol | Kategorie     | Bedeutung                                       |
|--------|---------------|-------------------------------------------------|
| ✅     | **OK**        | Dokumentation vorhanden und korrekt              |
| ⚠️     | **WARNUNG**   | Verbesserungspotenzial, aber kein kritischer Fehler |
| ❌     | **FEHLER**    | Fehlende oder inkonsistente Dokumentation        |

**Beispielausgabe:**

```
## Zusammenfassung

| Kategorie      | FEHLER | WARNUNG | OK |
|----------------|--------|---------|----|
| JavaDoc        |   1    |    3    |  8 |
| README         |   0    |    1    | 11 |
| Konsistenz     |   0    |    2    |  5 |
| **Gesamt**     | **1**  |  **6**  | **24** |
```

---

## Konfiguration / Anpassung

Der Skill kann über die `doc-validator.skill.md` angepasst werden:

### Schweregrade anpassen

Die Zuordnung von Regeln zu Schweregraden (FEHLER/WARNUNG) kann im Abschnitt „Regeln und Richtlinien" der Skill-Definition geändert werden. Zum Beispiel:

```markdown
# Von WARNUNG auf FEHLER ändern:
| Fehlende `@param`-Tags | **FEHLER** | Jeder Parameter ... |
```

### Pflicht-Abschnitte in README erweitern

Soll z. B. ein Abschnitt „API-Endpunkte" verpflichtend werden, ändere in der Skill-Definition:

```markdown
| API-Endpunkte | **Pflicht** | REST-API-Dokumentation |
```

### Spracheinstellungen

Standardmäßig erwartet der Skill deutsche Dokumentation. Für englischsprachige Projekte passe den Abschnitt „Sprachunterstützung" in der Skill-Definition an.

### Projektspezifische Klassen

Die Liste der fokussierten Klassen kann erweitert werden:

```markdown
**Spezifische Klassen im Fokus:**
- `KiCliPocApplication.java`
- `PromptController.java`
- `CopilotService.java`
- `CopilotConfig.java`
- `MeineNeueKlasse.java`          # ← hier ergänzen
```

---

## Prüfbereiche im Detail

### JavaDoc

Der Skill prüft alle `.java`-Dateien unter `src/main/java/net/tkinline/kiclipoc/`:

- **Klassen-JavaDoc:** Jede `public` Klasse braucht eine Beschreibung und `@author`
- **Methoden-JavaDoc:** Jede `public` Methode braucht eine Beschreibung, `@param`, `@return`, `@throws`
- **Package-Info:** Jedes Paket sollte eine `package-info.java` haben

> **Hinweis:** Die Fake-/Stub-Klassen unter `src/test/java/.../fake/` werden ebenfalls geprüft, da diese gemäß der Teststrategie (Fakes & Stubs statt Mocking-Frameworks) als vollwertige Implementierungen gelten und entsprechend dokumentiert sein sollten.

### README

Prüft die `README.md` im Projektwurzelverzeichnis auf 11 Pflicht-Abschnitte (Überblick, Projektversion, Voraussetzungen, Installationsanleitung, Build-Anleitung, Startanleitung, Funktionen, Konfiguration, Projektstruktur, Test, Lizenz) und 4 optionale Abschnitte.

### Changelog

Falls eine `CHANGELOG.md` existiert, wird das Format nach dem [Keep a Changelog](https://keepachangelog.com/de/) Standard validiert.

### Spring Boot Konfiguration

Prüft, ob alle Custom-Properties in `application.properties` (z. B. `copilot.cli.command`, `copilot.cli.timeout`, `copilot.shell.type`) in der README dokumentiert sind.

### Konsistenz

Vergleicht die Dokumentation mit dem tatsächlichen Code und erkennt:

- Nicht existierende Klassen/Methoden, die in der Doku genannt werden
- Verzeichnisstrukturen in der README, die nicht der Realität entsprechen
- Properties, die dokumentiert aber im Code nicht verwendet werden (oder umgekehrt)

---

## Best Practices für Skill-Erstellung

Dieser Skill dient auch als **Vorlage** für die Erstellung neuer Skills im Projekt. Folgende Best Practices sollten beachtet werden:

### Verzeichnisstruktur

```
.github/skills/<skill-name>/
├── README.md                              # Ausführliche Dokumentation
├── <skill-name>.skill.md                  # Skill-Definition (Hauptdatei)
├── examples/
│   ├── <anwendungsfall-1>.prompt.md       # Beispiel-Prompt 1
│   └── <anwendungsfall-2>.prompt.md       # Beispiel-Prompt 2
```

### Skill-Definition (`*.skill.md`)

| Element                  | Beschreibung                                                         |
|--------------------------|----------------------------------------------------------------------|
| **YAML-Frontmatter**    | `name`, `description`, `tools`, `model` – immer vollständig angeben  |
| **Rollenbeschreibung**   | Klar formulieren, was der Skill tut und was nicht                    |
| **Projektkontext**       | Relevante Projektinformationen als Tabelle einbetten                 |
| **Regeln**               | Konkrete, prüfbare Regeln mit Schweregrad-Zuordnung                  |
| **Ausgabeformat**        | Exaktes Template für die erwartete Ausgabe definieren                |
| **Workflow**             | Schrittweisen Ablauf beschreiben                                     |

### Qualitätskriterien für Skills

- **Sofort nutzbar:** Der Skill muss ohne zusätzliche Konfiguration funktionieren
- **Konsistenter Stil:** Markdown-Stil des Hauptprojekts übernehmen (Tabellen, Codeblöcke, `>`-Boxen)
- **Sprache:** Alle Dateien auf Deutsch (oder einheitlich in der Projektsprache)
- **Beispiele:** Mindestens 2 Beispiel-Prompts mit erwartetem Eingabe-/Ausgabeformat
- **Anpassbarkeit:** Konfigurationsoptionen dokumentieren
- **Projektkontext:** Echte Klassen-/Package-Namen verwenden, keine generischen Platzhalter

### Tools-Auswahl

Wähle nur Tools, die der Skill tatsächlich benötigt:

| Tool               | Typischer Einsatz                                      |
|--------------------|--------------------------------------------------------|
| `read_file`        | Dateien lesen und analysieren                          |
| `grep_search`      | Text-Suche in Dateien (z. B. fehlende JavaDoc finden)  |
| `file_search`      | Dateien nach Name/Muster finden                        |
| `list_dir`         | Verzeichnisstruktur prüfen                             |
| `semantic_search`  | Code-Bedeutung verstehen                               |
| `insert_edit_into_file` | Dokumentation direkt ergänzen                     |
| `run_in_terminal`  | Build-/Test-Befehle ausführen                          |

---

## Bekannte Einschränkungen

| Einschränkung                               | Beschreibung                                                                |
|---------------------------------------------|-----------------------------------------------------------------------------|
| **Keine automatische Ausführung**           | Der Skill muss manuell über einen Prompt aufgerufen werden – es gibt keinen automatischen Pre-Commit-Hook. |
| **Kontextfenster-Limit**                    | Bei sehr großen Projekten mit vielen Dateien kann das Kontextfenster des Modells nicht ausreichen, um alle Dateien gleichzeitig zu analysieren. In diesem Fall in Teilbereichen prüfen. |
| **Keine Grammatikprüfung**                  | Der Skill prüft die Struktur und Vollständigkeit der Dokumentation, aber keine Rechtschreibung oder Grammatik. |
| **Nur statische Analyse**                   | Der Skill kann keine Laufzeit-Dokumentation (z. B. Swagger/OpenAPI-Ausgaben) prüfen. |
| **Teststrategie-spezifisch**                | Die Regeln sind auf die Fakes & Stubs Teststrategie zugeschnitten. Projekte mit Mockito o. ä. müssen die Regeln anpassen. |

---

## Lizenz

Dieser Skill ist Teil des Projekts **ki-cli-poc** und steht unter der **MIT-Lizenz**.

Siehe [LICENSE](../../../LICENSE) für Details.

---

> **Kontakt:** Bei Fragen oder Verbesserungsvorschlägen bitte ein Issue im Repository erstellen.

