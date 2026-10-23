import { apiClient } from '../../lib/apiClient';
import type { UserSummary } from '../../types';
export interface LoginResponse { token: string; tokenType: 'Bearer'; expiresInSeconds: number; user: UserSummary; }
export const getToken = () => localStorage.getItem('token');
export const setToken = (token: string) => { localStorage.setItem('token', token); window.dispatchEvent(new Event('auth-change')); };
export const clearToken = () => { localStorage.removeItem('token'); window.dispatchEvent(new Event('auth-change')); };
export async function loginApi(credentials: { email: string; password: string }) { return (await apiClient.post<LoginResponse>('/auth/login', credentials)).data; }
export async function fetchMeApi() { return (await apiClient.get<UserSummary>('/users/me')).data; }
