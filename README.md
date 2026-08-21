# roommade-backend

청년 자립 준비를 지원하는 플랫폼 RoomMade의 백엔드 서버

## 기술 스택

- Java 17
- Spring Framework 5.3.x (Spring Legacy, XML 기반 설정)
- MyBatis
- Gradle
- Tomcat (로컬 구동: Gretty 플러그인)
- MySQL (예정 — DB 연결은 추후 Docker로 통일 예정)
- Lombok
- Logback

## 디렉터리 구조

```text
src/main/java/com/roommade/
├── domain/                    # 비즈니스 도메인 (도메인 확정 후 추가)
│   └── {domain}/
│       ├── controller/        # API 진입점
│       ├── dto/               # 요청·응답 객체
│       ├── mapper/            # MyBatis 매퍼 인터페이스
│       └── service/           # 비즈니스 로직
└── global/
    ├── common/                # 공통 기능
    ├── config/                # 공통 설정
    └── exception/             # 공통 예외

src/main/resources/
├── db.properties.sample   # DB 접속 정보 샘플 (실제 db.properties는 gitignore)
├── logback.xml
└── mapper/                 # MyBatis XML 매퍼

src/main/webapp/WEB-INF/
├── web.xml
└── spring/
    ├── root-context.xml                 # DataSource, MyBatis, 트랜잭션 설정
    └── appServlet/servlet-context.xml   # DispatcherServlet(MVC) 설정
```

## 로컬 실행 방법

> Windows 사용자는 Git Bash를 사용하면 macOS/Linux 명령어를 그대로 쓸 수 있습니다.
> cmd나 PowerShell을 사용한다면 아래 표의 운영체제별 명령어를 참고하세요.

1. 저장소를 clone 합니다.
2. DB 접속 정보 파일을 준비합니다. (아직 값이 없어도 서버 기동에는 문제 없습니다 —
   MyBatis 쿼리를 실제로 호출할 때만 DB 연결이 필요합니다. DB 환경은 추후 Docker로
   통일할 예정입니다.)

   | macOS / Linux / Git Bash | Windows (cmd) | Windows (PowerShell) |
   |---|---|---|
   | `cp src/main/resources/db.properties.sample src/main/resources/db.properties` | `copy src\main\resources\db.properties.sample src\main\resources\db.properties` | `Copy-Item src\main\resources\db.properties.sample src\main\resources\db.properties` |

3. 빌드하고 서버를 기동합니다.

   | 작업 | macOS / Linux / Git Bash | Windows (cmd) | Windows (PowerShell) |
   |---|---|---|---|
   | 빌드 | `./gradlew build` | `gradlew.bat build` | `.\gradlew.bat build` |
   | 서버 기동 | `./gradlew appRun` | `gradlew.bat appRun` | `.\gradlew.bat appRun` |

   Gretty가 내장 Tomcat으로 `http://localhost:8080`에 서버를 띄웁니다.

4. 정상 기동 여부를 확인합니다.

   ```bash
   curl http://localhost:8080/
   # roommade-backend is running
   ```

## 기여 가이드

브랜치 전략, 커밋 컨벤션, PR 규칙, 코드 리뷰 규칙은
[CONTRIBUTING.md](./CONTRIBUTING.md)를 참고하세요.
