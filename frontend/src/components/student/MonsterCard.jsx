import EnergyBar from '../monster/EnergyBar';
import Monster from '../monster/Monster';
import StageTrack from '../monster/StageTrack';

/** 중앙: 우리 반 공동 몬스터, 말풍선, 성장 에너지, 성장 단계 */
export default function MonsterCard({ monster, bubble, celebrating }) {
  return (
    <section className="card monster-card" aria-label="우리 반 몬스터">
      <div className="monster-scene">
        <h2 className="monster-heading">
          우리 반 몬스터, <span className="monster-name">{monster.name}</span>
        </h2>
        <div className="scene-deco scene-tree" aria-hidden="true">
          🌳
        </div>
        <div className="scene-deco scene-flowers" aria-hidden="true">
          🌷 🌼
        </div>
        <div className="scene-deco scene-fence" aria-hidden="true">
          🌼 🌿
        </div>
        <div className="speech-bubble" role="status">
          {bubble}
        </div>
        <Monster
          stage={monster.stage}
          size={230}
          mood={celebrating ? 'happy' : undefined}
          className={`scene-monster ${celebrating ? 'is-bouncing' : ''}`}
          title={`${monster.name} (${monster.stageLabel} 단계)`}
        />
      </div>

      <EnergyBar monster={monster} />
      <StageTrack stage={monster.stage} />
    </section>
  );
}
