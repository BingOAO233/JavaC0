# gradle 好大
FROM gradle:jdk14
WORKDIR /app
COPY build.gradle gradle settings.gradle /app/
COPY src /app/src
RUN gradle fatjar --no-daemon
# test
COPY in.txt /app/
#WORKDIR /app/build/libs
#RUN ls
#WORKDIR /app/
RUN java -jar build/libs/JavaC0.jar -s -o - ./in.txt