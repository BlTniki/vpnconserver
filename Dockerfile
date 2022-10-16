FROM openjdk:17-jdk-alpine
COPY build/libs/VPNconServer*SNAPSHOT.jar vpncon.jar
COPY ./init.sh init.sh
RUN chmod 777 init.sh
ENV SERVER_PORT ${SERVER_PORT}
ENV DB_PORT ${DB_PORT}
ENV ROOT_NAME ${ROOT_NAME}
ENV ROOT_PASSWORD ${ROOT_PASSWORD}
ENV TOKEN_SECRET_KEY ${TOKEN_SECRET_KEY}
ENTRYPOINT ["./init.sh"]