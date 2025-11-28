export const SplashScreen = () => {
  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-white">
      <div className="relative flex flex-col items-center">
        {/* Logo circular con doble anillo y blur, usando imagen del usuario */}
        <div className="relative mb-4">
          <div className="absolute inset-0 rounded-full blur-2xl" style={{ background: "rgba(15,106,123,0.10)" }}></div>
          <div className="relative rounded-full p-2" style={{ border: "2px solid rgba(15,106,123,0.35)" }}>
            <div className="relative rounded-full p-2" style={{ border: "2px solid rgba(15,106,123,0.20)" }}>
              <img
                src="/LogoClinicaVeterinaria.png"
                alt="Logo Clínica Veterinaria Universitaria Humboldt"
                className="h-[96px] w-[96px] rounded-full object-cover bg-white/5"
              />
            </div>
          </div>
        </div>
        <div className="mt-6 h-1 w-40 overflow-hidden rounded-full bg-gray-200">
          <div className="h-full w-1/3 animate-[loading_1.2s_ease-in-out_infinite] rounded-full bg-primary"></div>
        </div>
      </div>
      <style>
        {`
          @keyframes loading {
            0% { transform: translateX(-100%); }
            50% { transform: translateX(100%); }
            100% { transform: translateX(300%); }
          }
        `}
      </style>
    </div>
  );
};


