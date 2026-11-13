# 우리 반 과제 몬스터 (class-task-monster)

학생마다 수준에 맞는 영어 과제를 수행하고, 교사가 확인한 성취를 **먹이**로 모아 반 전체가 함께 **공동 몬스터**를 키우는 학습 도구입니다.

```
맞춤 과제 → 교사 피드백 → 먹이 주기(영어 표현 사용) → 공동 몬스터 성장 → 다음 도전
```

- 학생별 순위 대신, 각자의 도전이 우리 반 성장에 기여하는 구조입니다.
- 몬스터는 참여를 돕고, 실제 학습 여부는 교사가 과제 수행으로 확인합니다.

## 파일럿 범위

과제 카드 → 교사 확인 → 먹이 획득 → 몬스터 성장

## 핵심 규칙

| 항목 | 규칙 |
|---|---|
| 과제 상태 | 도전 중 → 확인 요청 → 선생님 확인 완료 / 다시 도전 |
| 먹이 획득 | 교사가 확인(승인)한 과제 1개당 먹이 1개, 종류(딸기·포도·사과·바나나·쿠키)는 무작위 |
| 먹이 주기 | 오늘의 표현 미션에서 **알맞은 표현을 골라야만 성공** (틀리면 먹이 유지, 다시 도전) |
| 성장 비율 | 먹이 1개 = 성장 에너지 1 (`app.monster.energy-per-food`) |
| 성장 단계 | 알(0) → 아기(10) → 성장(30), 단계가 바뀌면 "우리 반 성장 소식" 생성 |
| 반 관리 | 교사 1명이 여러 반을 생성·수정·삭제 (반 삭제 시 학생·과제·기록 함께 삭제) |

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Spring Security, JWT(jjwt), Spring Data JPA, Gradle |
| Database | H2 (개발용, DB 확정 전까지) — JPA 기반이라 드라이버/설정만 바꾸면 교체 가능 |
| Frontend | React (Vite) — 작업 예정 |

## 프로젝트 구조

```
class-task-monster/
├─ backend/                       # Spring Boot API 서버
│  └─ src/main/java/com/classmonster/
│     ├─ controller/              # 요청/응답 (REST API)
│     ├─ service/                 # 비즈니스 로직
│     ├─ repository/              # 데이터 접근 (Spring Data JPA)
│     ├─ domain/                  # 엔티티, enum
│     ├─ dto/                     # 요청/응답 DTO (기능별 하위 패키지)
│     ├─ security/                # JWT 발급·검증, 인증 필터
│     ├─ config/                  # 보안·CORS·JPA·시간 설정, 개발용 샘플 데이터
│     ├─ exception/               # 에러 코드, 공통 예외 처리
│     └─ util/
└─ frontend/                      # React 클라이언트 (작업 예정)
```

## 실행 방법 (Backend)

```bash
cd backend
./gradlew bootRun        # Windows: gradlew.bat bootRun
```

- API: http://localhost:8080
- H2 콘솔 (dev): http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:classmonster`, 사용자 `sa`)
- 테스트: `./gradlew test`

### 환경 변수

| 이름 | 설명 | 기본값 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev`(H2 + 샘플 데이터) / `prod` | `dev` |
| `JWT_SECRET` | JWT 서명 키 (32바이트 이상) | 개발용 값 — **운영에서는 반드시 변경** |
| `JWT_VALIDITY_SECONDS` | 토큰 유효 시간(초) | `43200` (12시간) |
| `CORS_ALLOWED_ORIGINS` | 프론트엔드 주소 | `http://localhost:5173` |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | prod DB 접속 정보 | - |

### 샘플 계정 (dev 프로필)

| 역할 | 아이디 | 비밀번호 |
|---|---|---|
| 교사 | `teacher` | `teacher1234` |
| 학생 | `jiwoo`, `minjun`, `seoyeon` | `student1234` |

샘플 반: 5학년 2반 / 몬스터 "몽글이" (에너지 18, 아기 단계), 오늘의 표현 미션 포함

## API 요약

인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더를 사용합니다.

### 인증
| Method | URL | 설명 |
|---|---|---|
| POST | `/api/auth/signup` | 교사 회원가입 |
| POST | `/api/auth/login` | 로그인 (교사/학생 공통) → accessToken |
| GET | `/api/auth/me` | 내 정보 |

### 교사 (`TEACHER`)
| Method | URL | 설명 |
|---|---|---|
| GET / POST | `/api/teacher/classrooms` | 내 반 목록 / 반 생성 |
| GET / PUT / DELETE | `/api/teacher/classrooms/{id}` | 반 조회 / 수정(반·몬스터 이름) / 삭제 |
| GET / POST | `/api/teacher/classrooms/{id}/students` | 학생 목록(지표 포함) / 학생 계정 생성 |
| PUT / DELETE | `/api/teacher/students/{id}` | 학생 수정(별명·비밀번호) / 삭제 |
| GET / POST | `/api/teacher/classrooms/{id}/assignments?status=` | 과제 목록(상태 필터) / 과제 지정 |
| PUT / DELETE | `/api/teacher/assignments/{id}` | 과제 수정 / 삭제 |
| POST | `/api/teacher/assignments/{id}/review` | 수행 확인 `{approved, comment}` → 승인 시 먹이 지급 |
| GET / POST | `/api/teacher/classrooms/{id}/missions` | 표현 미션 목록 / 등록 (날짜별 1개) |
| PUT / DELETE | `/api/teacher/missions/{id}` | 표현 미션 수정 / 삭제 |

### 학생 (`STUDENT`)
| Method | URL | 설명 |
|---|---|---|
| GET | `/api/student/dashboard` | 메인 화면 (반·별명, 몬스터, 오늘 도전, 먹이, 오늘의 표현, 성장 소식) |
| POST | `/api/student/assignments/{id}/submit` | 과제 확인 요청 `{note}` |
| POST | `/api/student/feed` | 먹이 주기 `{optionId}` — 알맞은 표현일 때만 성공 |
| GET | `/api/student/growth` | 성장 기록 (반 성장 소식 전체, 내 먹이 주기 기록) |

에러 응답 형식: `{ "code": "NO_FOOD", "message": "먹이가 없어요. ...", "fieldErrors": [] }`
