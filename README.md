Library Management System
Overview
This project consists of three microservices:


Library Service: Manages books, clients, and rentals.
Mail Service: Handles the sending of emails.
Technical Service: Monitors the application performance and logs method execution times.
Features
Library Service
Book Management:
Add, update, and delete books.
Search books by categories, titles, authors, etc.
Client Management:
Register clients.
Block/unblock clients based on predefined conditions.
Book Rental:
Rent books within a specified time frame, given that:
The book is available.
The client is not blocked.
Only users with the CUSTOMER role can rent books.


Mail Service
Asynchronously sends email notifications to users.
Communicates with other services via RabbitMQ/ActiveMQ.
Technical Service
Monitors and logs method execution times.
Logs include:
User email/ID executing the method.
Method execution time.
Class and method name.
Date and time of method execution.
Logs are asynchronously communicated to this service via RabbitMQ.


Integration Testing
Integration tests using Spring Boot Test Containers.
Tests cover workflows such as method monitoring and email sending.


Project Setup
Prerequisites
Java 11+
Maven 3.6+
Docker
Security
MySql
