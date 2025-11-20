// Página de bienvenida con diseño minimalista y elegante
// Replica el estilo del template Index.tsx
// import { LogoCircular } from "../../../shared/components/LogoCircular";

const WelcomePage = () => {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="text-center">
        <div className="flex justify-center mb-8">
          <div className="relative">
            <div className="absolute inset-0 rounded-full blur-2xl" style={{ background: "rgba(17,66,100,0.10)" }}></div>
            <div className="relative rounded-full p-2" style={{ border: "2px solid rgba(17,66,100,0.35)" }}>
              <div className="relative rounded-full p-2" style={{ border: "2px solid rgba(17,66,100,0.20)" }}>
                <img
                  src="/LogoClinicaVeterinaria.png"
                  alt="Logo Clínica Veterinaria Universitaria Humboldt"
                  className="h-[140px] w-[140px] rounded-full object-cover bg-white/5"
                />
              </div>
            </div>
          </div>
        </div>
        <h1 className="mb-4 text-4xl font-bold text-secondary">
          Bienvenido a CVUH
        </h1>
        <p className="text-xl text-muted-foreground">
          Sistema de gestión veterinaria integral
        </p>
      </div>
    </div>
  );
};

export default WelcomePage;

