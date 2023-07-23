FROM maven:3.8.1-openjdk-17 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
FROM maven:3.8.1-openjdk-17 as runtime
WORKDIR /app
COPY --from=build /app .
ENTRYPOINT ["mvn", "spring-boot:run"]
CMD ["-Dspring-boot.run.arguments=--initialTickers=btcusdt"]
