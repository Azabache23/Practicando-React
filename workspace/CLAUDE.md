# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Resumen de proyecto

Java REST API client para gestionar clientes (customers) vía CRUD a un backend en `http://localhost:8080/api/clientes`. Usa `java.net.HttpURLConnection` para HTTP y `org.json` para manejar JSON.

## Compilación y Ejecución

No existe herramienta de construcción configurada (Maven/Gradle). Compilar y ejecutar manualmente:

```bash
javac -cp "lib/json-20251224.jar" *.java
```

Ejecutar primero el servidor y después el cliente:

```bash                                                                  
java -cp ".;lib/json-20251224.jar" Servidor
java -cp ".;lib/json-20251224.jar" Main
```

## Arquitectura

- **Cliente.java** — Modelo de datos con los campos: `id`, `nombre`, `ubicacion`, `nif`. Incluye un método `toJson()` para trnasformar el objeto en: `JSONObject`.
- **GestorCliente.java** — Capa de servicio que recoge las llamadas HTTP (POST/GET/PUT/DELETE) a la REST API. Todos los métodos usan `HttpURLConnection` con UTF-8 JSON payloads.
- **Servidor.java** — Servidor HTTP embebido que usa `com.sun.net.httpserver.HttpServer` en el puerto 8080. Implementa la  API REST (`/api/clientes`) con almacenamiento en memoria usando un HashMap (`LinkedHashMap`). Maneja GET (all/by ID), POST, PUT, and DELETE.
- **Main.java** — Punto de entrada que muestra el funcionamiento del CRUD. Necesita que el `Servidor` esté ejecutado.

## Dependencias

- **json-20251224.jar** — Librería para manejar objetos y listas de tipo JSON; se encuentra en la carpeta lib.