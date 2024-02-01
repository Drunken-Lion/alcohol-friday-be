# openjdk:17라는 이미지를 pull 받아온다.
FROM openjdk:17
# JAVA 프로젝트를 클린하고 빌드한다.
CMD ["./mvnw", "clean", "package"]
# 환경 변수 선언
ARG JAR_FILE_PATH=build/libs/*.jar
# app.jar로 복사
COPY ${JAR_FILE_PATH} app.jar
# JAVA를 실행하는 파라미터
ENTRYPOINT ["java", "-jar", "app.jar"]