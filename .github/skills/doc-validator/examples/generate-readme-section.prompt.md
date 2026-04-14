# Beispiel-Prompt: README-Abschnitt generieren

> Dieser Prompt nutzt den **doc-validator** Skill, um fehlende oder unvollständige Abschnitte in der `README.md` des Projekts **ki-cli-poc** automatisch zu generieren.

---

## Variante 1: API-Endpunkte-Abschnitt generieren

### Eingabe

```
Analysiere mit dem doc-validator Skill alle @RestController-Klassen
im Projekt ki-cli-poc und generiere einen "API-Endpunkte"-Abschnitt
für die README.md.

Berücksichtige:
- Alle @RequestMapping, @GetMapping, @PostMapping etc.
- HTTP-Methode, Pfad, Beschreibung
- Request-Body und Response-Format
- Mögliche Fehlercodes

Verwende den Tabellen-Stil der bestehenden README.md.
```

### Erwartete Ausgabe

```markdown
## API-Endpunkte

Die Anwendung stellt folgende REST-Endpunkte bereit:

| Methode | Pfad           | Beschreibung                                    | Request-Body       | Response          |
|---------|----------------|-------------------------------------------------|--------------------|-------------------|
| POST    | `/api/prompt`  | Sendet einen Prompt an den GitHub Copilot CLI   | `String` (Klartext) | `String` (Klartext) |

### POST /api/prompt

Nimmt einen Prompt-Text entgegen, leitet ihn an den GitHub Copilot CLI weiter und gibt die Antwort zurück.

**Request:**

```bash
curl -X POST http://localhost:8080/api/prompt \
  -H "Content-Type: text/plain" \
  -d "Erkläre den Befehl: git rebase -i HEAD~3"
```

**Response (200 OK):**

```text
Der Befehl 'git rebase -i HEAD~3' startet einen interaktiven Rebase
der letzten 3 Commits...
```

**Fehlerfälle:**

| HTTP-Status | Ursache                                           |
|-------------|---------------------------------------------------|
| 500         | CLI-Aufruf fehlgeschlagen oder Timeout überschritten |
| 400         | Leerer oder ungültiger Prompt                      |

> **Hinweis:** Die API ist für das Proof of Concept nicht authentifiziert. In einer Produktionsumgebung sollte eine Authentifizierung ergänzt werden.
```

---

## Variante 2: Architektur-Abschnitt generieren

### Eingabe

```
Analysiere mit dem doc-validator Skill die Projektstruktur und
die Abhängigkeiten zwischen den Klassen in ki-cli-poc.

Generiere einen "Architektur"-Abschnitt für die README.md mit:
- Komponentendiagramm (als ASCII-Art, wie in der bestehenden README)
- Beschreibung der Schichten (Controller → Service → Shell-Prozess)
- Datenfluss-Beschreibung
- Verwendete Design-Patterns

Orientiere dich am Stil des bestehenden "Ablauf"-Diagramms in der README.
```

### Erwartete Ausgabe

```markdown
## Architektur

### Schichtenmodell

Das Projekt folgt einem klassischen Drei-Schichten-Modell:

```
┌─────────────────────────────────────────────────┐
│                 Präsentation                     │
│  ┌───────────────────────────────────────────┐  │
│  │  index.html (Static Web)                  │  │
│  │  → HTTP POST an /api/prompt               │  │
│  └───────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│                  Controller                      │
│  ┌───────────────────────────────────────────┐  │
│  │  PromptController                         │  │
│  │  @RestController @RequestMapping("/api")  │  │
│  │  → Validierung, Weiterleitung an Service  │  │
│  └───────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│                   Service                        │
│  ┌───────────────────────────────────────────┐  │
│  │  CopilotService                           │  │
│  │  → Shell-Prozess starten                  │  │
│  │  → gh copilot suggest/explain aufrufen    │  │
│  │  → Ausgabe lesen und zurückgeben          │  │
│  └───────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│               Externe Prozesse                   │
│  ┌───────────────────────────────────────────┐  │
│  │  Shell (PowerShell / bash / cmd)          │  │
│  │  → gh copilot suggest "<prompt>"          │  │
│  │  → GitHub API (Copilot Backend)           │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### Datenfluss

1. **Benutzer** gibt Prompt in der Web-Oberfläche ein
2. **HTTP POST** an `/api/prompt` mit Prompt als Request-Body
3. **PromptController** empfängt den Request und ruft `CopilotService.executePrompt()` auf
4. **CopilotService** startet einen Shell-Prozess mit `gh copilot suggest`
5. **GitHub Copilot API** verarbeitet den Prompt und liefert eine Antwort
6. **CopilotService** liest die Shell-Ausgabe und gibt sie als String zurück
7. **PromptController** gibt die Antwort als HTTP-Response zurück
8. **Web-Oberfläche** zeigt das Ergebnis an

### Konfiguration

Die Konfiguration wird über `CopilotConfig` (`@Configuration`) zentralisiert:

| Klasse           | Verantwortung                                      |
|------------------|----------------------------------------------------|
| `CopilotConfig`  | Liest Properties, stellt konfigurierte Beans bereit |

### Test-Architektur

