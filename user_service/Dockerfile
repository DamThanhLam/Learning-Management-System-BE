FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
# COPY /home/ubuntu/aws.env root/aws.env
# RUN export $(grep -v '^#' /root/aws.env | xargs)
ENTRYPOINT ["java","-jar","/app.jar"]



