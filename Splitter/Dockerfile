FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . /app
RUN ./gradlew build
EXPOSE 9000
ENV CLIENT_ID=14c658aa31046b5a215d \
    CLIENT_SECRET=ae3c2569221502902bc7c38a969b82ab89a7df48 \
    POSTGRES_DB=splitter \
    POSTGRES_USER=user \
    POSTGRES_PASSWORD=pw \
    DATABASE_URL=jdbc:postgresql://database:5432/splitter
CMD ["java", "-jar", "build/libs/Splitter-0.0.1-SNAPSHOT.jar"]
