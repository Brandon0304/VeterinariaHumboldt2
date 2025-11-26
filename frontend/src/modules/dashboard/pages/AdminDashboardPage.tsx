import { useAdminDashboard } from '../hooks/useAdminDashboard';
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { useState } from 'react';

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899', '#14B8A6', '#F97316'];

export const AdminDashboardPage = () => {
  const { data: dashboard, isLoading, error } = useAdminDashboard();
  const [activeTab, setActiveTab] = useState<'general' | 'financiero' | 'clinico' | 'inventario'>('general');

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md">
          <h3 className="text-red-800 font-semibold mb-2">Error al cargar el dashboard</h3>
          <p className="text-red-600 text-sm">{error.message}</p>
        </div>
      </div>
    );
  }

  if (!dashboard) return null;

  const formatCurrency = (value: number) => 
    new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(value);

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Dashboard del Administrador</h1>
          <p className="text-gray-600 mt-2">Vista general de toda la operación de la clínica veterinaria</p>
        </div>

        {/* KPI Cards - Resumen General */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Pacientes</p>
                <p className="text-2xl font-bold text-gray-900">{dashboard.resumenGeneral.totalPacientes}</p>
              </div>
              <div className="bg-blue-100 p-3 rounded-full">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Clientes</p>
                <p className="text-2xl font-bold text-gray-900">{dashboard.resumenGeneral.totalClientes}</p>
              </div>
              <div className="bg-green-100 p-3 rounded-full">
                <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Citas Total</p>
                <p className="text-2xl font-bold text-gray-900">{dashboard.resumenGeneral.totalCitas}</p>
              </div>
              <div className="bg-purple-100 p-3 rounded-full">
                <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Citas Pendientes</p>
                <p className="text-2xl font-bold text-orange-600">{dashboard.resumenGeneral.citasPendientes}</p>
              </div>
              <div className="bg-orange-100 p-3 rounded-full">
                <svg className="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Facturas</p>
                <p className="text-2xl font-bold text-gray-900">{dashboard.resumenGeneral.totalFacturas}</p>
              </div>
              <div className="bg-indigo-100 p-3 rounded-full">
                <svg className="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Ingresos Total</p>
                <p className="text-xl font-bold text-green-600">{formatCurrency(dashboard.resumenGeneral.ingresosTotales)}</p>
              </div>
              <div className="bg-green-100 p-3 rounded-full">
                <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200 mb-6">
          <nav className="-mb-px flex space-x-8">
            {['general', 'financiero', 'clinico', 'inventario'].map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab as typeof activeTab)}
                className={`py-4 px-1 border-b-2 font-medium text-sm capitalize ${
                  activeTab === tab
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                {tab}
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        {activeTab === 'general' && (
          <div className="space-y-6">
            {/* Gráficas de Ingresos y Citas */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Ingresos por Mes */}
              <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold mb-4">Ingresos por Mes (Año Actual)</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={dashboard.graficos.ingresosPorMes}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="mes" />
                    <YAxis />
                    <Tooltip formatter={(value) => formatCurrency(Number(value))} />
                    <Legend />
                    <Bar dataKey="valor" fill="#3B82F6" name="Ingresos" />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              {/* Citas por Mes */}
              <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold mb-4">Citas por Mes (Año Actual)</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={dashboard.graficos.citasPorMes}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="mes" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="cantidad" stroke="#10B981" strokeWidth={2} name="Citas" />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>

            {/* Distribución de Servicios y Tendencia Clientes */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Distribución de Servicios */}
              <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold mb-4">Distribución de Servicios</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={dashboard.graficos.distribucionServicios}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ tipo, porcentaje }) => `${tipo}: ${porcentaje.toFixed(1)}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="cantidad"
                    >
                      {dashboard.graficos.distribucionServicios.map((_, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              {/* Tendencia Clientes */}
              <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold mb-4">Tendencia de Clientes (Últimos 6 Meses)</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={dashboard.graficos.tendenciaClientes}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="periodo" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="nuevos" fill="#10B981" name="Nuevos Clientes" />
                    <Bar dataKey="activos" fill="#3B82F6" name="Clientes Activos" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'financiero' && (
          <div className="space-y-6">
            {/* Métricas Financieras */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Ingresos Mes Actual</p>
                <p className="text-2xl font-bold text-green-600">{formatCurrency(dashboard.finanzas.ingresosMesActual)}</p>
                <p className="text-xs text-gray-500 mt-1">{dashboard.finanzas.porcentajeCrecimiento.toFixed(2)}% vs mes anterior</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Ingresos Mes Anterior</p>
                <p className="text-2xl font-bold text-gray-700">{formatCurrency(dashboard.finanzas.ingresosMesAnterior)}</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Facturas Pendientes</p>
                <p className="text-2xl font-bold text-orange-600">{dashboard.finanzas.facturasPendientes}</p>
                <p className="text-xs text-gray-500 mt-1">{formatCurrency(dashboard.finanzas.montoFacturasPendientes)}</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Promedio por Cita</p>
                <p className="text-2xl font-bold text-blue-600">{formatCurrency(dashboard.finanzas.promedioIngresoPorCita)}</p>
              </div>
            </div>

            {/* Gráfico de Ingresos Detallado */}
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-lg font-semibold mb-4">Detalle de Ingresos Mensuales</h3>
              <ResponsiveContainer width="100%" height={400}>
                <BarChart data={dashboard.graficos.ingresosPorMes}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="mes" />
                  <YAxis />
                  <Tooltip 
                    formatter={(value, name) => {
                      if (name === 'valor') return formatCurrency(Number(value));
                      return value;
                    }}
                  />
                  <Legend />
                  <Bar dataKey="valor" fill="#3B82F6" name="Ingresos (COP)" />
                  <Bar dataKey="cantidad" fill="#10B981" name="Cantidad Facturas" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {activeTab === 'clinico' && (
          <div className="space-y-6">
            {/* Métricas de Pacientes */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Pacientes Activos</p>
                <p className="text-2xl font-bold text-blue-600">{dashboard.pacientes.pacientesActivos}</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Nuevos Este Mes</p>
                <p className="text-2xl font-bold text-green-600">{dashboard.pacientes.pacientesNuevosEsteMes}</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Citas Realizadas</p>
                <p className="text-2xl font-bold text-purple-600">{dashboard.pacientes.citasRealizadasEsteMes}</p>
                <p className="text-xs text-gray-500 mt-1">Este mes</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Citas Canceladas</p>
                <p className="text-2xl font-bold text-red-600">{dashboard.pacientes.citasCanceladasEsteMes}</p>
                <p className="text-xs text-gray-500 mt-1">Este mes</p>
              </div>
            </div>

            {/* Tareas Pendientes */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold mb-4">Vacunaciones Pendientes</h3>
                <div className="flex items-center justify-center h-32">
                  <div className="text-center">
                    <p className="text-5xl font-bold text-blue-600">{dashboard.pacientes.vacunacionesPendientes}</p>
                    <p className="text-gray-600 mt-2">Próximas dosis programadas</p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold mb-4">Desparasitaciones Pendientes</h3>
                <div className="flex items-center justify-center h-32">
                  <div className="text-center">
                    <p className="text-5xl font-bold text-green-600">{dashboard.pacientes.desparasitacionesPendientes}</p>
                    <p className="text-gray-600 mt-2">Próximas dosis programadas</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Rendimiento del Personal */}
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-lg font-semibold mb-4">Rendimiento del Personal</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="text-center p-4 bg-blue-50 rounded-lg">
                  <p className="text-3xl font-bold text-blue-600">{dashboard.personal.totalVeterinarios}</p>
                  <p className="text-gray-600 mt-1">Veterinarios</p>
                </div>
                <div className="text-center p-4 bg-green-50 rounded-lg">
                  <p className="text-3xl font-bold text-green-600">{dashboard.personal.totalSecretarios}</p>
                  <p className="text-gray-600 mt-1">Secretarios</p>
                </div>
                <div className="text-center p-4 bg-purple-50 rounded-lg">
                  <p className="text-3xl font-bold text-purple-600">
                    {Object.values(dashboard.personal.citasPorVeterinario).reduce((a, b) => a + b, 0)}
                  </p>
                  <p className="text-gray-600 mt-1">Citas Atendidas</p>
                </div>
              </div>

              {/* Citas por Veterinario */}
              <div className="space-y-2">
                <h4 className="font-medium text-gray-700 mb-3">Distribución de Citas por Veterinario</h4>
                {Object.entries(dashboard.personal.citasPorVeterinario).map(([nombre, cantidad]) => {
                  const total = Object.values(dashboard.personal.citasPorVeterinario).reduce((a, b) => a + b, 0);
                  const porcentaje = total > 0 ? (cantidad / total) * 100 : 0;
                  
                  return (
                    <div key={nombre} className="flex items-center gap-3">
                      <div className="w-48 text-sm text-gray-700">{nombre}</div>
                      <div className="flex-1 bg-gray-200 rounded-full h-4">
                        <div 
                          className="bg-blue-600 h-4 rounded-full transition-all duration-300"
                          style={{ width: `${porcentaje}%` }}
                        ></div>
                      </div>
                      <div className="w-20 text-right text-sm font-medium">{cantidad} citas</div>
                      <div className="w-16 text-right text-xs text-gray-500">{porcentaje.toFixed(1)}%</div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'inventario' && (
          <div className="space-y-6">
            {/* Métricas de Inventario */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Total Productos</p>
                <p className="text-2xl font-bold text-blue-600">{dashboard.inventario.totalProductos}</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Stock Bajo</p>
                <p className="text-2xl font-bold text-orange-600">{dashboard.inventario.productosStockBajo}</p>
                <p className="text-xs text-gray-500 mt-1">Requieren reposición</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Productos Agotados</p>
                <p className="text-2xl font-bold text-red-600">{dashboard.inventario.productosAgotados}</p>
                <p className="text-xs text-gray-500 mt-1">Sin stock</p>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <p className="text-sm text-gray-600 mb-2">Valor Total Inventario</p>
                <p className="text-xl font-bold text-green-600">{formatCurrency(dashboard.inventario.valorTotalInventario)}</p>
              </div>
            </div>

            {/* Estado Visual del Inventario */}
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-lg font-semibold mb-6">Estado del Inventario</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="text-center">
                  <div className="relative inline-flex items-center justify-center w-32 h-32 mb-4">
                    <svg className="w-32 h-32 transform -rotate-90">
                      <circle cx="64" cy="64" r="56" fill="none" stroke="#E5E7EB" strokeWidth="8" />
                      <circle 
                        cx="64" 
                        cy="64" 
                        r="56" 
                        fill="none" 
                        stroke="#10B981" 
                        strokeWidth="8"
                        strokeDasharray={`${(dashboard.inventario.totalProductos - dashboard.inventario.productosStockBajo - dashboard.inventario.productosAgotados) / dashboard.inventario.totalProductos * 351.86} 351.86`}
                      />
                    </svg>
                    <div className="absolute">
                      <p className="text-2xl font-bold">
                        {dashboard.inventario.totalProductos - dashboard.inventario.productosStockBajo - dashboard.inventario.productosAgotados}
                      </p>
                    </div>
                  </div>
                  <p className="text-gray-700 font-medium">Stock Normal</p>
                  <p className="text-sm text-gray-500">
                    {((dashboard.inventario.totalProductos - dashboard.inventario.productosStockBajo - dashboard.inventario.productosAgotados) / dashboard.inventario.totalProductos * 100).toFixed(1)}%
                  </p>
                </div>

                <div className="text-center">
                  <div className="relative inline-flex items-center justify-center w-32 h-32 mb-4">
                    <svg className="w-32 h-32 transform -rotate-90">
                      <circle cx="64" cy="64" r="56" fill="none" stroke="#E5E7EB" strokeWidth="8" />
                      <circle 
                        cx="64" 
                        cy="64" 
                        r="56" 
                        fill="none" 
                        stroke="#F59E0B" 
                        strokeWidth="8"
                        strokeDasharray={`${dashboard.inventario.productosStockBajo / dashboard.inventario.totalProductos * 351.86} 351.86`}
                      />
                    </svg>
                    <div className="absolute">
                      <p className="text-2xl font-bold">{dashboard.inventario.productosStockBajo}</p>
                    </div>
                  </div>
                  <p className="text-gray-700 font-medium">Stock Bajo</p>
                  <p className="text-sm text-gray-500">
                    {(dashboard.inventario.productosStockBajo / dashboard.inventario.totalProductos * 100).toFixed(1)}%
                  </p>
                </div>

                <div className="text-center">
                  <div className="relative inline-flex items-center justify-center w-32 h-32 mb-4">
                    <svg className="w-32 h-32 transform -rotate-90">
                      <circle cx="64" cy="64" r="56" fill="none" stroke="#E5E7EB" strokeWidth="8" />
                      <circle 
                        cx="64" 
                        cy="64" 
                        r="56" 
                        fill="none" 
                        stroke="#EF4444" 
                        strokeWidth="8"
                        strokeDasharray={`${dashboard.inventario.productosAgotados / dashboard.inventario.totalProductos * 351.86} 351.86`}
                      />
                    </svg>
                    <div className="absolute">
                      <p className="text-2xl font-bold">{dashboard.inventario.productosAgotados}</p>
                    </div>
                  </div>
                  <p className="text-gray-700 font-medium">Agotados</p>
                  <p className="text-sm text-gray-500">
                    {(dashboard.inventario.productosAgotados / dashboard.inventario.totalProductos * 100).toFixed(1)}%
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
