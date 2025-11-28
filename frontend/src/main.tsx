// Punto de entrada principal de la aplicación React.
// Se encarga de montar el árbol de componentes sobre el elemento raíz del DOM.
import React from "react";
import ReactDOM from "react-dom/client";

// Importación de la hoja de estilos global que incluye las directivas de Tailwind.
import "./styles/index.css";

// Importación del componente principal de la aplicación.
import { App } from "./app/App";

// Importación del contenedor de providers comunes (React Query, Zustand, etc.).
import { AppProviders } from "./app/providers/AppProviders";

// Obtiene el elemento raíz definido en `index.html`.
const rootElement = document.getElementById("root");

// Validación defensiva para asegurar que el elemento exista.
if (!rootElement) {
  throw new Error("No se encontró el elemento raíz para montar la aplicación.");
}

// Crea el root de React y monta la aplicación dentro de React.StrictMode.
ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    {/* AppProviders inyecta los contextos globales requeridos por toda la app */}
    <AppProviders>
      {/* El componente App contiene la configuración de rutas y layout base */}
      <App />
    </AppProviders>
  </React.StrictMode>,
);

