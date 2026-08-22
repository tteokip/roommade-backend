# roommade-backend

청년 자립 준비를 지원하는 플랫폼 RoomMade의 백엔드 서버

## 기술 스택

- Java 17
- Spring Framework 5.3.x (Spring Legacy, XML 기반 설정)
- MyBatis
- Gradle
- Tomcat (로컬 구동: Gretty 플러그인)
- MySQL (Docker Compose로 로컬 개발 환경 구성)
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

2. Docker Desktop이 설치되어 있고 실행 중인지 확인합니다.

   ```bash
   docker info
   ```

   정보가 정상적으로 출력되면 실행 중인 것입니다. 출력되지 않으면 Docker Desktop을 먼저 켜주세요.

3. 환경 변수 파일과 DB 접속 정보 파일을 준비합니다. 두 파일 모두 로컬 전용 값이며 커밋되지 않습니다(`.gitignore` 처리됨).

   | macOS / Linux / Git Bash | Windows (cmd) | Windows (PowerShell) |
   |---|---|---|
   | `cp .env.sample .env` | `copy .env.sample .env` | `Copy-Item .env.sample .env` |
   | `cp src/main/resources/db.properties.sample src/main/resources/db.properties` | `copy src\main\resources\db.properties.sample src\main\resources\db.properties` | `Copy-Item src\main\resources\db.properties.sample src\main\resources\db.properties` |

   `.env`의 포트·계정·DB명을 바꿨다면 `db.properties`도 함께 맞춰주세요.

4. Docker로 로컬 MySQL을 띄우고, 정상 기동(`healthy`)될 때까지 기다립니다.

   ```bash
   docker compose up -d
   docker compose ps
   ```

   `mysql` 서비스의 `STATUS`가 `healthy`로 바뀔 때까지 몇 초 걸립니다(최초 실행 시 이미지 다운로드 포함).

5. 빌드하고 서버를 기동합니다.

   | 작업 | macOS / Linux / Git Bash | Windows (cmd) | Windows (PowerShell) |
   |---|---|---|---|
   | 빌드 | `./gradlew build` | `gradlew.bat build` | `.\gradlew.bat build` |
   | 서버 기동 | `./gradlew appRun` | `gradlew.bat appRun` | `.\gradlew.bat appRun` |

   Gretty가 내장 Tomcat으로 `http://localhost:8080`에 서버를 띄웁니다.

6. 정상 기동 및 DB 연결 여부를 확인합니다.

   ```bash
   curl http://localhost:8080/
   # roommade-backend is running

   curl http://localhost:8080/health/db
   # {"status":"UP","database":"UP"}
   ```

## MySQL 컨테이너 종료

작업을 마치면 컨테이너를 내립니다. 두 명령은 동작이 다르니 구분해서 사용하세요.

| 명령 | 동작 |
|---|---|
| `docker compose down` | 컨테이너만 종료·삭제합니다. 데이터는 볼륨에 남아 다음 `up` 때 그대로 이어집니다. |
| `docker compose down -v` | 컨테이너와 **데이터 볼륨까지 삭제**합니다. 로컬 DB 데이터가 전부 사라지므로, 정말 초기화가 필요할 때만 사용하세요. |

## 기여 가이드

브랜치 전략, 커밋 컨벤션, PR 규칙, 코드 리뷰 규칙은
[CONTRIBUTING.md](./CONTRIBUTING.md)를 참고하세요.
