# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Resumen del proyecto

Gestor de clientes (CRUD): aplicación de página única creada con React 19, Vite 7 y Tailwind CSS 4. Todo el código está en JavaScript/JSX (no TypeScript).

## Comandos

- `npm run dev` — Start dev server (Vite)
- `npm run build` — Production build (outputs to `dist/`)
- `npm run preview` — Preview production build

No linter or test runner is configured.

## Arquitectura

**Jerarquía de componentes:** `App` → `ClienteForm` + `ClienteTable`

- **App.jsx** — Contiene los estados (`clientes`, `clienteEditar`, `error`) vía `useState`/`useEffect`. Orquesta las operaciones CRUD llamando a la capa servidor y pasando las llamadas como props.
- **ClienteForm.jsx** — Controlado por crear/editar clientes. Se sincroniza con el prop `clienteEditar` para cambiar entre crear y editar clientes.
- **ClienteTable.jsx** — Tabla presentación sin estado. Recibe datos y llamadas de acción mediante props.
- **services/clienteService.js** — Capa API que usa la Fetch API nativa del navegador `fetch` (async/await). Todas las llamadas HTTP `/api/clientes` pasan por aquí.

**Modelo de datos:** `{ id, nombre, ubicacion, nif }`

## Configuración

- **HTTP Client:** Se usa la Fetch API nativa del navegador (sin librerías externas como Axios). Las llamadas se encuentran en `services/clienteService.js`.

## Backend / API Proxy
Vite proxies `/api/*` to `http://localhost:8080` (configured in `vite.config.js`). The backend must be running separately for the app to function. REST endpoints follow standard CRUD at `/api/clientes`.
