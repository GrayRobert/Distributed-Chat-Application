# Cross Platform Distributed Chat Application (Similar to WhatsApp)

## Running the Server

Requirements: Java 8 & Maven

Local/Development — Import as a maven project into an IDE like IntelliJ or Eclipse. Then create a new run configuration that executes the main method in Application.java

Production — Package up the application as an executable jar # mvn package. Execute with # java -jar springboot-api-0.0.2-SNAPSHOT.jar &

## Running the Client

Requirements: Web Browser, Node & NPM

Local/Development — Import the project and install dependencies with # npm install. Then run the application with # npm run serve

Production — Package the application with # npm run build. Then just serve it up with a web server such as Apache or Nginx
