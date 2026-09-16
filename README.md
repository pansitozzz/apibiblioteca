# Biblioteca UNTELS

API REST para gestión de biblioteca universitaria con préstamos, reservas y control de multas, acompañada de un frontend en Angular.

## Contexto

Proyecto académico del curso **Gestión de Configuración de Software** de la Universidad Nacional Tecnológica de Lima Sur (UNTELS). Fue desarrollado en equipo bajo metodología **Scrum** durante **4 sprints**, usando **Jira** para la gestión de tareas y **GitFlow** para el control de versiones.

El sistema busca digitalizar procesos habituales de una biblioteca universitaria: consulta del catálogo, préstamos y devoluciones, reservas cuando no existe disponibilidad inmediata y cálculo automático de multas por atraso.

## Funcionalidades principales

- Autenticación y autorización mediante JWT.
- Gestión de libros y disponibilidad de ejemplares.
- Registro de préstamos y devoluciones.
- Reservas cuando no existe stock disponible.
- Cálculo de multas por atraso.
- Separación de permisos por rol.
- Documentación interactiva de la API con Swagger/OpenAPI.
- Migraciones de base de datos versionadas con Flyway.
- Pruebas unitarias y de integración en el backend.

## Stack tecnológico

### Backend

- Java 17
- Spring Boot 3.3
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway
- MapStruct
- springdoc-openapi / Swagger UI
- Lombok
- JUnit 5, Mockito, MockMvc y H2
- Maven

### Frontend

- Angular 22
- TypeScript
- Angular Material
- Reactive Forms
- RxJS

### Herramientas y proceso

- Git / GitFlow
- Jira
- Scrum
- Docker / Docker Compose

## Arquitectura

El backend utiliza una arquitectura en capas:

```text
controller -> service -> repository
                |
              mapper
```

Los DTO de entrada y salida están separados, las excepciones de negocio se centralizan con `@RestControllerAdvice`, y la seguridad se organiza mediante filtro JWT y reglas de autorización por rol.

El frontend se organiza por dominio:

```text
core/       -> servicios HTTP, interceptores, guards y modelos compartidos
features/   -> funcionalidades de autenticación, catálogo, préstamos y administración
shared/     -> componentes reutilizables
```

## Ejecución local

### Backend con Docker

1. Copiar `backend/.env.example` como `backend/.env` y completar las variables requeridas.
2. Ejecutar:

```bash
docker compose up --build
```

El backend queda disponible en `http://localhost:8080` y Swagger UI en `http://localhost:8080/swagger-ui.html`.

### Frontend

```bash
cd frontend
npm install
ng serve
```

La aplicación queda disponible en `http://localhost:4200`.

## Pruebas

```bash
cd backend
./mvnw test
```

Incluye pruebas unitarias sobre reglas de negocio de préstamos y pruebas de integración de controladores de autenticación, libros y préstamos.

## Datos de demostración

El proyecto incluye usuarios ficticios de ejemplo para facilitar pruebas locales. No se utilizan datos reales de personas ni de organizaciones.

## Autoría

Proyecto académico desarrollado en equipo para el curso **Gestión de Configuración de Software — UNTELS**.
