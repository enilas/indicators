# Use the official Clojure image with Lein pre-installed as the base image
FROM clojure:lein

# Create and set the working directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Copy the project files into the Docker image
COPY . .

# Resolve dependencies
RUN lein deps

# Build the application
RUN lein uberjar

# Set the command to run the application
CMD ["java", "-jar", "target/uberjar/indicators-0.1.0-standalone.jar"]