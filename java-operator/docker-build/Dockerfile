FROM openjdk:8
RUN /bin/mkdir /home/controller
ADD k8sJava.jar /home/controller
WORKDIR /home/controller
EXPOSE 8080
ENTRYPOINT ["/usr/local/openjdk-8/bin/java", "-jar", "k8sJava.jar"]



