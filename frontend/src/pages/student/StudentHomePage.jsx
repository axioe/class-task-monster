import { useCallback, useEffect, useRef, useState } from 'react';
import { errorMessage } from '../../api/client';
import { studentApi } from '../../api/studentApi';
import Modal from '../../components/common/Modal';
import ChallengeList from '../../components/student/ChallengeList';
import FoodPanel from '../../components/student/FoodPanel';
import MonsterCard from '../../components/student/MonsterCard';
import NewsBanner from '../../components/student/NewsBanner';
import SubmitModal from '../../components/student/SubmitModal';

const DEFAULT_BUBBLE = '함께 키워줘서 고마워!';

/**
 * 학생 메인 화면 (시안 기준)
 * 중앙: 공동 몬스터 / 오른쪽 위: 오늘 도전 / 오른쪽 아래: 먹이 주기 / 하단: 성장 소식
 */
export default function StudentHomePage() {
  const [dashboard, setDashboard] = useState(null);
  const [loadError, setLoadError] = useState('');
  const [submitTarget, setSubmitTarget] = useState(null);
  const [feeding, setFeeding] = useState(false);
  const [feedResult, setFeedResult] = useState(null);
  const [celebrating, setCelebrating] = useState(false);
  const [stageNews, setStageNews] = useState(null);
  const celebrateTimer = useRef(null);

  const load = useCallback(async () => {
    try {
      setDashboard(await studentApi.getDashboard());
      setLoadError('');
    } catch (err) {
      setLoadError(errorMessage(err));
    }
  }, []);

  useEffect(() => {
    load();
    return () => clearTimeout(celebrateTimer.current);
  }, [load]);

  const handleFeed = async (optionId) => {
    setFeeding(true);
    setFeedResult(null);
    try {
      const result = await studentApi.feed(optionId);
      setFeedResult(result);
      if (result.success) {
        setCelebrating(true);
        clearTimeout(celebrateTimer.current);
        celebrateTimer.current = setTimeout(() => setCelebrating(false), 1600);
        if (result.stageChanged && result.news) {
          setStageNews(result.news);
        }
      }
      await load();
    } catch (err) {
      setFeedResult({ success: false, message: errorMessage(err) });
    } finally {
      setFeeding(false);
    }
  };

  if (loadError && !dashboard) {
    return (
      <div className="card">
        <p className="alert alert-error">{loadError}</p>
        <button type="button" className="btn btn-secondary" onClick={load} style={{ marginTop: 12 }}>
          다시 불러오기
        </button>
      </div>
    );
  }
  if (!dashboard) {
    return <div className="page-loading">우리 반 몬스터를 깨우는 중…</div>;
  }

  const bubble = celebrating ? '냠냠! 정말 맛있어!' : DEFAULT_BUBBLE;

  return (
    <div className="student-home">
      <MonsterCard monster={dashboard.monster} bubble={bubble} celebrating={celebrating} />

      <div className="student-side">
        <ChallengeList
          nickname={dashboard.nickname}
          assignments={dashboard.assignments}
          onRequestSubmit={setSubmitTarget}
        />
        <FoodPanel
          foods={dashboard.foods}
          mission={dashboard.todayMission}
          feeding={feeding}
          result={feedResult}
          onFeed={handleFeed}
        />
      </div>

      <NewsBanner news={dashboard.news} monster={dashboard.monster} />

      {submitTarget && (
        <SubmitModal
          assignment={submitTarget}
          onClose={() => setSubmitTarget(null)}
          onSubmitted={() => {
            setSubmitTarget(null);
            load();
          }}
        />
      )}

      {stageNews && (
        <Modal
          title="🎉 우리 반 몬스터가 자랐어요!"
          onClose={() => setStageNews(null)}
          actions={
            <button type="button" className="btn btn-primary" onClick={() => setStageNews(null)}>
              좋아요!
            </button>
          }
        >
          <p>{stageNews.message}</p>
        </Modal>
      )}
    </div>
  );
}
