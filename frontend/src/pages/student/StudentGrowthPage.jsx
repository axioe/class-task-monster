import { useEffect, useState } from 'react';
import { errorMessage } from '../../api/client';
import { studentApi } from '../../api/studentApi';
import EnergyBar from '../../components/monster/EnergyBar';
import Monster from '../../components/monster/Monster';
import { formatDateTime } from '../../utils/format';

/** 성장 기록: 우리 반 성장 소식 전체 + 내가 먹이를 준 기록 */
export default function StudentGrowthPage() {
  const [growth, setGrowth] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    studentApi
      .getGrowth()
      .then(setGrowth)
      .catch((err) => setError(errorMessage(err)));
  }, []);

  if (error) return <p className="alert alert-error">{error}</p>;
  if (!growth) return <div className="page-loading">성장 기록을 불러오는 중…</div>;

  const { monster } = growth;

  return (
    <div className="page-stack">
      <header className="page-header">
        <h1>성장 기록</h1>
        <p className="muted">우리 반이 함께 만든 성장 이야기예요.</p>
      </header>

      <section className="card growth-summary">
        <Monster stage={monster.stage} size={120} />
        <div className="growth-summary-text">
          <h2>
            {monster.name} · {monster.stageLabel} 단계
          </h2>
          <EnergyBar monster={monster} />
          <p className="muted">
            내가 먹이를 준 횟수 <strong>{growth.mySuccessCount}번</strong>
          </p>
        </div>
      </section>

      <div className="growth-columns">
        <section className="card">
          <h2 className="card-title">
            <span className="icon" aria-hidden="true">
              📣
            </span>
            우리 반 성장 소식
          </h2>
          {growth.news.length === 0 ? (
            <p className="empty">아직 소식이 없어요.</p>
          ) : (
            <ol className="timeline">
              {growth.news.map((n) => (
                <li key={n.id}>
                  <p>{n.message}</p>
                  <time className="muted">{formatDateTime(n.createdAt)}</time>
                </li>
              ))}
            </ol>
          )}
        </section>

        <section className="card">
          <h2 className="card-title">
            <span className="icon" aria-hidden="true">
              🍽️
            </span>
            내가 사용한 표현
          </h2>
          {growth.myFeedings.length === 0 ? (
            <p className="empty">아직 먹이를 준 기록이 없어요.</p>
          ) : (
            <ul className="feeding-list">
              {growth.myFeedings.map((f) => (
                <li key={f.id} className={f.success ? 'is-success' : 'is-retry'}>
                  <span className="feeding-mark" aria-hidden="true">
                    {f.success ? '⭕' : '🔁'}
                  </span>
                  <div>
                    <strong>{f.chosenExpression}</strong>
                    <p className="muted">
                      {f.situation} · {f.success ? `${f.foodLabel} 냠냠!` : '다시 도전'}
                    </p>
                  </div>
                  <time className="muted">{formatDateTime(f.createdAt)}</time>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  );
}
