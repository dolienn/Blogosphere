# Blogosphere - Blogging Platform


**Blogosphere** is a robust blogging platform developed with **Spring Boot**, **Hibernate**, **Thymeleaf**, **JavaScript**, **PostgreSQL** and **Maven**. The platform offers a range of features designed to enhance user experience and facilitate content management.

## Key Features

- **User Accounts**: Users can create personal accounts and manage their posts, with integrated user
authentication and authorization.
- **CRUD Operations**: Facilitates Create, Read, Update, and Delete operations for posts and users if you are admin.
- **Privacy Settings**: Allows users to control the visibility of their posts with customizable privacy settings.
- **Admin Panel**: Includes an admin panel for comprehensive administrative control over the platform.
- **Interactive Features**: Users can rate and comment on posts, fostering an engaged and interactive community.


The platform emphasizes security and user experience, leveraging **JUnit** and **Mockito** for testing, **Docker** for containerization, and **Spring Security** for authentication.

# Project Setup

## Prerequisites

Ensure the following tools are installed on your system:

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)
- **Maven**: [Install Maven](https://maven.apache.org/install.html)
  
## Installation and Setup

1. **Clone the Repository**

   Clone the repository and navigate into the project directory:

   ```bash
   git clone https://github.com/dolienn/Blogosphere.git
   cd blogosphere

2. **Start Docker Services**

   Ensure Docker and Docker Compose are installed and running. Start the services with:

   ```bash
   docker-compose up --build

3. **Access the Application**

   Once the Docker services are up and running, you can visit the application at:

   http://localhost:8080

## Troubleshooting

If you encounter issues, try the following:

1. Verify that Docker and Docker Compose are properly installed and running

2. Check the logs for any errors:

   ```bash
   docker-compose logs

3. Refer to the project's documentation or reach out to the support team for further assistance.
