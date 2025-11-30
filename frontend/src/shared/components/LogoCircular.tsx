// Logo circular de la Clínica Veterinaria Universitaria Humboldt
import { useState } from "react";

export const LogoCircular = ({ size = 120 }: { size?: number }) => {
  const [imageError, setImageError] = useState(false);

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      {!imageError ? (
        <div className="relative" style={{ width: size, height: size }}>
          <img
            src="/logo-circular.png"
            alt="Clínica Veterinaria Universitaria Humboldt"
            className="h-full w-full object-contain drop-shadow-lg rounded-full"
            style={{ maxWidth: size, maxHeight: size, border: '2px solid rgba(255, 255, 255, 0.3)' }}
            onError={() => setImageError(true)}
          />
        </div>
      ) : (
        <div 
          className="flex h-full w-full items-center justify-center rounded-full bg-gradient-to-br from-secondary to-secondary/80 text-white font-black shadow-2xl"
          style={{ 
            width: size, 
            height: size, 
            border: '2px solid rgba(255, 255, 255, 0.3)',
            fontSize: size * 0.25 
          }}
        >
          <div className="text-center">
            <div className="font-bold uppercase tracking-wider mb-1" style={{ fontSize: size * 0.15 }}>
              CVUH
            </div>
            <div className="font-semibold opacity-80" style={{ fontSize: size * 0.09 }}>
              VETERINARIA
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

