import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import "dayjs/locale/es";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { PacientesRepository } from "../services/PacientesRepository";
import { CreatePacienteModal } from "../components/CreatePacienteModal";
import { PacienteDetailModal } from "../components/PacienteDetailModal";
import type { ApiPacienteResponse } from "../../shared/types/backend";
import { authStore } from "../../../shared/state/authStore";

dayjs.locale("es");

type SpeciesFilter = "TODOS" | "perro" | "gato" | "otros";
type OrderOption = "nombre-asc" | "nombre-desc";

const calculateAge = (birthDate?: string | null): string => {
  if (!birthDate) return "—";
  const years = dayjs().diff(dayjs(birthDate), "year");
  return years > 0 ? `${years} ${years === 1 ? "año" : "años"}` : "Menos de un año";
};

const formatWeight = (weight?: string | null): string => {
  if (!weight) return "—";
  const numeric = Number(weight);
  if (Number.isNaN(numeric)) return weight;
  return `${numeric.toFixed(1)} kg`;
};

const normalizeSpecies = (species?: string | null): SpeciesFilter => {
  if (!species) return "otros";
  const normalized = species.trim().toLowerCase();
  if (normalized.includes("perro")) return "perro";
  if (normalized.includes("gato")) return "gato";
  return "otros";
};

export const VeterinarianPatientsPage = () => {
  const [search, setSearch] = useState("");
  const [speciesFilter, setSpeciesFilter] = useState<SpeciesFilter>("TODOS");
  const [order, setOrder] = useState<OrderOption>("nombre-asc");
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [selectedPacienteId, setSelectedPacienteId] = useState<number | null>(null);
  const { user } = authStore.getState();
  const isVeterinario = user?.rol === "VETERINARIO";

  const { data, isLoading } = useQuery({
    queryKey: ["pacientes"],
    queryFn: PacientesRepository.getAll,
  });

  const stats = useMemo(() => buildStats(data ?? []), [data]);

  const filteredPatients = useMemo(() => {
    if (!data) return [];

    const term = search.trim().toLowerCase();

    let result = data.filter((paciente) => {
      const matchesSearch =
        term.length === 0 ||
        paciente.nombre.toLowerCase().includes(term) ||
        paciente.cliente?.nombre.toLowerCase().includes(term) ||
        paciente.cliente?.apellido.toLowerCase().includes(term);

      const species = normalizeSpecies(paciente.especie);
      const matchesSpecies = speciesFilter === "TODOS" || species === speciesFilter;

      return matchesSearch && matchesSpecies;
    });

    result = result.sort((a, b) => {
      const direction = order === "nombre-asc" ? 1 : -1;
      return a.nombre.localeCompare(b.nombre) * direction;
    });

    return result;
  }, [data, search, speciesFilter, order]);

  if (isLoading) {
    return (
      <div className="space-y-6">
        <header className="flex flex-wrap items-center justify-between gap-4">
          <div className="space-y-2">
            <div className="skeleton h-6 w-48"></div>
            <div className="skeleton h-4 w-80"></div>
          </div>
          <div className="flex gap-3">
            <div className="skeleton h-10 w-36"></div>
            <div className="skeleton h-10 w-36"></div>
          </div>
        </header>
        <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="card p-6">
              <div className="flex items-center gap-4">
                <span className="skeleton h-12 w-12 rounded-full"></span>
                <div className="flex-1 space-y-2">
                  <div className="skeleton h-4 w-32"></div>
                  <div className="skeleton h-3 w-20"></div>
                </div>
              </div>
              <div className="mt-4 grid grid-cols-2 gap-3">
                <div className="skeleton h-3 w-full"></div>
                <div className="skeleton h-3 w-full"></div>
                <div className="skeleton h-3 w-full"></div>
                <div className="skeleton h-3 w-full"></div>
              </div>
              <div className="mt-4 flex gap-2">
                <div className="skeleton h-9 w-full"></div>
                <div className="skeleton h-9 w-full"></div>
              </div>
            </div>
          ))}
        </section>
        <section className="card p-6">
          <div className="flex flex-wrap items-center gap-4">
            <div className="skeleton h-10 w-72"></div>
            <div className="skeleton h-10 w-44"></div>
            <div className="skeleton h-10 w-44"></div>
          </div>
          <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="skeleton h-44 w-full"></div>
            ))}
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Mis Pacientes</h2>
          <p className="text-sm text-gray-500">
            Consulta la información de tus pacientes y filtra por especie, propietario o estado.
          </p>
        </div>
        <div className="flex gap-3">
          <ExportarListaButton pacientes={filteredPatients} />
          {/* Oculto para rol VETERINARIO: no puede crear pacientes */}
          {!isVeterinario && (
            <button
              className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
              onClick={() => setIsCreateModalOpen(true)}
            >
              Nuevo Paciente
            </button>
          )}
        </div>
      </header>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Total Pacientes" value={stats.total.toString()} description="Bajo tu cuidado" />
        <StatCard title="Perros" value={stats.perros.toString()} description="Registrados" tone="primary" />
        <StatCard title="Gatos" value={stats.gatos.toString()} description="Registrados" tone="secondary" />
        <StatCard title="Nuevos (semana)" value={stats.nuevosSemana.toString()} description="Últimos 7 días" tone="accent" />
      </section>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="flex flex-wrap items-center gap-4">
          <input
            type="search"
            placeholder="Buscar por nombre del paciente o propietario..."
            className="flex-1 min-w-[200px] rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />

          <select
            className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            value={speciesFilter}
            onChange={(event) => setSpeciesFilter(event.target.value as SpeciesFilter)}
          >
            <option value="TODOS">Todas las especies</option>
            <option value="perro">Perros</option>
            <option value="gato">Gatos</option>
            <option value="otros">Otras especies</option>
          </select>

          <select
            className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            value={order}
            onChange={(event) => setOrder(event.target.value as OrderOption)}
          >
            <option value="nombre-asc">Nombre A-Z</option>
            <option value="nombre-desc">Nombre Z-A</option>
          </select>
        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
          {filteredPatients.length === 0 ? (
            <div className="col-span-full rounded-3xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
              No se encontraron pacientes con los criterios seleccionados.
            </div>
          ) : (
            filteredPatients.map((paciente) => (
              <PatientCard
                key={paciente.id}
                paciente={paciente}
                onViewDetail={() => setSelectedPacienteId(paciente.id)}
              />
            ))
          )}
        </div>
      </section>

      {/* Modal de creación solo para SECRETARIO/ADMIN */}
      {!isVeterinario && (
        <CreatePacienteModal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} />
      )}
      <PacienteDetailModal
        isOpen={selectedPacienteId !== null}
        pacienteId={selectedPacienteId}
        onClose={() => setSelectedPacienteId(null)}
      />
    </div>
  );
};

