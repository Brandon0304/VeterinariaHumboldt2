import { useState } from 'react';

interface UseConfirmDialogOptions {
  onConfirm: () => void | Promise<void>;
  title?: string;
  message?: string;
  variant?: 'danger' | 'warning' | 'info';
  requireConfirmation?: boolean;
  confirmationText?: string;
}

export const useConfirmDialog = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [options, setOptions] = useState<UseConfirmDialogOptions>({
    onConfirm: () => {},
    title: 'Confirmar acción',
    message: '¿Estás seguro de que deseas continuar?',
    variant: 'danger',
  });

  const openDialog = (dialogOptions: UseConfirmDialogOptions) => {
    setOptions({
      ...options,
      ...dialogOptions,
    });
    setIsOpen(true);
  };

  const closeDialog = () => {
    setIsOpen(false);
  };

  const handleConfirm = async () => {
    await options.onConfirm();
    closeDialog();
  };

  return {
    isOpen,
    openDialog,
    closeDialog,
    handleConfirm,
    options,
  };
};
