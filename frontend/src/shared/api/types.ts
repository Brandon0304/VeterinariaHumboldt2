export interface ApiResponse<T> {
  readonly success: boolean;
  readonly message: string;
  readonly data: T;
  readonly timestamp: string;
}


