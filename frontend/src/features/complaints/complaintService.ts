import { apiClient } from '../../lib/apiClient';
import type { ComplaintPriority, ComplaintStatus } from '../../types';
export interface ComplaintCreateDraft { categoryId:number; title:string; description:string; locationArea:string; locationDetails:string; priority:ComplaintPriority; }
export interface ComplaintItem { id:number; societyId:number; apartmentUnitId:number; categoryId:number; categoryName:string; createdBy:number; title:string; description:string; locationArea:string; locationDetails:string|null; priority:ComplaintPriority; status:ComplaintStatus; resolutionNotes:string|null; createdAt:string; updatedAt:string; }
export interface ComplaintPage { content:ComplaintItem[]; pageNumber:number; pageSize:number; totalElements:number; totalPages:number; isFirst:boolean; isLast:boolean; }
export interface ComplaintHistoryItem { id:number; changedBy:number; changedByName:string; previousStatus:ComplaintStatus|null; newStatus:ComplaintStatus; remarks:string|null; createdAt:string; }
export interface CreatedComplaint { id:number; title:string; categoryName:string; status:string; createdAt:string; }
export async function createComplaint(draft:ComplaintCreateDraft){return (await apiClient.post<CreatedComplaint>('/complaints',draft)).data;}
export async function listComplaints(){return (await apiClient.get<ComplaintPage>('/complaints',{params:{page:0,size:50}})).data.content;}
export async function getComplaintHistory(id:number){return (await apiClient.get<ComplaintHistoryItem[]>(`/complaints/${id}/history`)).data;}
export async function updateComplaintStatus(id:number,data:{newStatus:ComplaintStatus;remarks:string;resolutionNotes:string}){return (await apiClient.patch<ComplaintItem>(`/complaints/${id}/status`,data)).data;}
