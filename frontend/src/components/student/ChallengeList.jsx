import { Link } from 'react-router-dom';
import { canSubmit } from '../../utils/assignment';
import StatusBadge from '../common/StatusBadge';

const ACTIVE_ORDER = { REVISION_REQUESTED: 0, IN_PROGRESS: 1, SUBMITTED: 2, APPROVED: 3 };
const MAX_ITEMS = 4;

/** 오른쪽 위: ○○의 오늘 도전 (과제 카드 + 교사 확인 상태) */
export default function ChallengeList({ nickname, assignments, onRequestSubmit }) {
  const sorted = [...assignments].sort((a, b) => ACTIVE_ORDER[a.status] - ACTIVE_ORDER[b.status]);
  const visible = sorted.slice(0, MAX_ITEMS);

  return (
    <section className="card challenge-card" aria-label="오늘의 도전">
      <h2 className="card-title">
        <span className="icon" aria-hidden="true">
          ✅
        </span>
        {nickname}의 오늘 도전
      </h2>

      {visible.length === 0 ? (
        <p className="empty">아직 받은 과제가 없어요. 선생님이 곧 도전을 보내 줄 거예요!</p>
      ) : (
        <ul className="challenge-list">
          {visible.map((a) => (
            <li key={a.id} className={`challenge-item status-${a.status.toLowerCase()}`}>
              <span className="challenge-icon" aria-hidden="true">
                {a.status === 'APPROVED' ? '📖' : '✏️'}
              </span>
              <div className="challenge-text">
                <strong>{a.title}</strong>
                {a.teacherComment && <span className="challenge-comment">💬 {a.teacherComment}</span>}
              </div>
              <div className="challenge-actions">
                <StatusBadge status={a.status} />
                {canSubmit(a) && (
                  <button type="button" className="btn btn-secondary btn-sm" onClick={() => onRequestSubmit(a)}>
                    다 했어요!
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}

      {assignments.length > MAX_ITEMS && (
        <Link to="/student/assignments" className="more-link">
          과제 {assignments.length}개 모두 보기 →
        </Link>
      )}
    </section>
  );
}
