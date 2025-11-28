// Dashboard del secretario con resumen de citas, pacientes y tareas pendientes
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import "dayjs/locale/es";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { CitasRepository } from "../../citas/services/CitasRepository";
import { PacientesRepository } from "../../pacientes/services/PacientesRepository";
import { FacturasRepository } from "../../facturas/services/FacturasRepository";
import { ProductosRepository } from "../../inventario/services/ProductosRepository";
import { CreateCitaModal } from "../../citas/components/CreateCitaModal";
import { useState } from "react";
import { authStore } from "../../../shared/state/authStore";

dayjs.locale("es");

export const SecretaryDashboardPage = () => {
  const user = authStore((state) => state.user);
  const [isCreateCitaModalOpen, setIsCreateCitaModalOpen] = useState(false);

  // Obtener todas las citas (el secretario puede ver todas)
  const { data: todasLasCitas, isLoading: loadingCitas } = useQuery({
    queryKey: ["todas-las-citas"],
    queryFn: CitasRepository.getAll,
  });

  const { data: pacientes, isLoading: loadingPacientes } = useQuery({
    queryKey: ["pacientes"],
    queryFn: PacientesRepository.getAll,
  });

  const { data: facturas, isLoading: loadingFacturas } = useQuery({
    queryKey: ["facturas"],
    queryFn: FacturasRepository.getAll,
  });

  const { data: productos, isLoading: loadingProductos } = useQuery({
    queryKey: ["productos"],
    queryFn: ProductosRepository.getAll,
  });

  const { data: productosStockBajo } = useQuery({
    queryKey: ["productos-stock-bajo"],
    queryFn: () => ProductosRepository.obtenerProductosConStockBajo(10),
  });

  const citasHoy = todasLasCitas?.filter((cita) => dayjs(cita.fechaHora).isSame(dayjs(), "day")) || [];
  const citasPendientes = todasLasCitas?.filter((cita) => cita.estado === "PROGRAMADA") || [];
  const citasProximas = todasLasCitas
    ?.filter((cita) => dayjs(cita.fechaHora).isAfter(dayjs()) && cita.estado === "PROGRAMADA")
    .sort((a, b) => dayjs(a.fechaHora).diff(dayjs(b.fechaHora)))
    .slice(0, 5) || [];

  if (loadingCitas || loadingPacientes || loadingFacturas || loadingProductos) {
    return <FullscreenLoader />;
  }

  const facturasPendientes = facturas?.filter((f) => f.estado === "PENDIENTE") || [];
  const facturasPagadas = facturas?.filter((f) => f.estado === "PAGADA") || [];
  const totalFacturado = facturasPagadas.reduce((sum, f) => sum + parseFloat(f.total), 0);
  const totalProductos = productos?.length || 0;
  const productosConStockBajo = productosStockBajo?.length || 0;

  return (
    <div className="w-full space-y-6">
      {/* Tarjetas de resumen */}
      <section className="grid w-full gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard title="Citas Hoy" value={citasHoy.length} subtitle="Programadas para hoy" tone="primary" />
        <SummaryCard title="Citas Pendientes" value={citasPendientes.length} subtitle="Por atender" tone="warning" />
        <SummaryCard title="Total Pacientes" value={pacientes?.length || 0} subtitle="Registrados" tone="success" />
        <SummaryCard title="Citas Completadas" value={(todasLasCitas?.filter((c) => c.estado === "REALIZADA").length || 0)} subtitle="Este mes" tone="info" />
      </section>

      {/* Estadísticas adicionales */}
      <section className="grid w-full gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard
          title="Facturas Pendientes"
          value={facturasPendientes.length}
          subtitle="Por cobrar"
          tone="warning"
        />
        <SummaryCard
          title="Total Facturado"
          value={totalFacturado.toLocaleString("es-CO", { style: "currency", currency: "COP" })}
          subtitle="Este mes"
          tone="success"
        />
        <SummaryCard title="Total Productos" value={totalProductos} subtitle="En inventario" tone="info" />
        <SummaryCard
          title="Stock Bajo"
          value={productosConStockBajo}
          subtitle="Requieren atención"
          tone={productosConStockBajo > 0 ? "danger" : "success"}
        />
      </section>

      {/* Citas de hoy */}
      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold text-gray-900">Citas de Hoy</h2>
            <p className="mt-1 text-sm text-gray-500">Gestiona las citas programadas para el día</p>
          </div>
          <button
            onClick={() => setIsCreateCitaModalOpen(true)}
            className="rounded-lg border border-primary bg-primary px-4 py-2 text-sm font-medium text-white shadow-sm transition-all hover:bg-primary/90 hover:shadow-md"
          >
            Nueva Cita
          </button>
        </div>

        {citasHoy.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-8 text-center text-sm text-gray-500">
            No hay citas programadas para hoy.
          </div>
        ) : (
          <div className="space-y-3">
            {citasHoy.map((cita) => {
              const fecha = dayjs(cita.fechaHora);
              const estadoTone =
                cita.estado === "REALIZADA"
                  ? "bg-success/20 text-success"
                  : cita.estado === "CANCELADA"
                    ? "bg-danger/20 text-danger"
                    : "bg-warning/20 text-warning";

              return (
                <div key={cita.idCita} className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <span className="text-lg font-semibold text-gray-900">{fecha.format("HH:mm")}</span>
                        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${estadoTone}`}>
                          {cita.estado === "REALIZADA" ? "Completada" : cita.estado === "CANCELADA" ? "Cancelada" : "Programada"}
                        </span>
                      </div>
                      <p className="mt-1 text-sm font-semibold text-gray-900">
                        {cita.paciente?.nombre || "Sin nombre"} ({cita.paciente?.especie || "Sin especie"})
                      </p>
                      <p className="mt-1 text-sm text-gray-600">{cita.motivo || cita.tipoServicio || "Consulta"}</p>
                      <p className="mt-1 text-xs text-gray-500">
                        Propietario: <span className="font-medium">{cita.paciente?.propietario || "No asignado"}</span>
                      </p>
                      {cita.veterinario && (
                        <p className="mt-1 text-xs text-gray-500">
                          Veterinario: <span className="font-medium">{cita.veterinario.nombreCompleto}</span>
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* Próximas citas */}
      {citasProximas.length > 0 && (
        <section className="rounded-3xl bg-white p-6 shadow-soft">
          <h2 className="mb-4 text-xl font-semibold text-gray-900">Próximas Citas</h2>
          <div className="space-y-3">
            {citasProximas.map((cita) => {
              const fecha = dayjs(cita.fechaHora);
              return (
                <div key={cita.idCita} className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <span className="text-sm font-semibold text-gray-900">{fecha.format("DD/MM/YYYY HH:mm")}</span>
                      </div>
                      <p className="mt-1 text-sm font-semibold text-gray-900">
                        {cita.paciente?.nombre || "Sin nombre"} ({cita.paciente?.especie || "Sin especie"})
                      </p>
                      <p className="mt-1 text-sm text-gray-600">{cita.motivo || cita.tipoServicio || "Consulta"}</p>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </section>
      )}

      <CreateCitaModal isOpen={isCreateCitaModalOpen} onClose={() => setIsCreateCitaModalOpen(false)} />
    </div>
  );
};

interface SummaryCardProps {
  readonly title: string;
  readonly value: number | string;
  readonly subtitle: string;
  readonly tone?: "primary" | "success" | "warning" | "danger" | "info";
}

const SummaryCard = ({ title, value, subtitle, tone = "primary" }: SummaryCardProps) => {
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
      <p className="mt-2 text-3xl font-semibold text-secondary">{value}</p>
      <span className={`mt-3 inline-flex rounded-full border px-3 py-1 text-xs font-semibold ${toneClasses[tone]}`}>
        {subtitle}
      </span>
    </article>
  );
};

