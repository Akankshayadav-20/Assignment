# Salesforce Validation Rules Management App

## Project Overview

This is a Spring Boot web application integrated with Salesforce using OAuth 2.0 authentication.

The main goal of this project is to manage Salesforce validation rules from a custom web interface instead of manually handling them in Salesforce Setup.

After logging in with Salesforce, the application fetches validation rules from the Salesforce org and allows users to view, enable, disable, and manage them directly from the application.

## Features

- Salesforce Login using OAuth 2.0
- Fetch all Validation Rules from Salesforce
- View Validation Rule Details
- Enable or Disable individual rules
- Enable or Disable all rules at once
- Deploy changes back to Salesforce

## Authentication Flow

- Uses Salesforce Connected App
- OAuth 2.0 Authorization Code Flow
- Secure token-based access to Salesforce APIs

## Tech Stack

- Java 17
- Spring Boot
- Spring MVC
- REST APIs
- Salesforce REST / Metadata API
- Docker
- HTML, CSS, JavaScript
- Maven

## How It Works

1. User clicks login button
2. Redirected to Salesforce login page
3. After successful login, Salesforce returns an authorization code
4. Application exchanges code for access token
5. Token is used to call Salesforce APIs
6. Validation rules are fetched and displayed in UI
7. User can enable/disable rules and push changes back to Salesforce

## Application Endpoints

- `/login` → Login Page  
- `/dashboard` → Dashboard Page  
- `/validation` → Validation Rules Page  
- `/rule-details` → Rule Details Page  
- `/logout` → Logout User  

## Docker Setup

### Build Docker Image
```bash
docker build -t salesforce-app .
