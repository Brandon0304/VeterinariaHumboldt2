// Layout principal que replica la estructura del prototipo:
// Sidebar fijo a la izquierda y contenido principal con barra superior.
import { ReactNode, useState } from "react";

import { Sidebar } from "../components/navigation/Sidebar";
import { Topbar } from "../components/navigation/Topbar";

interface DashboardLayoutProps {
  readonly children: ReactNode;
}

export const DashboardLayout = ({ children }: DashboardLayoutProps) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex h-screen flex-col overflow-hidden lg:flex-row">
        {/* Sidebar Desktop - hidden on mobile */}
        <div className="hidden lg:block">
          <Sidebar />
        </div>

        {/* Mobile Sidebar Overlay */}
        {isMobileMenuOpen && (
          <>
            {/* Backdrop */}
            <div
              className="fixed inset-0 z-40 bg-black/60 backdrop-blur-sm transition-opacity lg:hidden"
              onClick={() => setIsMobileMenuOpen(false)}
              aria-hidden="true"
            />
            {/* Sidebar Mobile */}
            <div className="fixed inset-y-0 left-0 z-50 w-72 transform transition-transform duration-300 ease-in-out lg:hidden">
              <Sidebar onClose={() => setIsMobileMenuOpen(false)} />
            </div>
          </>
        )}

        <main className="flex flex-1 flex-col overflow-hidden">
          <Topbar onMenuClick={() => setIsMobileMenuOpen(true)} />
          <section className="flex-1 space-y-6 overflow-y-auto bg-gray-50 px-4 py-6 sm:px-6 lg:px-8 xl:px-10">
            {children}
          </section>
        </main>
      </div>
    </div>
  );
};


