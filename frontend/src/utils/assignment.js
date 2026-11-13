/** 학생이 "다 했어요!"(확인 요청)를 할 수 있는 상태인지 */
export function canSubmit(assignment) {
  return assignment.status === 'IN_PROGRESS' || assignment.status === 'REVISION_REQUESTED';
}
