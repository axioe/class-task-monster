import { useState } from 'react';
import { errorMessage } from '../../../api/client';

/** 반 이름 + 몬스터 이름 입력 폼 (생성/수정 공용) */
export default function ClassroomForm({ initial, submitLabel, onSubmit, onCancel }) {
  const [form, setForm] = useState({ name: initial?.name ?? '', monsterName: initial?.monsterName ?? '' });
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await onSubmit({ name: form.name.trim(), monsterName: form.monsterName.trim() });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const update = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  return (
    <form className="form-grid" onSubmit={handleSubmit}>
      <div className="field">
        <label htmlFor="classroom-name">반 이름</label>
        <input
          id="classroom-name"
          className="input"
          placeholder="예: 5학년 2반"
          value={form.name}
          onChange={update('name')}
          maxLength={50}
          required
        />
      </div>
      <div className="field">
        <label htmlFor="monster-name">몬스터 이름</label>
        <input
          id="monster-name"
          className="input"
          placeholder="예: 몽글이"
          value={form.monsterName}
          onChange={update('monsterName')}
          maxLength={30}
          required
        />
      </div>
      {error && <p className="alert alert-error form-full">{error}</p>}
      <div className="form-actions form-full">
        {onCancel && (
          <button type="button" className="btn btn-ghost" onClick={onCancel}>
            취소
          </button>
        )}
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {submitLabel}
        </button>
      </div>
    </form>
  );
}
