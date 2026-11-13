import { useCallback, useEffect, useState } from 'react';
import { errorMessage } from '../../api/client';
import { studentApi } from '../../api/studentApi';
import StatusBadge from '../../components/common/StatusBadge';
import SubmitModal from '../../components/student/SubmitModal';
import { canSubmit } from '../../utils/assignment';
import { formatDateTime } from '../../utils/format';

/** 나의 과제: 받은 과제 전체와 선생님 피드백 */
export default function StudentAssignmentsPage() {
  const [assignments, setAssignments] = useState(null);
  const [error, setError] = useState('');
  const [submitTarget, setSubmitTarget] = useState(null);

  const load = useCallback(async () => {
    try {
      const dashboard = await studentApi.getDashboard();
      setAssignments(dashboard.assignments);
    } catch (err) {
      setError(errorMessage(err));
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  if (error) return <p className="alert alert-error">{error}</p>;
  if (!assignments) return <div className="page-loading">과제를 불러오는 중…</div>;

  return (
    <div className="page-stack">
      <header className="page-header">
        <h1>나의 과제</h1>
        <p className="muted">과제를 마치면 "다 했어요!"를 눌러 선생님께 확인을 받아요. 확인을 받으면 먹이가 생겨요.</p>
      </header>

      {assignments.length === 0 ? (
        <p className="empty">아직 받은 과제가 없어요.</p>
      ) : (
        <ul className="assignment-grid">
          {assignments.map((a) => (
            <li key={a.id} className="card assignment-card">
              <div className="assignment-card-head">
                <h3>{a.title}</h3>
                <StatusBadge status={a.status} />
              </div>
              {a.description && <p>{a.description}</p>}
              {a.submissionNote && (
                <p className="note-box">
                  <span className="note-label">내가 남긴 말</span>
                  {a.submissionNote}
                </p>
              )}
              {a.teacherComment && (
                <p className="note-box note-teacher">
                  <span className="note-label">선생님 피드백</span>
                  {a.teacherComment}
                </p>
              )}
              <div className="assignment-card-foot">
                <span className="muted">
                  {a.reviewedAt
                    ? `확인 ${formatDateTime(a.reviewedAt)}`
                    : a.submittedAt
                      ? `요청 ${formatDateTime(a.submittedAt)}`
                      : `받은 날 ${formatDateTime(a.createdAt)}`}
                </span>
                {a.foodGranted && <span className="badge badge-success">🍓 먹이 받음</span>}
                {canSubmit(a) && (
                  <button type="button" className="btn btn-primary btn-sm" onClick={() => setSubmitTarget(a)}>
                    다 했어요!
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}

      {submitTarget && (
        <SubmitModal
          assignment={submitTarget}
          onClose={() => setSubmitTarget(null)}
          onSubmitted={() => {
            setSubmitTarget(null);
            load();
          }}
        />
      )}
    </div>
  );
}
