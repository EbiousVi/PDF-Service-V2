FROM bellsoft/liberica-openjdk-alpine-musl:8u322-6
LABEL maintainer="EbiousVi" github="https://github.com/EbiousVi"
ARG JAR_FILE=target/pdf.jar
WORKDIR /opt
COPY  ${JAR_FILE} .
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "pdf.jar"]