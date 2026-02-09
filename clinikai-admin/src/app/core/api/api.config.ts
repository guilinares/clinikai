import { environment } from '../../../environments/environment';

export const API = {
  base: environment.apiBaseUrl,
  auth: {
    login: () => `${environment.apiBaseUrl}/auth/login`,
  },
  kb: {
    list: (clinicId: string) => `${environment.apiBaseUrl}/clinics/${clinicId}/kb`,
    search: (clinicId: string) => `${environment.apiBaseUrl}/clinics/${clinicId}/kb/search`,
    setEnabled: (id: string) => `${environment.apiBaseUrl}/kb/${id}/enabled`,
    byId: (id: string) => `${environment.apiBaseUrl}/kb/${id}`,
  },
};
