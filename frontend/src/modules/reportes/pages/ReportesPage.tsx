import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
import dayjs from "dayjs";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { ReportesRepository } from "../services/ReportesRepository";
import type { ApiReporteResponse } from "../../shared/types/backend";

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

export const ReportesPage = () => {
  const [isGenerarModalOpen, setIsGenerarModalOpen] = useState(false);
  const [reportesGenerados, setReportesGenerados] = useState<ApiReporteResponse[]>([]);
  const queryClient = useQueryClient();

  const generarReporteMutation = useMutation({
    mutationFn: ReportesRepository.generar,
    onSuccess: (reporte) => {
      toast.success("Reporte generado exitosamente");
      setReportesGenerados((prev) => [reporte, ...prev]);
      setIsGenerarModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ["reportes"] });
    },
    onError: (error: Error) => {
      toast.error(error.message || "Error al generar el reporte");
    },
  });

  const handleExportarPDF = async (id: number) => {
    try {
      const blob = await ReportesRepository.exportarPDF(id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `reporte_${id}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success("PDF descargado exitosamente");
    } catch (error) {
      toast.error("Error al exportar el PDF");
    }
  };

  const handleExportarExcel = async (id: number) => {
    try {
      const blob = await ReportesRepository.exportarExcel(id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `reporte_${id}.xlsx`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success("Excel descargado exitosamente");
    } catch (error) {
      toast.error("Error al exportar el Excel");
    }
  };

  return (
    <div className="w-full space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Reportes y Estadísticas</h2>
          <p className="text-sm text-gray-500">Genera y exporta reportes del sistema</p>
        </div>
        <button
          onClick={() => setIsGenerarModalOpen(true)}
          className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
        >
          Generar Reporte
        </button>
      </header>

      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard
          title="Reportes Generados"
          value={reportesGenerados.length.toString()}
          description="Total de reportes"
          tone="primary"
        />
        <StatCard
          title="Reportes del Mes"
          value={reportesGenerados.filter((r) => dayjs(r.fechaGeneracion).isSame(dayjs(), "month")).length.toString()}
          description="Este mes"
          tone="info"
        />
      </section>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <h3 className="mb-4 text-lg font-semibold text-secondary">Reportes Generados</h3>
        {reportesGenerados.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
            No hay reportes generados. Genera tu primer reporte para comenzar.
          </div>
        ) : (
          <div className="space-y-3">
            {reportesGenerados.map((reporte) => (
              <div
                key={reporte.id}
                className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4 transition-base hover:border-primary/40 hover:bg-white"
              >
                <div className="flex-1 min-w-[200px]">
                  <h4 className="font-semibold text-secondary">{reporte.nombre}</h4>
                  <p className="text-xs text-gray-500">
                    {reporte.tipo || "Sin tipo"} • {dayjs(reporte.fechaGeneracion).format("DD/MM/YYYY HH:mm")}
                  </p>
                  {reporte.estadisticas && reporte.estadisticas.length > 0 && (
                    <p className="mt-1 text-xs text-gray-600">
                      {reporte.estadisticas.length} estadística{reporte.estadisticas.length !== 1 ? "s" : ""}
                    </p>
                  )}
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleExportarPDF(reporte.id)}
                    className="rounded-xl border border-primary bg-primary/10 px-3 py-2 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white"
                  >
                    📄 PDF
                  </button>
                  <button
                    onClick={() => handleExportarExcel(reporte.id)}
                    className="rounded-xl border border-success bg-success/10 px-3 py-2 text-xs font-semibold text-success transition-base hover:bg-success hover:text-white"
                  >
                    📊 Excel
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {isGenerarModalOpen && (
        <GenerarReporteModal
          isOpen={isGenerarModalOpen}
          onClose={() => setIsGenerarModalOpen(false)}
          onGenerar={(request) => generarReporteMutation.mutate(request)}
          isLoading={generarReporteMutation.isPending}
        />
      )}
    </div>
  );
};

interface GenerarReporteModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onGenerar: (request: { nombre: string; tipo?: string; parametros?: Record<string, unknown> }) => void;
  readonly isLoading: boolean;
}

const GenerarReporteModal = ({ isOpen, onClose, onGenerar, isLoading }: GenerarReporteModalProps) => {
  const [nombre, setNombre] = useState("");
  const [tipo, setTipo] = useState("DIARIO");
  const [fechaInicio, setFechaInicio] = useState(dayjs().startOf("month").format("YYYY-MM-DD"));
  const [fechaFin, setFechaFin] = useState(dayjs().format("YYYY-MM-DD"));

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onGenerar({
      nombre,
      tipo,
      parametros: {
        fechaInicio,
        fechaFin,
      },
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-md rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-secondary">Generar Reporte</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            ✕
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Nombre del Reporte</label>
            <input
              type="text"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              required
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="Ej: Reporte Mensual de Consultas"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Tipo de Reporte</label>
            <select
              value={tipo}
              onChange={(e) => setTipo(e.target.value)}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            >
              <option value="DIARIO">Diario</option>
              <option value="SEMANAL">Semanal</option>
              <option value="MENSUAL">Mensual</option>
              <option value="ANUAL">Anual</option>
              <option value="PERSONALIZADO">Personalizado</option>
            </select>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Fecha Inicio</label>
              <input
                type="date"
                value={fechaInicio}
                onChange={(e) => setFechaInicio(e.target.value)}
                required
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Fecha Fin</label>
              <input
                type="date"
                value={fechaFin}
                onChange={(e) => setFechaFin(e.target.value)}
                required
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              />
            </div>
          </div>
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-base hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="flex-1 rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white transition-base hover:bg-primary-dark disabled:opacity-50"
            >
              {isLoading ? "Generando..." : "Generar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

