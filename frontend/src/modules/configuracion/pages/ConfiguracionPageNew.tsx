import { useState } from 'react';
import InformacionClinicaTab from '../components/tabs/InformacionClinicaTab';
import PermisosTab from '../components/tabs/PermisosTab';
import ServiciosTab from '../components/tabs/ServiciosTab';
import HorariosTab from '../components/tabs/HorariosTab';
import AuditoriaTab from '../components/tabs/AuditoriaTab';
import RespaldosConfigTab from '../components/tabs/RespaldosConfigTab';
import { 
  ClinicaIcon, 
  PermisosIcon, 
  ServiciosIcon, 
  HorariosIcon, 
  AuditoriaIcon, 
  RespaldosIcon 
} from '../../../shared/components/icons/ConfigIcons';

type Tab = 'clinica' | 'permisos' | 'servicios' | 'horarios' | 'auditoria' | 'respaldos';

const TABS: { id: Tab; label: string; Icon: React.ComponentType<any> }[] = [
  { id: 'clinica', label: 'Información Clínica', Icon: ClinicaIcon },
  { id: 'permisos', label: 'Permisos y Roles', Icon: PermisosIcon },
  { id: 'servicios', label: 'Servicios', Icon: ServiciosIcon },
  { id: 'horarios', label: 'Horarios', Icon: HorariosIcon },
  { id: 'auditoria', label: 'Auditoría', Icon: AuditoriaIcon },
  { id: 'respaldos', label: 'Respaldos y Config', Icon: RespaldosIcon }
];

export default function ConfiguracionPage() {
  const [activeTab, setActiveTab] = useState<Tab>('clinica');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Configuración del Sistema</h1>
        <p className="mt-2 text-gray-600">Gestiona la configuración completa de la clínica veterinaria</p>
      </div>

      <div className="bg-white rounded-lg shadow-md mb-6">
        <div className="border-b border-gray-200">
          <nav className="flex -mb-px overflow-x-auto" aria-label="Tabs">
            {TABS.map((tab) => {
              const Icon = tab.Icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`whitespace-nowrap py-4 px-6 border-b-2 font-medium text-sm flex items-center gap-3 transition-all duration-200 ${
                    activeTab === tab.id
                      ? 'border-primary text-primary bg-primary/5'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 hover:bg-gray-50'
                  }`}
                >
                  <Icon size={20} className={activeTab === tab.id ? 'text-primary' : 'text-gray-400'} />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </nav>
        </div>
      </div>

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
