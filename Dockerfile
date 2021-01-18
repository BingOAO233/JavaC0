# gradle 好大
FROM gradle:jdk14
WORKDIR /app
COPY build.gradle gradle settings.gradle /app/
COPY src /app/src
RUN gradle fatjar --no-daemon
# test
COPY in.txt /app/
RUN java -jar build/libs/javac0.jar -s -o - ./in.txt