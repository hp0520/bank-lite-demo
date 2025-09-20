# bank-lite-demo

Minimal Spring Boot Java app with a Jenkins pipeline that:
- builds & tests (Dockerized Maven — agent only needs Docker)
- packages a JAR
- builds a Docker image
- runs the app in a container and smoke-tests `/health`

## Local (no Maven/Java needed)
```bash
# build & test
docker run --rm -v "$PWD":/ws -v "$HOME/.m2":/root/.m2 -w /ws maven:3.9-eclipse-temurin-17 mvn -B clean verify
# package
docker run --rm -v "$PWD":/ws -v "$HOME/.m2":/root/.m2 -w /ws maven:3.9-eclipse-temurin-17 mvn -q -DskipTests package
# image
docker build -t bank-lite-demo:local .
# run
docker run --rm -p 8080:8080 bank-lite-demo:local
# test
curl -s http://localhost:8080/health
```
