# Dockerfile untuk hosting berbasis Docker (BELUM diuji di host mana pun - uji dulu secara lokal).
# Build:  docker build -t budget-buddies .
# Run:    docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... budget-buddies
# Buka:   http://localhost:8080/

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/budget-buddies.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
