import { useCallback, useEffect, useState } from 'react';
import { errorMessage } from '../../../api/client';
import { teacherApi } from '../../../api/teacherApi';
import Modal from '../../../components/common/Modal';
import { formatDate, todayInSeoul } from '../../../utils/format';

const MIN_OPTIONS = 2;
const MAX_OPTIONS = 6;

const newForm = () => ({
  id: null,
  missionDate: todayInSeoul(),
  situation: '',
  options: [
    { expression: '', correct: true },
    { expression: '', correct: false },
    { expression: '', correct: false },
  ],
});

/**
 * 표현 미션: 날짜별 상황(예: 칭찬해 줘)과 영어 표현 선택지.
 * 학생은 알맞은 표현(정답)을 골라야 먹이 주기에 성공한다.
 */
export default function MissionsTab({ classroomId }) {
  const [missions, setMissions] = useState(null);
  const [form, setForm] = useState(newForm);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(null);

  const today = todayInSeoul();

  const load = useCallback(async () => {
    try {
      setMissions(await teacherApi.getMissions(classroomId));
    } catch (err) {
      setError(errorMessage(err));
    }
  }, [classroomId]);

  useEffect(() => {
    load();
  }, [load]);

  const setOption = (index, patch) =>
    setForm((f) => ({ ...f, options: f.options.map((o, i) => (i === index ? { ...o, ...patch } : o)) }));

  const addOption = () =>
    setForm((f) => ({ ...f, options: [...f.options, { expression: '', correct: false }] }));

  const removeOption = (index) =>
    setForm((f) => ({ ...f, options: f.options.filter((_, i) => i !== index) }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const options = form.options
      .map((o) => ({ expression: o.expression.trim(), correct: o.correct }))
      .filter((o) => o.expression);
    if (options.length < MIN_OPTIONS) {
      setError('표현을 2개 이상 입력해 주세요.');
      return;
    }
    if (!options.some((o) => o.correct)) {
      setError('알맞은 표현(정답)을 1개 이상 체크해 주세요.');
      return;
    }
    setSaving(true);
    try {
      const payload = { missionDate: form.missionDate, situation: form.situation.trim(), options };
      if (form.id) {
        await teacherApi.updateMission(form.id, payload);
      } else {
        await teacherApi.createMission(classroomId, payload);
      }
      setForm(newForm());
      await load();
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const startEdit = (mission) => {
    setError('');
    setForm({
      id: mission.id,
      missionDate: mission.missionDate,
      situation: mission.situation,
      options: mission.options.map((o) => ({ expression: o.expression, correct: o.correct })),
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async () => {
    try {
      await teacherApi.deleteMission(deleting.id);
      setDeleting(null);
      await load();
    } catch (err) {
      setError(errorMessage(err));
      setDeleting(null);
    }
  };

  return (
    <div className="tab-stack">
      <section className="card">
        <h2 className="card-title">{form.id ? '표현 미션 수정' : '표현 미션 등록'}</h2>
        <form className="mission-form" onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="field">
              <label htmlFor="mission-date">날짜</label>
              <input
                id="mission-date"
                type="date"
                className="input"
                value={form.missionDate}
                onChange={(e) => setForm((f) => ({ ...f, missionDate: e.target.value }))}
                required
              />
            </div>
            <div className="field">
              <label htmlFor="mission-situation">상황 (학생에게 보이는 미션)</label>
              <input
                id="mission-situation"
                className="input"
                placeholder="예: 몽글이를 칭찬해 줘 / 심부름을 보내 봐"
                value={form.situation}
                onChange={(e) => setForm((f) => ({ ...f, situation: e.target.value }))}
                maxLength={200}
                required
              />
            </div>
          </div>

          <fieldset className="option-fieldset">
            <legend className="label">영어 표현 선택지 · 알맞은 표현에 체크</legend>
            {form.options.map((o, i) => (
              <div key={i} className="option-row">
                <label className="correct-toggle">
                  <input
                    type="checkbox"
                    checked={o.correct}
                    onChange={(e) => setOption(i, { correct: e.target.checked })}
                  />
                  정답
                </label>
                <input
                  className="input"
                  placeholder={i === 0 ? '예: Good job!' : '예: See you later.'}
                  value={o.expression}
                  maxLength={100}
                  onChange={(e) => setOption(i, { expression: e.target.value })}
                  aria-label={`표현 ${i + 1}`}
                />
                <button
                  type="button"
                  className="btn btn-ghost btn-sm"
                  onClick={() => removeOption(i)}
                  disabled={form.options.length <= MIN_OPTIONS}
                  aria-label={`표현 ${i + 1} 삭제`}
                >
                  ✕
                </button>
              </div>
            ))}
            {form.options.length < MAX_OPTIONS && (
              <button type="button" className="btn btn-secondary btn-sm add-option" onClick={addOption}>
                + 표현 추가
              </button>
            )}
          </fieldset>

          {error && <p className="alert alert-error">{error}</p>}
          <div className="form-actions">
            {form.id && (
              <button type="button" className="btn btn-ghost" onClick={() => setForm(newForm())}>
                수정 취소
              </button>
            )}
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {form.id ? '저장' : '미션 등록'}
            </button>
          </div>
        </form>
      </section>

      <section className="card">
        <h2 className="card-title">등록된 미션</h2>
        {!missions ? (
          <div className="page-loading">불러오는 중…</div>
        ) : missions.length === 0 ? (
          <p className="empty">아직 등록된 미션이 없어요. 오늘의 미션이 있어야 학생이 먹이를 줄 수 있어요.</p>
        ) : (
          <ul className="mission-list">
            {missions.map((m) => (
              <li key={m.id} className={`mission-item ${m.missionDate === today ? 'is-today' : ''}`}>
                <div className="mission-item-head">
                  <span className="mission-date">
                    {formatDate(m.missionDate)}
                    {m.missionDate === today && <span className="today-tag">오늘</span>}
                  </span>
                  <strong>{m.situation}</strong>
                  <div className="cell-actions">
                    <button type="button" className="btn btn-ghost btn-sm" onClick={() => startEdit(m)}>
                      수정
                    </button>
                    <button type="button" className="btn btn-danger btn-sm" onClick={() => setDeleting(m)}>
                      삭제
                    </button>
                  </div>
                </div>
                <ul className="option-chips">
                  {m.options.map((o) => (
                    <li key={o.id} className={o.correct ? 'is-correct' : ''}>
                      {o.correct && <span aria-label="정답">✔ </span>}
                      {o.expression}
                    </li>
                  ))}
                </ul>
              </li>
            ))}
          </ul>
        )}
      </section>

      {deleting && (
        <Modal
          title="미션을 삭제할까요?"
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
            {formatDate(deleting.missionDate)} "{deleting.situation}" 미션을 삭제해요. 학생들이 이미 사용한 표현 기록은
            남아요.
          </p>
        </Modal>
      )}
    </div>
  );
}
