# AGENTS.md

roommade-backend는 청년 자립 준비를 지원하는 플랫폼 RoomMade의 백엔드 서버다.

이 문서는 코드 컨벤션과 작업 플로우의 정본이다. 이슈/브랜치/커밋/PR/코드리뷰 규칙은
[CONTRIBUTING.md](./CONTRIBUTING.md), 기술 스택과 디렉터리 구조는 [README.md](./README.md)를 따르며
여기서 중복 정의하지 않는다.

```bash
docker compose up -d   # 로컬 MySQL
./gradlew build         # 컴파일 + 테스트 + war 패키징
./gradlew test           # 테스트만
./gradlew appRun          # http://localhost:8080 (Gretty) — MySQL 필요 (Flyway가 기동 시 마이그레이션 적용)
```

---

## 0. 기본 원칙 (AI 에이전트용)

- 답변, 진행 상황, PR/이슈/리뷰 답글은 한국어로, 코드/식별자는 영어로, 주석은 한국어로 작성한다.
- 구현 전 요구사항을 이해하고 작업 계획을 간단히 설명한다.
- 추측하지 말고 파일, diff, 문서, 명령 결과로 근거를 확인한다. 서드파티 라이브러리·프레임워크·SDK·CLI
  사용법은 공식 문서나 사용 가능한 문서 조회 도구(예: Context7 MCP, 없으면 공식 문서 웹 검색)로 확인한다.
- 사용자 변경분을 되돌리지 않는다. Git 상태가 불분명하면 실제 파일 내용을 기준으로 조심스럽게 작업하고,
  변경 후에는 관련 검증(빌드/테스트 등)으로 확인한다.

---

## 1. 기술 스택 제약

Spring Boot가 아니라 순수 Spring Framework(5.3.x) MVC를 WAR로 패키징해 서블릿 컨테이너에서 돌린다.
서블릿은 `javax.servlet`이다(**jakarta 아님** — jakarta 계열 라이브러리를 섞지 않는다). 설정은 전부 XML이며
(`root-context.xml`, `servlet-context.xml`, `web.xml`), 자바 `@Configuration` 클래스를 임의로 추가하지 않는다.
버전과 전체 의존성 목록은 `build.gradle`을 확인한다.

---

## 2. 패키지 / 도메인 구조

도메인은 `user`, `preparation`, `room`, `coin`, `quiz`, `living`, `house`, `policy`, `financialproduct` 9개이며
(전체 트리는 README.md 참고), 각 도메인은 `controller` / `service` / `mapper` / `dto/{request,response}` /
`exception`으로 나눈다. `global/common`(공통 기능) · `global/config`(공통 설정) · `global/exception`(공통 예외)은
특정 도메인에 속하지 않는 코드 전용이며, 도메인 패키지로 옮기지 않는다.

첫 도메인을 구현하고 검증한 뒤, 그 구조와 공통 패턴을 이후 도메인의 참조 구현으로 사용한다.

---

## 3. 네이밍 컨벤션

| 대상 | 규칙 | 예 |
|---|---|---|
| 패키지 | 소문자·단수 | `com.roommade.domain.user` |
| 컨트롤러 / 매퍼 | `{도메인}Controller` / `{도메인}Mapper` | `UserController` / `UserMapper` |
| 매퍼 XML | `resources/mapper/{도메인}/{도메인}Mapper.xml` | `mapper/user/UserMapper.xml` |
| 요청 DTO | `{동작}{대상}Request` | `UserSignupRequest` |
| 응답 DTO | `{대상}{용도}Response` | `UserProfileResponse` |
| 매퍼 메서드 | 조회 `find*`/`count*`/`exists*`, 변경 `insert*`/`update*`/`delete*` | `findByUserId` |
| DB → 필드 | `snake_case` → `camelCase` | `user_id` → `userId` |
| 서비스 인터페이스 / 구현체 | `{도메인}Service` / `{도메인}ServiceImpl` | `UserService` / `UserServiceImpl` |

Service는 인터페이스와 구현체로 분리한다.

---

## 4. DTO 규칙

- **별도 Entity/VO 계층을 기본으로 두지 않는다.** Mapper는 조회 결과를 Response DTO로 직접 반환한다.
- 단순 등록·수정에서는 Request DTO를 Mapper 파라미터로 그대로 쓸 수 있다.
- 인증 정보, 경로 변수 및 시스템 관리 값은 클라이언트 Request DTO에 포함하지 않는다. 각각 인증 컨텍스트,
  Controller 경로 파라미터, DB 또는 Service에서 관리한다.
- DTO로 표현하기 어려운 비즈니스 상태나 계산 모델이 필요해지면 도메인 객체를 별도로 도입한다.
- Mapper 파라미터가 2개 이상이면 `@Param`을 반드시 붙인다.
- DTO에는 `@Setter`를 두지 않는다. GET 쿼리 파라미터를 `@ModelAttribute` DTO로 바인딩할 때는 기본 생성자를
  제공하고, Controller의 `@InitBinder`에서 `binder.initDirectFieldAccess()`를 설정한다.

