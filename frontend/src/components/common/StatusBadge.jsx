const STATUS_STYLE = {
  IN_PROGRESS: { className: 'badge-progress', icon: '◌', label: '도전 중' },
  SUBMITTED: { className: 'badge-waiting', icon: '⏳', label: '확인 요청' },
  APPROVED: { className: 'badge-success', icon: '✔', label: '선생님 확인 완료' },
  REVISION_REQUESTED: { className: 'badge-revision', icon: '↺', label: '다시 도전' },
};

export default function StatusBadge({ status }) {
  const style = STATUS_STYLE[status] ?? STATUS_STYLE.IN_PROGRESS;
  return (
    <span className={`badge ${style.className}`}>
      <span aria-hidden="true">{style.icon}</span>
      {style.label}
    </span>
  );
}
