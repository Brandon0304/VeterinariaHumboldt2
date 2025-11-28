// Página de gestión de inventario para el secretario
import { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
import dayjs from "dayjs";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { ProductosRepository } from "../../inventario/services/ProductosRepository";
import { MovimientosInventarioRepository } from "../../inventario/services/MovimientosInventarioRepository";
import { CreateProductoModal } from "../../inventario/components/CreateProductoModal";
import { MovimientoInventarioModal } from "../../inventario/components/MovimientoInventarioModal";
import type { ApiProductoResponse } from "../../shared/types/backend";

type TipoFilter = "TODOS" | "MEDICAMENTO" | "INSUMO" | "PRODUCTO";
type MovimientoType = "ENTRADA" | "SALIDA" | null;

export const SecretaryInventarioPage = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isMovimientoModalOpen, setIsMovimientoModalOpen] = useState(false);
  const [isMovimientosModalOpen, setIsMovimientosModalOpen] = useState(false);
  const [selectedProductoParaMovimientos, setSelectedProductoParaMovimientos] = useState<number | null>(null);
  const [movimientoType, setMovimientoType] = useState<MovimientoType>(null);
  const [selectedProductoId, setSelectedProductoId] = useState<number | undefined>(undefined);
  const [search, setSearch] = useState("");
  const [tipoFilter, setTipoFilter] = useState<TipoFilter>("TODOS");
  const [stockBajoFilter, setStockBajoFilter] = useState(false);

  const { data: productos, isLoading } = useQuery({
    queryKey: ["productos"],
    queryFn: ProductosRepository.getAll,
  });

  const { data: productosStockBajo } = useQuery({
    queryKey: ["productos-stock-bajo"],
    queryFn: () => ProductosRepository.obtenerProductosConStockBajo(10),
  });

  const productosFiltrados = useMemo(() => {
    if (!productos) return [];

    let filtered = productos.filter((producto) => {
      const matchesSearch =
        search === "" ||
        producto.nombre.toLowerCase().includes(search.toLowerCase()) ||
        producto.sku.toLowerCase().includes(search.toLowerCase());

      const matchesTipo = tipoFilter === "TODOS" || producto.tipo === tipoFilter;

      const matchesStockBajo = !stockBajoFilter || producto.stock <= 10;

      return matchesSearch && matchesTipo && matchesStockBajo;
    });

    return filtered.sort((a, b) => a.nombre.localeCompare(b.nombre));
  }, [productos, search, tipoFilter, stockBajoFilter]);

  const stats = useMemo(() => {
    if (!productos) return { total: 0, stockBajo: 0, valorTotal: 0 };
    const stockBajo = productos.filter((p) => p.stock <= 10).length;
    const valorTotal = productos.reduce(
      (sum, p) => sum + parseFloat(p.precioUnitario) * p.stock,
      0
    );
    return { total: productos.length, stockBajo, valorTotal };
  }, [productos]);

  const handleAbrirMovimiento = (tipo: "ENTRADA" | "SALIDA", productoId?: number) => {
    setMovimientoType(tipo);
    setSelectedProductoId(productoId);
    setIsMovimientoModalOpen(true);
  };

  const handleVerMovimientos = (productoId: number) => {
    setSelectedProductoParaMovimientos(productoId);
    setIsMovimientosModalOpen(true);
  };

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="w-full space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Gestión de Inventario</h2>
          <p className="text-sm text-gray-500">Administra el inventario de productos y medicamentos</p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => handleAbrirMovimiento("ENTRADA")}
            className="rounded-2xl border border-success bg-success/10 px-4 py-2 text-sm font-semibold text-success transition-base hover:bg-success/20"
          >
            Entrada
          </button>
          <button
            onClick={() => handleAbrirMovimiento("SALIDA")}
            className="rounded-2xl border border-warning bg-warning/10 px-4 py-2 text-sm font-semibold text-warning transition-base hover:bg-warning/20"
          >
            Salida
          </button>
          <button
            onClick={() => setIsCreateModalOpen(true)}
            className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
          >
            Nuevo Producto
          </button>
        </div>
      </header>

      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard title="Total Productos" value={stats.total.toString()} description="En inventario" tone="primary" />
        <StatCard
          title="Stock Bajo"
          value={stats.stockBajo.toString()}
          description="≤ 10 unidades"
          tone={stats.stockBajo > 0 ? "danger" : "success"}
        />
        <StatCard
          title="Valor Total"
          value={stats.valorTotal.toLocaleString("es-CO", { style: "currency", currency: "COP" })}
          description="Inventario"
          tone="info"
        />
      </section>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex flex-wrap items-center gap-4">
          <input
            type="text"
            placeholder="Buscar por nombre o SKU..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 min-w-[200px] rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          />
          <select
            value={tipoFilter}
            onChange={(e) => setTipoFilter(e.target.value as TipoFilter)}
            className="rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          >
            <option value="TODOS">Todos los tipos</option>
            <option value="MEDICAMENTO">Medicamentos</option>
            <option value="INSUMO">Insumos</option>
            <option value="PRODUCTO">Productos</option>
          </select>
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={stockBajoFilter}
              onChange={(e) => setStockBajoFilter(e.target.checked)}
              className="rounded border-gray-300 text-primary focus:ring-primary"
            />
            <span className="text-sm text-gray-600">Solo stock bajo</span>
          </label>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {productosFiltrados.length === 0 ? (
            <div className="col-span-full rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
              No se encontraron productos con los filtros seleccionados.
            </div>
          ) : (
            productosFiltrados.map((producto) => (
              <ProductoCard
                key={producto.id}
                producto={producto}
                onEntrada={() => handleAbrirMovimiento("ENTRADA", producto.id)}
                onSalida={() => handleAbrirMovimiento("SALIDA", producto.id)}
                onVerMovimientos={() => handleVerMovimientos(producto.id)}
              />
            ))
          )}
        </div>
      </section>

      <CreateProductoModal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} />
      {movimientoType && (
        <MovimientoInventarioModal
          isOpen={isMovimientoModalOpen}
          tipo={movimientoType}
          productoId={selectedProductoId}
          onClose={() => {
            setIsMovimientoModalOpen(false);
            setMovimientoType(null);
            setSelectedProductoId(undefined);
          }}
        />
      )}
      {isMovimientosModalOpen && selectedProductoParaMovimientos && (
        <MovimientosProductoModal
          isOpen={isMovimientosModalOpen}
          productoId={selectedProductoParaMovimientos}
          onClose={() => {
            setIsMovimientosModalOpen(false);
            setSelectedProductoParaMovimientos(null);
          }}
        />
      )}
    </div>
  );
};

