# Copilot Instructions for ki-cli-poc

## Build, Test, and Lint Commands

- **Build project:**
  ```bash
  mvn clean install
  ```
- **Compile only (no tests):**
  ```bash
  mvn clean compile
  ```
- **Run all tests:**
  ```bash
  mvn test
  ```
- **Run a single test class:**
  ```bash
  mvn test -Dtest=PromptControllerTest
  ```
- **Run with detailed output:**
  ```bash
  mvn test -Dsurefire.useFile=false
  ```
- **Package JAR (skip tests):**
  ```bash
  mvn clean package -DskipTests
  ```
- **Start via Maven:**
  ```bash
  mvn spring-boot:run
  ```
- **Start JAR directly:**
  ```bash
  java -jar target/ki-cli-poc-<VERSION>.jar
  ```

## High-Level Architecture

- **Spring Boot 3.3.7** (Java 17, Maven)
- **Shell Integration:**
  - Backend launches a system shell (e.g., PowerShell, bash) and exposes Copilot CLI (`gh copilot`) via REST API and web UI.
- **Web UI:**
  - Users enter prompts in a browser; prompts are sent to the backend, which executes them via Copilot CLI and returns results.
- **Key Classes:**
  - `KiCliPocApplication.java`: Main Spring Boot entry point
  - `PromptController.java`: REST controller for prompt handling
  - `CopilotService.java`: Manages shell/CLI integration
  - `CopilotConfig.java`: Configuration
- **Test Strategy:**
  - Uses Fakes and Stubs (no Mockito). Test classes are under `src/test/java/net/tkinline/kiclipoc/`.

## Key Conventions

- **No mocking frameworks:** Only Fakes and Stubs for tests. Place Fakes in `fake/`, Stubs for controllers.
- **Configuration:**
  - Use `application.properties` for settings (e.g., `copilot.cli.command`, `copilot.cli.timeout`).
  - All config values can be overridden via environment variables or JVM args.
- **Project structure:**
  - Main code: `src/main/java/net/tkinline/kiclipoc/`
  - Tests: `src/test/java/net/tkinline/kiclipoc/`
  - Web templates: `src/main/resources/templates/`
- **Skill/Agent Integration:**
  - Custom Copilot skills/agents are in `.github/skills/` and `.github/agents/`.
  - The `doc-validator` skill checks documentation quality and can auto-generate missing docs.
- **Language:**
  - Documentation and code comments are primarily in German.

## Additional Notes

- **IDE:**
  - Recommended: IntelliJ IDEA. Open the project via `pom.xml` for best results.
- **Copilot CLI:**
  - Requires `gh` CLI and Copilot CLI extension installed and authenticated.
- **Security:**
  - This is a proof of concept. Do not use in production without additional security measures.

---

For more details, see `README.md`, `docs/INTELLIJ-MAVEN-IMPORT.md`, and `.github/skills/doc-validator/README.md`.
