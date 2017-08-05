FROM openjdk:8-jre-alpine

ADD target/app.jar /app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xmx2048m -XX:+UseConcMarkSweepGC","-jar","/app.jar"]
