import { useState } from 'react';
import { studentApi } from '../../api/studentApi';
import { errorMessage } from '../../api/client';
import Modal from '../common/Modal';

/** 학생이 과제를 마치고 선생님께 확인을 요청한다. */
export default function SubmitModal({ assignment, onClose, onSubmitted }) {
  const [note, setNote] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const submit = async () => {
    setSubmitting(true);
    setError('');
    try {
      await studentApi.submitAssignment(assignment.id, note.trim() || null);
      onSubmitted();
    } catch (err) {
      setError(errorMessage(err));
      setSubmitting(false);
    }
  };

  return (
    <Modal
      title={`"${assignment.title}" 다 했어요!`}
      onClose={onClose}
      actions={
        <>
          <button type="button" className="btn btn-ghost" onClick={onClose}>
            취소
          </button>
          <button type="button" className="btn btn-primary" onClick={submit} disabled={submitting}>
            선생님께 확인 요청
          </button>
        </>
      }
    >
      {assignment.description && <p className="muted">{assignment.description}</p>}
      <div className="field">
        <label htmlFor="note">선생님께 남길 말 (선택)</label>
        <textarea
          id="note"
          className="textarea"
          value={note}
          maxLength={1000}
          placeholder="예: 단어 세 개를 다섯 번씩 써 봤어요."
          onChange={(e) => setNote(e.target.value)}
        />
      </div>
      {error && <p className="alert alert-error">{error}</p>}
    </Modal>
  );
}
