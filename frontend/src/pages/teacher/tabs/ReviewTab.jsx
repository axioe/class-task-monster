import { useCallback, useEffect, useState } from 'react';
import { errorMessage } from '../../../api/client';
import { teacherApi } from '../../../api/teacherApi';
import { FOOD_EMOJI, formatDateTime } from '../../../utils/format';

/** 확인 대기: 학생이 확인 요청한 과제를 확인(먹이 지급)하거나 다시 도전을 요청한다. */
export default function ReviewTab({ classroomId, onChanged }) {
  const [pending, setPending] = useState(null);
  const [comments, setComments] = useState({});
  const [busyId, setBusyId] = useState(null);
  const [message, setMessage] = useState(null);

  const load = useCallback(async () => {
    try {
      setPending(await teacherApi.getAssignments(classroomId, 'SUBMITTED'));
    } catch (err) {
      setMessage({ type: 'error', text: errorMessage(err) });
    }
  }, [classroomId]);

  useEffect(() => {
    load();
  }, [load]);

  const review = async (assignment, approved) => {
    setBusyId(assignment.id);
    setMessage(null);
    try {
      const result = await teacherApi.reviewAssignment(assignment.id, approved, comments[assignment.id]?.trim() || null);
      const food = result.grantedFood;
      setMessage({
        type: 'success',
        text: approved
          ? `${assignment.studentNickname} 학생의 "${assignment.title}"을 확인했어요.${
              food ? ` 먹이 ${FOOD_EMOJI[food.type] ?? ''} ${food.label}를 받았어요!` : ''
            }`
          : `${assignment.studentNickname} 학생에게 다시 도전을 요청했어요.`,
      });
      await load();
      onChanged();
    } catch (err) {
      setMessage({ type: 'error', text: errorMessage(err) });
    } finally {
      setBusyId(null);
    }
  };

  if (!pending) return <div className="page-loading">불러오는 중…</div>;

  return (
    <div className="tab-stack">
      {message && <p className={`alert alert-${message.type}`}>{message.text}</p>}

      {pending.length === 0 ? (
        <p className="empty">확인을 기다리는 과제가 없어요. 🎉</p>
      ) : (
        <ul className="review-list">
          {pending.map((a) => (
            <li key={a.id} className="card review-card">
              <div className="review-head">
                <span className="student-chip">{a.studentNickname}</span>
                <h3>{a.title}</h3>
                <span className="muted review-time">{formatDateTime(a.submittedAt)} 요청</span>
              </div>
              {a.description && <p className="muted">과제: {a.description}</p>}
              <p className="note-box">
                <span className="note-label">학생이 남긴 말</span>
                {a.submissionNote || '(남긴 말 없음)'}
              </p>
              <div className="field">
                <label htmlFor={`comment-${a.id}`}>짧은 피드백</label>
                <input
                  id={`comment-${a.id}`}
                  className="input"
                  maxLength={500}
                  placeholder="예: 발음이 아주 정확했어요!"
                  value={comments[a.id] ?? ''}
                  onChange={(e) => setComments((c) => ({ ...c, [a.id]: e.target.value }))}
                />
              </div>
              <div className="form-actions">
                <button
                  type="button"
                  className="btn btn-ghost"
                  disabled={busyId === a.id}
                  onClick={() => review(a, false)}
                >
                  ↺ 다시 도전
                </button>
                <button
                  type="button"
                  className="btn btn-success"
                  disabled={busyId === a.id}
                  onClick={() => review(a, true)}
                >
                  ✔ 확인 완료 (먹이 지급)
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
