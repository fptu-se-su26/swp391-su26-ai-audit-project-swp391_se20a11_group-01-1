export interface RoleResponse {
  id: number;
  name: string;
  description?: string;
}

export type UserStatus = 'ACTIVE' | 'LOCKED' | 'DISABLED';

export interface UserListResponse {
  id: number;
  username: string;
  email: string;
  isActive: boolean;
  roles: string[];
  fullName: string;
}

export interface UserDetailResponse {
  id: number;
  username: string;
  email: string;
  isActive: boolean;
  createdAt: string;
  roles: RoleResponse[];
  fullName: string;
  phone: string;
  address: string;
}

export interface UpdateUserStatusRequest {
  status: UserStatus;
}
