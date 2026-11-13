import { useCallback, useEffect, useState } from 'react';
import { errorMessage } from '../../../api/client';
import { teacherApi } from '../../../api/teacherApi';
import Modal from '../../../components/common/Modal';
import StatusBadge from '../../../components/common/StatusBadge';

const FILTERS = [
  { value: '', label: '전체' },
  { value: 'IN_PROGRESS', label: '도전 중' },
  { value: 'SUBMITTED', label: '확인 요청' },
  { value: 'REVISION_REQUESTED', label: '다시 도전' },
  { value: 'APPROVED', label: '확인 완료' },
];

const EMPTY_FORM = { studentId: '', title: '', description: '' };

/** 과제 지정: 학생별 맞춤 과제 카드 만들기와 목록 */
export default function AssignmentsTab({ classroomId, students, onChanged }) {
  const [assignments, setAssignments] = useState(null);
  const [filter, setFilter] = useState('');
  const [form, setForm] = useState(EMPTY_FORM);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);

  const load = useCallback(async () => {
    try {
      setAssignments(await teacherApi.getAssignments(classroomId, filter || undefined));
    } catch (err) {
      setError(errorMessage(err));
    }
  }, [classroomId, filter]);

  useEffect(() => {
    load();
  }, [load]);

  const handleCreate = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await teacherApi.createAssignment(classroomId, {
        studentId: Number(form.studentId),
        title: form.title.trim(),
        description: form.description.trim() || null,
      });
      setForm((f) => ({ ...EMPTY_FORM, studentId: f.studentId }));
      await load();
      onChanged();
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleUpdate = async () => {
    try {
      await teacherApi.updateAssignment(editing.id, {
        title: editing.title.trim(),
        description: editing.description?.trim() || null,
      });
      setEditing(null);
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const handleDelete = async () => {
    try {
      await teacherApi.deleteAssignment(deleting.id);
      setDeleting(null);
      await load();
      onChanged();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const update = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  return (
    <div className="tab-stack">
      <section className="card">
        <h2 className="card-title">맞춤 과제 보내기</h2>
        {students.length === 0 ? (
          <p className="empty">먼저 [학생] 탭에서 학생 계정을 만들어 주세요.</p>
        ) : (
          <form className="form-grid" onSubmit={handleCreate}>
            <div className="field">
              <label htmlFor="assign-student">학생</label>
              <select id="assign-student" className="select" value={form.studentId} onChange={update('studentId')} required>
                <option value="">학생 선택</option>
                {students.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.nickname}
                  </option>
                ))}
              </select>
            </div>
            <div className="field">
              <label htmlFor="assign-title">과제 제목</label>
              <input
                id="assign-title"
                className="input"
                placeholder="예: 단어 3개 익히기"
                value={form.title}
                onChange={update('title')}
                maxLength={100}
                required
              />
            </div>
            <div className="field form-full">
              <label htmlFor="assign-desc">설명 (선택)</label>
              <textarea
                id="assign-desc"
                className="textarea"
                placeholder="예: apple, banana, orange 를 소리 내어 읽고 써 보기"
                value={form.description}
                onChange={update('description')}
                maxLength={1000}
              />
            </div>
            {error && <p className="alert alert-error form-full">{error}</p>}
            <div className="form-actions form-full">
              <button type="submit" className="btn btn-primary" disabled={saving}>
                과제 보내기
              </button>
            </div>
          </form>
        )}
      </section>

      <section className="card">
        <div className="section-head">
          <h2 className="card-title">과제 목록</h2>
          <select className="select filter-select" value={filter} onChange={(e) => setFilter(e.target.value)} aria-label="상태 필터">
            {FILTERS.map((f) => (
              <option key={f.value} value={f.value}>
                {f.label}
              </option>
            ))}
          </select>
        </div>

        {!assignments ? (
          <div className="page-loading">불러오는 중…</div>
        ) : assignments.length === 0 ? (
          <p className="empty">과제가 없어요.</p>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>학생</th>
                  <th>과제</th>
                  <th>상태</th>
                  <th>피드백</th>
                  <th aria-label="관리" />
                </tr>
              </thead>
              <tbody>
                {assignments.map((a) => (
                  <tr key={a.id}>
                    <td data-label="학생">
                      <span className="student-chip">{a.studentNickname}</span>
                    </td>
                    <td data-label="과제">
                      <div>
                        <strong>{a.title}</strong>
                        {a.description && <div className="muted cell-sub">{a.description}</div>}
                      </div>
                    </td>
                    <td data-label="상태">
                      <StatusBadge status={a.status} />
                    </td>
                    <td data-label="피드백" className="muted">
                      {a.teacherComment || '-'}
                    </td>
                    <td className="cell-actions">
                      <button type="button" className="btn btn-ghost btn-sm" onClick={() => setEditing({ ...a })}>
                        수정
                      </button>
                      <button type="button" className="btn btn-danger btn-sm" onClick={() => setDeleting(a)}>
                        삭제
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {editing && (
        <Modal
          title="과제 수정"
          onClose={() => setEditing(null)}
          actions={
            <>
              <button type="button" className="btn btn-ghost" onClick={() => setEditing(null)}>
                취소
              </button>
              <button type="button" className="btn btn-primary" onClick={handleUpdate} disabled={!editing.title.trim()}>
                저장
              </button>
            </>
          }
        >
          <div className="field">
            <label htmlFor="edit-title">과제 제목</label>
            <input
              id="edit-title"
              className="input"
              value={editing.title}
              maxLength={100}
              onChange={(e) => setEditing((v) => ({ ...v, title: e.target.value }))}
            />
          </div>
          <div className="field">
            <label htmlFor="edit-desc">설명</label>
            <textarea
              id="edit-desc"
              className="textarea"
              value={editing.description ?? ''}
              maxLength={1000}
              onChange={(e) => setEditing((v) => ({ ...v, description: e.target.value }))}
            />
          </div>
        </Modal>
      )}

      {deleting && (
        <Modal
          title="과제를 삭제할까요?"
          onClose={() => setDeleting(null)}
          actions={
            <>
              <button type="button" className="btn btn-ghost" onClick={() => setDeleting(null)}>
                취소
              </button>
              <button type="button" className="btn btn-danger" onClick={handleDelete}>
                삭제
              </button>
            </>
          }
        >
          <p>
            {deleting.studentNickname} 학생의 "{deleting.title}" 과제를 삭제해요. 이미 지급된 먹이는 그대로 남아요.
          </p>
        </Modal>
      )}
    </div>
  );
}