interface MovimientosProductoModalProps {
  readonly isOpen: boolean;
  readonly productoId: number;
  readonly onClose: () => void;
}

const MovimientosProductoModal = ({ isOpen, productoId, onClose }: MovimientosProductoModalProps) => {
  const { data: movimientos, isLoading } = useQuery({
    queryKey: ["movimientos-producto", productoId],
    queryFn: () => MovimientosInventarioRepository.obtenerPorProducto(productoId),
    enabled: isOpen,
  });

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-secondary">Movimientos de Inventario</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            ✕
          </button>
        </div>
        {isLoading ? (
          <FullscreenLoader />
        ) : movimientos && movimientos.length > 0 ? (
          <div className="space-y-3">
            {movimientos.map((movimiento) => (
              <div
                key={movimiento.id}
                className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4"
              >
                <div className="flex-1 min-w-[200px]">
                  <div className="flex items-center gap-2">
                    <span
                      className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                        movimiento.tipoMovimiento === "ENTRADA"
                          ? "bg-success/10 text-success"
                          : "bg-warning/10 text-warning"
                      }`}
                    >
                      {movimiento.tipoMovimiento}
                    </span>
                    <span className="text-sm font-semibold text-secondary">
                      Cantidad: {movimiento.cantidad} {movimiento.producto?.nombre || ""}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-gray-500">
                    Fecha: {dayjs(movimiento.fecha).format("DD/MM/YYYY HH:mm")}
                  </p>
                  {movimiento.referencia && (
                    <p className="mt-1 text-xs text-gray-500">Referencia: {movimiento.referencia}</p>
                  )}
                  {movimiento.usuario && (
                    <p className="mt-1 text-xs text-gray-500">
                      Usuario: {movimiento.usuario.nombre} {movimiento.usuario.apellido}
                    </p>
                  )}
                  <p className="mt-1 text-xs font-semibold text-secondary">
                    Stock resultante: {movimiento.stockResultante}
                  </p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
            No hay movimientos registrados para este producto.
          </div>
        )}
      </div>
    </div>
  );
};

interface StatCardProps {
  readonly title: string;
  readonly value: string;
  readonly description: string;
  readonly tone?: "primary" | "success" | "warning" | "danger" | "info";
}

const StatCard = ({ title, value, description, tone = "primary" }: StatCardProps) => {
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
      <p className="mt-2 text-2xl font-semibold text-secondary">{value}</p>
      <span className={`mt-3 inline-flex rounded-full border px-3 py-1 text-xs font-semibold ${toneClasses[tone]}`}>
        {description}
      </span>
    </article>
  );
};

interface ProductoCardProps {
  readonly producto: ApiProductoResponse;
  readonly onEntrada: () => void;
  readonly onSalida: () => void;
  readonly onVerMovimientos: () => void;
}

const ProductoCard = ({ producto, onEntrada, onSalida, onVerMovimientos }: ProductoCardProps) => {
  const stockTone = producto.stock <= 10 ? "bg-danger/10 text-danger" : producto.stock <= 30 ? "bg-warning/10 text-warning" : "bg-success/10 text-success";
  const precio = parseFloat(producto.precioUnitario);

  return (
    <article className="flex flex-col rounded-2xl border border-gray-100 bg-gray-50 p-4 shadow-sm">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h3 className="text-sm font-semibold text-secondary">{producto.nombre}</h3>
          <p className="mt-1 text-xs text-gray-500">SKU: {producto.sku}</p>
          {producto.tipo && (
            <span className="mt-1 inline-block rounded-full bg-primary/10 px-2 py-0.5 text-xs font-semibold text-primary">
              {producto.tipo}
            </span>
          )}
        </div>
      </div>

      <dl className="mt-3 space-y-1 text-xs">
        <div className="flex justify-between">
          <dt className="font-semibold text-secondary">Stock</dt>
          <dd className={`rounded-full px-2 py-0.5 font-semibold ${stockTone}`}>{producto.stock} {producto.um || "unidades"}</dd>
        </div>
        <div className="flex justify-between">
          <dt className="font-semibold text-secondary">Precio Unit.</dt>
          <dd>{precio.toLocaleString("es-CO", { style: "currency", currency: "COP" })}</dd>
        </div>
        <div className="flex justify-between">
          <dt className="font-semibold text-secondary">Valor Total</dt>
          <dd className="font-semibold text-secondary">
            {(precio * producto.stock).toLocaleString("es-CO", { style: "currency", currency: "COP" })}
          </dd>
        </div>
      </dl>

      <div className="mt-4 flex flex-col gap-2">
        <div className="flex gap-2">
          <button
            onClick={onEntrada}
            className="flex-1 rounded-xl border border-success bg-success/10 px-3 py-2 text-xs font-semibold text-success transition-base hover:bg-success/20"
          >
            Entrada
          </button>
          <button
            onClick={onSalida}
            disabled={producto.stock === 0}
            className="flex-1 rounded-xl border border-warning bg-warning/10 px-3 py-2 text-xs font-semibold text-warning transition-base hover:bg-warning/20 disabled:opacity-50"
          >
            Salida
          </button>
        </div>
        <button
          onClick={onVerMovimientos}
          className="w-full rounded-xl border border-info bg-info/10 px-3 py-2 text-xs font-semibold text-info transition-base hover:bg-info/20"
        >
          Ver Movimientos
        </button>
      </div>
    </article>
  );
};

