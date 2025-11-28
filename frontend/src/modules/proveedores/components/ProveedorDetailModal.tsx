import { useQuery } from "@tanstack/react-query";

import { ProveedoresRepository } from "../services/ProveedoresRepository";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";

interface ProveedorDetailModalProps {
  readonly isOpen: boolean;
  readonly proveedorId: number;
  readonly onClose: () => void;
}

export const ProveedorDetailModal = ({ isOpen, proveedorId, onClose }: ProveedorDetailModalProps) => {
  const { data: proveedor, isLoading } = useQuery({
    queryKey: ["proveedor", proveedorId],
    queryFn: () => ProveedoresRepository.getById(proveedorId),
    enabled: isOpen && proveedorId !== null,
  });

  if (!isOpen || !proveedorId) return null;

  if (isLoading) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
        <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl p-6">
          <FullscreenLoader />
        </div>
      </div>
    );
  }

  if (!proveedor) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
        <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl p-6">
          <p className="text-center text-gray-500">Proveedor no encontrado</p>
          <button
            onClick={onClose}
            className="mt-4 w-full rounded-xl bg-primary px-4 py-2 text-sm font-semibold text-white"
          >
            Cerrar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">Detalle del Proveedor</h2>
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          <div className="space-y-4">
            <div>
              <p className="text-xs font-medium text-gray-500">Nombre</p>
              <p className="mt-1 text-sm font-semibold text-gray-900">{proveedor.nombre}</p>
            </div>

            {proveedor.contacto && (
              <div>
                <p className="text-xs font-medium text-gray-500">Persona de Contacto</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{proveedor.contacto}</p>
              </div>
            )}

            {proveedor.telefono && (
              <div>
                <p className="text-xs font-medium text-gray-500">Teléfono</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{proveedor.telefono}</p>
              </div>
            )}

            {proveedor.correo && (
              <div>
                <p className="text-xs font-medium text-gray-500">Correo Electrónico</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{proveedor.correo}</p>
              </div>
            )}

            {proveedor.direccion && (
              <div>
                <p className="text-xs font-medium text-gray-500">Dirección</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{proveedor.direccion}</p>
              </div>
            )}
          </div>

          <div className="mt-6">
            <button
              onClick={onClose}
              className="w-full rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

