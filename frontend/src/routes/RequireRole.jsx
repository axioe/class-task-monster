import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function homePathFor(user) {
  if (!user) return '/login';
  return user.role === 'TEACHER' ? '/teacher' : '/student';
}

/** 로그인 + 역할 확인. 다른 역할이면 자기 홈으로 보낸다. */
export default function RequireRole({ role }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="page-loading">불러오는 중…</div>;
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (user.role !== role) {
    return <Navigate to={homePathFor(user)} replace />;
  }
  return <Outlet />;
}
