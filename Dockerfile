FROM openjdk:8-jre-alpine

RUN apk --update add tzdata && \
    cp /usr/share/zoneinfo/Asia/Hong_Kong /etc/localtime && \
    apk del tzdata && \
    rm -rf /var/cache/apk/*

ADD target/app.jar /app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xmx1024m -XX:+UseConcMarkSweepGC","-jar","/app.jar"]
