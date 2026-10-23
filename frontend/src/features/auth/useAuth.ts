import { useCallback, useEffect, useState } from 'react';
import type { UserSummary } from '../../types';
import { clearToken, fetchMeApi, getToken, loginApi, setToken } from './authService';
export function useAuth() {
 const [user,setUser]=useState<UserSummary|null>(null); const [loading,setLoading]=useState(Boolean(getToken()));
 const refresh=useCallback(async()=>{if(!getToken()){setUser(null);setLoading(false);return;}setLoading(true);try{setUser(await fetchMeApi());}catch{clearToken();setUser(null);}finally{setLoading(false);}},[]);
 useEffect(()=>{void refresh();const onChange=()=>void refresh();window.addEventListener('auth-change',onChange);return()=>window.removeEventListener('auth-change',onChange);},[refresh]);
 const login=useCallback(async(email:string,password:string)=>{const result=await loginApi({email,password});setToken(result.token);setUser(result.user);return result.user;},[]);
 const logout=useCallback(()=>{clearToken();setUser(null);},[]);
 return {user,loading,login,logout};
}
