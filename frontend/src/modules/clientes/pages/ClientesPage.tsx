import { useMemo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { PhoneIcon, LocationIcon, DeleteIcon } from "../../../shared/components/icons/Icons";
import { ClientesRepository, type ClienteRequest, type ClienteUpdateRequest } from "../services/ClientesRepository";
import type { ApiClienteResponse } from "../../shared/types/backend";
import { CreateClienteModal } from "../components/CreateClienteModal";
import { EditClienteModal } from "../components/EditClienteModal";
import { ClienteDetailModal } from "../components/ClienteDetailModal";

export const ClientesPage = () => {
  const queryClient = useQueryClient();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedClienteId, setSelectedClienteId] = useState<number | null>(null);
  const [search, setSearch] = useState("");

  const { data: clientes, isLoading } = useQuery({
    queryKey: ["clientes"],
    queryFn: ClientesRepository.getAll,
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      // Nota: El backend no tiene endpoint DELETE, así que desactivamos el cliente
      toast.error("La funcionalidad de eliminar clientes no está disponible en el backend");
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["clientes"] });
    },
  });

  const filteredClientes = useMemo(() => {
    if (!clientes) return [];

    const term = search.trim().toLowerCase();
    if (term.length === 0) return clientes;

    return clientes.filter(
      (cliente) =>
        cliente.nombre.toLowerCase().includes(term) ||
        cliente.apellido.toLowerCase().includes(term) ||
        cliente.correo.toLowerCase().includes(term) ||
        (cliente.telefono && cliente.telefono.includes(term)) ||
        (cliente.identificacion && cliente.identificacion.includes(term))
    );
  }, [clientes, search]);

  const stats = useMemo(() => {
    if (!clientes) return { total: 0, conTelefono: 0, conDireccion: 0 };
    return {
      total: clientes.length,
      conTelefono: clientes.filter((c) => c.telefono).length,
      conDireccion: clientes.filter((c) => c.direccion).length,
    };
  }, [clientes]);

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="w-full space-y-4 sm:space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:flex-wrap sm:items-center sm:justify-between sm:gap-4">
        <div className="min-w-0 flex-1">
          <h2 className="text-xl font-semibold text-secondary sm:text-2xl">Gestión de Propietarios</h2>
          <p className="mt-1 text-xs text-gray-500 sm:text-sm">Administra la información de los propietarios de mascotas</p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="w-full rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark sm:w-auto sm:rounded-2xl"
        >
          ➕ Nuevo Propietario
        </button>
      </header>

      <section className="grid gap-3 sm:gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard title="Total Propietarios" value={stats.total.toString()} description="Registrados" tone="primary" />
        <StatCard
          title="Con Teléfono"
          value={stats.conTelefono.toString()}
          description="Contacto disponible"
          tone="info"
        />
        <StatCard
          title="Con Dirección"
          value={stats.conDireccion.toString()}
          description="Dirección registrada"
          tone="success"
        />
      </section>

      <section className="rounded-2xl bg-white p-4 shadow-soft sm:rounded-3xl sm:p-6">
        <div className="mb-4 flex flex-col gap-3 sm:mb-6 sm:flex-row sm:flex-wrap sm:items-center sm:gap-4">
          <h3 className="text-base font-semibold text-secondary sm:text-lg">Listado de Propietarios</h3>
          <input
            type="text"
            placeholder="Buscar cliente..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 min-w-0 rounded-xl border border-gray-200 bg-gray-50 px-3 py-2 text-xs focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 sm:min-w-[200px] sm:rounded-2xl sm:px-4 sm:text-sm"
          />
        </div>

        {filteredClientes.length === 0 ? (
          <div className="rounded-xl border border-dashed border-gray-200 bg-gray-50 p-8 text-center text-xs text-gray-500 sm:rounded-2xl sm:p-10 sm:text-sm">
            {search.length > 0
              ? "No se encontraron clientes con los criterios de búsqueda."
              : "No hay clientes registrados. Crea el primero haciendo clic en 'Nuevo Cliente'."}
          </div>
        ) : (
          <div className="grid gap-3 sm:gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {filteredClientes.map((cliente) => (
              <ClienteCard
                key={cliente.id}
                cliente={cliente}
                onView={() => {
                  setSelectedClienteId(cliente.id);
                  setIsDetailModalOpen(true);
                }}
                onEdit={() => {
                  setSelectedClienteId(cliente.id);
                  setIsEditModalOpen(true);
                }}
                onDelete={() => {
                  if (confirm(`¿Está seguro de que desea eliminar al cliente ${cliente.nombre} ${cliente.apellido}?`)) {
                    deleteMutation.mutate(cliente.id);
                  }
                }}
              />
            ))}
          </div>
        )}
      </section>

      <CreateClienteModal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} />
      {selectedClienteId && (
        <>
          <EditClienteModal
            isOpen={isEditModalOpen}
            clienteId={selectedClienteId}
            onClose={() => {
              setIsEditModalOpen(false);
              setSelectedClienteId(null);
            }}
          />
          <ClienteDetailModal
            isOpen={isDetailModalOpen}
            clienteId={selectedClienteId}
            onClose={() => {
              setIsDetailModalOpen(false);
              setSelectedClienteId(null);
            }}
          />
        </>
      )}
    </div>
  );
};

