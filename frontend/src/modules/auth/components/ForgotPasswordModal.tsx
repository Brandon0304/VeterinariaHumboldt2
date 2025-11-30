import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

import { AuthRepository } from "../services/AuthRepository";

interface ForgotPasswordModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onTokenReceived?: (token: string) => void;
}

interface ForgotPasswordFormData {
  readonly emailOrUsername: string;
}

export const ForgotPasswordModal = ({ isOpen, onClose, onTokenReceived }: ForgotPasswordModalProps) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [tokenReceived, setTokenReceived] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<ForgotPasswordFormData>({
    defaultValues: {
      emailOrUsername: "",
    },
  });

  const onSubmit = async (data: ForgotPasswordFormData) => {
    if (!data.emailOrUsername.trim()) {
      toast.error("Por favor, ingresa tu correo electrónico o nombre de usuario");
      return;
    }

    setIsSubmitting(true);
    try {
      const token = await AuthRepository.forgotPassword(data.emailOrUsername.trim());
      setTokenReceived(token);
      toast.success("Token de recuperación generado. Revisa la consola del servidor o el mensaje de respuesta.");
      if (onTokenReceived) {
        onTokenReceived(token);
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || "Error al solicitar recuperación de contraseña";
      toast.error(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    reset();
    setTokenReceived(null);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-md rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">Recuperar Contraseña</h2>
            <button
              onClick={handleClose}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          {!tokenReceived ? (
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div>
                <p className="mb-4 text-sm text-gray-600">
                  Ingresa tu correo electrónico o nombre de usuario. Te enviaremos instrucciones para restablecer tu contraseña.
                </p>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Correo o Usuario <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  placeholder="usuario@ejemplo.com o nombreUsuario"
                  className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("emailOrUsername", {
                    required: "El correo o nombre de usuario es obligatorio",
                  })}
                />
                {errors.emailOrUsername && (
                  <p className="mt-1 text-xs text-danger">{errors.emailOrUsername.message}</p>
                )}
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={handleClose}
                  className="flex-1 rounded-xl border border-gray-300 bg-white px-4 py-2.5 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-1 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark disabled:cursor-not-allowed disabled:bg-primary/60"
                >
                  {isSubmitting ? "Enviando..." : "Enviar Solicitud"}
                </button>
              </div>
            </form>
          ) : (
            <div className="space-y-4">
              <div className="rounded-xl border border-success/20 bg-success/5 p-4">
                <p className="text-sm font-semibold text-success">Token generado exitosamente</p>
                <p className="mt-2 text-xs text-gray-600">
                  En desarrollo, el token se muestra aquí. En producción, se enviaría por correo electrónico.
                </p>
              </div>
              <div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <label className="mb-1 block text-xs font-medium text-gray-500">Token de recuperación:</label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    value={tokenReceived}
                    readOnly
                    className="flex-1 rounded-lg border border-gray-300 bg-white px-3 py-2 text-xs font-mono"
                  />
                  <button
                    onClick={() => {
                      navigator.clipboard.writeText(tokenReceived);
                      toast.success("Token copiado al portapapeles");
                    }}
                    className="rounded-lg border border-primary bg-primary/10 px-3 py-2 text-xs font-semibold text-primary transition-all hover:bg-primary hover:text-white"
                  >
                    Copiar
                  </button>
                </div>
              </div>
              <div className="flex gap-3 pt-4">
                <button
                  onClick={() => {
                    setTokenReceived(null);
                    reset();
                  }}
                  className="flex-1 rounded-xl border border-gray-300 bg-white px-4 py-2.5 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
                >
                  Solicitar otro
                </button>
                <button
                  onClick={handleClose}
                  className="flex-1 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
                >
                  Cerrar
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

