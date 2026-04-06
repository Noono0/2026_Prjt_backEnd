# syntax=docker/dockerfile:1
# 멀티 스테이지: 리포지토리만 있으면 서버에서 docker build 가능
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN mkdir -p /data/uploads
COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