interface StatCardProps {
  readonly title: string;
  readonly value: string;
  readonly description: string;
  readonly tone?: "primary" | "success" | "warning" | "danger" | "info";
}

const StatCard = ({ title, value, description, tone = "primary" }: StatCardProps) => {
  const toneClasses = {
    primary: "bg-primary/10 text-primary border-primary/20",
    success: "bg-success/10 text-success border-success/20",
    warning: "bg-warning/10 text-warning border-warning/20",
    danger: "bg-danger/10 text-danger border-danger/20",
    info: "bg-blue-100 text-blue-600 border-blue-200",
  };

  return (
    <article className="rounded-2xl border bg-white p-6 shadow-soft">
      <p className="text-sm font-medium text-gray-500">{title}</p>
      <p className="mt-2 text-2xl font-semibold text-secondary">{value}</p>
      <span className={`mt-3 inline-flex rounded-full border px-3 py-1 text-xs font-semibold ${toneClasses[tone]}`}>
        {description}
      </span>
    </article>
  );
};

interface ClienteCardProps {
  readonly cliente: ApiClienteResponse;
  readonly onView: () => void;
  readonly onEdit: () => void;
  readonly onDelete: () => void;
}

const ClienteCard = ({ cliente, onView, onEdit, onDelete }: ClienteCardProps) => {
  return (
    <article className="group flex flex-col justify-between rounded-2xl border border-gray-200 bg-white p-5 shadow-sm transition-all hover:border-primary/40 hover:shadow-lg">
      <div>
        <div className="mb-3 flex items-start justify-between">
          <div>
            <h4 className="text-lg font-bold text-secondary">
              {cliente.nombre} {cliente.apellido}
            </h4>
            <p className="mt-1 text-sm text-gray-600">{cliente.correo}</p>
          </div>
        </div>
        <div className="space-y-1.5 text-xs text-gray-500">
          {cliente.telefono && (
            <p className="flex items-center gap-1.5">
              <PhoneIcon size={14} className="text-primary" /> {cliente.telefono}
            </p>
          )}
          {cliente.direccion && (
            <p className="flex items-center gap-1.5">
              <LocationIcon size={14} className="text-primary" /> {cliente.direccion}
            </p>
          )}
          {cliente.identificacion && (
            <p className="flex items-center gap-1.5">
              <span>🆔</span> {cliente.identificacion}
            </p>
          )}
        </div>
      </div>
      <div className="mt-4 flex gap-2">
        <button
          onClick={onView}
          className="flex-1 rounded-xl border border-primary bg-white px-3 py-2 text-xs font-semibold text-primary transition-all hover:bg-primary hover:text-white"
        >
          Ver
        </button>
        <button
          onClick={onEdit}
          className="flex-1 rounded-xl border border-gray-300 bg-white px-3 py-2 text-xs font-semibold text-gray-700 transition-all hover:bg-gray-50"
        >
          Editar
        </button>
        <button
          onClick={onDelete}
          title="Eliminar cliente"
          className="rounded-xl border border-red-300 bg-white px-3 py-2 text-xs font-semibold text-red-600 transition-all hover:bg-red-50 flex items-center justify-center"
        >
          <DeleteIcon size={16} />
        </button>
      </div>
    </article>
  );
};

