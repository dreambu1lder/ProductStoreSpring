FROM tomcat:10.1.28-jdk21

# Устанавливаем telnet и netcat-openbsd
RUN apt-get update && apt-get install -y telnet netcat-openbsd

COPY target/ProductStore.war /usr/local/tomcat/webapps/

ENV JPDA_ADDRESS="*:8000"
ENV JPDA_TRANSPORT="dt_socket"

CMD ["catalina.sh", "jpda", "run"]

EXPOSE 8080