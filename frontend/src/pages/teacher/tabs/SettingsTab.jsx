import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { errorMessage } from '../../../api/client';
import { teacherApi } from '../../../api/teacherApi';
import Modal from '../../../components/common/Modal';
import ClassroomForm from '../components/ClassroomForm';

/** 반 설정: 반 이름·몬스터 이름 수정, 반 삭제 */
export default function SettingsTab({ classroomId, classroom, onChanged }) {
  const navigate = useNavigate();
  const [saved, setSaved] = useState(false);
  const [confirming, setConfirming] = useState(false);
  const [confirmName, setConfirmName] = useState('');
  const [error, setError] = useState('');

  const handleUpdate = async (payload) => {
    setSaved(false);
    await teacherApi.updateClassroom(classroomId, payload);
    setSaved(true);
    onChanged();
  };

  const handleDelete = async () => {
    try {
      await teacherApi.deleteClassroom(classroomId);
      navigate('/teacher', { replace: true });
    } catch (err) {
      setError(errorMessage(err));
      setConfirming(false);
    }
  };

  return (
    <div className="tab-stack">
      <section className="card">
        <h2 className="card-title">우리 반 정체성</h2>
        <p className="muted settings-help">반 이름과 몬스터 이름은 학생 화면 상단과 몬스터 카드에 보여요.</p>
        <ClassroomForm
          key={`${classroom.name}-${classroom.monster.name}`}
          initial={{ name: classroom.name, monsterName: classroom.monster.name }}
          submitLabel="저장"
          onSubmit={handleUpdate}
        />
        {saved && <p className="alert alert-success settings-saved">저장했어요.</p>}
      </section>

      <section className="card danger-zone">
        <h2 className="card-title">반 삭제</h2>
        <p className="muted">
          반을 삭제하면 학생 계정, 과제, 먹이, 표현 미션, 성장 기록이 모두 함께 삭제되고 되돌릴 수 없어요.
        </p>
        {error && <p className="alert alert-error">{error}</p>}
        <div className="form-actions">
          <button type="button" className="btn btn-danger" onClick={() => setConfirming(true)}>
            이 반 삭제하기
          </button>
        </div>
      </section>

      {confirming && (
        <Modal
          title="정말 삭제할까요?"
          onClose={() => setConfirming(false)}
          actions={
            <>
              <button type="button" className="btn btn-ghost" onClick={() => setConfirming(false)}>
                취소
              </button>
              <button
                type="button"
                className="btn btn-danger"
                onClick={handleDelete}
                disabled={confirmName !== classroom.name}
              >
                영구 삭제
              </button>
            </>
          }
        >
          <p>
            확인을 위해 반 이름 <strong>{classroom.name}</strong>을(를) 입력해 주세요.
          </p>
          <input
            className="input"
            value={confirmName}
            onChange={(e) => setConfirmName(e.target.value)}
            aria-label="삭제할 반 이름 입력"
          />
        </Modal>
      )}
    </div>
  );
}
