# hap-metadata-all

Mono-repo that hosts the Java Spring Boot back end and the Vue 3 front end for the metadata
management suite.

## Layout

```
├── server   # Spring Boot / Maven module (WAR packaging for legacy deployment)
├── client   # Vue 3 + Vite SPA that talks to the APIs exposed by `server`
├── scripts  # Helper tooling that boots both stacks together or performs a full build
└── pom.xml  # Aggregator that wires the Maven module graph
```

The `server` module now inherits from the root aggregator POM and remains responsible for compiling
and packaging the legacy WAR. The `client` project is a standard Vite application whose build output
is treated as static assets.

## Development workflow

1. Install Node.js 18+ and a JDK 8 runtime.
2. Bootstrap the dev environment via `./scripts/dev.sh`. The script:
   * installs `client` dependencies when `node_modules/` is missing,
   * starts the Vue dev server via `npm run dev -- --host 0.0.0.0`, and
   * runs `./mvnw -pl server -am spring-boot:run` so the Spring Boot API is available on port 8080.
3. Visit the address printed by Vite (default `http://localhost:5173`). Requests to `/api/**` should
   be proxied to the Boot app as you wire things together.

> The provided `mvnw` shim simply forwards to the Maven installation found on your PATH. Install
> Apache Maven locally if the shim cannot find it.

## Building for CI/CD

* `./scripts/build.sh` runs `npm install && npm run build` inside `client/` and then executes
  `./mvnw -pl server -am clean package` to build the WAR.
* The `server` module uses the [`frontend-maven-plugin`](https://github.com/eirslett/frontend-maven-plugin)
  to re-run the same `npm install`/`npm run build` steps inside the Maven lifecycle. The `maven-resources-plugin`
  then copies the generated `client/dist` folder into `server/src/main/resources/static`. The folder is
  git-ignored and only populated during CI, ensuring the packaged WAR always contains the latest Vue build.
* If you prefer to deploy the Vue bundle separately, skip the Maven integration and host the `client/dist`
  folder directly. For the default integrated workflow simply run `mvn -pl server -am clean package` on CI
  and the plugins will prepare the static assets automatically.

## Scripts and automation

| Script | Purpose |
| --- | --- |
| `scripts/dev.sh` | Concurrently runs `./mvnw -pl server -am spring-boot:run` and `npm run dev` |
| `scripts/build.sh` | Runs the production build for both the client (`npm run build`) and server (`mvn clean package`) |

## Continuous integration

A GitHub Actions workflow (see `.github/workflows/ci.yml`) checks out the repo, installs Node + Java,
executes `npm run build` for the Vue client, and packages the Spring Boot app via `./mvnw -pl server -am clean package`.
The workflow fails immediately if either half of the stack breaks so regressions are caught early.
