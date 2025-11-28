import { useQuery } from "@tanstack/react-query";

import { authStore } from "../../../shared/state/authStore";

import { CitasRepository } from "../services/CitasRepository";

export const useCitasVeterinario = () => {
  const user = authStore((state) => state.user);

  return useQuery({
    queryKey: ["citas-veterinario", user?.id],
    enabled: Boolean(user?.id),
    queryFn: async () => {
      if (!user) {
        throw new Error("Usuario no autenticado");
      }
      return CitasRepository.getByVeterinario(user.id);
    },
  });
};


