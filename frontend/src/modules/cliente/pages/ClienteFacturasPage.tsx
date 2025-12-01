import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import { authStore } from "../../../shared/state/authStore";
import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

interface Factura {
  idFactura: number;
  numero: string;
  fecha: string;
  total: number;
  estado: string;
  metodoPago?: string;
  contenido?: any;
}

export const ClienteFacturasPage = () => {
  const user = authStore((state) => state.user);
  const [selectedFactura, setSelectedFactura] = useState<Factura | null>(null);

  const { data: facturas = [], isLoading } = useQuery({
    queryKey: ["facturas-cliente", user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      const client = getApiClient();
      const { data } = await client.get<ApiResponse<Factura[]>>(`/facturas/cliente/${user.id}`);
      return unwrapResponse(data).sort((a, b) => 
        new Date(b.fecha).getTime() - new Date(a.fecha).getTime()
      );
    },
    enabled: !!user?.id,
  });

  const facturasPagadas = facturas.filter((f) => f.estado === "PAGADA");
  const facturasPendientes = facturas.filter((f) => f.estado === "PENDIENTE");
  const totalPagado = facturasPagadas.reduce((sum, f) => sum + f.total, 0);

  return (
    <div className="p-6 space-y-6">
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h1 className="text-2xl font-bold text-gray-900">Mis Facturas</h1>
        <p className="text-gray-600 mt-2">Historial de pagos y facturas</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-green-50 rounded-lg p-6 border border-green-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-green-500 rounded-lg flex items-center justify-center text-white text-xl">
              ✅
            </div>
            <div>
              <p className="text-sm text-green-600 font-medium">Pagadas</p>
              <p className="text-2xl font-bold text-green-900">{facturasPagadas.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-yellow-50 rounded-lg p-6 border border-yellow-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-yellow-500 rounded-lg flex items-center justify-center text-white text-xl">
              ⏳
            </div>
            <div>
              <p className="text-sm text-yellow-600 font-medium">Pendientes</p>
              <p className="text-2xl font-bold text-yellow-900">{facturasPendientes.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-blue-50 rounded-lg p-6 border border-blue-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center text-white text-xl">
              💰
            </div>
            <div>
              <p className="text-sm text-blue-600 font-medium">Total Pagado</p>
              <p className="text-2xl font-bold text-blue-900">
                {totalPagado.toLocaleString("es-CO", {
                  style: "currency",
                  currency: "COP",
                })}
              </p>
            </div>
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="text-gray-500 mt-4">Cargando facturas...</p>
        </div>
      ) : facturas.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="text-6xl mb-4">💳</div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No tienes facturas registradas
          </h3>
          <p className="text-gray-500">
            Tus facturas de servicios veterinarios aparecerán aquí
          </p>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-sm">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Número
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Fecha
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Total
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Estado
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Método de Pago
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {facturas.map((factura) => (
                  <tr key={factura.idFactura} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {factura.numero}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {dayjs(factura.fecha).format("DD/MM/YYYY")}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                      {factura.total.toLocaleString("es-CO", {
                        style: "currency",
                        currency: "COP",
                      })}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`px-2 py-1 text-xs font-medium rounded-full ${
                          factura.estado === "PAGADA"
                            ? "bg-green-100 text-green-800"
                            : factura.estado === "PENDIENTE"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-gray-100 text-gray-800"
                        }`}
                      >
                        {factura.estado}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {factura.metodoPago || "N/A"}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <button
                        onClick={() => setSelectedFactura(factura)}
                        className="text-blue-600 hover:text-blue-800 font-medium"
                      >
                        Ver Detalle
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal de Detalle */}
      {selectedFactura && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b">
              <div className="flex justify-between items-center">
                <h2 className="text-2xl font-semibold text-gray-900">
                  Detalle de Factura
                </h2>
                <button
                  onClick={() => setSelectedFactura(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>
            </div>
            <div className="p-6 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-500">Número de Factura</p>
                  <p className="text-base font-semibold text-gray-900">
                    {selectedFactura.numero}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Fecha</p>
                  <p className="text-base font-semibold text-gray-900">
                    {dayjs(selectedFactura.fecha).format("DD/MM/YYYY")}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Estado</p>
                  <span
                    className={`inline-block px-3 py-1 text-sm font-medium rounded-full ${
                      selectedFactura.estado === "PAGADA"
                        ? "bg-green-100 text-green-800"
                        : "bg-yellow-100 text-yellow-800"
                    }`}
                  >
                    {selectedFactura.estado}
                  </span>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Método de Pago</p>
                  <p className="text-base font-semibold text-gray-900">
                    {selectedFactura.metodoPago || "N/A"}
                  </p>
                </div>
              </div>

              {selectedFactura.contenido && (() => {
                try {
                  const contenido = typeof selectedFactura.contenido === 'string' 
                    ? JSON.parse(selectedFactura.contenido) 
                    : selectedFactura.contenido;
                  const detalles = contenido?.detalles || [];
                  
                  return (
                    <div className="border-t pt-4 mt-4">
                      <p className="text-sm font-semibold text-gray-700 mb-3">Servicios</p>
                      {detalles.length > 0 ? (
                        <div className="space-y-2">
                          {detalles.map((detalle: any, index: number) => (
                            <div key={index} className="flex justify-between items-start border-b border-gray-200 pb-2 last:border-0">
                              <div className="flex-1">
                                <p className="text-sm font-medium text-gray-700">
                                  {detalle.servicio || detalle.descripcion || `Item ${index + 1}`}
                                </p>
                                {detalle.cantidad && (
                                  <p className="text-xs text-gray-500">Cantidad: {detalle.cantidad}</p>
                                )}
                              </div>
                              <div className="text-right ml-4">
                                <p className="text-sm font-semibold text-gray-900">
                                  {parseFloat(detalle.valor || detalle.precio || 0).toLocaleString("es-CO", { 
                                    style: "currency", currency: "COP" 
                                  })}
                                </p>
                              </div>
                            </div>
                          ))}
                        </div>
                      ) : (
                        <p className="text-sm text-gray-500">Sin detalles</p>
                      )}
                    </div>
                  );
                } catch {
                  return null;
                }
              })()}

              <div className="border-t pt-4 mt-4">
                <div className="flex justify-between items-center">
                  <p className="text-lg font-semibold text-gray-900">Total</p>
                  <p className="text-2xl font-bold text-blue-600">
                    {selectedFactura.total.toLocaleString("es-CO", {
                      style: "currency",
                      currency: "COP",
                    })}
                  </p>
                </div>
              </div>
            </div>
            <div className="p-6 bg-gray-50 border-t">
              <button
                onClick={() => setSelectedFactura(null)}
                className="w-full bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition-colors"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
