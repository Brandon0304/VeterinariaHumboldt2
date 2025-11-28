// Layout principal que replica la estructura del prototipo:
// Sidebar fijo a la izquierda y contenido principal con barra superior.
import { ReactNode } from "react";

import { Sidebar } from "../components/navigation/Sidebar";
import { Topbar } from "../components/navigation/Topbar";

interface DashboardLayoutProps {
  readonly children: ReactNode;
}

export const DashboardLayout = ({ children }: DashboardLayoutProps) => {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex h-screen flex-col overflow-hidden lg:flex-row">
        <Sidebar />
        <main className="flex flex-1 flex-col overflow-hidden">
          <Topbar />
          <section className="flex-1 space-y-6 overflow-y-auto bg-gray-50 px-4 py-6 sm:px-6 lg:px-8 xl:px-10">
            {children}
          </section>
        </main>
      </div>
    </div>
  );
};


