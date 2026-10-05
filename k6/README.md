# k6 부하 테스트

백엔드와 MySQL을 먼저 실행한 뒤 PowerShell에서 실행한다.

```powershell
& "C:\Program Files\k6\k6.exe" run -e PROFILE=smoke .\k6\load-test.js
& "C:\Program Files\k6\k6.exe" run -e PROFILE=load .\k6\load-test.js
& "C:\Program Files\k6\k6.exe" run -e PROFILE=stress .\k6\load-test.js
& "C:\Program Files\k6\k6.exe" run -e PROFILE=spike .\k6\load-test.js
& "C:\Program Files\k6\k6.exe" run -e PROFILE=soak .\k6\load-test.js
```

기본 프로필은 `load`, 기본 서버 주소는 `http://localhost:8080`이다. 다른 서버를 테스트할 때는
`BASE_URL`을 지정한다.

```powershell
& "C:\Program Files\k6\k6.exe" run `
  -e PROFILE=load `
  -e BASE_URL=http://localhost:8080 `
  .\k6\load-test.js
```

각 VU는 최초 실행 시 `loadtest.` 접두어가 붙은 고유 계정을 만든다. 테스트 계정은 자동 삭제되지 않는다.
동일한 실행 식별자가 필요하면 `-e RUN_ID=my-run`을 추가할 수 있지만, 이미 사용한 `RUN_ID`를 다시 쓰면
회원가입 중복 오류가 발생한다.

판정 기준은 업무 요청 실패율 1% 미만, 업무 요청 응답시간 p95 500ms 미만, 전체 check 성공률 99% 초과다.
