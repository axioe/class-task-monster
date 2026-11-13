# 우리 반 과제 몬스터 (class-task-monster)

학생마다 수준에 맞는 영어 과제를 수행하고, 교사가 확인한 성취를 **먹이**로 모아 반 전체가 함께 **공동 몬스터**를 키우는 학습 도구입니다.

```
맞춤 과제 → 교사 피드백 → 먹이 주기(영어 표현 사용) → 공동 몬스터 성장 → 다음 도전
```

- 학생별 순위 대신, 각자의 도전이 우리 반 성장에 기여하는 구조입니다.
- 몬스터는 참여를 돕고, 실제 학습 여부는 교사가 과제 수행으로 확인합니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Spring Security, JWT, Spring Data JPA, Gradle |
| Database | H2 (개발용, DB 확정 전까지) |
| Frontend | React (Vite) |

## 프로젝트 구조

```
class-task-monster/
├─ backend/    # Spring Boot API 서버 (MVC 계층 구조)
└─ frontend/   # React 클라이언트
```

## 파일럿 범위

과제 카드 → 교사 확인 → 먹이 획득 → 몬스터 성장
