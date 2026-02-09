export interface AuthUser {
  id: string;
  clinicId: string;
  name: string;
  email: string;
  role: 'ADMIN' | 'USER' | string;
}

export interface LoginResponse {
  accessToken: string;
  clinicId: string;
  user: AuthUser;
}
