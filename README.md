# Biblioteca UNTELS

API REST para gestión de biblioteca universitaria con préstamos, reservas y control de multas, con frontend en Angular.

## Problema y contexto

Este proyecto nace como trabajo del curso **Gestión de Configuración de Software** de la Universidad Nacional Tecnológica de Lima Sur (UNTELS). Se desarrolló en equipo (**Grupo 03**) siguiendo la metodología **Scrum** a lo largo de **4 sprints**, con gestión de tareas en **Jira** y control de versiones bajo el flujo **GitFlow** (ramas `develop`, `feature/*`, `release/*`).

El objetivo funcional es digitalizar los procesos de una biblioteca universitaria: catálogo de libros, préstamos y devoluciones, reservas cuando no hay stock disponible, y el cálculo automático de multas por atraso.

## Stack tecnológico

### Backend

- Java 17
- Spring Boot 3.3
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway (migraciones versionadas)
- MapStruct (mapeo entidad-DTO)
- springdoc-openapi (Swagger UI)
- Lombok
- JUnit 5 + Mockito + MockMvc + H2 (tests)
- Maven

### Frontend

- Angular 22 (standalone components, sin NgModules)
- TypeScript en modo estricto
- Angular Material
- Reactive Forms
- RxJS

## Arquitectura

El backend sigue una arquitectura en capas clásica:

```
controller -> service (interfaz + implementacion) -> repository (Spring Data JPA)
                    |
                 mapper (entidad <-> DTO)
```

- `dto/request` y `dto/response` están separados: los DTOs de entrada nunca se reutilizan como salida.
- `exception` centraliza las excepciones de negocio (`ResourceNotFoundException`, `BusinessRuleException`) y un `@RestControllerAdvice` que devuelve errores en un formato JSON consistente.
- `security` contiene el filtro JWT, el servicio de tokens y las reglas de autorización por rol.
- Las migraciones de base de datos viven en `db/migration` y se aplican con Flyway; no se usa `ddl-auto: update` en ningún perfil.

El frontend organiza el código por dominio:

```
core/        -> servicios HTTP, interceptores, guards y modelos compartidos
features/    -> páginas por funcionalidad (auth, catalogo, prestamos, admin)
shared/      -> componentes reutilizables (navbar, páginas de error)
```

Un interceptor adjunta el JWT a cada petición y otro intercepta los errores 401/403 para cerrar sesión o redirigir según corresponda. Las rutas de administración están protegidas con `authGuard` y `roleGuard`.

## Cómo correrlo localmente

### Backend

1. Copiar `backend/.env.example` a `backend/.env` y completar los valores (usuario, password de PostgreSQL y un `JWT_SECRET` propio).
2. Levantar backend + PostgreSQL con Docker:

   ```bash
   docker compose up --build
   ```

   Esto construye la imagen del backend, levanta PostgreSQL y aplica las migraciones de Flyway automáticamente al iniciar.

3. Alternativa sin Docker (requiere PostgreSQL corriendo localmente):

   ```bash
   cd backend
   export DB_URL=jdbc:postgresql://localhost:5432/biblioteca_db
   export DB_USERNAME=biblioteca_user
   export DB_PASSWORD=tu_password
   export JWT_SECRET=una_clave_larga_y_aleatoria
   ./mvnw spring-boot:run
   ```

   Flyway aplica las migraciones (`V1__init.sql`, `V2__seed_data.sql`) contra la base indicada la primera vez que arranca la aplicación.

4. Documentación interactiva de la API (Swagger UI): `http://localhost:8080/swagger-ui.html`

Usuarios de ejemplo cargados por el seed (todos ficticios):

| Rol | Email | Password |
|---|---|---|
| ADMIN | admin@biblioteca.demo | Password123 |
| BIBLIOTECARIO | bibliotecario@biblioteca.demo | Password123 |
| ESTUDIANTE | estudiante@biblioteca.demo | Password123 |

### Frontend

```bash
cd frontend
npm install
ng serve
```

La app queda disponible en `http://localhost:4200`. Por defecto apunta a `http://localhost:8080/api/v1` (ver `src/environments/environment.development.ts`); si el backend corre en otra URL, ajustar ese archivo.

## Tests del backend

```bash
cd backend
./mvnw test
```

Incluye tests unitarios de la lógica de negocio de préstamos (validación de stock, préstamo duplicado, cálculo de multas por atraso) con JUnit 5 + Mockito, y tests de integración con MockMvc + H2 sobre los controllers de autenticación, libros y préstamos.

## Créditos

Proyecto académico desarrollado en equipo (Grupo 03) para el curso Gestión de Configuración de Software, UNTELS.
