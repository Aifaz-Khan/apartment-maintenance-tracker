import {apiClient} from '../../lib/apiClient';
import type {ApartmentUnit,UnitPage} from './unitService';

export interface ResidencyTransfer {
 id:number;residentId:number;residentName:string;residentEmail:string;societyId:number;societyName:string;
 currentApartmentUnitId:number|null;currentBuildingName:string|null;currentUnitNumber:string|null;
 targetApartmentUnitId:number;targetBuildingName:string;targetUnitNumber:string;residencyType:string;
 effectiveDate:string;reason:string;status:'PENDING'|'APPROVED'|'REJECTED'|'CANCELLED';reviewedBy:number|null;
 rejectionReason:string|null;createdAt:string;reviewedAt:string|null;
}
export interface TransferPage {content:ResidencyTransfer[];pageNumber:number;pageSize:number;totalElements:number;totalPages:number;isFirst:boolean;isLast:boolean;}
export async function getAvailableTransferUnits(){return (await apiClient.get<UnitPage>('/residency-transfers/available-units',{params:{page:0,size:100}})).data.content as ApartmentUnit[];}
export async function getMyTransfers(){return (await apiClient.get<TransferPage>('/residency-transfers',{params:{page:0,size:50}})).data.content;}
export async function createTransfer(data:{targetApartmentUnitId:number;effectiveDate:string;reason:string}){return (await apiClient.post<ResidencyTransfer>('/residency-transfers',data)).data;}
export async function getPendingTransfers(){return (await apiClient.get<TransferPage>('/admin/residency-transfers',{params:{page:0,size:50}})).data.content;}
export async function reviewTransfer(id:number,action:'APPROVE'|'REJECT',rejectionReason?:string){return (await apiClient.patch<ResidencyTransfer>(`/admin/residency-transfers/${id}`,{action,rejectionReason})).data;}
