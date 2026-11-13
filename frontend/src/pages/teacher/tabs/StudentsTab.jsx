import { useState } from 'react';
import { errorMessage } from '../../../api/client';
import { teacherApi } from '../../../api/teacherApi';
import Modal from '../../../components/common/Modal';

const EMPTY_FORM = { nickname: '', loginId: '', password: '' };

/** 학생: 계정 만들기·수정·삭제 + 파일럿 지표(확인된 과제 수, 표현 정확도) */
export default function StudentsTab({ classroomId, students, onChanged }) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);

  const handleCreate = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await teacherApi.createStudent(classroomId, {
        nickname: form.nickname.trim(),
        loginId: form.loginId.trim(),
        password: form.password,
      });
      setForm(EMPTY_FORM);
      onChanged();
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleUpdate = async () => {
    try {
      await teacherApi.updateStudent(editing.id, {
        nickname: editing.nickname.trim(),
        password: editing.password ? editing.password : null,
      });
      setEditing(null);
      onChanged();
    } catch (err) {
      setError(errorMessage(err));
      setEditing(null);
    }
  };

  const handleDelete = async () => {
    try {
      await teacherApi.deleteStudent(deleting.id);
      setDeleting(null);
      onChanged();
    } catch (err) {
      setError(errorMessage(err));
      setDeleting(null);
    }
  };

  const update = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  return (
    <div className="tab-stack">
      <section className="card">
        <h2 className="card-title">학생 계정 만들기</h2>
        <form className="form-grid form-grid-3" onSubmit={handleCreate}>
          <div className="field">
            <label htmlFor="student-nickname">별명</label>
            <input
              id="student-nickname"
              className="input"
              placeholder="예: 지우"
              value={form.nickname}
              onChange={update('nickname')}
              maxLength={30}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="student-login">아이디 (영문·숫자·_)</label>
            <input
              id="student-login"
              className="input"
              placeholder="예: jiwoo"
              value={form.loginId}
              onChange={update('loginId')}
              minLength={4}
              maxLength={50}
              pattern="[A-Za-z0-9_]+"
              required
            />
          </div>
          <div className="field">
            <label htmlFor="student-password">비밀번호 (4자 이상)</label>
            <input
              id="student-password"
              className="input"
              type="password"
              autoComplete="new-password"
              value={form.password}
              onChange={update('password')}
              minLength={4}
              required
            />
          </div>
          {error && <p className="alert alert-error form-full">{error}</p>}
          <div className="form-actions form-full">
            <button type="submit" className="btn btn-primary" disabled={saving}>
              학생 추가
            </button>
          </div>
        </form>
      </section>

      <section className="card">
        <h2 className="card-title">우리 반 학생 {students.length}명</h2>
        {students.length === 0 ? (
          <p className="empty">아직 학생이 없어요.</p>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>별명</th>
                  <th>아이디</th>
                  <th>보유 먹이</th>
                  <th>확인된 과제</th>
                  <th>표현 정확도</th>
                  <th aria-label="관리" />
                </tr>
              </thead>
              <tbody>
                {students.map((s) => (
                  <tr key={s.id}>
                    <td data-label="별명">
                      <span className="student-chip">{s.nickname}</span>
                    </td>
                    <td data-label="아이디" className="muted">
                      {s.loginId}
                    </td>
                    <td data-label="보유 먹이">{s.foodCount}개</td>
                    <td data-label="확인된 과제">{s.approvedAssignmentCount}개</td>
                    <td data-label="표현 정확도">
                      {s.feedingAttemptCount === 0
                        ? '-'
                        : `${Math.round((s.feedingSuccessCount / s.feedingAttemptCount) * 100)}% (${s.feedingSuccessCount}/${s.feedingAttemptCount})`}
                    </td>
                    <td className="cell-actions">
                      <button
                        type="button"
                        className="btn btn-ghost btn-sm"
                        onClick={() => setEditing({ id: s.id, nickname: s.nickname, password: '' })}
                      >
                        수정
                      </button>
                      <button type="button" className="btn btn-danger btn-sm" onClick={() => setDeleting(s)}>
                        삭제
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <p className="muted table-note">표현 정확도: 먹이 주기에서 알맞은 표현을 고른 비율 (성공 / 시도)</p>
      </section>

      {editing && (
        <Modal
          title="학생 정보 수정"
          onClose={() => setEditing(null)}
          actions={
            <>
              <button type="button" className="btn btn-ghost" onClick={() => setEditing(null)}>
                취소
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleUpdate}
                disabled={!editing.nickname.trim() || (editing.password && editing.password.length < 4)}
              >
                저장
              </button>
            </>
          }
        >
          <div className="field">
            <label htmlFor="edit-nickname">별명</label>
            <input
              id="edit-nickname"
              className="input"
              value={editing.nickname}
              maxLength={30}
              onChange={(e) => setEditing((v) => ({ ...v, nickname: e.target.value }))}
            />
          </div>
          <div className="field">
            <label htmlFor="edit-password">새 비밀번호 (바꿀 때만 입력, 4자 이상)</label>
            <input
              id="edit-password"
              className="input"
              type="password"
              autoComplete="new-password"
              value={editing.password}
              onChange={(e) => setEditing((v) => ({ ...v, password: e.target.value }))}
            />
          </div>
        </Modal>
      )}

      {deleting && (
        <Modal
          title="학생을 삭제할까요?"
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
            <strong>{deleting.nickname}</strong> 학생의 계정과 과제, 먹이, 먹이 주기 기록이 모두 삭제되고 되돌릴 수
            없어요. 몬스터의 성장 에너지는 그대로 유지돼요.
          </p>
        </Modal>
      )}
    </div>
  );
}
