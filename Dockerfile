# syntax=docker/dockerfile:1.6

FROM gradle:8.7-jdk21 AS builder
WORKDIR /build

COPY build.gradle settings.gradle /build/

# Gradle 캐시 사용 (BuildKit)
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle dependencies --no-daemon > /dev/null 2>&1 || true \

COPY src /build/src

RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre AS prod
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

