import { apiClient } from '../../lib/apiClient';
export interface ApartmentUnit { id:number; societyId:number; buildingName:string; unitNumber:string; floorNumber:number|null; unitLabel:string|null; }
export interface UnitPage { content:ApartmentUnit[]; pageNumber:number; pageSize:number; totalElements:number; totalPages:number; isFirst:boolean; isLast:boolean; }
export interface ResidentRequest { id:number; userId:number; fullName:string; email:string; phone:string|null; societyId:number; apartmentUnitId:number; societyName:string; buildingName:string; unitNumber:string; residencyType:string; status:string; requestedStartDate:string; }
export async function listUnits(societyId:number){return (await apiClient.get<UnitPage>(`/societies/${societyId}/units`)).data;}
export async function createUnit(societyId:number,data:{buildingName:string;unitNumber:string;floorNumber:number|null;unitLabel:string}){return (await apiClient.post<ApartmentUnit>(`/societies/${societyId}/units`,data)).data;}
export async function listResidentRequests(){return (await apiClient.get<{content:ResidentRequest[]}>('/admin/resident-requests')).data.content;}
export async function reviewResidentRequest(id:number,action:'APPROVE'|'REJECT',rejectionReason?:string){return (await apiClient.patch<ResidentRequest>(`/admin/resident-requests/${id}`,{action,rejectionReason})).data;}
export async function submitResidentRequest(data:{societyId:number;apartmentUnitId:number;email:string;password:string;fullName:string;phone:string;residencyType:string}){return (await apiClient.post<ResidentRequest>('/auth/resident-registration-requests',data)).data;}
