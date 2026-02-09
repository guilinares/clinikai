export interface PageMeta {
  number: number;
  size: number;
  totalItems: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface PagedResponse<T> {
  items: T[];
  page: PageMeta;
}

export interface ClinicKbEntry {
  id: string;
  title: string;
  content: string;
  category: string;      // ex: "FAQ"
  tags: string[];
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateKbRequest {
  title: string;
  content: string;
  category: string;
  tags?: string[];
  enabled?: boolean;
}
