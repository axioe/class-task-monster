import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { errorMessage } from '../../api/client';
import { teacherApi } from '../../api/teacherApi';
import EnergyBar from '../../components/monster/EnergyBar';
import Monster from '../../components/monster/Monster';
import AssignmentsTab from './tabs/AssignmentsTab';
import MissionsTab from './tabs/MissionsTab';
import ReviewTab from './tabs/ReviewTab';
import SettingsTab from './tabs/SettingsTab';
import StudentsTab from './tabs/StudentsTab';

const TABS = [
  { key: 'review', label: '확인 대기' },
  { key: 'assignments', label: '과제 지정' },
  { key: 'students', label: '학생' },
  { key: 'missions', label: '표현 미션' },
  { key: 'settings', label: '반 설정' },
];

/**
 * 반 관리: 학생별 과제 지정 → 수행 확인 → 피드백, 표현 미션, 반 설정
 */
export default function ClassroomDetailPage() {
  const { classroomId } = useParams();
  const [classroom, setClassroom] = useState(null);
  const [students, setStudents] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [tab, setTab] = useState('review');
  const [error, setError] = useState('');

  const refresh = useCallback(async () => {
    try {
      const [c, s, pending] = await Promise.all([
        teacherApi.getClassroom(classroomId),
        teacherApi.getStudents(classroomId),
        teacherApi.getAssignments(classroomId, 'SUBMITTED'),
      ]);
      setClassroom(c);
      setStudents(s);
      setPendingCount(pending.length);
    } catch (err) {
      setError(errorMessage(err));
    }
  }, [classroomId]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  if (error) {
    return (
      <div className="page-stack">
        <p className="alert alert-error">{error}</p>
        <Link to="/teacher">← 내 반 목록</Link>
      </div>
    );
  }
  if (!classroom) return <div className="page-loading">반 정보를 불러오는 중…</div>;

  const tabProps = { classroomId, classroom, students, onChanged: refresh };

  return (
    <div className="page-stack">
      <Link to="/teacher" className="back-link">
        ← 내 반 목록
      </Link>

      <section className="card classroom-hero">
        <Monster stage={classroom.monster.stage} size={110} />
        <div className="classroom-hero-body">
          <h1>{classroom.name}</h1>
          <p className="muted">
            {classroom.monster.name} · {classroom.monster.stageLabel} 단계 · 학생 {classroom.studentCount}명
          </p>
          <EnergyBar monster={classroom.monster} />
        </div>
      </section>

      <div className="tabs" role="tablist" aria-label="반 관리 메뉴">
        {TABS.map((t) => (
          <button
            key={t.key}
            type="button"
            role="tab"
            aria-selected={tab === t.key}
            className={`tab ${tab === t.key ? 'is-active' : ''}`}
            onClick={() => setTab(t.key)}
          >
            {t.label}
            {t.key === 'review' && pendingCount > 0 && <span className="tab-count">{pendingCount}</span>}
          </button>
        ))}
      </div>

      <div role="tabpanel">
        {tab === 'review' && <ReviewTab {...tabProps} />}
        {tab === 'assignments' && <AssignmentsTab {...tabProps} />}
        {tab === 'students' && <StudentsTab {...tabProps} />}
        {tab === 'missions' && <MissionsTab {...tabProps} />}
        {tab === 'settings' && <SettingsTab {...tabProps} />}
      </div>
    </div>
  );
}