const buildStats = (pacientes: ApiPacienteResponse[]) => {
  const total = pacientes.length;
  const perros = pacientes.filter((paciente) => normalizeSpecies(paciente.especie) === "perro").length;
  const gatos = pacientes.filter((paciente) => normalizeSpecies(paciente.especie) === "gato").length;

  const nuevosSemana = pacientes
    .slice()
    .sort((a, b) => b.id - a.id)
    .slice(0, Math.min(3, pacientes.length))
    .length;

  return { total, perros, gatos, nuevosSemana };
};

interface StatCardProps {
  readonly title: string;
  readonly value: string;
  readonly description: string;
  readonly tone?: "default" | "primary" | "secondary" | "accent";
}

const toneClasses: Record<NonNullable<StatCardProps["tone"]>, string> = {
  default: "bg-primary/10 text-secondary",
  primary: "bg-primary/10 text-primary",
  secondary: "bg-secondary/10 text-secondary",
  accent: "bg-success/10 text-success",
};

const StatCard = ({ title, value, description, tone = "default" }: StatCardProps) => (
  <article className="rounded-3xl border border-gray-100 bg-white p-6 shadow-soft">
    <p className="text-xs font-medium uppercase tracking-wider text-gray-500">{title}</p>
    <p className="mt-2 text-3xl font-semibold text-secondary">{value}</p>
    <span className={`mt-3 inline-flex rounded-full px-3 py-1 text-xs font-semibold ${toneClasses[tone]}`}>{description}</span>
  </article>
);

interface PatientCardProps {
  readonly paciente: ApiPacienteResponse;
  readonly onViewDetail: () => void;
}

