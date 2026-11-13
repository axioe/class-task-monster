import { Outlet } from 'react-router-dom';
import Brand from '../../components/common/Brand';
import UserMenu from '../../components/common/UserMenu';
import '../../styles/student.css';
import '../../styles/teacher.css';

export default function TeacherLayout() {
  return (
    <div className="teacher-shell">
      <header className="app-header">
        <Brand to="/teacher" />
        <div className="header-right">
          <span className="role-badge">선생님 관리 화면</span>
          <UserMenu avatar="📚" />
        </div>
      </header>
      <main className="teacher-main">
        <Outlet />
      </main>
    </div>
  );
}
