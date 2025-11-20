// DTOs utilizados para comunicarse con el backend en las operaciones de autenticación.

export interface LoginRequestDTO {
  readonly username: string;
  readonly password: string;
}

export interface LoginResponseDTO {
  readonly token: string;
  readonly type: string;
  readonly usuario: {
    readonly idUsuario: number;
    readonly nombre: string;
    readonly apellido: string;
    readonly correo: string;
    readonly rol: string;
  };
}


