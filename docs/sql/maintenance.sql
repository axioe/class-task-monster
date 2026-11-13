-- ============================================================================
-- 우리 반 과제 몬스터 - 관리용 SQL 모음 (DBeaver / Supabase SQL Editor)
-- ----------------------------------------------------------------------------
-- 사용법 (DBeaver)
--   - 실행할 문장에 커서를 두고 Ctrl+Enter
--   - :이름 형태의 값은 실행 시 입력 창이 뜸 (Supabase SQL Editor 에서는 직접 값으로 바꿔서 실행)
--   - UPDATE / DELETE 는 Manual Commit 모드에서 실행 → 결과 확인 → Commit (잘못되면 Rollback)
-- 수정 규칙은 docs/DATABASE.md 의 "3. 직접 수정 규칙" 참고
-- ============================================================================


-- ============================================================================
-- [조회] 현황 확인
-- ============================================================================

-- 반별 현황 (몬스터 에너지·단계, 학생 수)
SELECT c.id, c.name, c.monster_name, c.growth_energy, c.stage,
       t.nickname AS teacher,
       (SELECT count(*) FROM users s WHERE s.classroom_id = c.id) AS students
FROM classroom c
JOIN users t ON t.id = c.teacher_id
ORDER BY c.id;

-- 반의 학생 목록 (보유 먹이 포함)
SELECT s.id, s.login_id, s.nickname,
       (SELECT count(*) FROM food_item f WHERE f.owner_id = s.id AND NOT f.consumed) AS food_left
FROM users s
WHERE s.classroom_id = :classroom_id AND s.role = 'STUDENT'
ORDER BY s.nickname;

-- 오늘의 표현 미션 (한국 날짜 기준)
SELECT c.name AS classroom, m.id AS mission_id, m.situation, o.id AS option_id, o.expression, o.correct
FROM expression_mission m
JOIN classroom c ON c.id = m.classroom_id
JOIN expression_option o ON o.mission_id = m.id
WHERE m.mission_date = (now() AT TIME ZONE 'Asia/Seoul')::date
ORDER BY c.id, o.id;

-- 확인 대기 중인 과제
SELECT a.id, c.name AS classroom, s.nickname AS student, a.title, a.submission_note, a.submitted_at
FROM assignment a
JOIN users s ON s.id = a.student_id
JOIN classroom c ON c.id = a.classroom_id
WHERE a.status = 'SUBMITTED'
ORDER BY a.submitted_at;

-- 학생별 성취·표현 정확도 (파일럿 분석)
SELECT c.name AS classroom, s.nickname,
       count(DISTINCT a.id) FILTER (WHERE a.status = 'APPROVED') AS approved_assignments,
       count(DISTINCT f.id) AS feeding_attempts,
       count(DISTINCT f.id) FILTER (WHERE f.success) AS feeding_success,
       round(100.0 * count(DISTINCT f.id) FILTER (WHERE f.success) / nullif(count(DISTINCT f.id), 0)) AS accuracy_pct
FROM users s
JOIN classroom c ON c.id = s.classroom_id
LEFT JOIN assignment a ON a.student_id = s.id
LEFT JOIN feeding_log f ON f.student_id = s.id
WHERE s.role = 'STUDENT'
GROUP BY c.name, s.nickname
ORDER BY c.name, s.nickname;

-- 최근 먹이 주기 기록 20건
SELECT f.created_at, c.name AS classroom, s.nickname, f.situation, f.chosen_expression, f.success, f.food_type
FROM feeding_log f
JOIN users s ON s.id = f.student_id
JOIN classroom c ON c.id = f.classroom_id
ORDER BY f.id DESC
LIMIT 20;


-- ============================================================================
-- [수정] 자주 하는 데이터 변경  (Manual Commit 모드 권장)
-- ============================================================================

-- 반 이름 / 몬스터 이름 변경
UPDATE classroom
SET name = :new_name, monster_name = :new_monster_name,
    updated_at = now() AT TIME ZONE 'Asia/Seoul'
WHERE id = :classroom_id;

-- 몬스터 에너지 보정 (단계도 기준에 맞게 함께 변경: 0~9 EGG, 10~29 BABY, 30~ GROWN)
UPDATE classroom
SET growth_energy = :energy,
    stage = CASE WHEN :energy >= 30 THEN 'GROWN' WHEN :energy >= 10 THEN 'BABY' ELSE 'EGG' END,
    updated_at = now() AT TIME ZONE 'Asia/Seoul'
WHERE id = :classroom_id;

-- 학생 별명 변경 (비밀번호는 SQL 로 바꾸지 말고 교사 화면에서 변경)
UPDATE users
SET nickname = :new_nickname, updated_at = now() AT TIME ZONE 'Asia/Seoul'
WHERE id = :student_id AND role = 'STUDENT';

-- 과제 피드백 수정
UPDATE assignment
SET teacher_comment = :comment, updated_at = now() AT TIME ZONE 'Asia/Seoul'
WHERE id = :assignment_id;

-- 미션 정답 바꾸기: 해당 미션의 정답을 모두 해제한 뒤 하나만 정답으로
UPDATE expression_option SET correct = false WHERE mission_id = :mission_id;
UPDATE expression_option SET correct = true  WHERE id = :option_id AND mission_id = :mission_id;

-- 성장 소식 올리기
INSERT INTO growth_news (classroom_id, message)
VALUES (:classroom_id, :message);

-- 특정 날짜에 표현 미션 추가 (선택지는 아래 문장으로 이어서 추가)
INSERT INTO expression_mission (classroom_id, mission_date, situation)
VALUES (:classroom_id, CAST(:mission_date AS date), :situation)
RETURNING id;

INSERT INTO expression_option (mission_id, expression, correct)
VALUES (:mission_id, :expression, CAST(:is_correct AS boolean));


-- ============================================================================
-- [점검] 데이터 일관성 확인 (결과가 0행이면 정상)
-- ============================================================================

-- 에너지와 단계가 맞지 않는 반
SELECT id, name, growth_energy, stage
FROM classroom
WHERE stage <> CASE WHEN growth_energy >= 30 THEN 'GROWN' WHEN growth_energy >= 10 THEN 'BABY' ELSE 'EGG' END;

-- 정답이 없는 표현 미션 (학생이 먹이 주기에 성공할 수 없음)
SELECT m.id, m.mission_date, m.situation
FROM expression_mission m
WHERE NOT EXISTS (SELECT 1 FROM expression_option o WHERE o.mission_id = m.id AND o.correct);

-- 확인 완료인데 먹이 지급 표시가 없는 과제
SELECT id, title, status, food_granted
FROM assignment
WHERE status = 'APPROVED' AND NOT food_granted;
