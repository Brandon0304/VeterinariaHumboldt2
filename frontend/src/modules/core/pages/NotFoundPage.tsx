// Página de error 404 genérica.
// Mantiene coherencia visual con el resto de la interfaz.
import { Link } from "react-router-dom";

export const NotFoundPage = () => {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center gap-6 text-center">
      <div className="space-y-2">
        <p className="text-sm font-semibold text-primary">404</p>
        <h1 className="text-3xl font-semibold text-secondary">Página no encontrada</h1>
        <p className="mx-auto max-w-lg text-sm text-gray-500">
          La ruta solicitada no existe o fue movida. Utiliza la navegación para regresar al panel principal.
        </p>
      </div>
      <Link
        to="/veterinario/inicio"
        className="rounded-2xl bg-primary px-6 py-3 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
      >
        Volver al inicio
      </Link>
    </div>
  );
};

export default NotFoundPage;


