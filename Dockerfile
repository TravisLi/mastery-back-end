FROM openjdk:8-jre-alpine

RUN apk add --update \
    	python \
        python-dev \
        py-pip \
        build-base \
    	gcc \
    	musl-dev && \
        pip install yowsup2

RUN apk --update add tzdata && \
    cp /usr/share/zoneinfo/Asia/Hong_Kong /etc/localtime && \
    apk del tzdata && \
    rm -rf /var/cache/apk/*

ADD target/app.jar /app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xmx1024m -XX:+UseConcMarkSweepGC","-jar","/app.jar"]
