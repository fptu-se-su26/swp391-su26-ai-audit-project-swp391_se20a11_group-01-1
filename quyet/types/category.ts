export interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  imageUrl?: string;
}

export interface CategoryRequest {
  name: string;
  description?: string;
  imageUrl?: string;
}