const PatientCard = ({ paciente, onViewDetail }: PatientCardProps) => {
  const initials = `${paciente.nombre.charAt(0)}`;
  const ownerFullName = paciente.cliente ? `${paciente.cliente.nombre} ${paciente.cliente.apellido}` : "Sin asignar";
  const speciesBadge =
    normalizeSpecies(paciente.especie) === "perro"
      ? "bg-primary/10 text-primary"
      : normalizeSpecies(paciente.especie) === "gato"
        ? "bg-secondary/10 text-secondary"
        : "bg-gray-200 text-secondary";

  return (
    <article className="flex h-full flex-col gap-3 rounded-3xl border border-gray-100 bg-gray-50 p-6 shadow-sm transition-base hover:border-primary/40 hover:bg-white">
      <div className="flex items-center gap-4">
        <span className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 text-lg font-semibold text-primary">
          {initials}
        </span>
        <div>
          <p className="text-sm font-semibold text-secondary">{paciente.nombre}</p>
          <span className={`inline-flex rounded-full px-2 py-1 text-xs font-medium capitalize ${speciesBadge}`}>
            {paciente.especie ?? "Sin especie"}
          </span>
        </div>
      </div>

      <dl className="grid grid-cols-2 gap-2 text-xs text-gray-500">
        <div>
          <dt className="font-semibold text-secondary">Edad</dt>
          <dd>{calculateAge(paciente.fechaNacimiento)}</dd>
        </div>
        <div>
          <dt className="font-semibold text-secondary">Peso</dt>
          <dd>{formatWeight(paciente.pesoKg)}</dd>
        </div>
        <div>
          <dt className="font-semibold text-secondary">Propietario</dt>
          <dd>{ownerFullName}</dd>
        </div>
        <div>
          <dt className="font-semibold text-secondary">Estado</dt>
          <dd>{paciente.estadoSalud ?? "Sin registrar"}</dd>
        </div>
      </dl>

      <div className="mt-auto flex gap-2">
        <button
          className="flex-1 rounded-2xl border border-gray-200 px-4 py-2 text-xs font-semibold text-secondary transition-base hover:border-primary hover:text-primary"
          onClick={onViewDetail}
        >
          Ver perfil
        </button>
        <button
          className="flex-1 rounded-2xl bg-primary px-4 py-2 text-xs font-semibold text-white transition-base hover:bg-primary-dark"
          onClick={() => toast("Enlazando con la historia clínica...", { icon: "📄" })}
        >
          Historia
        </button>
      </div>
    </article>
  );
};

interface ExportarListaButtonProps {
  readonly pacientes: ApiPacienteResponse[];
}

