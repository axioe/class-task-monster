import { Fragment } from 'react';
import Monster from './Monster';

const STAGES = [
  { key: 'EGG', label: '알' },
  { key: 'BABY', label: '아기' },
  { key: 'GROWN', label: '성장' },
];

/** 알 → 아기 → 성장 단계 표시. 현재 단계를 강조한다. */
export default function StageTrack({ stage }) {
  const currentIndex = STAGES.findIndex((s) => s.key === stage);

  return (
    <ol className="stage-track" aria-label="성장 단계">
      {STAGES.map((s, i) => (
        <Fragment key={s.key}>
          {i > 0 && (
            <li className="stage-arrow" aria-hidden="true">
              ›
            </li>
          )}
          <li
            className={`stage-item ${i === currentIndex ? 'is-current' : ''} ${i > currentIndex ? 'is-locked' : ''}`}
            aria-current={i === currentIndex ? 'step' : undefined}
          >
            <Monster stage={s.key} size={74} />
            <span className="stage-label">{s.label}</span>
          </li>
        </Fragment>
      ))}
    </ol>
  );
}
