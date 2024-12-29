# jamify-uaa

Jamify microservice handling user, authentication, and authorization.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Introduction

Jamify-uaa is a microservice responsible for managing users, authentication, and authorization within the Jamify application. It leverages Spring Boot and Spring Security to provide a robust and secure authentication mechanism.

## Features

- User management
- OAuth2 authentication with Music providers
  - Once the access token is obtained, it is sent to the Jamify engine for further processing. If it's expired, the jamify-uaa will refresh it. 
- JWT token support
  - RSA key pair for token signing
  - Refresh token
- Logging of HTTP requests

## Requirements

- Java 21
- Maven 3.6 or higher
- PostgreSQL

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/jamify-uaa.git
    cd jamify-uaa
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Set up the PostgreSQL database using Docker:
    ```sh
    docker compose -f docker/compose.yml up -d
    ```

## Configuration

1. Ensure the following environment variables are set in your Docker or system environment:
    ```sh
    export SPOTIFY_CLIENT_ID=your_spotify_client_id
    export SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
    export POSTGRES_USER=your_postgres_user
    export POSTGRES_PASSWORD=your_postgres_password
    export POSTGRES_DB=your_postgres_db
    export POSTGRES_HOST=your_postgres_host eg. localhost
    export POSTGRES_PORT=your_postgres_port eg. 5432
    ```
   
2. To generate a pair of RSA keys for JWT token signing, run the following command:
    ```sh
    openssl genrsa -out private.pem 2048
    openssl rsa -in private.pem -pubout -out public.pem
    ```

## Usage

1. Run the application with the `dev` profile in local development:
    ```sh
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

2. Access the application at `http://localhost:8081`.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.