import { apiClient } from '../../lib/apiClient';
export interface Society { id:number; name:string; code:string; addressLine1:string; addressLine2:string|null; city:string; state:string; postalCode:string; contactEmail:string; status:'ACTIVE'|'INACTIVE'; }
export interface SocietyPage { content:Society[]; pageNumber:number; pageSize:number; totalElements:number; totalPages:number; isFirst:boolean; isLast:boolean; }
export interface SocietyDraft { name:string; code:string; addressLine1:string; addressLine2:string; city:string; state:string; postalCode:string; contactEmail:string; }
export const listSocieties=async()=> (await apiClient.get<SocietyPage>('/super-admin/societies')).data;
export const createSociety=async(draft:SocietyDraft)=> (await apiClient.post<Society>('/super-admin/societies',draft)).data;
export const updateSociety=async(id:number,patch:Partial<Pick<Society,'status'>>)=> (await apiClient.patch<Society>(`/super-admin/societies/${id}`,patch)).data;
export const appointSocietyAdmin=async(id:number,data:{email:string;fullName:string;phone:string;initialPassword:string})=> (await apiClient.post(`/super-admin/societies/${id}/admins`,data)).data;
