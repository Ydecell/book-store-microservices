FROM maven:3.9-eclipse-temurin-25-alpine AS deps
WORKDIR /workspace

COPY api-gateway/pom.xml pom.xml
RUN mvn -f pom.xml dependency:go-offline -q -Dcheckstyle.skip=true

FROM deps AS build
COPY api-gateway/src src
RUN mvn -f pom.xml package -DskipTests -q -Dcheckstyle.skip=true

FROM eclipse-temurin:25-jre-alpine AS extract
WORKDIR /workspace
COPY --from=build /workspace/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract --destination layers

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=extract --chown=spring:spring /workspace/layers/dependencies/           ./
COPY --from=extract --chown=spring:spring /workspace/layers/spring-boot-loader/     ./
COPY --from=extract --chown=spring:spring /workspace/layers/snapshot-dependencies/  ./
COPY --from=extract --chown=spring:spring /workspace/layers/application/            ./

USER spring:spring
EXPOSE 8765

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8765/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "org.springframework.boot.loader.launch.JarLauncher"]
