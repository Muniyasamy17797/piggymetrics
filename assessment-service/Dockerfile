FROM openjdk:8-jdk-alpine
MAINTAINER Aleksandr Bratchik <bratchik@gmail.com>

ADD target/assessment-service.jar assessment-service.jar
ENTRYPOINT ["java", "-jar", "/assessment-service.jar"]