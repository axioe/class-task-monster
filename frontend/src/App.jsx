import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import RequireRole, { homePathFor } from './routes/RequireRole';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import StudentLayout from './pages/student/StudentLayout';
import StudentHomePage from './pages/student/StudentHomePage';
import StudentAssignmentsPage from './pages/student/StudentAssignmentsPage';
import StudentGrowthPage from './pages/student/StudentGrowthPage';
import TeacherLayout from './pages/teacher/TeacherLayout';
import TeacherClassroomsPage from './pages/teacher/TeacherClassroomsPage';
import ClassroomDetailPage from './pages/teacher/ClassroomDetailPage';

function HomeRedirect() {
  const { user, loading } = useAuth();
  if (loading) return <div className="page-loading">불러오는 중…</div>;
  return <Navigate to={homePathFor(user)} replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomeRedirect />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />

      <Route element={<RequireRole role="STUDENT" />}>
        <Route path="/student" element={<StudentLayout />}>
          <Route index element={<StudentHomePage />} />
          <Route path="assignments" element={<StudentAssignmentsPage />} />
          <Route path="growth" element={<StudentGrowthPage />} />
        </Route>
      </Route>

      <Route element={<RequireRole role="TEACHER" />}>
        <Route path="/teacher" element={<TeacherLayout />}>
          <Route index element={<TeacherClassroomsPage />} />
          <Route path="classrooms/:classroomId" element={<ClassroomDetailPage />} />
        </Route>
      </Route>

      <Route path="*" element={<HomeRedirect />} />
    </Routes>
  );
}
