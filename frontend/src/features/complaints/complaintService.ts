import { apiClient } from '../../lib/apiClient';
import type { ComplaintPriority } from '../../types';
export interface ComplaintCreateDraft { categoryId:number; title:string; description:string; locationArea:string; locationDetails:string; priority:ComplaintPriority; }
export interface CreatedComplaint { id:number; title:string; categoryName:string; status:string; createdAt:string; }
export async function createComplaint(draft:ComplaintCreateDraft){return (await apiClient.post<CreatedComplaint>('/complaints',draft)).data;}
