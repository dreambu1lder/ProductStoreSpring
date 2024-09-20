FROM tomcat:10.1.28-jdk21

COPY target/ProductStore.war /usr/local/tomcat/webapps/

ENV JPDA_ADDRESS="*:8000"
ENV JPDA_TRANSPORT="dt_socket"

CMD ["catalina.sh", "jpda", "run"]

EXPOSE 8080