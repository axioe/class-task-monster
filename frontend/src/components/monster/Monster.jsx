/**
 * 우리 반 공동 몬스터 그림 (SVG).
 * stage: EGG(알) → BABY(아기) → GROWN(성장, 날개가 생김)
 */
const BODY = '#c7b0f6';
const BODY_SHADE = '#a98be8';
const INK = '#3b2a63';
const WING = '#c4f0df';
const WING_LINE = '#86d3b6';

// 몸 둘레의 복슬복슬한 털 (원 여러 개)
const FLUFF = Array.from({ length: 24 }, (_, i) => {
  const angle = (i / 24) * Math.PI * 2;
  return { x: 100 + Math.cos(angle) * 58, y: 112 + Math.sin(angle) * 56 };
});

function Wing({ mirrored }) {
  return (
    <path
      d="M58 100 C 30 66, 4 82, 14 104 C 2 116, 20 134, 42 124 C 48 130, 60 124, 62 114 Z"
      fill={WING}
      stroke={WING_LINE}
      strokeWidth="3"
      strokeLinejoin="round"
      transform={mirrored ? 'translate(200 0) scale(-1 1)' : undefined}
    />
  );
}

function Egg() {
  return (
    <g>
      <ellipse cx="100" cy="182" rx="48" ry="8" fill="#000" opacity="0.06" />
      <ellipse cx="100" cy="112" rx="54" ry="68" fill="#faf7ff" stroke="#cdbbf3" strokeWidth="4" />
      <circle cx="82" cy="86" r="11" fill="#d9c8fa" />
      <circle cx="120" cy="104" r="14" fill="#d9c8fa" />
      <circle cx="90" cy="138" r="9" fill="#d9c8fa" />
      <circle cx="126" cy="146" r="6" fill="#d9c8fa" />
      <circle cx="70" cy="118" r="5" fill="#d9c8fa" />
      <ellipse cx="76" cy="70" rx="9" ry="15" fill="#fff" opacity="0.9" transform="rotate(25 76 70)" />
    </g>
  );
}

function Creature({ withWings, mood }) {
  const happy = mood === 'happy';
  return (
    <g>
      <ellipse cx="100" cy="184" rx="56" ry="8" fill="#000" opacity="0.06" />
      {withWings && (
        <>
          <Wing />
          <Wing mirrored />
        </>
      )}

      {/* 귀 */}
      <path d="M62 72 L58 34 L88 58 Z" fill={BODY} stroke={BODY} strokeWidth="10" strokeLinejoin="round" />
      <path d="M138 72 L142 34 L112 58 Z" fill={BODY} stroke={BODY} strokeWidth="10" strokeLinejoin="round" />

      {/* 털 테두리 → 몸 */}
      {FLUFF.map((p, i) => (
        <circle key={`s${i}`} cx={p.x} cy={p.y} r="14" fill={BODY_SHADE} />
      ))}
      {FLUFF.map((p, i) => (
        <circle key={`b${i}`} cx={p.x} cy={p.y} r="12" fill={BODY} />
      ))}
      <ellipse cx="100" cy="112" rx="60" ry="58" fill={BODY} />

      {/* 정수리 털 */}
      <circle cx="92" cy="52" r="10" fill={BODY} />
      <circle cx="106" cy="48" r="11" fill={BODY} />
      <path d="M100 42 q 10 -10 18 -2" stroke={BODY_SHADE} strokeWidth="3" fill="none" strokeLinecap="round" />

      {/* 하이라이트 */}
      <ellipse cx="76" cy="80" rx="14" ry="9" fill="#fff" opacity="0.28" transform="rotate(-25 76 80)" />

      {/* 팔 */}
      <ellipse cx="66" cy="142" rx="12" ry="17" fill={BODY} stroke={BODY_SHADE} strokeWidth="2.5" transform="rotate(-25 66 142)" />
      <ellipse cx="134" cy="142" rx="12" ry="17" fill={BODY} stroke={BODY_SHADE} strokeWidth="2.5" transform="rotate(25 134 142)" />

      {/* 발 */}
      <ellipse cx="78" cy="172" rx="18" ry="10" fill={BODY} stroke={BODY_SHADE} strokeWidth="2.5" />
      <ellipse cx="122" cy="172" rx="18" ry="10" fill={BODY} stroke={BODY_SHADE} strokeWidth="2.5" />

      {/* 볼 */}
      <ellipse cx="62" cy="120" rx="11" ry="7" fill="#f5a1c4" opacity="0.75" />
      <ellipse cx="138" cy="120" rx="11" ry="7" fill="#f5a1c4" opacity="0.75" />

      {/* 눈: 웃는 눈 */}
      <path d="M68 106 q 10 -12 20 0" stroke={INK} strokeWidth="4.5" fill="none" strokeLinecap="round" />
      <path d="M112 106 q 10 -12 20 0" stroke={INK} strokeWidth="4.5" fill="none" strokeLinecap="round" />

      {/* 입 */}
      {happy ? (
        <>
          <path d="M84 116 q 16 26 32 0 z" fill={INK} />
          <path d="M91 126 q 9 9 18 0 q -9 -5 -18 0" fill="#f07a8c" />
        </>
      ) : (
        <>
          <path d="M88 118 q 12 16 24 0 z" fill={INK} />
          <path d="M93 124 q 7 7 14 0 q -7 -4 -14 0" fill="#f07a8c" />
        </>
      )}
    </g>
  );
}

export default function Monster({ stage = 'EGG', size = 200, mood, className, title }) {
  return (
    <svg
      viewBox="0 0 200 200"
      width={size}
      height={size}
      className={className}
      role={title ? 'img' : undefined}
      aria-label={title}
      aria-hidden={title ? undefined : true}
    >
      {stage === 'EGG' ? <Egg /> : <Creature withWings={stage === 'GROWN'} mood={mood} />}
    </svg>
  );
}
