import dayjs from "dayjs";
import toast from "react-hot-toast";

import type { ApiRegistroMedicoResponse } from "../../shared/types/backend";

interface RegistroMedicoDetailModalProps {
  readonly isOpen: boolean;
  readonly registro: ApiRegistroMedicoResponse | null;
  readonly onClose: () => void;
}

export const RegistroMedicoDetailModal = ({ isOpen, registro, onClose }: RegistroMedicoDetailModalProps) => {
  if (!isOpen || !registro) return null;

  const fecha = dayjs(registro.fecha);

  const handleImprimir = () => {
    const ventanaImpresion = window.open("", "_blank");
    if (!ventanaImpresion) {
      toast.error("Por favor, permite las ventanas emergentes para imprimir");
      return;
    }

    const contenido = `
      <!DOCTYPE html>
      <html>
        <head>
          <title>Registro Médico - ${fecha.format("DD/MM/YYYY")}</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 20px; }
            h1 { color: #333; border-bottom: 2px solid #4F46E5; padding-bottom: 10px; }
            .section { margin: 20px 0; }
            .label { font-weight: bold; color: #666; }
            .value { margin-left: 10px; }
            .signos-vitales { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin: 10px 0; }
            .signo-item { padding: 8px; background: #f5f5f5; border-radius: 4px; }
            table { width: 100%; border-collapse: collapse; margin: 10px 0; }
            th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }
            th { background-color: #4F46E5; color: white; }
            @media print { button { display: none; } }
          </style>
        </head>
        <body>
          <h1>Registro Médico</h1>
          
          <div class="section">
            <p><span class="label">Fecha:</span> <span class="value">${fecha.format("DD/MM/YYYY HH:mm")}</span></p>
            <p><span class="label">Motivo:</span> <span class="value">${registro.motivo || "No especificado"}</span></p>
            ${registro.veterinario ? `<p><span class="label">Veterinario:</span> <span class="value">${registro.veterinario.nombre} ${registro.veterinario.apellido}${registro.veterinario.especialidad ? ` - ${registro.veterinario.especialidad}` : ""}</span></p>` : ""}
          </div>

          ${registro.diagnostico ? `
          <div class="section">
            <h2>Diagnóstico</h2>
            <p>${registro.diagnostico}</p>
          </div>
          ` : ""}

          ${registro.tratamiento ? `
          <div class="section">
            <h2>Tratamiento</h2>
            <p>${registro.tratamiento}</p>
          </div>
          ` : ""}

          ${registro.signosVitales && Object.keys(registro.signosVitales).length > 0 ? `
          <div class="section">
            <h2>Signos Vitales</h2>
            <div class="signos-vitales">
              ${Object.entries(registro.signosVitales).map(([key, value]) => `
                <div class="signo-item">
                  <span class="label">${key}:</span> <span class="value">${value}</span>
                </div>
              `).join("")}
            </div>
          </div>
          ` : ""}

          ${registro.insumosUsados && registro.insumosUsados.length > 0 ? `
          <div class="section">
            <h2>Insumos Utilizados</h2>
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th>Precio Unitario</th>
                </tr>
              </thead>
              <tbody>
                ${registro.insumosUsados.map((insumo: any) => `
                  <tr>
                    <td>${insumo.nombre || insumo.productoId || "N/A"}</td>
                    <td>${insumo.cantidad || "N/A"}</td>
                    <td>${insumo.precioUnitario ? `$${insumo.precioUnitario}` : "N/A"}</td>
                  </tr>
                `).join("")}
              </tbody>
            </table>
          </div>
          ` : ""}

          ${registro.archivos && registro.archivos.length > 0 ? `
          <div class="section">
            <h2>Archivos Adjuntos</h2>
            <ul>
              ${registro.archivos.map((archivo: string) => `<li>${archivo}</li>`).join("")}
            </ul>
          </div>
          ` : ""}

          <button onclick="window.print()" style="margin-top: 20px; padding: 10px 20px; background: #4F46E5; color: white; border: none; border-radius: 4px; cursor: pointer;">Imprimir</button>
        </body>
      </html>
    `;

    ventanaImpresion.document.write(contenido);
    ventanaImpresion.document.close();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-3xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white shadow-xl">
        <div className="sticky top-0 border-b border-gray-200 bg-white px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-gray-900">Detalle del Registro Médico</h2>
              <p className="mt-1 text-sm text-gray-500">{fecha.format("dddd, D [de] MMMM YYYY • HH:mm")}</p>
            </div>
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          <div className="space-y-6">
            {/* Información básica */}
            <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
              <h3 className="mb-4 text-lg font-semibold text-gray-900">Información General</h3>
              <div className="grid gap-4 sm:grid-cols-2">
                <div>
                  <p className="text-xs font-medium text-gray-500">Fecha y Hora</p>
                  <p className="mt-1 text-sm font-semibold text-gray-900">{fecha.format("DD/MM/YYYY HH:mm")}</p>
                </div>
                {registro.veterinario && (
                  <div>
                    <p className="text-xs font-medium text-gray-500">Veterinario</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">
                      {registro.veterinario.nombre} {registro.veterinario.apellido}
                    </p>
                    {registro.veterinario.especialidad && (
                      <p className="mt-0.5 text-xs text-gray-500">{registro.veterinario.especialidad}</p>
                    )}
                  </div>
                )}
                <div className="sm:col-span-2">
                  <p className="text-xs font-medium text-gray-500">Motivo de Consulta</p>
                  <p className="mt-1 text-sm font-semibold text-gray-900">{registro.motivo || "No especificado"}</p>
                </div>
              </div>
            </section>

            {/* Diagnóstico */}
            {registro.diagnostico && (
              <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-4 text-lg font-semibold text-gray-900">Diagnóstico</h3>
                <p className="text-sm text-gray-900 whitespace-pre-wrap">{registro.diagnostico}</p>
              </section>
            )}

            {/* Tratamiento */}
            {registro.tratamiento && (
              <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-4 text-lg font-semibold text-gray-900">Tratamiento</h3>
                <p className="text-sm text-gray-900 whitespace-pre-wrap">{registro.tratamiento}</p>
              </section>
            )}

            {/* Signos Vitales */}
            {registro.signosVitales && Object.keys(registro.signosVitales).length > 0 && (
              <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-4 text-lg font-semibold text-gray-900">Signos Vitales</h3>
                <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {Object.entries(registro.signosVitales).map(([key, value]) => (
                    <div key={key} className="rounded-lg bg-white p-3">
                      <p className="text-xs font-medium text-gray-500">{key}</p>
                      <p className="mt-1 text-sm font-semibold text-gray-900">{String(value)}</p>
                    </div>
                  ))}
                </div>
              </section>
            )}

            {/* Insumos Utilizados */}
            {registro.insumosUsados && registro.insumosUsados.length > 0 && (
              <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-4 text-lg font-semibold text-gray-900">Insumos Utilizados</h3>
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-gray-300">
                        <th className="px-3 py-2 text-left font-semibold text-gray-700">Producto</th>
                        <th className="px-3 py-2 text-left font-semibold text-gray-700">Cantidad</th>
                        <th className="px-3 py-2 text-left font-semibold text-gray-700">Precio Unitario</th>
                      </tr>
                    </thead>
                    <tbody>
                      {registro.insumosUsados.map((insumo: any, index: number) => (
                        <tr key={index} className="border-b border-gray-200">
                          <td className="px-3 py-2">{insumo.nombre || insumo.productoId || "N/A"}</td>
                          <td className="px-3 py-2">{insumo.cantidad || "N/A"}</td>
                          <td className="px-3 py-2">
                            {insumo.precioUnitario ? `$${parseFloat(insumo.precioUnitario).toLocaleString()}` : "N/A"}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>
            )}

            {/* Archivos Adjuntos */}
            {registro.archivos && registro.archivos.length > 0 && (
              <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-4 text-lg font-semibold text-gray-900">Archivos Adjuntos</h3>
                <div className="space-y-2">
                  {registro.archivos.map((archivo: string, index: number) => (
                    <div key={index} className="flex items-center gap-2 rounded-lg bg-white p-2">
                      <span className="text-sm text-gray-700">📎 {archivo}</span>
                    </div>
                  ))}
                </div>
              </section>
            )}

            {/* Acciones */}
            <div className="flex gap-3 border-t border-gray-200 pt-4">
              <button
                onClick={handleImprimir}
                className="flex-1 rounded-lg border border-primary bg-white px-4 py-2 text-sm font-medium text-primary transition-all hover:bg-primary hover:text-white"
              >
                Imprimir
              </button>
              <button
                onClick={onClose}
                className="flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

