-- V2: 공용 DB(Supabase) 운영을 위한 보안·유지보수 설정
--
-- 1) RLS(Row Level Security) 활성화
--    Supabase 는 public 스키마의 테이블을 Data API(anon 키)로 외부에 노출한다.
--    이 서비스는 Data API 를 쓰지 않으므로, RLS 를 켜고 정책을 만들지 않아 API 접근을 모두 막는다.
--    테이블 소유자(앱이 접속하는 postgres 계정)는 RLS 를 적용받지 않아 앱 동작에는 영향이 없다.
--
-- 2) created_at / updated_at 기본값
--    대시보드 Table Editor 에서 행을 직접 추가할 때 시간을 비워도 되도록 한국 시간 기본값을 둔다.
--
-- 3) 테이블·컬럼 설명(COMMENT)
--    Supabase Table Editor 와 DB 툴에서 설명으로 보인다. 처음 보는 사람도 의미를 알 수 있게 한다.

-- ===== 1) RLS =====
ALTER TABLE users              ENABLE ROW LEVEL SECURITY;
ALTER TABLE classroom          ENABLE ROW LEVEL SECURITY;
ALTER TABLE assignment         ENABLE ROW LEVEL SECURITY;
ALTER TABLE food_item          ENABLE ROW LEVEL SECURITY;
ALTER TABLE expression_mission ENABLE ROW LEVEL SECURITY;
ALTER TABLE expression_option  ENABLE ROW LEVEL SECURITY;
ALTER TABLE feeding_log        ENABLE ROW LEVEL SECURITY;
ALTER TABLE growth_news        ENABLE ROW LEVEL SECURITY;
-- 주의: flyway_schema_history 는 마이그레이션 도중 Flyway 가 잠그고 있어 여기서 변경하면 교착 상태가 된다.
--       Supabase 에서는 docs/DATABASE.md "최초 1회 설정" 의 SQL 로 따로 RLS 를 켠다.

-- ===== 2) 시간 기본값 (한국 시간, 앱이 저장하는 값과 같은 기준) =====
ALTER TABLE users              ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');
ALTER TABLE classroom          ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');
ALTER TABLE assignment         ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');
ALTER TABLE food_item          ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');
ALTER TABLE expression_mission ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');
ALTER TABLE feeding_log        ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');
ALTER TABLE growth_news        ALTER COLUMN created_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul'),
                               ALTER COLUMN updated_at SET DEFAULT (now() AT TIME ZONE 'Asia/Seoul');

-- ===== 3) 설명 =====
COMMENT ON TABLE users IS '사용자. 교사(TEACHER)는 직접 가입, 학생(STUDENT)은 교사가 반에 계정을 만든다.';
COMMENT ON COLUMN users.login_id IS '로그인 아이디 (영문·숫자·_, 중복 불가)';
COMMENT ON COLUMN users.password IS 'BCrypt 암호화된 비밀번호. 직접 수정 금지 → 교사 화면 [학생 > 수정]에서 변경';
COMMENT ON COLUMN users.nickname IS '화면에 보이는 이름/별명';
COMMENT ON COLUMN users.role IS 'TEACHER 또는 STUDENT';
COMMENT ON COLUMN users.classroom_id IS '학생이 속한 반 (교사는 비어 있음)';

COMMENT ON TABLE classroom IS '반. 반마다 공동 몬스터 하나를 함께 키운다.';
COMMENT ON COLUMN classroom.name IS '반 이름 (예: 5학년 2반)';
COMMENT ON COLUMN classroom.monster_name IS '몬스터 이름 (예: 몽글이)';
COMMENT ON COLUMN classroom.growth_energy IS '성장 에너지 (먹이 1개 = 1). 수정 시 stage 도 함께 맞출 것: 0~9 EGG, 10~29 BABY, 30 이상 GROWN';
COMMENT ON COLUMN classroom.stage IS '성장 단계 EGG(알) / BABY(아기) / GROWN(성장). growth_energy 기준과 일치해야 함';
COMMENT ON COLUMN classroom.teacher_id IS '반을 만든 교사 (users.id)';

COMMENT ON TABLE assignment IS '학생별 맞춤 과제 카드';
COMMENT ON COLUMN assignment.status IS 'IN_PROGRESS(도전 중) → SUBMITTED(확인 요청) → APPROVED(확인 완료) / REVISION_REQUESTED(다시 도전)';
COMMENT ON COLUMN assignment.submission_note IS '학생이 확인 요청 때 남긴 말';
COMMENT ON COLUMN assignment.teacher_comment IS '교사 피드백';
COMMENT ON COLUMN assignment.food_granted IS '이 과제로 먹이를 이미 지급했는지 (과제당 1회). 직접 false 로 바꾸면 먹이가 중복 지급될 수 있음';

COMMENT ON TABLE food_item IS '학생이 받은 먹이 1개 = 1행. 확인된 과제 1개당 1개 지급, 종류는 무작위';
COMMENT ON COLUMN food_item.type IS 'STRAWBERRY / GRAPE / APPLE / BANANA / COOKIE (기능 차이 없음)';
COMMENT ON COLUMN food_item.consumed IS '먹이 주기에 사용했는지';

COMMENT ON TABLE expression_mission IS '날짜별 영어 표현 미션 (반마다 하루 1개)';
COMMENT ON COLUMN expression_mission.mission_date IS '미션 날짜 (한국 날짜 기준)';
COMMENT ON COLUMN expression_mission.situation IS '학생에게 보이는 상황 (예: 몽글이를 칭찬해 줘)';

COMMENT ON TABLE expression_option IS '표현 미션의 영어 표현 선택지';
COMMENT ON COLUMN expression_option.correct IS '알맞은 표현(정답)인지. 미션마다 1개 이상 true 필요';

COMMENT ON TABLE feeding_log IS '먹이 주기 시도 기록 (성공·실패 모두). 표현 정확도 분석용, 수정하지 않는 것을 권장';
COMMENT ON COLUMN feeding_log.success IS '알맞은 표현을 골라 먹이 주기에 성공했는지';
COMMENT ON COLUMN feeding_log.food_type IS '성공 시 사용한 먹이 종류 (실패 시 비어 있음)';

COMMENT ON TABLE growth_news IS '우리 반 성장 소식 (학생 화면 하단·성장 기록에 표시). 직접 추가해도 됨';
