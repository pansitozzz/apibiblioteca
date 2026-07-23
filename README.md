# Biblioteca UNTELS

API REST para gestion de biblioteca universitaria con prestamos, reservas y control de multas, con frontend en Angular.

## Problema y contexto

Este proyecto nace como trabajo del curso **Gestion de Configuracion de Software** de la Universidad Nacional Tecnologica de Lima Sur (UNTELS). Se desarrollo en equipo (**Grupo 03**) siguiendo la metodologia **Scrum** a lo largo de **4 sprints**, con gestion de tareas en **Jira** y control de versiones bajo el flujo **GitFlow** (ramas `develop`, `feature/*`, `release/*`).

El objetivo funcional es digitalizar los procesos de una biblioteca universitaria: catalogo de libros, prestamos y devoluciones, reservas cuando no hay stock disponible, y el calculo automatico de multas por atraso.

## Stack tecnologico

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

El backend sigue una arquitectura en capas clasica:

```
controller -> service (interfaz + implementacion) -> repository (Spring Data JPA)
                    |
                 mapper (entidad <-> DTO)
```

- `dto/request` y `dto/response` estan separados: los DTOs de entrada nunca se reutilizan como salida.
- `exception` centraliza las excepciones de negocio (`ResourceNotFoundException`, `BusinessRuleException`) y un `@RestControllerAdvice` que devuelve errores en un formato JSON consistente.
- `security` contiene el filtro JWT, el servicio de tokens y las reglas de autorizacion por rol.
- Las migraciones de base de datos viven en `db/migration` y se aplican con Flyway; no se usa `ddl-auto: update` en ningun perfil.

El frontend organiza el codigo por dominio:

```
core/        -> servicios HTTP, interceptores, guards y modelos compartidos
features/    -> paginas por funcionalidad (auth, catalogo, prestamos, admin)
shared/      -> componentes reutilizables (navbar, paginas de error)
```

Un interceptor adjunta el JWT a cada peticion y otro intercepta los errores 401/403 para cerrar sesion o redirigir segun corresponda. Las rutas de administracion estan protegidas con `authGuard` y `roleGuard`.

## Como correrlo localmente

### Backend

1. Copiar `backend/.env.example` a `backend/.env` y completar los valores (usuario, password de PostgreSQL y un `JWT_SECRET` propio).
2. Levantar backend + PostgreSQL con Docker:

   ```bash
   docker compose up --build
   ```

   Esto construye la imagen del backend, levanta PostgreSQL y aplica las migraciones de Flyway automaticamente al iniciar.

3. Alternativa sin Docker (requiere PostgreSQL corriendo localmente):

   ```bash
   cd backend
   export DB_URL=jdbc:postgresql://localhost:5432/biblioteca_db
   export DB_USERNAME=biblioteca_user
   export DB_PASSWORD=tu_password
   export JWT_SECRET=una_clave_larga_y_aleatoria
   ./mvnw spring-boot:run
   ```

   Flyway aplica las migraciones (`V1__init.sql`, `V2__seed_data.sql`) contra la base indicada la primera vez que arranca la aplicacion.

4. Documentacion interactiva de la API (Swagger UI): `http://localhost:8080/swagger-ui.html`

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

Incluye tests unitarios de la logica de negocio de prestamos (validacion de stock, prestamo duplicado, calculo de multas por atraso) con JUnit 5 + Mockito, y tests de integracion con MockMvc + H2 sobre los controllers de autenticacion, libros y prestamos.

## Capturas de pantalla

_Pendiente: agregar capturas del catalogo, el flujo de prestamo y el panel de gestion._

## Creditos

Proyecto realizado en equipo (Grupo 03, curso Gestion de Configuracion de Software, UNTELS). Nombres de companeros pendientes de confirmar con Frank.
