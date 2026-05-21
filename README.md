# E-Commerce Microservices System

Microservice-based e-commerce backend system developed using Spring Boot and Spring Cloud.

## Technologies

- Java 17
- Spring Boot
- Spring Security
- Spring Cloud
- Eureka Server
- API Gateway
- RabbitMQ
- PostgreSQL
- Redis
- Docker Compose
- JWT Authentication

## Features

- JWT Authentication & Authorization
- Refresh Token Mechanism
- API Gateway Routing
- Eureka Service Discovery
- RabbitMQ Async Communication
- Event-Driven Stock Management
- Redis Token Blacklist
- Dockerized Microservices
- Role-Based Access Control

## Microservices

- auth-service
- product-service
- order-service
- notification-service
- api-gateway
- discovery-server

## RabbitMQ Flow

Order Service → RabbitMQ → Product Service

When an order is created:
1. Order service publishes event
2. Product service consumes event
3. Product stock is updated asynchronously

## **About** 
Backend-focused e-commerce microservices project built using Java and Spring ecosystem technologies.
The system includes authentication, product and order management, asynchronous messaging with RabbitMQ, Redis caching, and Dockerized deployment.

## **Haqqında**
Java və Spring ekosistem texnologiyaları istifadə edilərək hazırlanmış mikroservis əsaslı e-commerce backend layihəsi.
Layihədə JWT authentication, RabbitMQ ilə asynchronous communication, Redis caching, API Gateway, Eureka Service Discovery və Dockerized deployment tətbiq olunub.
