FROM bellsoft/liberica-openjdk-alpine-musl:latest
LABEL maintainer="EbiousVi" github="https://github.com/EbiousVi"
ARG JAR_FILE=target/pdf.jar
WORKDIR /opt/pdf-back
COPY  ${JAR_FILE} .
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "pdf.jar"]