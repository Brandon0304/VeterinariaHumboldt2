import { useState } from 'react';
import InformacionClinicaTab from '../components/tabs/InformacionClinicaTab';
import PermisosTab from '../components/tabs/PermisosTab';
import ServiciosTab from '../components/tabs/ServiciosTab';
import HorariosTab from '../components/tabs/HorariosTab';
import AuditoriaTab from '../components/tabs/AuditoriaTab';
import RespaldosConfigTab from '../components/tabs/RespaldosConfigTab';

type Tab = 'clinica' | 'permisos' | 'servicios' | 'horarios' | 'auditoria' | 'respaldos';

const TABS: { id: Tab; label: string; icon: string }[] = [
  { id: 'clinica', label: 'Información Clínica', icon: '🏥' },
  { id: 'permisos', label: 'Permisos y Roles', icon: '🔐' },
  { id: 'servicios', label: 'Servicios', icon: '💉' },
  { id: 'horarios', label: 'Horarios', icon: '🕐' },
  { id: 'auditoria', label: 'Auditoría', icon: '📋' },
  { id: 'respaldos', label: 'Respaldos y Config', icon: '💾' }
];

export default function ConfiguracionPage() {
  const [activeTab, setActiveTab] = useState<Tab>('clinica');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Configuración del Sistema</h1>
        <p className="mt-2 text-gray-600">
          Gestiona la configuración completa de la clínica veterinaria
        </p>
      </div>

      {/* Tabs Navigation */}
      <div className="bg-white rounded-lg shadow-md mb-6">
        <div className="border-b border-gray-200">
          <nav className="flex -mb-px overflow-x-auto" aria-label="Tabs">
            {TABS.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`
                  whitespace-nowrap py-4 px-6 border-b-2 font-medium text-sm flex items-center gap-2
                  transition-colors duration-150
                  ${
                    activeTab === tab.id
                      ? 'border-blue-600 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }
                `}
              >
                <span className="text-lg">{tab.icon}</span>
                <span>{tab.label}</span>
              </button>
            ))}
          </nav>
        </div>
      </div>

      {/* Tab Content */}
      <div className="bg-white rounded-lg shadow-md p-6">
        {activeTab === 'clinica' && <InformacionClinicaTab />}
        {activeTab === 'permisos' && <PermisosTab />}
        {activeTab === 'servicios' && <ServiciosTab />}
        {activeTab === 'horarios' && <HorariosTab />}
        {activeTab === 'auditoria' && <AuditoriaTab />}
        {activeTab === 'respaldos' && <RespaldosConfigTab />}
      </div>
    </div>
  );
}

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

