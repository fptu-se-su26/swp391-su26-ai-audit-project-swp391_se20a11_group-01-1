import type { CategoryResponse } from './category';

export interface FoodResponse {
  id: number;
  name: string;
  price: number;
  imageUrl?: string;
  isAvailable: boolean;
  categoryId?: number;
  categoryName?: string;
}

export interface FoodDetailResponse {
  id: number;
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  isAvailable: boolean;
  category: CategoryResponse;
}

export interface FoodSearchParams {
  page?: number;
  size?: number;
  keyword?: string;
  categoryId?: number;
  isAvailable?: boolean;
  sortBy?: string;
  sortDirection?: string;
}
