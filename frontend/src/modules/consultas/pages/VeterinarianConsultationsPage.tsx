import { useMemo, useState } from "react";
import dayjs from "dayjs";
import { useQuery } from "@tanstack/react-query";
import "dayjs/locale/es";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { useCitasVeterinario } from "../../citas/hooks/useCitasVeterinario";
import { ConsultasRepository } from "../services/ConsultasRepository";
import type { ApiCitaResponse, ApiServicioPrestadoResponse } from "../../shared/types/backend";

dayjs.locale("es");

type EstadoFilter = "TODOS" | "PROGRAMADA" | "REALIZADA" | "CANCELADA";

export const VeterinarianConsultationsPage = () => {
  const { data: citas, isLoading } = useCitasVeterinario();

  const [search, setSearch] = useState("");
  const [estadoFilter, setEstadoFilter] = useState<EstadoFilter>("TODOS");
  const [dateFrom, setDateFrom] = useState<string>("");
  const [dateTo, setDateTo] = useState<string>("");
  const [expandedCitaId, setExpandedCitaId] = useState<number | null>(null);

  const filteredCitas = useMemo(() => {
    if (!citas) return [];
    const term = search.trim().toLowerCase();
    const fromDate = dateFrom ? dayjs(dateFrom) : null;
    const toDate = dateTo ? dayjs(dateTo).endOf("day") : null;

    return citas.filter((cita) => {
      const matchesSearch =
        term.length === 0 ||
        cita.paciente?.nombre?.toLowerCase().includes(term) ||
        cita.paciente?.propietario?.toLowerCase().includes(term) ||
        cita.tipoServicio?.toLowerCase().includes(term) ||
        cita.motivo?.toLowerCase().includes(term);

      const matchesEstado = estadoFilter === "TODOS" || cita.estado === estadoFilter;

      const fecha = dayjs(cita.fechaHora);
      const matchesDate =
        (!fromDate || fecha.isAfter(fromDate.subtract(1, "day"))) &&
        (!toDate || fecha.isBefore(toDate.add(1, "day")));

      return matchesSearch && matchesEstado && matchesDate;
    });
  }, [citas, search, estadoFilter, dateFrom, dateTo]);

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Consultas Veterinarias</h2>
          <p className="text-sm text-gray-500">Revisa el historial de atenciones realizadas y filtra por estado o periodo.</p>
        </div>
        {/* Oculto para rol VETERINARIO: no puede crear consultas */}
      </header>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="flex flex-wrap items-center gap-3">
          <input
            type="search"
            placeholder="Buscar por paciente, propietario o motivo"
            className="flex-1 min-w-[200px] rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
          <select
            className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-secondary focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
            value={estadoFilter}
            onChange={(event) => setEstadoFilter(event.target.value as EstadoFilter)}
          >
            <option value="TODOS">Todos los estados</option>
            <option value="PROGRAMADA">Programadas</option>
            <option value="REALIZADA">Realizadas</option>
            <option value="CANCELADA">Canceladas</option>
          </select>
          <input
            type="date"
            className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-secondary focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
            value={dateFrom}
            onChange={(event) => setDateFrom(event.target.value)}
          />
          <input
            type="date"
            className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-secondary focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
            value={dateTo}
            onChange={(event) => setDateTo(event.target.value)}
          />
          <button
            className="rounded-2xl border border-primary px-4 py-2 text-sm font-semibold text-primary transition-base hover:bg-primary hover:text-white"
            onClick={() => toast("La exportación estará disponible en próximas versiones")}
          >
            Exportar reporte
          </button>
        </div>

        <div className="mt-6 overflow-hidden rounded-3xl border border-gray-100">
          <table className="min-w-full divide-y divide-gray-100 text-sm text-gray-600">
            <thead className="bg-gray-50 text-xs uppercase text-gray-500">
              <tr>
                <th className="px-4 py-3 text-left">Fecha y hora</th>
                <th className="px-4 py-3 text-left">Paciente</th>
                <th className="px-4 py-3 text-left">Propietario</th>
                <th className="px-4 py-3 text-left">Tipo</th>
                <th className="px-4 py-3 text-left">Motivo</th>
                <th className="px-4 py-3 text-left">Estado</th>
                <th className="px-4 py-3 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white">
              {filteredCitas.length === 0 ? (
                <tr>
                  <td className="px-4 py-6 text-center text-sm text-gray-500" colSpan={7}>
                    No se encontraron consultas con los criterios seleccionados.
                  </td>
                </tr>
              ) : (
                filteredCitas.map((cita) => (
                  <ConsultationRow
                    key={cita.idCita}
                    cita={cita}
                    expanded={expandedCitaId === cita.idCita}
                    onToggle={() => setExpandedCitaId((prev) => (prev === cita.idCita ? null : cita.idCita))}
                  />
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
};

interface ConsultationRowProps {
  readonly cita: ApiCitaResponse;
  readonly expanded: boolean;
  readonly onToggle: () => void;
}

const ConsultationRow = ({ cita, expanded, onToggle }: ConsultationRowProps) => {
  const fecha = dayjs(cita.fechaHora);
  const estadoTone = getStatusTone(cita.estado);

  return (
    <>
      <tr className="transition-base hover:bg-gray-50">
        <td className="px-4 py-3">{fecha.format("DD/MM/YYYY HH:mm")}</td>
        <td className="px-4 py-3">{cita.paciente?.nombre ?? "Sin nombre"}</td>
        <td className="px-4 py-3">{cita.paciente?.propietario ?? "Sin asignar"}</td>
        <td className="px-4 py-3">
          <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary">
            {cita.tipoServicio ?? "Consulta"}
          </span>
        </td>
        <td className="px-4 py-3">{cita.motivo ?? "—"}</td>
        <td className="px-4 py-3">
          <span className={`rounded-full px-3 py-1 text-xs font-semibold ${estadoTone}`}>{mapEstado(cita.estado)}</span>
        </td>
        <td className="px-4 py-3 text-right">
          <button
            className="rounded-2xl border border-primary px-4 py-2 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white"
            onClick={onToggle}
          >
            {expanded ? "Ocultar" : "Ver detalle"}
          </button>
        </td>
      </tr>
      {expanded ? (
        <tr className="bg-gray-50/80">
          <td colSpan={7} className="px-6 py-4">
            <ConsultationDetails citaId={cita.idCita} />
          </td>
        </tr>
      ) : null}
    </>
  );
};

interface ConsultationDetailsProps {
  readonly citaId: number;
}

const ConsultationDetails = ({ citaId }: ConsultationDetailsProps) => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["servicios-prestados", citaId],
    queryFn: () => ConsultasRepository.getByCita(citaId),
  });

  if (isLoading) {
    return <p className="text-xs text-gray-500">Cargando información de la consulta...</p>;
  }

  if (error) {
    return <p className="text-xs text-danger">No fue posible obtener el detalle del servicio.</p>;
  }

  if (!data || data.length === 0) {
    return <p className="text-xs text-gray-500">Aún no se registró un servicio prestado para esta cita.</p>;
  }

  return (
    <div className="space-y-3">
      {data.map((servicio) => (
        <ServicioItem key={servicio.idPrestado} servicio={servicio} />
      ))}
    </div>
  );
};

interface ServicioItemProps {
  readonly servicio: ApiServicioPrestadoResponse;
}

const ServicioItem = ({ servicio }: ServicioItemProps) => {
  return (
    <div className="rounded-2xl border border-gray-200 bg-white p-4 text-xs text-gray-600">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <p className="font-semibold text-secondary">{servicio.servicio?.nombre ?? "Servicio prestado"}</p>
        <span className="rounded-full bg-success/10 px-2 py-1 text-[11px] font-semibold text-success">
          {Number(servicio.costoTotal).toLocaleString("es-CO", { style: "currency", currency: "COP" })}
        </span>
      </div>
      {servicio.observaciones ? <p className="mt-2 text-gray-500">{servicio.observaciones}</p> : null}
      {servicio.insumos.length > 0 ? (
        <div className="mt-2 grid grid-cols-2 gap-2 rounded-2xl bg-primary/5 p-3">
          {servicio.insumos.map((insumo, index) => (
            <div key={index}>
              <span className="font-semibold text-secondary">Producto #{insumo.productoId ?? index + 1}</span>
              <p>
                {insumo.cantidad ?? "—"} unidades ·{" "}
                {insumo.precioUnitario
                  ? Number(insumo.precioUnitario).toLocaleString("es-CO", { style: "currency", currency: "COP" })
                  : "Sin costo"}
              </p>
            </div>
          ))}
        </div>
      ) : null}
    </div>
  );
};

const mapEstado = (estado: string): string => {
  switch (estado) {
    case "REALIZADA":
      return "Completada";
    case "CANCELADA":
      return "Cancelada";
    case "PROGRAMADA":
    default:
      return "Programada";
  }
};

const getStatusTone = (estado: string): string => {
  switch (estado) {
    case "REALIZADA":
      return "bg-success/20 text-success";
    case "CANCELADA":
      return "bg-danger/20 text-danger";
    case "PROGRAMADA":
    default:
      return "bg-warning/20 text-warning";
  }
};

export default VeterinarianConsultationsPage;

