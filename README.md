# Cross Platform Distributed Chat Application (Similar to WhatsApp)

Demo: https://chatappus.com

## Running the Server

Requirements: Java 8 & Maven

### Local/Development
Import as a maven project into an IDE like IntelliJ or Eclipse. Then create a new run configuration that executes the main method in Application.java. Update application.properties for local development.

### Production
Update application.properties for production. Package up the application as an executable jar and upload to your server.
```sh
$ mvn package
```
Execute with
```sh
$ java -jar whatEverLatestVersionIs.jar OR ./whatEverLatestVersionIs.jar OR add as a service
```

## Running the Client

Requirements: Web Browser, Node & NPM

### Local/Development
Import the project and install dependencies with # npm install. 
Then run the application with 
```sh
$ npm run serve
```

### Production
Package the application with 
```sh
$ npm run build
```
Then just serve it up with a web server such as Apache or Nginx
