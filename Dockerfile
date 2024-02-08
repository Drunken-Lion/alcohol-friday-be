# 먼저 필요한 패키지를 설치하고 두 스테이지를 합치기
FROM alpine:3.19 as builder
ARG version=17.0.10.7.1
RUN apk add --no-cache wget tar && \
    wget -O /THIRD-PARTY-LICENSES-20200824.tar.gz https://corretto.aws/downloads/resources/licenses/alpine/THIRD-PARTY-LICENSES-20200824.tar.gz && \
    echo "82f3e50e71b2aee21321b2b33de372feed5befad6ef2196ddec92311bc09becb  /THIRD-PARTY-LICENSES-20200824.tar.gz" | sha256sum -c - && \
    tar xzf THIRD-PARTY-LICENSES-20200824.tar.gz && \
    rm -rf THIRD-PARTY-LICENSES-20200824.tar.gz && \
    wget -O /etc/apk/keys/amazoncorretto.rsa.pub https://apk.corretto.aws/amazoncorretto.rsa.pub && \
    SHA_SUM="6cfdf08be09f32ca298e2d5bd4a359ee2b275765c09b56d514624bf831eafb91" && \
    echo "${SHA_SUM}  /etc/apk/keys/amazoncorretto.rsa.pub" | sha256sum -c - && \
    echo "https://apk.corretto.aws" >> /etc/apk/repositories && \
    apk add --no-cache amazon-corretto-17=$version-r0 && \
    rm -rf /usr/lib/jvm/java-17-amazon-corretto/lib/src.zip

# 필요한 패키지를 설치
RUN apk update && apk add --no-cache findutils

# gradle 설정 및 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 실행 권한 주고 gradle 실행
RUN chmod +x ./gradlew && ./gradlew bootJar

# 나머지 빌드를 위한 스테이지
FROM alpine:3.19
ARG version=17.0.10.7.1
COPY --from=builder /usr/lib/jvm/default-jvm /usr/lib/jvm/default-jvm
ENV LANG C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH=$PATH:/usr/lib/jvm/default-jvm/bin
# 생성된 JAR 파일을 app.jar로 복사
COPY --from=builder build/libs/*.jar app.jar

# RUNTIME 환경변수 인자 설정
ARG PROFILE
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

# Java 실행
ENTRYPOINT ["java", "-jar", "app.jar"]