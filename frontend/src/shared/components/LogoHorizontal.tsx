// Logo horizontal/banner de la Clínica Veterinaria Universitaria Humboldt
export const LogoHorizontal = ({ height = 80 }: { height?: number }) => {
  return (
    <div className="relative w-full" style={{ height }}>
      <img
        src="/logo-horizontal.png"
        alt="Clínica Veterinaria Universitaria Humboldt"
        className="h-full w-full object-cover object-center drop-shadow-md"
        style={{ maxHeight: height }}
        onError={(e) => {
          // Fallback si la imagen no existe
          const target = e.target as HTMLImageElement;
          target.style.display = 'none';
          const fallback = document.createElement('div');
          fallback.className = 'flex h-full w-full items-center justify-center bg-secondary text-white text-lg font-bold';
          fallback.textContent = 'Clínica Veterinaria Universitaria Humboldt';
          target.parentElement?.appendChild(fallback);
        }}
      />
    </div>
  );
};

