# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Resumen del proyecto

Gestor de clientes (CRUD) con autenticación JWT: aplicación de página única creada con React 19, Vite 7 y Tailwind CSS 4. Todo el código está en TypeScript/TSX. El backend Java se encuentra dentro de `server/`.

## Comandos

- `npm run dev` — Start dev server (Vite)
- `npm run build` — Production build (outputs to `dist/`)
- `npm run preview` — Preview production build
- `npx tsc --noEmit` — Type-check without emitting files

No linter or test runner is configured.

## Convenciones de código

- **Lenguaje:** TypeScript estricto (`strict: true` en `tsconfig.json`). No se usa JavaScript plano.
- **Funciones:** Se usan funciones flecha (`const fn = () => {}`) como estándar en todo el proyecto. No se usan declaraciones `function`.
- **Tipos:** Las interfaces y types del modelo de datos se definen en `src/types/`. Los componentes usan interfaces `Props` locales.

## Arquitectura

### Jerarquía de componentes:** `App` → `LoginForm` | (`ClienteForm` + `ClienteTable`)

- **App.tsx** — Contiene los estados de autenticación (`autenticado`, `loginError`, `loginCargando`) y los estados CRUD (`clientes`, `clienteEditar`, `error`) vía `useState`/`useEffect`. Muestra `LoginForm` si no hay sesión, o el gestor de clientes si está autenticado. Incluye botón de cerrar sesión con nombre de usuario.
- **LoginForm.tsx** — Formulario de inicio de sesión con campos usuario/contraseña. Props: `onLogin`, `error`, `cargando`.
- **ClienteForm.tsx** — Controlado por crear/editar clientes. Se sincroniza con el prop `clienteEditar` para cambiar entre crear y editar clientes.
- **ClienteTable.tsx** — Tabla presentación sin estado. Recibe datos y llamadas de acción mediante props.
- **services/authService.ts** — Funciones de autenticación: `login()`, `logout()`, `getToken()`, `getUsername()`, `isAuthenticated()`. Guarda el token JWT en localStorage.
- **services/clienteService.ts** — Capa API que usa la Fetch API nativa del navegador `fetch` (async/await). Todas las llamadas HTTP `/api/clientes` pasan por aquí. Incluye `getAuthHeaders()` que añade el header `Authorization: Bearer <token>` y `handleUnauthorized()` que limpia la sesión si recibe 401.
- **types/Cliente.ts** — Interface `Cliente` y type `ClienteForm` (modelo de datos).
- **types/Auth.ts** — Interfaces `LoginCredentials` y `AuthResponse`.

### Modelo de datos:** `{ id: string, nombre: string, ubicacion: string, nif: string }`

### Configuración

- **HTTP Client:** Se usa la Fetch API nativa del navegador (sin librerías externas como Axios). Las llamadas se encuentran en `services/clienteService.ts` y `services/authService.ts`.


## Servidor Java

El backend se encuentra en `server/` dentro de este proyecto. Es una aplicación Java pura (sin Spring Boot, sin Maven/Gradle) que usa el servidor HTTP integrado del JDK (`com.sun.net.httpserver.HttpServer`). Dependencias externas: `org.json` (`lib/json-20251224.jar`) y PostgreSQL JDBC (`lib/postgresql-42.7.5.jar`).

### Compilación y ejecución

```bash
# Compilar (desde server/)
javac -cp "lib/json-20251224.jar;lib/postgresql-42.7.5.jar" *.java

# Iniciar servidor (puerto 8080)
java -cp ".;lib/json-20251224.jar;lib/postgresql-42.7.5.jar" Servidor

# Ejecutar cliente demo (opcional)
java -cp ".;lib/json-20251224.jar;lib/postgresql-42.7.5.jar" Main
```

### Bases de datos (PostgreSQL)

- **PostgreSQL** en localhost:5432, usuario `admin`, contraseña `postgres`
- Dos bases de datos separadas (se crean automáticamente al iniciar el servidor):
  - **`clientes_app`** → tabla `clientes` (persistencia CRUD)
  - **`usuarios_API`** → tabla `usuarios` (autenticación)

#### Base de datos `clientes_app`

Tabla **`clientes`** — almacena los datos del CRUD de clientes.

| Columna    | Tipo         | Restricciones       |
|------------|--------------|---------------------|
| id         | VARCHAR(255) | PRIMARY KEY         |
| nombre     | VARCHAR(255) | NOT NULL            |
| ubicacion  | VARCHAR(255) | NOT NULL            |
| nif        | VARCHAR(255) | NOT NULL            |

