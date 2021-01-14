# gradle 好大
FROM gradle:jdk14
WORKDIR /app
COPY build.gradle gradle settings.gradle miniplc0-java.iml /app/
COPY src /app/src
RUN gradle fatjar --no-daemon
# test
#COPY input1 /app/
#RUN java -jar build/libs/JavaC0.jar -t ./input -o -
