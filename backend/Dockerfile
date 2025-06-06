FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17

RUN apt-get update && apt-get install -y python3 python3-pip python3-venv && \
    python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --upgrade pip && \
    /opt/venv/bin/pip install pandas numpy sqlalchemy psycopg2-binary && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

ENV PATH="/opt/venv/bin:$PATH"

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

COPY ml_service.py .
COPY Animal_Dataset.csv .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
