# Docker Setup for CargoGo

This document explains how to run the entire CargoGo application using Docker Compose.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed

## Quick Start

To start all services (MySQL, Backend, Frontend):

```bash
docker-compose up -d
```

This will:
- Start MySQL database on port 3306
- Build and start the Spring Boot backend on port 8080
- Build and start the React frontend on port 80
- Set up proper networking between services

## Access the Application

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080/api/v1
- **MySQL**: localhost:3306

## Services

### MySQL
- Container name: `porter-mysql`
- Database: `porter_db`
- User: `porter_user`
- Password: `porter_password`
- Root password: `rootpassword`

### Backend
- Container name: `porter-backend`
- Port: 8080
- Depends on MySQL (waits for MySQL to be healthy)
- Environment variables configured for database connection

### Frontend
- Container name: `porter-frontend`
- Port: 80
- Serves React app via nginx
- Proxies `/api/*` requests to backend

## Docker Compose Commands

### Start all services
```bash
docker-compose up -d
```

### Stop all services
```bash
docker-compose down
```

### Stop and remove volumes (deletes data)
```bash
docker-compose down -v
```

### View logs
```bash
docker-compose logs -f
```

### View logs for specific service
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### Rebuild specific service
```bash
docker-compose up -d --build backend
docker-compose up -d --build frontend
```

## Development

### Running only backend (with local MySQL)
```bash
docker-compose up -d mysql backend
```

### Running only frontend (with local backend)
```bash
docker-compose up -d frontend
```

### Accessing individual containers

```bash
# Backend shell
docker exec -it porter-backend sh

# Frontend shell
docker exec -it porter-frontend sh

# MySQL shell
docker exec -it porter-mysql mysql -u porter_user -pporter_password porter_db
```

## Troubleshooting

### Backend fails to start
Check if MySQL is healthy:
```bash
docker-compose ps mysql
```

View backend logs:
```bash
docker-compose logs backend
```

### Frontend can't connect to backend
Check if backend is running:
```bash
docker-compose ps backend
```

Check nginx configuration in `frontend/nginx.conf`

### Port conflicts
If ports 80, 8080, or 3306 are already in use, modify the ports in `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Change backend port
  - "81:80"      # Change frontend port
  - "3307:3306"  # Change MySQL port
```

### Reset everything
To completely reset the application (deletes all data):
```bash
docker-compose down -v
docker-compose up -d --build
```

## Building Images Separately

If you want to build images without running them:

```bash
# Build backend image
docker build -t porter-backend .

# Build frontend image
docker build -t porter-frontend ./frontend
```

## Production Considerations

For production deployment:

1. **Change passwords**: Update MySQL passwords in `docker-compose.yml`
2. **Environment variables**: Use environment files or secrets management
3. **SSL/TLS**: Configure nginx for HTTPS
4. **Resource limits**: Add resource constraints to services
5. **Logging**: Configure centralized logging
6. **Monitoring**: Add health checks and monitoring
7. **Backup**: Set up MySQL backup strategy

## Architecture

```
┌─────────────┐
│   Frontend  │ (nginx:80)
│  (React)    │
└──────┬──────┘
       │
       │ /api/*
       ↓
┌─────────────┐
│   Backend   │ (Spring Boot:8080)
└──────┬──────┘
       │
       │ JDBC
       ↓
┌─────────────┐
│   MySQL     │ (3306)
└─────────────┘
```

All services communicate via the `porter-network` Docker network.
