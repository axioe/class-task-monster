import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { errorMessage } from '../../api/client';
import { teacherApi } from '../../api/teacherApi';
import Monster from '../../components/monster/Monster';
import ClassroomForm from './components/ClassroomForm';

/** 교사 홈: 내 반 목록과 반 만들기 */
export default function TeacherClassroomsPage() {
  const [classrooms, setClassrooms] = useState(null);
  const [error, setError] = useState('');
  const [creating, setCreating] = useState(false);

  const load = useCallback(async () => {
    try {
      setClassrooms(await teacherApi.getClassrooms());
    } catch (err) {
      setError(errorMessage(err));
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleCreate = async (payload) => {
    await teacherApi.createClassroom(payload);
    setCreating(false);
    await load();
  };

  if (error) return <p className="alert alert-error">{error}</p>;
  if (!classrooms) return <div className="page-loading">반 목록을 불러오는 중…</div>;

  return (
    <div className="page-stack">
      <header className="page-header page-header-row">
        <div>
          <h1>내 반</h1>
          <p className="muted">반을 선택해 학생, 과제, 표현 미션을 관리하세요.</p>
        </div>
        {!creating && (
          <button type="button" className="btn btn-primary" onClick={() => setCreating(true)}>
            + 반 만들기
          </button>
        )}
      </header>

      {creating && (
        <section className="card">
          <h2 className="card-title">새 반 만들기</h2>
          <ClassroomForm submitLabel="반 만들기" onSubmit={handleCreate} onCancel={() => setCreating(false)} />
        </section>
      )}

      {classrooms.length === 0 && !creating ? (
        <div className="empty">
          아직 만든 반이 없어요. <strong>+ 반 만들기</strong>로 시작해 보세요.
        </div>
      ) : (
        <ul className="classroom-grid">
          {classrooms.map((c) => (
            <li key={c.id}>
              <Link to={`/teacher/classrooms/${c.id}`} className="card classroom-card">
                <Monster stage={c.monster.stage} size={88} />
                <div className="classroom-card-body">
                  <h3>{c.name}</h3>
                  <p className="muted">
                    {c.monster.name} · {c.monster.stageLabel} 단계 · 에너지 {c.monster.energy}
                  </p>
                  <p className="muted">학생 {c.studentCount}명</p>
                </div>
                <span className="classroom-card-arrow" aria-hidden="true">
                  ›
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
