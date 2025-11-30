import { ReactNode, useEffect } from "react";

interface ModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly title: string;
  readonly subtitle?: string;
  readonly children: ReactNode;
  readonly maxWidth?: "sm" | "md" | "lg" | "xl" | "2xl" | "4xl" | "full";
}

export const Modal = ({ 
  isOpen, 
  onClose, 
  title, 
  subtitle, 
  children, 
  maxWidth = "2xl" 
}: ModalProps) => {
  // Close on ESC key
  useEffect(() => {
    if (!isOpen) return;

    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, [isOpen, onClose]);

  // Prevent body scroll when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }

    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const maxWidthClasses = {
    sm: "max-w-sm",
    md: "max-w-md",
    lg: "max-w-lg",
    xl: "max-w-xl",
    "2xl": "max-w-2xl",
    "4xl": "max-w-4xl",
    full: "max-w-full mx-4",
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 overflow-y-auto"
      onClick={onClose}
    >
      <div 
        className={`w-full ${maxWidthClasses[maxWidth]} rounded-2xl bg-white shadow-xl my-8 max-h-[90vh] flex flex-col`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="border-b border-gray-200 px-4 py-4 sm:px-6 flex-shrink-0">
          <div className="flex items-start justify-between">
            <div className="flex-1 min-w-0">
              <h2 className="text-lg font-semibold text-gray-900 sm:text-xl truncate">{title}</h2>
              {subtitle && <p className="mt-1 text-xs text-gray-500 sm:text-sm">{subtitle}</p>}
            </div>
            <button
              onClick={onClose}
              className="ml-4 flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-lg text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
              aria-label="Cerrar"
            >
              ✕
            </button>
          </div>
        </div>

        {/* Content - Scrollable */}
        <div className="overflow-y-auto flex-1 p-4 sm:p-6">
          {children}
        </div>
      </div>
    </div>
  );
};
