import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { ConfiguracionRepository } from "../services/ConfiguracionRepository";

interface StatCardProps {
  readonly title: string;
  readonly value: string;
  readonly description?: string;
  readonly tone?: "primary" | "success" | "warning" | "danger" | "info";
}

const StatCard = ({ title, value, description, tone = "primary" }: StatCardProps) => {
  const toneClasses = {
    primary: "border-primary bg-primary/10 text-primary",
    success: "border-success bg-success/10 text-success",
    warning: "border-warning bg-warning/10 text-warning",
    danger: "border-danger bg-danger/10 text-danger",
    info: "border-info bg-info/10 text-info",
  };

  return (
    <div className={`rounded-3xl border p-6 ${toneClasses[tone]}`}>
      <h3 className="text-sm font-medium opacity-80">{title}</h3>
      <p className="mt-2 text-3xl font-bold">{value}</p>
      {description && <p className="mt-1 text-xs opacity-70">{description}</p>}
    </div>
  );
};

export const ConfiguracionPage = () => {
  const [search, setSearch] = useState("");
  const [editingKey, setEditingKey] = useState<string | null>(null);
  const [editValue, setEditValue] = useState("");

  const { data: parametros, isLoading } = useQuery({
    queryKey: ["configuracion-parametros"],
    queryFn: ConfiguracionRepository.obtenerTodos,
  });

  const queryClient = useQueryClient();

  const actualizarMutation = useMutation({
    mutationFn: ({ clave, valor }: { clave: string; valor: string }) =>
      ConfiguracionRepository.actualizarParametro(clave, valor),
    onSuccess: () => {
      toast.success("Parámetro actualizado exitosamente");
      setEditingKey(null);
      queryClient.invalidateQueries({ queryKey: ["configuracion-parametros"] });
    },
    onError: (error: Error) => {
      toast.error(error.message || "Error al actualizar el parámetro");
    },
  });

  const recargarMutation = useMutation({
    mutationFn: ConfiguracionRepository.recargarParametros,
    onSuccess: () => {
      toast.success("Parámetros recargados exitosamente");
      queryClient.invalidateQueries({ queryKey: ["configuracion-parametros"] });
    },
    onError: (error: Error) => {
      toast.error(error.message || "Error al recargar los parámetros");
    },
  });

  const parametrosFiltrados = parametros
    ? Object.entries(parametros).filter(([clave]) => clave.toLowerCase().includes(search.toLowerCase()))
    : [];

  const handleStartEdit = (clave: string, valor: string) => {
    setEditingKey(clave);
    setEditValue(valor);
  };

  const handleSave = (clave: string) => {
    actualizarMutation.mutate({ clave, valor: editValue });
  };

  const handleCancel = () => {
    setEditingKey(null);
    setEditValue("");
  };

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="w-full space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Configuración del Sistema</h2>
          <p className="text-sm text-gray-500">Gestiona los parámetros de configuración del sistema</p>
        </div>
        <button
          onClick={() => recargarMutation.mutate()}
          disabled={recargarMutation.isPending}
          className="rounded-2xl border border-primary bg-primary/10 px-4 py-2 text-sm font-semibold text-primary transition-base hover:bg-primary hover:text-white disabled:opacity-50"
        >
          {recargarMutation.isPending ? "Recargando..." : "Recargar Parámetros"}
        </button>
      </header>

      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard
          title="Parámetros Totales"
          value={parametros ? Object.keys(parametros).length.toString() : "0"}
          description="Configurados"
          tone="primary"
        />
      </section>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6">
          <input
            type="text"
            placeholder="Buscar parámetro por clave..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          />
        </div>

        {parametrosFiltrados.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
            No se encontraron parámetros con el filtro seleccionado.
          </div>
        ) : (
          <div className="space-y-3">
            {parametrosFiltrados.map(([clave, valor]) => (
              <div
                key={clave}
                className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4 transition-base hover:border-primary/40 hover:bg-white"
              >
                <div className="flex-1 min-w-[200px]">
                  <h4 className="font-semibold text-secondary">{clave}</h4>
                  {editingKey === clave ? (
                    <div className="mt-2 flex gap-2">
                      <input
                        type="text"
                        value={editValue}
                        onChange={(e) => setEditValue(e.target.value)}
                        className="flex-1 rounded-xl border border-primary bg-white px-3 py-1 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                      />
                      <button
                        onClick={() => handleSave(clave)}
                        disabled={actualizarMutation.isPending}
                        className="rounded-xl bg-primary px-3 py-1 text-xs font-semibold text-white transition-base hover:bg-primary-dark disabled:opacity-50"
                      >
                        Guardar
                      </button>
                      <button
                        onClick={handleCancel}
                        className="rounded-xl border border-gray-200 bg-white px-3 py-1 text-xs font-semibold text-gray-700 transition-base hover:bg-gray-50"
                      >
                        Cancelar
                      </button>
                    </div>
                  ) : (
                    <div className="mt-1 flex items-center gap-2">
                      <p className="text-sm text-gray-600">{valor}</p>
                      <button
                        onClick={() => handleStartEdit(clave, valor)}
                        className="rounded-lg border border-primary bg-primary/10 px-2 py-1 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white"
                      >
                        Editar
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

