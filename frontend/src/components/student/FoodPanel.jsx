import { useEffect, useState } from 'react';
import { FOOD_EMOJI } from '../../utils/format';

const MAX_VISIBLE_FOODS = 6;

/**
 * 오른쪽 아래: 보유 먹이, 오늘의 표현 미션, 먹이 주기 버튼.
 * 알맞은 영어 표현을 골라야만 먹이 주기에 성공한다.
 */
export default function FoodPanel({ foods, mission, feeding, result, onFeed }) {
  const [optionId, setOptionId] = useState('');

  // 미션이 바뀌면 선택을 초기화
  useEffect(() => {
    setOptionId('');
  }, [mission?.id]);

  const hasFood = foods.length > 0;
  const canFeed = hasFood && mission && optionId && !feeding;

  return (
    <section className="card food-card" aria-label="먹이 주기">
      <h2 className="card-title">
        <span className="icon" aria-hidden="true">
          🥣
        </span>
        내 먹이 {foods.length}개
      </h2>

      {hasFood ? (
        <ul className="food-grid" aria-label="보유한 먹이">
          {foods.slice(0, MAX_VISIBLE_FOODS).map((food) => (
            <li key={food.id} className="food-tile" title={food.label}>
              <span aria-hidden="true">{FOOD_EMOJI[food.type] ?? '🍬'}</span>
              <span className="sr-only">{food.label}</span>
            </li>
          ))}
          {foods.length > MAX_VISIBLE_FOODS && (
            <li className="food-tile food-more">+{foods.length - MAX_VISIBLE_FOODS}</li>
          )}
        </ul>
      ) : (
        <p className="empty">과제를 마치고 선생님 확인을 받으면 먹이를 받아요!</p>
      )}

      {mission ? (
        <div className="mission-box">
          <p className="mission-situation">
            <span className="mission-tag">오늘의 미션</span>
            {mission.situation}
          </p>
          <div className="field">
            <label htmlFor="expression">함께 말할 표현</label>
            <select
              id="expression"
              className="select expression-select"
              value={optionId}
              onChange={(e) => setOptionId(e.target.value)}
            >
              <option value="">표현을 골라 주세요</option>
              {mission.options.map((o) => (
                <option key={o.id} value={o.id}>
                  {o.expression}
                </option>
              ))}
            </select>
          </div>
        </div>
      ) : (
        <p className="alert alert-info">오늘의 표현 미션이 아직 없어요. 선생님을 기다려 주세요!</p>
      )}

      {result && (
        <p className={`alert ${result.success ? 'alert-success' : 'alert-error'}`} role="status">
          {result.message}
        </p>
      )}

      <button
        type="button"
        className="btn btn-primary btn-lg feed-button"
        disabled={!canFeed}
        onClick={() => onFeed(Number(optionId))}
      >
        <span aria-hidden="true">🍽️</span> {feeding ? '주는 중…' : '먹이 주기'}
      </button>
    </section>
  );
}
