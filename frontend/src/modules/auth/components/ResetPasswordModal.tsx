import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

import { AuthRepository } from "../services/AuthRepository";

interface ResetPasswordModalProps {
  readonly isOpen: boolean;
  readonly token?: string;
  readonly onClose: () => void;
  readonly onSuccess?: () => void;
}

interface ResetPasswordFormData {
  readonly token: string;
  readonly newPassword: string;
  readonly confirmPassword: string;
}

export const ResetPasswordModal = ({ isOpen, token, onClose, onSuccess }: ResetPasswordModalProps) => {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    reset,
  } = useForm<ResetPasswordFormData>({
    defaultValues: {
      token: token || "",
      newPassword: "",
      confirmPassword: "",
    },
  });

  const newPassword = watch("newPassword");

  const onSubmit = async (data: ResetPasswordFormData) => {
    if (data.newPassword !== data.confirmPassword) {
      toast.error("Las contraseñas no coinciden");
      return;
    }

    if (data.newPassword.length < 8) {
      toast.error("La contraseña debe tener al menos 8 caracteres");
      return;
    }

    setIsSubmitting(true);
    try {
      await AuthRepository.resetPassword(data.token.trim(), data.newPassword);
      toast.success("Contraseña restablecida exitosamente. Ahora puedes iniciar sesión.");
      reset();
      if (onSuccess) {
        onSuccess();
      }
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || "Error al restablecer la contraseña";
      toast.error(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-md rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">Restablecer Contraseña</h2>
            <button
              onClick={handleClose}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Token de recuperación <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                placeholder="Pega el token recibido"
                className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm font-mono focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("token", {
                  required: "El token es obligatorio",
                })}
              />
              {errors.token && <p className="mt-1 text-xs text-danger">{errors.token.message}</p>}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Nueva Contraseña <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                placeholder="Mínimo 8 caracteres"
                className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("newPassword", {
                  required: "La nueva contraseña es obligatoria",
                  minLength: {
                    value: 8,
                    message: "La contraseña debe tener al menos 8 caracteres",
                  },
                })}
              />
              {errors.newPassword && <p className="mt-1 text-xs text-danger">{errors.newPassword.message}</p>}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Confirmar Contraseña <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                placeholder="Repite la nueva contraseña"
                className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("confirmPassword", {
                  required: "Por favor, confirma tu contraseña",
                  validate: (value) => value === newPassword || "Las contraseñas no coinciden",
                })}
              />
              {errors.confirmPassword && <p className="mt-1 text-xs text-danger">{errors.confirmPassword.message}</p>}
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
                {isSubmitting ? "Restableciendo..." : "Restablecer Contraseña"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

