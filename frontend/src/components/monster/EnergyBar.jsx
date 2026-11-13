/** 성장 에너지 바 (예: 18 / 30). 마지막 단계면 MAX 로 표시한다. */
export default function EnergyBar({ monster }) {
  const goal = monster.nextStageEnergy;
  const percent = goal ? Math.min(100, Math.round((monster.energy / goal) * 100)) : 100;

  return (
    <div className="energy">
      <span className="energy-label">
        <span aria-hidden="true">⚡</span> 성장 에너지
      </span>
      <div
        className="energy-track"
        role="progressbar"
        aria-valuemin={0}
        aria-valuemax={goal ?? monster.energy}
        aria-valuenow={monster.energy}
        aria-label="성장 에너지"
      >
        <div className="energy-fill" style={{ width: `${Math.max(percent, 12)}%` }}>
          {goal ? `${monster.energy} / ${goal}` : `${monster.energy} · MAX`}
        </div>
      </div>
    </div>
  );
}
