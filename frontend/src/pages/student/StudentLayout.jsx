import { NavLink, Outlet } from 'react-router-dom';
import Brand from '../../components/common/Brand';
import UserMenu from '../../components/common/UserMenu';
import { useAuth } from '../../context/AuthContext';
import '../../styles/student.css';

const NAV = [
  { to: '/student', label: '우리 반 몬스터', icon: '🐾', end: true },
  { to: '/student/assignments', label: '나의 과제', icon: '📋' },
  { to: '/student/growth', label: '성장 기록', icon: '📊' },
];

export default function StudentLayout() {
  const { user } = useAuth();

  return (
    <div className="student-shell">
      <header className="app-header">
        <Brand to="/student" />
        <div className="header-right">
          {user?.classroomName && (
            <span className="class-badge">
              <span aria-hidden="true">👥</span>
              {user.classroomName}
            </span>
          )}
          <UserMenu avatar="🧒" />
        </div>
      </header>

      <div className="student-body">
        <nav className="side-nav" aria-label="학생 메뉴">
          {NAV.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} className="side-nav-item">
              <span className="side-nav-icon" aria-hidden="true">
                {item.icon}
              </span>
              {item.label}
            </NavLink>
          ))}
          <div className="side-nav-deco" aria-hidden="true">
            🌼 🌿 🌼
          </div>
        </nav>

        <main className="student-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
