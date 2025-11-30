// Loader a pantalla completa utilizado durante la carga de módulos o datos críticos.
export const FullscreenLoader = () => {
  return (
    <div className="flex h-[60vh] items-center justify-center">
      <div className="flex flex-col items-center gap-3 text-secondary">
        <span className="h-12 w-12 animate-spin rounded-full border-4 border-primary/30 border-t-primary" />
        <p className="text-sm font-medium">Cargando información...</p>
      </div>
    </div>
  );
};


