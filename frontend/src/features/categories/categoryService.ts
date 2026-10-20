import { apiClient } from '../../lib/apiClient';

export interface ComplaintCategoryItem {
  id: number;
  societyId?: number | null;
  name: string;
  description?: string;
  isGlobal: boolean;
  active: boolean;
}

export const fetchCategories = async (societyId?: number): Promise<ComplaintCategoryItem[]> => {
  const params = societyId ? { societyId } : {};
  const response = await apiClient.get<ComplaintCategoryItem[]>('/categories', { params });
  return response.data;
};
