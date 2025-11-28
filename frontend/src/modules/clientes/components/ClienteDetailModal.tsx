import { useQuery } from "@tanstack/react-query";

import { ClientesRepository } from "../services/ClientesRepository";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";

interface ClienteDetailModalProps {
  readonly isOpen: boolean;
  readonly clienteId: number;
  readonly onClose: () => void;
}

export const ClienteDetailModal = ({ isOpen, clienteId, onClose }: ClienteDetailModalProps) => {
  const { data: cliente, isLoading } = useQuery({
    queryKey: ["cliente", clienteId],
    queryFn: () => ClientesRepository.getById(clienteId),
    enabled: isOpen && clienteId !== null,
  });

  if (!isOpen || !clienteId) return null;

  if (isLoading) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
        <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl p-6">
          <FullscreenLoader />
        </div>
      </div>
    );
  }

  if (!cliente) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
        <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl p-6">
          <p className="text-center text-gray-500">Cliente no encontrado</p>
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
            <h2 className="text-xl font-semibold text-gray-900">Detalle del Cliente</h2>
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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-xs font-medium text-gray-500">Nombre</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{cliente.nombre}</p>
              </div>
              <div>
                <p className="text-xs font-medium text-gray-500">Apellido</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{cliente.apellido}</p>
              </div>
            </div>

            <div>
              <p className="text-xs font-medium text-gray-500">Correo Electrónico</p>
              <p className="mt-1 text-sm font-semibold text-gray-900">{cliente.correo}</p>
            </div>

            {cliente.telefono && (
              <div>
                <p className="text-xs font-medium text-gray-500">Teléfono</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{cliente.telefono}</p>
              </div>
            )}

            {cliente.direccion && (
              <div>
                <p className="text-xs font-medium text-gray-500">Dirección</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{cliente.direccion}</p>
              </div>
            )}

            {cliente.identificacion && (
              <div>
                <p className="text-xs font-medium text-gray-500">Identificación</p>
                <p className="mt-1 text-sm font-semibold text-gray-900">{cliente.identificacion}</p>
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