---

## 5. 응답 포맷 / 예외 처리

`/api` 아래 응답은 `ApiResponse<T>`로 통일하며 `success`, `code`, `message`, `data`를 포함한다. 실패 응답의
`data`는 기본적으로 `null`이고, 검증 실패일 때만 `errors`를 추가한다.

비즈니스 규칙 위반은 공통 `BusinessException`으로 표현하고, 오류 종류는 예외 클래스를 늘리지 않고 도메인별
`{도메인}ErrorCode` enum 상수로 구분한다. 성공 코드도 도메인별 `{도메인}SuccessCode` enum으로 관리한다.

**컨트롤러에서 try-catch로 비즈니스 예외 응답을 만들지 않는다.** 공통 `GlobalExceptionHandler`가 전역 처리한다.

---

## 6. MyBatis 매퍼 규칙

- SQL 파라미터에는 `#{}`를 사용하고 `${}`는 원칙적으로 사용하지 않는다. 동적 정렬 컬럼처럼 불가피한 경우에는
  허용 목록으로 검증한 값만 사용한다.
- `SELECT *` 금지 — 컬럼을 명시한다. 반복되면 `<sql>` + `<include>`로 뺀다.
- 평면 응답은 `resultType`, 중첩 1:1은 `resultMap` + `<association>`, 중첩 1:N은 `resultMap` + `<collection>`을
  쓴다. `<association>`/`<collection>`이 들어간 resultMap은 자동 매핑이 꺼지므로 나머지 컬럼도 `<result>`로
  전부 명시한다.
- `mybatis-config.xml`에서 `mapUnderscoreToCamelCase`를 활성화한다. 단순한 `snake_case` 컬럼을 `camelCase`
  필드로 매핑하기 위한 `AS` 별칭은 사용하지 않는다. 집계·계산 결과나 의미가 다른 필드명으로 매핑할 때는
  명시적인 별칭을 사용한다.

---

## 7. 트랜잭션 경계

운영 코드에서 `@Transactional`은 Service 구현체에만 붙인다(조회는 `readOnly = true`). Controller와 Mapper에는
붙이지 않는다 —
`<tx:annotation-driven>`이 root-context에만 있고 root-context의 component-scan은 `@Controller`를 제외하므로,
Controller에 붙여도 걸리지 않는다.

---

## 8. DB 마이그레이션

- 스키마 변경은 `src/main/resources/db/migration/`에 `V{다음 번호}__{설명}.sql`을 새로 추가한다
  (README 참고).
  **이미 적용된 마이그레이션 파일은 절대 수정하지 않는다** — 고치면 Flyway가 checksum 불일치로 기동을 막는다.
- 마이그레이션 실패(DB 미기동 포함) 시 앱이 기동되지 않는 fail-fast 구조다. 로컬 DB를 초기화해야 하면
  `docker compose down -v && docker compose up -d`를 쓰되, **볼륨 삭제는 사용자 승인 후 실행한다.**
- 금액 컬럼은 자바에서 `BigDecimal`로 다룬다(`double`/`float` 금지).

---

## 9. 환경 설정

로컬 전용 값은 `.env`(docker compose용, 샘플 `.env.sample`)와 `src/main/resources/db.properties`(앱 DataSource용,
샘플 `db.properties.sample`) 두 파일로 나뉘며 둘 다 커밋하지 않는다(README 참고). **비밀값은 어떤 파일에도
커밋하지 않는다.**

---

## 10. 테스트 규칙

JUnit 5 + Mockito + AssertJ + spring-test 조합을 쓴다. 아직 `build.gradle`에 테스트 의존성이 없으므로 첫
테스트를 추가할 때 함께 넣는다.

- Service 단위 테스트는 Spring Context와 DB 없이 실행하고, Mapper 등 의존성은 Mockito로 대체한다.
- Controller 테스트는 MockMvc와 Mock Service를 사용해 요청 매핑, HTTP 상태와 JSON 응답을 검증한다.
- Mapper 통합 테스트는 Spring TestContext와 Docker Compose MySQL을 사용하고, 클래스 레벨 `@Transactional`로
  테스트 변경을 자동 롤백한다.
- Spring Context 로딩 방식은 첫 Mapper 통합 테스트를 추가할 때 현재 설정 구조를 기준으로 구성하고 검증한다.
- 일반 객체 단언은 AssertJ `assertThat`을 사용한다. MockMvc 등 테스트 도구의 전용 matcher는 허용한다.

---

## 11. Git / 이슈 / PR

브랜치, 커밋, PR 및 코드 리뷰 규칙은 [CONTRIBUTING.md](./CONTRIBUTING.md)를 따른다.

- 작업 커밋은 `type: 설명` 형식을 사용한다.
- PR 제목만 `[#이슈번호] type: 작업 내용` 형식을 사용한다.
- 이슈는 `.github/ISSUE_TEMPLATE`의 제목 접두어와 기본 라벨을 따른다.