```
┌────────────────────────────────────────────┐
│              Produktivcode                  │
│  PromptController → CopilotService → Shell │
└──────────────┬─────────────────────────────┘
               │ wird in Tests ersetzt durch
               ▼
┌────────────────────────────────────────────┐
│          Fakes & Stubs (Test)              │
│  StubPromptController                      │
│  FakeCopilotService → keine echte Shell    │
└────────────────────────────────────────────┘
```

> **Hinweis:** Das Projekt verwendet bewusst **Fakes & Stubs statt Mocking-Frameworks**. Siehe den Abschnitt [Test](#test) für Details zur Teststrategie.
```

---

## Variante 3: Fehlenden Abschnitt nach Prüfung generieren

### Eingabe

```
Führe mit dem doc-validator Skill eine README-Vollständigkeitsprüfung durch.
Für jeden fehlenden Pflicht-Abschnitt: Generiere den Abschnitt automatisch
basierend auf dem vorhandenen Code.

Für fehlende optionale Abschnitte: Zeige mir eine Vorschau und frage,
ob ich den Abschnitt hinzufügen möchte.
```

### Erwartete Ausgabe

```markdown
## README-Vollständigkeitsprüfung

### Pflicht-Abschnitte

| Abschnitt              | Status  |
|------------------------|---------|
| Überblick              | ✅ OK   |
| Projektversion         | ✅ OK   |
| Voraussetzungen        | ✅ OK   |
| Installationsanleitung | ✅ OK   |
| Build-Anleitung        | ✅ OK   |
| Startanleitung         | ✅ OK   |
| Funktionen             | ✅ OK   |
| Konfiguration          | ✅ OK   |
| Projektstruktur        | ✅ OK   |
| Test                   | ✅ OK   |
| Lizenz                 | ✅ OK   |

### Optionale Abschnitte

| Abschnitt       | Status          | Aktion                              |
|-----------------|-----------------|-------------------------------------|
| API-Endpunkte   | ❌ Fehlt        | Vorschau generiert (siehe unten)    |
| Architektur     | ❌ Fehlt        | Vorschau generiert (siehe unten)    |
| Changelog       | ⚠️ Keine Datei  | CHANGELOG.md existiert nicht        |
| Contributing    | ❌ Fehlt        | Vorschau generiert (siehe unten)    |

> Möchtest du einen oder mehrere der optionalen Abschnitte hinzufügen?
> Antworte z. B. mit: "Füge API-Endpunkte und Architektur hinzu"
```

---

## Variante 4: Abschnitt mit Platzhaltern generieren

### Eingabe

```
Generiere mit dem doc-validator Skill eine Vorlage für einen
"{{ABSCHNITT_NAME}}"-Abschnitt in der README.md.

Verwende Platzhalter, die ich anschließend ausfüllen kann:
- {{BESCHREIBUNG}} für Freitext-Beschreibungen
- {{VERSION}} für Versionsnummern
- {{BEFEHL}} für Shell-Befehle
- {{URL}} für URLs

Orientiere dich am Markdown-Stil der bestehenden README.md.
```

**Beispiel mit `ABSCHNITT_NAME = "Deployment"`:**

### Erwartete Ausgabe

```markdown
## Deployment

### Voraussetzungen für das Deployment

| Werkzeug              | Version   | Hinweis                          |
|-----------------------|-----------|----------------------------------|
| {{WERKZEUG_1}}        | {{VERSION}} | {{HINWEIS}}                    |
| {{WERKZEUG_2}}        | {{VERSION}} | {{HINWEIS}}                    |

### Deployment-Schritte

#### 1. Artefakt erstellen

```bash
{{BEFEHL_BUILD}}
```

#### 2. Auf Zielserver übertragen

```bash
{{BEFEHL_DEPLOY}}
```

#### 3. Anwendung starten

```bash
{{BEFEHL_START}}
```

Nach dem Deployment ist die Anwendung erreichbar unter:

```
{{URL}}
```

> **Hinweis:** {{BESCHREIBUNG_HINWEIS}}
```

---

## Verfügbare Platzhalter

Folgende Platzhalter können in allen Varianten verwendet werden:

| Platzhalter            | Beschreibung                                      | Beispielwert                             |
|------------------------|---------------------------------------------------|------------------------------------------|
| `{{ABSCHNITT_NAME}}`  | Name des zu generierenden Abschnitts              | `API-Endpunkte`, `Architektur`           |
| `{{BESCHREIBUNG}}`    | Freitext-Beschreibung                              | `Kurze Beschreibung der Komponente`      |
| `{{VERSION}}`         | Versionsnummer                                     | `0.1.0-SNAPSHOT`                         |
| `{{BEFEHL}}`          | Shell-Befehl                                       | `mvn clean install`                      |
| `{{URL}}`             | URL                                                | `http://localhost:8080`                  |
| `{{KLASSE}}`          | Java-Klassenname                                   | `PromptController`                       |
| `{{PAKET}}`           | Java-Paketname                                     | `de.tk.it.tm.kiclipoc.controller`       |
| `{{PROPERTY}}`        | Spring Boot Property                               | `copilot.cli.timeout`                    |

---

> **Tipp:** Kombiniere die README-Generierung mit einer vorherigen Vollständigkeitsprüfung, um gezielt nur fehlende Abschnitte zu ergänzen:
>
> ```
> Prüfe die README mit dem doc-validator Skill auf Vollständigkeit
> und generiere alle fehlenden Pflicht-Abschnitte automatisch.
> ```

