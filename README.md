# 우리 반 과제 몬스터 (class-task-monster)

학생마다 수준에 맞는 영어 과제를 수행하고, 교사가 확인한 성취를 **먹이**로 모아 반 전체가 함께 **공동 몬스터**를 키우는 학습 도구입니다.

```
맞춤 과제 → 교사 피드백 → 먹이 주기(영어 표현 사용) → 공동 몬스터 성장 → 다음 도전
```

- 학생별 순위 대신, 각자의 도전이 우리 반 성장에 기여하는 구조입니다.
- 몬스터는 참여를 돕고, 실제 학습 여부는 교사가 과제 수행으로 확인합니다.

## 현재 상태

> ⚠️ **현재는 로컬 개발 환경에서만 동작합니다.** 외부에서 접속할 수 있는 서버에는 아직 배포하지 않았습니다.

| 항목 | 상태 |
|---|---|
| 로컬 실행 (H2 / 로컬 PostgreSQL 17) | ✅ 동작 확인 (학생·교사 화면, 데스크톱·모바일) |
| 자동 테스트 (H2 + PostgreSQL 17.11) | ✅ 통과 |
| 공용 DB (Supabase PostgreSQL, Seoul) | 🔧 프로젝트 생성·`remote` 프로필 준비 완료 — 운영 가이드: [docs/DATABASE.md](docs/DATABASE.md) |
| Docker Compose 구성 (`docker-compose.yml`, Dockerfile) | 📝 작성 완료 · **실제 컨테이너 실행은 미검증** (개발 PC 에 Docker 미설치) |
| 서버 배포 / 도메인 / HTTPS | ⏳ 진행 전 |

- 로컬 실행 시 권장 방법: **B'. Docker 없이 로컬 PostgreSQL** (아래 [실행 방법](#실행-방법) 참고)
- 샘플 계정(`teacher` / `jiwoo` 등)과 `.env.example` 의 기본 비밀번호·JWT 키는 **로컬 확인용**입니다. 배포 시 반드시 변경하세요.

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
| Database | PostgreSQL 17 + Flyway 마이그레이션 (로컬: `gradlew localDb`) · 빠른 개발용 H2 |
| Frontend | React 19, Vite 7, React Router 7, axios (반응형: 데스크톱·모바일) |
| Test | JUnit 5, MockMvc, embedded-postgres (Docker 없이 실제 PostgreSQL 로 통합 테스트) |
| Infra (배포 준비) | Docker Compose (PostgreSQL + Spring Boot + nginx) — 미검증 |

## 프로젝트 구조

```
class-task-monster/
├─ docker-compose.yml             # db + backend + frontend 전체 실행
├─ .env.example                   # 환경 변수 예시 (복사해서 .env 로 사용)
├─ backend/                       # Spring Boot API 서버 (Dockerfile 포함)
│  ├─ .localdb/                   # 로컬 PostgreSQL 데이터 (gradlew localDb, git 제외)
│  ├─ src/test/java/com/classmonster/
│  │  ├─ PilotFlowIntegrationTest.java          # 파일럿 흐름 통합 테스트 (H2)
│  │  ├─ PostgresPilotFlowIntegrationTest.java  # 같은 테스트를 PostgreSQL 17 + Flyway 로
│  │  └─ devtools/LocalPostgresServer.java      # gradlew localDb 실행기
│  ├─ src/main/resources/db/migration/          # Flyway SQL (V1__init_schema.sql ...)
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
└─ frontend/                      # React 클라이언트 (Dockerfile, nginx.conf 포함)
   └─ src/
      ├─ api/                     # axios 클라이언트(JWT 헤더 자동 첨부), API 모듈
      ├─ context/                 # 로그인 상태 (AuthContext)
      ├─ routes/                  # 역할별 접근 제어
      ├─ components/              # 공통 / 몬스터(SVG) / 학생 화면 컴포넌트
      ├─ pages/                   # 로그인, 학생(메인·나의 과제·성장 기록), 교사(반 목록·반 관리 탭)
      ├─ styles/                  # 디자인 토큰, 화면별 스타일
      └─ utils/
```

## 화면 구성

**학생 메인 (시안 기준)**
| 영역 | 내용 |
|---|---|
| 상단 | 서비스 이름, 반 이름, 학생 별명(로그아웃) |
| 왼쪽 | 우리 반 몬스터 / 나의 과제 / 성장 기록 |
| 중앙 | 공동 몬스터와 말풍선, 성장 에너지(예: 18 / 30), 알 → 아기 → 성장 단계 |
| 오른쪽 위 | ○○의 오늘 도전: 과제 카드와 교사 확인 상태, "다 했어요!"(확인 요청) |
| 오른쪽 아래 | 내 먹이, 오늘의 표현 미션, 함께 말할 표현 선택, 먹이 주기 |
| 하단 | 우리 반 성장 소식 |

