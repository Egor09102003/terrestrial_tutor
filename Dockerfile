FROM eclipse-temurin:19-jdk-alpine as builder
RUN apk update && apk add maven
WORKDIR /app
#COPY . /app/.
ADD src /app/src
ADD pom.xml /app
RUN mvn -f /app/pom.xml -T 3 package -am -Dmaven.test.skip=true

FROM eclipse-temurin:19-jdk-alpine as app
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar
EXPOSE 8181
ENTRYPOINT ["java", "-Xms4G", "-jar", "/app/*.jar"]

FROM postgres:16.1 as postgres
RUN apt update && apt install -y cron

RUN rm -rf /etc/localtime && \
    ln -s /usr/share/zoneinfo/Asia/Yekaterinburg /etc/localtime

ADD ./db_snapshots /dumps

RUN echo "43 17 * * * /dumps/autodump/pg_backup.bash" >> /etc/cron.d/autodump && \
  chmod 777 /dumps/autodump/pg_backup.bash && \
  crontab /etc/cron.d/autodump
