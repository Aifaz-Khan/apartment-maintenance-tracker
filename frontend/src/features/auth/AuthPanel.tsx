import React, { useState } from 'react';
import { KeyRound, LogIn, LogOut } from 'lucide-react';
import { useAuth } from './useAuth';
export const AuthPanel: React.FC = () => {
 const { user, loading, login, logout }=useAuth(); const [email,setEmail]=useState('');const [password,setPassword]=useState('');const [busy,setBusy]=useState(false);const [error,setError]=useState('');
 async function submit(event:React.FormEvent){event.preventDefault();setBusy(true);setError('');try{await login(email,password);}catch{setError('Invalid email or password. Check your details and try again.');}finally{setBusy(false);}}
 return <section className="auth-panel" aria-labelledby="auth-title">
  <div className="panel-header"><div style={{display:'flex',alignItems:'center',gap:'.5rem'}}><KeyRound size={14} color="var(--accent-color)"/><h3 id="auth-title">Authentication</h3></div><span className={`badge ${user?'badge-success':'badge-neutral'}`}>{loading?'Checking':user?'Session active':'Signed out'}</span></div>
  {user ? <div className="auth-panel-body" style={{gridTemplateColumns:'repeat(auto-fit,minmax(180px,1fr))'}}>
   <div><small style={{color:'var(--text-tertiary)'}}>ACCOUNT</small><div>{user.fullName} <span style={{color:'var(--text-secondary)'}}>· {user.email}</span></div></div><div><small style={{color:'var(--text-tertiary)'}}>ROLE</small><div>{user.role}</div></div><div><small style={{color:'var(--text-tertiary)'}}>SOCIETY / UNIT</small><div>{user.societyName ?? 'No society assigned'}{user.unitNumber ? ` · ${user.buildingName} ${user.unitNumber}` : ''}</div></div><div><small style={{color:'var(--text-tertiary)'}}>USER ID</small><div className="tnum">{user.id}</div></div><button className="auth-button auth-button-quiet" type="button" onClick={logout} style={{justifySelf:'start'}}><LogOut size={13}/> Sign out</button>
  </div> : <form className="auth-panel-body" onSubmit={submit}>
   <label>Email<input className="auth-input" type="email" autoComplete="username" required value={email} onChange={e=>setEmail(e.target.value)} /></label><label>Password<input className="auth-input" type="password" autoComplete="current-password" required value={password} onChange={e=>setPassword(e.target.value)} /></label>
   {error && <p role="alert" style={{color:'var(--status-warning-text)',gridColumn:'1 / -1'}}>{error}</p>}
   <div style={{display:'flex',gap:'.5rem',alignItems:'center',gridColumn:'1 / -1',flexWrap:'wrap'}}><button className="auth-button" disabled={busy||loading} type="submit"><LogIn size={14}/> {busy?'Signing in…':'Sign in'}</button><button className="auth-button auth-button-quiet" type="button" onClick={()=>{setEmail('superadmin@maintenancetracker.com');setPassword('Admin@12345');}}>Use local Super Admin credentials</button></div>
  </form>}
 </section>;
};
