# Marketplace API

API REST de un marketplace de e-commerce: catálogo de productos, carrito de compras,
gestión de órdenes y administración de usuarios, con autenticación JWT y autorización
por roles.

Backend del trabajo práctico de Desarrollo de Aplicaciones (grupo 5) — UADE.
El frontend vive en [proyectoAPI_FRONT](https://github.com/romanhubUADE/proyectoAPI_FRONT).

## Stack

- Java · Spring Boot
- Spring Security + JWT
- Spring Data JPA / Hibernate
- MySQL
- Maven

## Funcionalidad

**Autenticación y autorización**
Registro y login con JWT. Los tokens se validan en un filtro propio
(`JwtAuthenticationFilter`) y los endpoints sensibles se protegen con `@PreAuthorize`
sobre dos roles, `USER` y `ADMIN`.

**Catálogo**
Productos con categorías, control de stock, descuentos por producto y descuentos
globales, y baja lógica con reactivación. Carga de imágenes por producto vía
`multipart/form-data`, con un límite de 5 MB por archivo.

**Compras y órdenes**
Creación de compras con múltiples ítems, resueltas contra stock. La relación entre
órdenes y productos se modela con una entidad intermedia (`Order_Product`) con clave
compuesta, de modo que cada línea de la orden conserva su cantidad y su precio al
momento de la compra.

## Endpoints

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/api/v1/auth/register` | público |
| `POST` | `/api/v1/auth/authenticate` | público |
| `GET` | `/api/v1/auth/me` | autenticado |
| `GET` | `/api/products` | público |
| `GET` | `/api/products/{id}` | público |
| `POST` | `/api/products` | `ADMIN` |
| `PATCH` | `/api/products/{id}` | `ADMIN` |
| `PATCH` | `/api/products/{id}/stock` | `ADMIN` |
| `PATCH` | `/api/products/{id}/activar` | `ADMIN` |
| `DELETE` | `/api/products/{id}` | `ADMIN` |
| `POST` | `/api/products/{productId}/images` | `ADMIN` |
| `GET` | `/api/categories` | público |
| `POST` | `/api/categories` | `ADMIN` |
| `DELETE` | `/api/categories/{id}` | `ADMIN` |
| `POST` | `/api/compras` | `USER` · `ADMIN` |
| `GET` | `/api/compras/mias` | autenticado |
| `GET` | `/api/orders` | `ADMIN` |
| `GET` | `/api/orders/by-user/{userId}` | `ADMIN` |
| `GET` | `/api/users` | `ADMIN` |
| `PATCH` | `/api/users/{id}/role` | `ADMIN` |

## Arquitectura

Capas convencionales de Spring, con DTOs dedicados en los bordes para no exponer las
entidades JPA directamente:

```
controllers/   → endpoints REST, validación de entrada
service/       → reglas de negocio (stock, totales, descuentos)
repository/    → Spring Data JPA
entity/        → modelo de dominio
entity/dtos/   → contratos de request y response
Security/      → configuración de Spring Security, JwtService, filtro JWT
```

## Cómo correrlo

Requiere Java 17+, Maven y una instancia de MySQL.

```bash
# 1. crear la base
mysql -u root -p -e "CREATE DATABASE marketplace;"

# 2. configurar credenciales y secreto JWT
#    editar src/main/resources/application.properties:
#      spring.datasource.username / spring.datasource.password
#      application.security.jwt.secretKey  (mínimo 256 bits)

# 3. levantar
./mvnw spring-boot:run
```

La API queda en `http://localhost:4002`. Hibernate crea el esquema al arrancar
(`ddl-auto=update`).

## Notas

- `application.properties` está versionado con valores de ejemplo, no con credenciales
  reales. Reemplazalos antes de correr el proyecto.
- `ddl-auto=update` es cómodo para desarrollo; para producción correspondería migrar el
  esquema con una herramienta como Flyway o Liquibase.
