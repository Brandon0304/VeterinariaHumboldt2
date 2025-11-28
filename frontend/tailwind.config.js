// Configuración principal de Tailwind CSS para el proyecto frontend.
// Aquí se declaran los paths que Tailwind escaneará para generar las clases utilitarias.
export default {
  darkMode: "media",
  // `content` indica los archivos donde se buscarán clases de Tailwind.
  content: [
    "./index.html",
    "./src/**/*.{ts,tsx}",
  ],
  // `theme` permite extender o sobrescribir el diseño por defecto.
  theme: {
    extend: {
      // Ejemplo de paleta base alineada con el prototipo de Figma.
      colors: {
        primary: {
          DEFAULT: "#1ABCBC",
          dark: "#0F6A7B",
          light: "#55E0D5",
        },
        secondary: {
          DEFAULT: "#114264",
          light: "#1F5C82",
        },
        gray: {
          50: "#F8FAFC",
          100: "#F1F5F9",
          200: "#E2E8F0",
          300: "#CBD5F5",
          400: "#94A3B8",
          500: "#64748B",
          600: "#475569",
          700: "#334155",
          800: "#1E293B",
          900: "#0F172A",
        },
        success: "#4ADE80",
        warning: "#FACC15",
        danger: "#F87171",
        info: "#60A5FA",
        background: "#F8FAFC",
        muted: {
          foreground: "#64748B",
        },
      },
      // Se define la familia tipográfica base según el mockup.
      fontFamily: {
        sans: ["'Poppins'", "ui-sans-serif", "system-ui"],
      },
      // Sombra suave para tarjetas y paneles.
      boxShadow: {
        soft: "0 4px 24px rgba(16, 24, 40, 0.08)",
        sm: "0 1px 2px rgba(16, 24, 40, 0.06)",
        md: "0 6px 20px rgba(16, 24, 40, 0.10)",
      },
      // Radio de borde acorde al diseño.
      borderRadius: {
        xl: "20px",
        "2xl": "28px",
        "3xl": "32px",
      },
      transitionTimingFunction: {
        "ease-out-curve": "cubic-bezier(0.22,1,0.36,1)",
      },
      transitionDuration: {
        200: "200ms",
        300: "300ms",
      },
    },
  },
  // `plugins` puede incluir extensiones futuras (por ahora se deja vacío).
  plugins: [],
};

