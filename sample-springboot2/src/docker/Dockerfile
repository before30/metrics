FROM java:8-jre-alpine
ARG JAR_FILE
ENV JAR_FILE=${JAR_FILE}
COPY ${JAR_FILE} .
ENTRYPOINT java -Dspring.profiles.active=production ${JAVA_OPTS} -jar ${JAR_FILE}