**교사 관리**: 내 반 목록·만들기 → 반 관리 탭 (확인 대기 / 과제 지정 / 학생 / 표현 미션 / 반 설정)

**모바일 (폭 760px 이하)**
- 학생: 왼쪽 메뉴가 상단 3칸 메뉴로 바뀌고, 몬스터 → 오늘 도전 → 먹이 주기 → 성장 소식 순서로 세로 배치
- 교사: 반 관리 탭 5개를 한 줄로 균등 배치, 학생·과제 표는 행마다 카드 형태로 표시

## 실행 방법

> 모든 방법은 **로컬 PC 에서 실행**하는 방식입니다. (외부 배포 전)

| 방법 | 용도 | DB | 접속 주소 | 확인 상태 |
|---|---|---|---|---|
| A. Docker Compose 전체 실행 | 시연·배포와 같은 환경 | PostgreSQL (컨테이너) | http://localhost:3000 | 미검증 |
| B. DB만 Docker + IDE 개발 | 일상 개발 | PostgreSQL (컨테이너) | http://localhost:5173 | 미검증 |
| **B'. Docker 없이 로컬 PostgreSQL** (권장) | Docker 를 못 쓰는 PC 에서 개발 | PostgreSQL 17 (`gradlew localDb`) | http://localhost:5173 | ✅ |
| C. H2 빠른 실행 | DB 설치 없이 바로 확인 | H2 인메모리 (재시작 시 초기화) | http://localhost:5173 | ✅ |

필요한 도구: **Java 21**, **Node.js 22 이상** (A, B 는 Docker Desktop 추가 필요)

