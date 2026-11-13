/** 한국 시간 기준 오늘 날짜 (YYYY-MM-DD) — 서버의 "오늘 미션" 기준과 맞춘다. */
export function todayInSeoul() {
  return new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Seoul' }).format(new Date());
}

export function formatDateTime(value) {
  if (!value) return '';
  return new Intl.DateTimeFormat('ko-KR', {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}

export function formatDate(value) {
  if (!value) return '';
  return new Intl.DateTimeFormat('ko-KR', { month: 'long', day: 'numeric', weekday: 'short' }).format(
    new Date(`${value}T00:00:00`),
  );
}

/** 먹이 종류별 그림 (기능 차이는 없음) */
export const FOOD_EMOJI = {
  STRAWBERRY: '🍓',
  GRAPE: '🍇',
  APPLE: '🍎',
  BANANA: '🍌',
  COOKIE: '🍪',
};

/** 이름 받침에 따라 조사 선택 (예: 몽글이를 / 콩떡을) */
export function josa(word, withFinal, withoutFinal) {
  if (!word) return withoutFinal;
  const code = word.charCodeAt(word.length - 1);
  if (code < 0xac00 || code > 0xd7a3) return withoutFinal;
  return (code - 0xac00) % 28 === 0 ? withoutFinal : withFinal;
}
