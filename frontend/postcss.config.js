// Archivo de configuración de PostCSS requerido por Tailwind CSS
// Se definen los plugins utilizados durante el procesamiento de estilos.
export default {
  // Lista de plugins ejecutados por PostCSS.
  plugins: {
    // Importa las directivas de Tailwind (`@tailwind`) y genera las utilidades.
    tailwindcss: {},
    // Agrega compatibilidad automática de prefijos CSS para distintos navegadores.
    autoprefixer: {},
  },
};

