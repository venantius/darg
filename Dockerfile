FROM java:8
MAINTAINER David Jarvis <david@darg.io>

ADD target/darg.jar /srv/app.jar

WORKDIR /srv
CMD ["java", "-jar", "/srv/app.jar", "server"]
