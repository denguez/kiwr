FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8080
ADD build/libs/kiwr.jar kiwr.jar
ENTRYPOINT [ "java", "-jar", "kiwr.jar" ]