환경 변수 파일을 만듭니다 (A, B 필수 / B', C 는 없으면 기본값 사용).

```bash
cp .env.example .env     # 이후 POSTGRES_PASSWORD, JWT_SECRET 값 수정
```

### A. Docker Compose 전체 실행 (Docker Desktop 필요 · 미검증)

> Docker Desktop 은 BIOS 가상화(VT-x)와 Windows "가상 머신 플랫폼"(WSL2)이 켜져 있어야 설치·실행됩니다.

```bash
docker compose up -d --build
```

- `db`(PostgreSQL 17) → `backend`(prod 프로필, Flyway 로 스키마 생성) → `frontend`(nginx, `/api` 를 backend 로 전달) 순서로 뜹니다.
- DB 데이터는 `pgdata` 볼륨에 유지됩니다. 완전히 초기화: `docker compose down -v`
- 로그 확인: `docker compose logs -f backend`

### B. DB만 Docker, 백엔드·프론트는 로컬 실행

```bash
docker compose up -d db
cd backend && ./gradlew bootRun --args='--spring.profiles.active=local'
cd frontend && npm install && npm run dev
```

- `local` 프로필은 루트 `.env` 의 `POSTGRES_*` 값으로 `localhost:5432` 에 접속합니다.

### B'. Docker 없이 로컬 PostgreSQL 17 (관리자 권한·설치 불필요)

Docker Desktop 을 설치할 수 없는 환경(BIOS 가상화 꺼짐, 관리자 권한 없음 등)에서 사용합니다.
Gradle 이 Maven Central 에서 PostgreSQL 17 실행 파일을 받아 프로젝트 안에서 실행합니다.

```bash
# 터미널 1: DB 실행 (Ctrl+C 로 종료)
cd backend && ./gradlew localDb

# 터미널 2: 백엔드
cd backend && ./gradlew bootRun --args='--spring.profiles.active=local'

# 터미널 3: 프론트엔드
cd frontend && npm run dev
```

- 처음 실행할 때 `.env` 의 `POSTGRES_USER` 사용자와 `POSTGRES_DB` 데이터베이스를 자동으로 만듭니다.
- 데이터는 `backend/.localdb/` 에 저장되어 재시작해도 유지됩니다. 초기화하려면 DB 를 끄고 이 폴더를 삭제하세요.
- 터미널을 닫아 DB 가 백그라운드에 남았다면: `./gradlew localDbStop`
- `local` 프로필이므로 Docker 의 `db` 서비스(방법 B)와 같은 설정으로 접속합니다. (둘 다 5432 포트라 동시에 실행하지 않기)

### D. 공용 DB(Supabase)에 붙여 실행 (`remote` 프로필)

여러 사람이 같은 데이터를 보고 관리할 때 사용합니다. 접속 정보 입력 방법·**DBeaver 연결**·테이블 설명·수정 규칙·백업은 **[docs/DATABASE.md](docs/DATABASE.md)**, 바로 실행할 수 있는 관리용 SQL 은 **[docs/sql/maintenance.sql](docs/sql/maintenance.sql)** 에 있습니다. (관리용 SQL 은 `MaintenanceSqlTest` 가 실제 스키마에서 실행되는지 자동 검사)

```bash
# .env 에 DB_URL / DB_USERNAME / DB_PASSWORD 입력 후
cd backend && ./gradlew bootRun --args='--spring.profiles.active=remote'
cd frontend && npm run dev
```

### C. H2 로 빠르게 실행 (기본 `dev` 프로필)

```bash
cd backend && ./gradlew bootRun
cd frontend && npm install && npm run dev
```

### Frontend 참고 (Node.js 22 이상)

```bash
cd frontend
npm install
npm run dev              # http://localhost:5173 (/api 요청은 8080 으로 프록시)
```

- 배포 빌드: `npm run build` → `frontend/dist`
- 백엔드가 다른 주소라면 `VITE_API_BASE_URL` 환경 변수로 지정

### Backend 참고

- API: http://localhost:8080 (Windows 는 `gradlew.bat`)
- H2 콘솔 (dev): http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:classmonster`, 사용자 `sa`)
- 테스트: `./gradlew test` — H2 와 **실제 PostgreSQL 17.11**(embedded, Docker 불필요) 두 환경에서 같은 흐름 테스트를 실행합니다.

### 프로필

| 프로필 | DB | 스키마 | 샘플 데이터 |
|---|---|---|---|
| `dev` (기본) | H2 인메모리 (PostgreSQL 호환 모드) | JPA 자동 생성 | O |
| `local` | localhost:5432 PostgreSQL (`gradlew localDb` 또는 `docker compose up -d db`) | Flyway | `SEED_DATA` (기본 O) |
| `remote` | 공용 Supabase PostgreSQL (`.env` 의 `DB_URL`) | Flyway + validate | `SEED_DATA` (기본 X) |
| `prod` | `DB_URL` 의 PostgreSQL | Flyway + validate | `SEED_DATA` (기본 X) |

스키마 변경은 엔티티 수정 + `db/migration/V{n}__설명.sql` 추가로 관리합니다. (이미 적용된 V 파일은 수정하지 않기)

### 환경 변수

| 이름 | 설명 | 기본값 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` / `local` / `prod` | `dev` |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT` | Docker PostgreSQL 설정 (`.env`) | `classmonster` / - / 5432 |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_POOL_SIZE` | prod DB 접속 정보 (compose 가 자동 설정) | - / - / - / 10 |
| `JWT_SECRET` | JWT 서명 키 (32바이트 이상) | 개발용 값 — **운영에서는 반드시 변경** |
| `JWT_VALIDITY_SECONDS` | 토큰 유효 시간(초) | `43200` (12시간) |
| `SEED_DATA` | 시연용 샘플 데이터 생성 | local: `true`, prod: `false` |
| `CORS_ALLOWED_ORIGINS` | 프론트엔드 주소 (개발 서버 직접 호출 시) | `http://localhost:5173` |
| `FRONTEND_PORT` | Docker 실행 시 접속 포트 | `3000` |

### 샘플 계정 (dev 프로필, 또는 SEED_DATA=true)

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

## 변경 이력

| 단계 | 내용 |
|---|---|
| 1. 초기 설정 | README, .gitignore |
| 2. 백엔드 | Spring Boot MVC 계층 구조, JWT 인증, 반·학생·과제·표현 미션·먹이·성장 API, H2 + 샘플 데이터, 파일럿 흐름 통합 테스트 |
| 3. 프론트엔드 | React 학생 화면(시안 기준 메인·나의 과제·성장 기록), 교사 관리 화면(반 목록·반 관리 탭), 몬스터 SVG |
| 4. DB 전환 | PostgreSQL 17 + Flyway 마이그레이션(V1), 프로필 분리(dev / local / prod), 루트 `.env` 설정, Docker Compose·Dockerfile·nginx 작성(배포 준비) |
| 5. 로컬 PostgreSQL | Docker 없이 실행하는 `gradlew localDb` / `localDbStop`, 테스트용 PostgreSQL 17.11 고정 |
| 6. 모바일·문서 | 모바일 레이아웃 개선(학생 상단 메뉴 3칸, 교사 탭 한 줄, 표 → 카드), README 현재 상태(로컬 전용)·변경 이력 정리 |
| 7. 공용 DB | Supabase 연결용 `remote` 프로필, V2 마이그레이션(RLS·시간 기본값·테이블/컬럼 설명), DB 운영 가이드·사람별 계정 SQL (`docs/`) |

## 다음 단계 (예정)

- Docker Desktop 사용 가능한 환경에서 `docker compose up` 실제 실행 검증
- 서버 배포 (호스팅 선택, 도메인·HTTPS, 운영용 비밀번호·JWT 키 설정)
- 파일럿 운영 후 교사용 지표(과제 정확도, 다음 도전 참여) 화면 보강
