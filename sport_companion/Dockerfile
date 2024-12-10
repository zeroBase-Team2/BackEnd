FROM amazoncorretto:17 AS builder
ARG MODULE_NAME

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY ${MODULE_NAME} ${MODULE_NAME}
COPY module-domain module-domain
COPY module-core module-core

# ./gradlew 파일 권한 변경
RUN chmod +x ./gradlew

# gradle 빌드
RUN ./gradlew :${MODULE_NAME}:bootJar
FROM amazoncorretto:17
ARG MODULE_NAME

# 크롬 설치
RUN yum update
RUN yum -y install wget
RUN yum -y install unzip
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
RUN yum -y install google-chrome-stable_current_x86_64.rpm
RUN rm -rf /var/lib/apt/lists/*

COPY --from=builder ${MODULE_NAME}/build/libs/*.jar app.jar


ENTRYPOINT [ "java","-jar","/app.jar" ]
VOLUME /tmp