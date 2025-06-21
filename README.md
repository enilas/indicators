# Indicators API

REST API for searching and displaying Indicators of Compromise from AlienVault OTX.

## Prerequisites

You will need [Java](https://www.oracle.com/java/technologies/downloads/), [Clojure](https://clojure.org/guides/install_clojure), and [Leiningen](https://leiningen.org/) to run this project.

## Usage

This is the command to create an uberjar:
```
java -jar indicators-0.1.0-standalone.jar
```
This is the command to start the server:
```
lein run
```
Once the application is running, you can hit the following endpoints through the browser or with a GET request:
```
http://localhost:8890/indicators
http://localhost:8890/indicators/:indicator-id
```
This command runs tests:
```
lein test
```
## Citations

Pedestal and Component:

https://pedestal.io/pedestal/0.8/guides/pedestal-with-component.html
https://www.youtube.com/playlist?list=PLRGAFpvDgm2ylbXYIjvu3kI426zAP_Lqc

Elasticsearch Component:

https://github.com/pardeep-singh/restro-search-engine/tree/master

Docker:

https://www.freshcodeit.com/blog/clojure-in-docker-101


