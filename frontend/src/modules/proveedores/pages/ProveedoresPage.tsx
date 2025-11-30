import { useState, useMemo } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { ProveedoresRepository, type ProveedorRequest } from "../services/ProveedoresRepository";
import type { ApiProveedorResponse } from "../../shared/types/backend";
import { CreateProveedorModal } from "../components/CreateProveedorModal";
import { EditProveedorModal } from "../components/EditProveedorModal";
import { ProveedorDetailModal } from "../components/ProveedorDetailModal";
import ConfirmDialog from "../../../shared/components/ConfirmDialog";
import { useConfirmDialog } from "../../../shared/hooks/useConfirmDialog";

export const ProveedoresPage = () => {
  const queryClient = useQueryClient();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedProveedorId, setSelectedProveedorId] = useState<number | null>(null);
  const [search, setSearch] = useState("");
  
  const confirmDialog = useConfirmDialog();

  const { data: proveedores, isLoading } = useQuery({
    queryKey: ["proveedores"],
    queryFn: ProveedoresRepository.getAll,
  });

  const deleteMutation = useMutation({
    mutationFn: ProveedoresRepository.delete,
    onSuccess: () => {
      toast.success("Proveedor eliminado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["proveedores"] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al eliminar el proveedor");
    },
  });

  const filteredProveedores = useMemo(() => {
    if (!proveedores) return [];

    const term = search.trim().toLowerCase();
    if (term.length === 0) return proveedores;

    return proveedores.filter(
      (proveedor) =>
        proveedor.nombre.toLowerCase().includes(term) ||
        (proveedor.contacto && proveedor.contacto.toLowerCase().includes(term)) ||
        (proveedor.correo && proveedor.correo.toLowerCase().includes(term)) ||
        (proveedor.telefono && proveedor.telefono.includes(term))
    );
  }, [proveedores, search]);

  const stats = useMemo(() => {
    if (!proveedores) return { total: 0, conTelefono: 0, conCorreo: 0 };
    return {
      total: proveedores.length,
      conTelefono: proveedores.filter((p) => p.telefono).length,
      conCorreo: proveedores.filter((p) => p.correo).length,
    };
  }, [proveedores]);

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="w-full space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Gestión de Proveedores</h2>
          <p className="text-sm text-gray-500">Administra la información de los proveedores del sistema</p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
        >
          ➕ Nuevo Proveedor
        </button>
      </header>

      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard title="Total Proveedores" value={stats.total.toString()} description="Registrados" tone="primary" />
        <StatCard
          title="Con Teléfono"
          value={stats.conTelefono.toString()}
          description="Contacto disponible"
          tone="info"
        />
        <StatCard
          title="Con Correo"
          value={stats.conCorreo.toString()}
          description="Email registrado"
          tone="success"
        />
      </section>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex flex-wrap items-center gap-4">
          <h3 className="text-lg font-semibold text-secondary">Listado de Proveedores</h3>
          <input
            type="text"
            placeholder="Buscar por nombre, contacto, correo o teléfono..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 min-w-[200px] rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          />
        </div>

        {filteredProveedores.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
            {search.length > 0
              ? "No se encontraron proveedores con los criterios de búsqueda."
              : "No hay proveedores registrados. Crea el primero haciendo clic en 'Nuevo Proveedor'."}
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {filteredProveedores.map((proveedor) => (
              <ProveedorCard
                key={proveedor.idProveedor}
                proveedor={proveedor}
                onView={() => {
                  setSelectedProveedorId(proveedor.idProveedor);
                  setIsDetailModalOpen(true);
                }}
                onEdit={() => {
                  setSelectedProveedorId(proveedor.idProveedor);
                  setIsEditModalOpen(true);
                }}
                onDelete={() => {
                  confirmDialog.openDialog({
                    title: 'Eliminar Proveedor',
                    message: `¿Está seguro de que desea eliminar al proveedor "${proveedor.nombre}"? Esta acción no se puede deshacer.`,
                    variant: 'danger',
                    onConfirm: () => deleteMutation.mutate(proveedor.idProveedor),
                  });
                }}
              />
            ))}
          </div>
        )}
      </section>

      <CreateProveedorModal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} />
      {selectedProveedorId && (
        <>
          <EditProveedorModal
            isOpen={isEditModalOpen}
            proveedorId={selectedProveedorId}
            onClose={() => {
              setIsEditModalOpen(false);
              setSelectedProveedorId(null);
            }}
          />
          <ProveedorDetailModal
            isOpen={isDetailModalOpen}
            proveedorId={selectedProveedorId}
            onClose={() => {
              setIsDetailModalOpen(false);
              setSelectedProveedorId(null);
            }}
          />
        </>
      )}
      
      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        onClose={confirmDialog.closeDialog}
        onConfirm={confirmDialog.handleConfirm}
        title={confirmDialog.options.title || 'Confirmar'}
        message={confirmDialog.options.message || '¿Continuar?'}
        variant={confirmDialog.options.variant}
      />
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

interface ProveedorCardProps {
  readonly proveedor: ApiProveedorResponse;
  readonly onView: () => void;
  readonly onEdit: () => void;
  readonly onDelete: () => void;
}

const ProveedorCard = ({ proveedor, onView, onEdit, onDelete }: ProveedorCardProps) => {
  return (
    <article className="group flex flex-col justify-between rounded-2xl border border-gray-200 bg-white p-5 shadow-sm transition-all hover:border-primary/40 hover:shadow-lg">
      <div>
        <div className="mb-3 flex items-start justify-between">
          <div>
            <h4 className="text-lg font-bold text-secondary">{proveedor.nombre}</h4>
            {proveedor.contacto && <p className="mt-1 text-sm text-gray-600">Contacto: {proveedor.contacto}</p>}
          </div>
        </div>
        <div className="space-y-1.5 text-xs text-gray-500">
          {proveedor.telefono && (
            <p className="flex items-center gap-1.5">
              <span>📞</span> {proveedor.telefono}
            </p>
          )}
          {proveedor.correo && (
            <p className="flex items-center gap-1.5">
              <span>📧</span> {proveedor.correo}
            </p>
          )}
          {proveedor.direccion && (
            <p className="flex items-center gap-1.5">
              <span>📍</span> {proveedor.direccion}
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
          className="rounded-xl border border-red-300 bg-white px-3 py-2 text-xs font-semibold text-red-600 transition-all hover:bg-red-50"
        >
          🗑️
        </button>
      </div>
    </article>
  );
};

