import Monster from '../monster/Monster';
import { josa } from '../../utils/format';

/** 하단: 우리 반 성장 소식 */
export default function NewsBanner({ news, monster }) {
  const latest = news[0];

  return (
    <section className="news-banner" aria-label="우리 반 성장 소식">
      <h2 className="news-title">
        <span aria-hidden="true">📣</span> 우리 반 성장 소식
      </h2>
      <div className="news-body">
        <p className="news-main">
          <span aria-hidden="true">🌱</span>
          {latest ? <mark>{latest.message}</mark> : '첫 번째 성장 소식을 함께 만들어 봐요!'}
        </p>
        <p className="news-sub">
          <span aria-hidden="true">🌿</span> 각자의 도전이 {monster.name}
          {josa(monster.name, '을', '를')} 키워요.
        </p>
      </div>
      <Monster stage={monster.stage} size={110} className="news-monster" />
    </section>
  );
}