const ExportarListaButton = ({ pacientes }: ExportarListaButtonProps) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const exportarCSV = () => {
    if (pacientes.length === 0) {
      toast.error("No hay pacientes para exportar");
      return;
    }

    const headers = ["Nombre", "Especie", "Raza", "Edad", "Peso (kg)", "Sexo", "Estado de Salud", "Propietario", "Correo", "Teléfono"];
    const rows = pacientes.map((paciente) => {
      const edad = calculateAge(paciente.fechaNacimiento);
      const peso = paciente.pesoKg ? Number(paciente.pesoKg).toFixed(1) : "—";
      const propietario = paciente.cliente ? `${paciente.cliente.nombre} ${paciente.cliente.apellido}` : "Sin asignar";
      const correo = paciente.cliente?.correo || "—";
      const telefono = paciente.cliente?.telefono || "—";

      return [
        paciente.nombre,
        paciente.especie || "—",
        paciente.raza || "—",
        edad,
        peso,
        paciente.sexo || "—",
        paciente.estadoSalud || "Sin registrar",
        propietario,
        correo,
        telefono,
      ];
    });

    const csvContent = [headers, ...rows].map((row) => row.map((cell) => `"${cell}"`).join(",")).join("\n");
    const blob = new Blob(["\uFEFF" + csvContent], { type: "text/csv;charset=utf-8;" }); // BOM para Excel
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `pacientes_${dayjs().format("YYYY-MM-DD_HH-mm-ss")}.csv`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
    toast.success("Lista exportada a CSV exitosamente");
    setIsMenuOpen(false);
  };

  const exportarPDF = async () => {
    if (pacientes.length === 0) {
      toast.error("No hay pacientes para exportar");
      return;
    }

    try {
      // Usar jsPDF si está disponible, sino usar window.print
      const { jsPDF } = await import("jspdf");
      const doc = new jsPDF();

      // Título
      doc.setFontSize(18);
      doc.text("Lista de Pacientes", 14, 20);
      doc.setFontSize(10);
      doc.text(`Generado el: ${dayjs().format("DD/MM/YYYY HH:mm")}`, 14, 28);

      // Tabla
      let y = 40;
      const pageHeight = doc.internal.pageSize.height;
      const margin = 14;
      const rowHeight = 8;
      const maxRowsPerPage = Math.floor((pageHeight - y - margin) / rowHeight);

      // Headers
      doc.setFontSize(10);
      doc.setFont(undefined, "bold");
      doc.text("Nombre", margin, y);
      doc.text("Especie", margin + 50, y);
      doc.text("Edad", margin + 80, y);
      doc.text("Peso", margin + 100, y);
      doc.text("Propietario", margin + 120, y);
      doc.setFont(undefined, "normal");

      y += 6;
      let rowCount = 0;

      pacientes.forEach((paciente, index) => {
        if (rowCount >= maxRowsPerPage) {
          doc.addPage();
          y = margin;
          rowCount = 0;
        }

        const edad = calculateAge(paciente.fechaNacimiento);
        const peso = paciente.pesoKg ? Number(paciente.pesoKg).toFixed(1) + " kg" : "—";
        const propietario = paciente.cliente ? `${paciente.cliente.nombre} ${paciente.cliente.apellido}` : "Sin asignar";

        doc.setFontSize(9);
        doc.text(paciente.nombre.substring(0, 20), margin, y);
        doc.text((paciente.especie || "—").substring(0, 15), margin + 50, y);
        doc.text(edad.substring(0, 10), margin + 80, y);
        doc.text(peso.substring(0, 10), margin + 100, y);
        doc.text(propietario.substring(0, 25), margin + 120, y);

        y += rowHeight;
        rowCount++;
      });

      doc.save(`pacientes_${dayjs().format("YYYY-MM-DD_HH-mm-ss")}.pdf`);
      toast.success("Lista exportada a PDF exitosamente");
      setIsMenuOpen(false);
    } catch (error) {
      // Si jsPDF no está disponible, usar window.print
      toast.error("Error al exportar PDF. Intentando impresión...");
      const printWindow = window.open("", "_blank");
      if (printWindow) {
        const htmlContent = `
          <!DOCTYPE html>
          <html>
            <head>
              <title>Lista de Pacientes</title>
              <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { color: #1e40af; }
                table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #1e40af; color: white; }
                tr:nth-child(even) { background-color: #f2f2f2; }
              </style>
            </head>
            <body>
              <h1>Lista de Pacientes</h1>
              <p>Generado el: ${dayjs().format("DD/MM/YYYY HH:mm")}</p>
              <table>
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Especie</th>
                    <th>Raza</th>
                    <th>Edad</th>
                    <th>Peso</th>
                    <th>Propietario</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  ${pacientes
                    .map(
                      (p) => `
                    <tr>
                      <td>${p.nombre}</td>
                      <td>${p.especie || "—"}</td>
                      <td>${p.raza || "—"}</td>
                      <td>${calculateAge(p.fechaNacimiento)}</td>
                      <td>${p.pesoKg ? Number(p.pesoKg).toFixed(1) + " kg" : "—"}</td>
                      <td>${p.cliente ? `${p.cliente.nombre} ${p.cliente.apellido}` : "Sin asignar"}</td>
                      <td>${p.estadoSalud || "Sin registrar"}</td>
                    </tr>
                  `,
                    )
                    .join("")}
                </tbody>
              </table>
            </body>
          </html>
        `;
        printWindow.document.write(htmlContent);
        printWindow.document.close();
        printWindow.print();
      }
      setIsMenuOpen(false);
    }
  };

  return (
    <div className="relative">
      <button
        className="rounded-2xl border border-primary px-4 py-2 text-sm font-semibold text-primary transition-base hover:bg-primary hover:text-white"
        onClick={() => setIsMenuOpen(!isMenuOpen)}
      >
        Exportar lista
      </button>
      {isMenuOpen && (
        <>
          <div className="fixed inset-0 z-40" onClick={() => setIsMenuOpen(false)} />
          <div className="absolute right-0 top-full z-50 mt-2 w-48 rounded-2xl border border-gray-200 bg-white shadow-soft">
            <button
              onClick={exportarCSV}
              className="w-full rounded-t-2xl px-4 py-3 text-left text-sm font-medium text-gray-700 transition-base hover:bg-gray-50"
            >
              📊 Exportar CSV
            </button>
            <button
              onClick={exportarPDF}
              className="w-full rounded-b-2xl px-4 py-3 text-left text-sm font-medium text-gray-700 transition-base hover:bg-gray-50"
            >
              📄 Exportar PDF
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default VeterinarianPatientsPage;

