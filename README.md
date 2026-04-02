# E-Commerce API REST

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=flat-square&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![JWT](https://img.shields.io/badge/Security-JWT-black?style=flat-square&logo=jsonwebtokens)

Una API RESTful robusta para la gestion de una tienda online. Construida con **Java y Spring Boot**, implementa seguridad con JWT, persistencia con MySQL y cache con Redis para optimizar el rendimiento.

---

## Tecnologias

| Capa            | Tecnologia |
|-----------------|-----------|
| Backend         | Java, Spring Boot (Web, Data JPA, Security) |
| Base de Datos   | MySQL 8 |
| Cache           | Redis |
| Seguridad       | Spring Security + JWT |
| Infraestructura | Docker & Docker Compose |
| Documentacion   | Swagger / OpenAPI |
| Testing         | JUnit 5, Mockito, Testcontainers |

---

## Features implementadas

- [x] Registro e inicio de sesion con JWT
- [x] Gestion de productos (CRUD completo)
- [x] Carrito de compras y ordenes
- [x] Gestion de perfil de usuario
- [x] Cache con Redis para endpoints de alta demanda
- [x] Contenedores Docker para app, base de datos y cache
- [x] Documentacion interactiva con Swagger UI
- [x] Control de concurrencia con bloqueo pesimista (`PESSIMISTIC_WRITE`) para evitar condiciones de carrera al comprar stock limitado
- [x] Paginacion en listados de productos, ordenes y usuarios
- [x] Roles diferenciados: `ADMIN` y `CUSTOMER`
- [x] Manejo global de errores con `@RestControllerAdvice` — respuestas estructuradas para errores de negocio, validacion y excepciones inesperadas
- [x] Validaciones de entrada con `@Valid` y anotaciones en DTOs
- [x] Tests de integracion con Testcontainers

---

## Endpoints principales

> La documentacion completa e interactiva esta disponible en Swagger una vez levantado el proyecto.

### Auth — `/api/auth`
| Metodo | Ruta | Descripcion | Auth requerida |
|--------|------|-------------|----------------|
| `POST` | `/api/auth/register` | Registro de nuevo usuario | No |
| `POST` | `/api/auth/login` | Login, retorna JWT | No |

### Productos — `/api/products`
| Metodo | Ruta | Descripcion | Rol requerido |
|--------|------|-------------|---------------|
| `GET` | `/api/products` | Listar productos (paginado) | ADMIN, CUSTOMER |
| `GET` | `/api/products/{id}` | Obtener producto por ID | ADMIN, CUSTOMER |
| `GET` | `/api/products/{id}/stock` | Ver stock de un producto | Publico |
| `GET` | `/api/products/{id}/check-stock` | Verificar disponibilidad (`?quantity=N`) | Publico |
| `POST` | `/api/products` | Crear producto | ADMIN |
| `PUT` | `/api/products/{id}` | Actualizar producto | ADMIN |
| `DELETE` | `/api/products/{id}` | Eliminar producto | ADMIN |

### Carrito — `/api/carts`
| Metodo | Ruta | Descripcion | Auth requerida |
|--------|------|-------------|----------------|
| `GET` | `/api/carts` | Ver carrito activo con subtotal | Si |
| `POST` | `/api/carts/items` | Agregar producto al carrito | Si |
| `PUT` | `/api/carts/items` | Actualizar cantidad de un item | Si |
| `DELETE` | `/api/carts/items/{productId}` | Eliminar producto del carrito | Si |

### Ordenes — `/api/orders`
| Metodo | Ruta | Descripcion | Rol requerido |
|--------|------|-------------|---------------|
| `GET` | `/api/orders` | Listar todas las ordenes (paginado) | ADMIN |
| `GET` | `/api/orders/{id}` | Ver orden por ID | ADMIN / dueno |
| `POST` | `/api/orders` | Crear orden | ADMIN, CUSTOMER |
| `POST` | `/api/orders/checkout` | Confirmar carrito como orden | CUSTOMER |
| `GET` | `/api/orders/my-orders` | Ver mis ordenes (paginado) | CUSTOMER |
| `GET` | `/api/orders/my-purchases` | Ver mis compras completadas | CUSTOMER |
| `PUT` | `/api/orders/{id}/cancel` | Cancelar una orden propia | CUSTOMER |
| `PUT` | `/api/orders/{id}/status` | Actualizar estado de orden | ADMIN |
| `DELETE` | `/api/orders/{id}` | Eliminar orden | ADMIN |

### Usuarios — `/api/users`
| Metodo | Ruta | Descripcion | Rol requerido |
|--------|------|-------------|---------------|
| `GET` | `/api/users/me` | Ver perfil propio | Autenticado |
| `GET` | `/api/users` | Listar usuarios (paginado) | ADMIN |
| `GET` | `/api/users/{id}` | Ver usuario por ID | ADMIN / propio |
| `POST` | `/api/users` | Crear usuario como admin | ADMIN |
| `PUT` | `/api/users/{id}` | Actualizar usuario | ADMIN / propio |
| `DELETE` | `/api/users/{id}` | Eliminar usuario | ADMIN |

---

## Arquitectura

El proyecto utiliza **Docker Compose** para orquestar tres servicios aislados que se comunican en red interna:

```
+---------------------------------------------+
|              Docker Compose                  |
|                                             |
|  +--------------+     +------------------+  |
|  |  Spring Boot |---->|    MySQL 8       |  |
|  |  :8080       |     |    :3306         |  |
|  |              |     +------------------+  |
|  |              |     +------------------+  |
|  |              |---->|    Redis         |  |
|  +--------------+     |    :6379         |  |
|                       +------------------+  |
+---------------------------------------------+
```

---

## Manejo de errores

La API devuelve respuestas estructuradas en todos los casos mediante `@RestControllerAdvice`:

| Situacion | HTTP | Descripcion |
|-----------|------|-------------|
| Error de negocio (stock insuficiente, recurso no encontrado, etc.) | Variable `4xx` | Mensaje + codigo de error personalizado |
| Validacion fallida (`@Valid`) | `400 Bad Request` | Mapa de campos con sus mensajes |
| Error inesperado | `500 Internal Server Error` | Mensaje generico seguro |

Ejemplo de respuesta ante validacion fallida:
```json
{
  "name": "must not be blank",
  "price": "must be greater than 0"
}
```

---

## Como ejecutar el proyecto localmente

### Prerrequisitos
- [Docker](https://www.docker.com/) y Docker Compose instalados
- Git instalado

### Instalacion paso a paso

**1. Clonar el repositorio:**
```bash
git clone https://github.com/tu-usuario/tu-repo.git
cd tu-repo
```

**2. Configurar variables de entorno:**

El proyecto usa variables de entorno para proteger credenciales sensibles.

```bash
cp .env.example .env
```

Abri el archivo `.env` y completa los valores:

```env
# Base de datos
DB_NAME=miproyecto_db
DB_USER=app_user
DB_PASSWORD=tu_password_local
DB_ROOT_PASSWORD=tu_password_root_local

# JWT
JWT_SECRET=escribe_aqui_una_clave_secreta_muy_larga_y_segura

# Redis (opcional si usas el puerto por defecto)
REDIS_PORT=6379
```

**3. Levantar los contenedores:**
```bash
docker-compose up --build -d
```

La API estara corriendo en `http://localhost:8080`

**4. Verificar que los servicios esten activos:**
```bash
docker-compose ps
```

---

## Documentacion interactiva (Swagger)

Una vez que los contenedores esten corriendo, accede a la documentacion completa en:

**http://localhost:8080/swagger-ui/index.html**

Desde Swagger podes ver todos los endpoints, los esquemas de datos y realizar pruebas en tiempo real. Para probar endpoints protegidos, primero hace login y pega el JWT en el boton **Authorize**.

---

## Estructura del proyecto

```
src/
+-- main/
|   +-- java/com/mateo/springboot/tienda/
|   |   +-- auth/           # AuthController y logica de autenticacion
|   |   +-- config/         # Configuracion general (Security, Redis, etc.)
|   |   +-- controller/     # Controllers REST (Product, Order, Cart, User)
|   |   +-- dto/            # DTOs de entrada y salida por modulo
|   |   |   +-- auth/
|   |   |   +-- cart/
|   |   |   +-- order/
|   |   |   +-- product/
|   |   |   +-- user/
|   |   +-- exceptions/     # GlobalExceptionHandler y excepciones custom
|   |   +-- mapper/         # Mappers (entidad <-> DTO)
|   |   +-- models/         # Entidades JPA (User, Product, Order, Cart)
|   |   +-- repository/     # Repositorios Spring Data JPA
|   |   +-- security/       # JWT, CustomUserDetails, filtros
|   +-- resources/
|       +-- application.properties
+-- test/                   # Tests de integracion con Testcontainers
docker-compose.yml
.env.example
```

---

## Licencia

Este proyecto esta bajo la licencia MIT.