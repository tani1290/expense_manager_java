# ===== Stage 1: Build =====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -q

# ===== Stage 2: Run =====
FROM jetty:11.0-jdk17
USER root

RUN mkdir -p /data && chown jetty:jetty /data

COPY --from=build /app/target/expense-manager.war /var/lib/jetty/webapps/ROOT.war

USER jetty

ENV DB_URL=jdbc:h2:/data/expense_db
ENV DB_USER=sa
ENV DB_PASSWORD=

ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=Asia/Kolkata"

EXPOSE 8080

CMD ["--module=http", "jetty.http.port=8080"]
