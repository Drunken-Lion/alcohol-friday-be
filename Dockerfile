# 먼저 필요한 패키지를 설치하고 두 스테이지를 합치기
FROM amazoncorretto:17 as builder

# gradle 설정 및 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 실행 권한 주고 gradle 실행
RUN chmod +x ./gradlew && ./gradlew bootJar -x test

# 나머지 빌드를 위한 스테이지
FROM amazoncorretto:17
# 생성된 JAR 파일을 app.jar로 복사
COPY --from=builder build/libs/*.jar app.jar

# RUNTIME 환경변수 인자 설정
ARG PROFILE
ENV SPRING_PROFILES_ACTIVE=${PROFILE}
ENV SPRING_PROFILES_INCLUDE=file,secret

# Java 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE},${SPRING_PROFILES_INCLUDE}", "app.jar"]
