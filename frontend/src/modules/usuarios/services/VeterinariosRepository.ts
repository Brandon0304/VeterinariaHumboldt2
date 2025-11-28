import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";
import type { ApiUsuarioResponse } from "../../shared/types/backend";

const BASE_PATH = "/usuarios";

export const VeterinariosRepository = {
  getAll: async (): Promise<ApiUsuarioResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiUsuarioResponse[]>>(`${BASE_PATH}/veterinarios`);
    return unwrapResponse(data);
  },
};