#### Base de datos `usuarios_API`

Tabla **`usuarios`** — almacena las credenciales de autenticación.

| Columna    | Tipo         | Restricciones              |
|------------|--------------|----------------------------|
| id         | SERIAL       | PRIMARY KEY                |
| username   | VARCHAR(100) | UNIQUE NOT NULL            |
| password   | VARCHAR(255) | NOT NULL                   |
| created_at | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  |

> **Nota:** Las contraseñas se almacenan hasheadas con PBKDF2-HMAC-SHA256 (65536 iteraciones, salt de 16 bytes). El formato almacenado es `salt_base64:hash_base64`. La clase `PasswordUtil` gestiona el hashing y la verificación. Al iniciar el servidor, `DatabaseManager.migratePasswords()` detecta y migra automáticamente cualquier contraseña en texto plano al formato hasheado.

### Clases

- **Cliente.java** — POJO del modelo de datos con campos `id`, `nombre`, `ubicacion` y `nif`. Incluye getters/setters y un método `toJson()` que convierte el objeto a `JSONObject`.

- **Servidor.java** — Punto de entrada del backend. Inicializa las bases de datos con `DatabaseManager.initialize()`, crea un `HttpServer` en el puerto 8080 y registra los handlers `/api/clientes` y `/api/auth/login`. El `ClientesHandler` valida el token JWT en todas las peticiones (excepto OPTIONS) y delega las operaciones CRUD a `DatabaseManager` (persistencia en PostgreSQL).

- **AuthHandler.java** — Handler para `POST /api/auth/login`. Valida credenciales contra la base de datos y devuelve un token JWT. Errores: 400 (campos vacíos), 401 (credenciales inválidas), 405 (método no permitido).

- **JwtUtil.java** — Implementación manual de JWT con HMAC-SHA256. `generateToken(username)` crea tokens con expiración de 1 hora. `validateToken(token)` verifica firma y expiración.

- **DatabaseManager.java** — Gestión de conexiones PostgreSQL a dos bases de datos: `getClientesConnection()` (clientes_app) y `getAuthConnection()` (usuarios_API). `initialize()` crea ambas BD y tablas automáticamente. Incluye métodos CRUD (`getAllClientes`, `getClienteById`, `createCliente`, `updateCliente`, `deleteCliente`) y `authenticateUser()` para autenticación.

- **PasswordUtil.java** — Utilidad de hashing de contraseñas con PBKDF2-HMAC-SHA256. Métodos: `hash(password)` genera salt+hash, `verify(password, stored)` verifica con comparación en tiempo constante, `isHashed(password)` detecta si ya está hasheada.

- **GestorCliente.java** — Cliente HTTP Java que consume la API REST usando `HttpURLConnection`. Es el equivalente Java de `services/clienteService.ts` del frontend React.

- **Main.java** — Programa demo que ejecuta un ciclo CRUD completo usando `GestorCliente`.

### Flujo de autenticación

1. El usuario envía `POST /api/auth/login` con username y password
2. El servidor verifica las credenciales contra la tabla `usuarios` en PostgreSQL
3. Si son válidas, genera un token JWT (HMAC-SHA256, expiración 1 hora) y lo devuelve
4. El frontend guarda el token en localStorage y lo envía en el header `Authorization: Bearer <token>` en cada petición
5. Si el token expira o es inválido, el servidor responde 401 y el frontend redirige al login

### Endpoints REST

| Endpoint | Método | Body | Auth | Código éxito | Respuesta |
|---|---|---|---|---|---|
| `/api/auth/login` | POST | `{ username, password }` | No | 200 | `{ token, username }` |
| `/api/clientes` | GET | — | JWT | 200 | JSON array de clientes |
| `/api/clientes/{id}` | GET | — | JWT | 200 | JSON objeto cliente |
| `/api/clientes` | POST | `{ id?, nombre, ubicacion, nif }` | JWT | 201 | Cliente creado |
| `/api/clientes/{id}` | PUT | `{ nombre, ubicacion, nif }` | JWT | 200 | Cliente actualizado |
| `/api/clientes/{id}` | DELETE | — | JWT | 204 | Sin cuerpo |

**Códigos de error:** 400 (campos faltantes), 401 (no autenticado / token inválido), 404 (id no encontrado), 405 (método no soportado), 409 (id duplicado), 500 (error interno).

## Backend / API Proxy
Vite proxies `/api/*` to `http://localhost:8080` (configured in `vite.config.ts`). The backend must be running separately for the app to function. REST endpoints follow standard CRUD at `/api/clientes` and authentication at `/api/auth/login`.