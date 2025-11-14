# Multi-stage build for Spring Boot application
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle ./

# 의존성 다운로드를 위해 빈 소스 복사 (캐시 최적화)
COPY src src

# 애플리케이션 빌드 (gradle 명령 직접 사용)
RUN gradle bootJar --no-daemon

# 런타임 이미지
FROM eclipse-temurin:17-jdk

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 데이터 디렉토리 생성
RUN mkdir -p /app/data

EXPOSE 8090

# 환경 변수 기본값 설정
ENV PORT=8090
ENV JSON_DATA_PATH=/app/data

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

