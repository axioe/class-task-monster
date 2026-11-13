-- ============================================================================
-- 사람별 DB 계정 만들기 (선택)
-- ----------------------------------------------------------------------------
-- 하나의 postgres 비밀번호를 여러 사람이 돌려 쓰지 않도록, 역할별 계정을 만든다.
--   ctm_maintainer : 데이터 조회·수정 담당 (테이블 구조 변경 불가)
--   ctm_readonly   : 조회만 가능 (분석·확인용)
--
-- 실행 위치: Supabase 대시보드 → SQL Editor  (postgres 권한으로 실행됨)
-- 실행 전  : 아래 'CHANGE_ME_...' 비밀번호를 각자 강한 값으로 바꾼다. 이 파일에 실제 비밀번호를 저장해 커밋하지 말 것.
-- 접속 방법: Session pooler 주소로, 사용자명은 "<역할명>.<프로젝트 ref>"
--            예) ctm_readonly.idmnlplkscxxzdboizzj
--
-- 테이블 구조 변경(컬럼 추가 등)은 이 계정들이 아니라 Flyway 마이그레이션(V3__...sql)으로만 한다.
-- 앱(백엔드)은 테이블 소유자인 postgres 계정으로 접속한다.
-- ============================================================================

-- 1) 역할 생성 -----------------------------------------------------------------
CREATE ROLE ctm_maintainer WITH LOGIN PASSWORD 'CHANGE_ME_maintainer_password';
CREATE ROLE ctm_readonly   WITH LOGIN PASSWORD 'CHANGE_ME_readonly_password';

-- 2) 권한 ----------------------------------------------------------------------
GRANT USAGE ON SCHEMA public TO ctm_maintainer, ctm_readonly;

GRANT SELECT, INSERT, UPDATE, DELETE ON
    users, classroom, assignment, food_item, expression_mission, expression_option, feeding_log, growth_news
    TO ctm_maintainer;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO ctm_maintainer;

GRANT SELECT ON
    users, classroom, assignment, food_item, expression_mission, expression_option, feeding_log, growth_news
    TO ctm_readonly;

-- 3) RLS 정책 ------------------------------------------------------------------
-- V2 마이그레이션에서 RLS 를 켰기 때문에, 이 역할들에 허용 정책을 따로 준다.
-- (Supabase Data API 의 anon / authenticated 에는 정책이 없으므로 계속 차단됨)
DO $$
DECLARE
    t text;
BEGIN
    FOREACH t IN ARRAY ARRAY['users', 'classroom', 'assignment', 'food_item',
                             'expression_mission', 'expression_option', 'feeding_log', 'growth_news']
    LOOP
        EXECUTE format('CREATE POLICY ctm_maintainer_all ON %I FOR ALL TO ctm_maintainer USING (true) WITH CHECK (true)', t);
        EXECUTE format('CREATE POLICY ctm_readonly_select ON %I FOR SELECT TO ctm_readonly USING (true)', t);
    END LOOP;
END $$;

-- 4) 비밀번호 열람 제한 --------------------------------------------------------
-- 조회 전용 계정은 암호화된 비밀번호 컬럼도 볼 필요가 없다.
REVOKE SELECT ON users FROM ctm_readonly;
GRANT SELECT (id, login_id, nickname, role, classroom_id, created_at, updated_at) ON users TO ctm_readonly;

-- ----------------------------------------------------------------------------
-- 계정 삭제가 필요할 때 (참고)
--   DROP POLICY ctm_maintainer_all ON <테이블>;  DROP POLICY ctm_readonly_select ON <테이블>;
--   REVOKE ALL ON ALL TABLES IN SCHEMA public FROM ctm_maintainer, ctm_readonly;
--   REVOKE ALL ON SCHEMA public FROM ctm_maintainer, ctm_readonly;
--   DROP ROLE ctm_maintainer;  DROP ROLE ctm_readonly;
-- 비밀번호 변경: ALTER ROLE ctm_readonly WITH PASSWORD '새 비밀번호';
-- ----------------------------------------------------------------------------
