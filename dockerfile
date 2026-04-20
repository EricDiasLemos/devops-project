FROM eclipse-temurin:26

COPY Servidor.java .
COPY index.html .

RUN javac Servidor.java

CMD java Servidor.java

