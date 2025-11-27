
# Gym ERP Backend

헬스장 회원·직원·PT 일정을 통합 관리하는 웹 ERP 프로젝트의 **백엔드 서버**입니다.  
트레이너 PT 일정 / 휴가 / 기타 일정과 회원권·PT횟수를 연동하여, 예약 가능 여부를 검증하고 로그를 관리합니다.

---

##  사용 기술
- Language: Java 17
- Framework: Spring Boot 3.0
- Persistence: MyBatis
- DB: Oracle XE
- Build: Maven
-인프라 및 배포
  - Docker, Docker Network
  - AWS EC2 (Linux)
  - Nginx (Reverse Proxy, HTTPS)

---

##  주요 기능

### 1. 직원 일정 관리
- PT / 휴가 / 기타 일정 유형별 등록, 수정, 삭제
- `react-big-calendar`와 연동되는 일정 조회 API 제공

### 2. PT 예약 검증 로직
- 회원권 테이블의 등록일·종료일과 `SYSDATE` 비교 → 유효 회원권만 예약 가능
- PT 전체 횟수 / 남은 횟수 > 0 인 경우에만 일정 등록
- MyBatis Mapper에서 `EXISTS / NOT EXISTS` 서브쿼리로 유효성 검사

### 3. 일정 중복 방지
- 동일 트레이너 기준, 시작·종료 시간이 겹치는 PT/기타/휴가 일정 등록 차단
- 휴가 일정 등록 시 기간이 겹치는 다른 휴가 일정 존재 여부 검사

### 4. PT 취소·변경 로그
- PT 취소 시 사용한 회차 복구
- `PT_LOG` 테이블에 변경·취소 이력 기록

---

##  개인 기여 (윤재원)

- **스케줄·PT예약 도메인 설계**
  - 직원, 회원, 스케줄, PT등록, 회원권, PT_LOG 테이블 설 관계 정의
- **비즈니스 로직 구현**
  - PT 일정 등록/수정/취소 API
  - 회원권 유효성·남은 PT횟수 검증, 일정 겹침 검사
- **배포 환경 구축**
  - Docker 기반 Oracle XE / Spring Boot 컨테이너 구성
  - Nginx 리버스 프록시 설정, `bonobono.online` 도메인 HTTPS 적용(Let's Encrypt)
  - 헬스ERP: https://bonobono.online/ 배포, email/비밀번호: admin 

---














# Acorn  final project repository

## 브랜치 작업 규칙
1. main branch: 배포용 브랜치
2. develop branch: 통합 브랜치
3. feature branch: 모든 작업은 feature 브랜치에서 파생하여 'feature/기능이름/작업자명' 형식으로 작업해주세요.

### PR 규칙
1. 2명 이상의 검토 필요.
2. 코드 리뷰하고 PR.


### 공유할 내용

# <JUnit 테스트 사용 안내>

## 한 번씩 읽어주시고 테스트 코드로 꾸준히 테스트를 부탁드립니다!

### 1. 사용 목적

1. 오류를 줄이고자 테스트를 사용하기로 했습니다. 
2. 모든 테스트는 공통된 테스트 환경(H2 메모리 데이터베이스, Maven 기반, GitHub Actions 자동화)에서 수행됩니다.

### 2. 테스트 사용 방법

1. 테스트는 **H2 인메모리 데이터베이스**에서 실행됩니다.
2. 설정 파일은 `src/test/resources/application-test.properties` 에 있습니다.
3. 데이터베이스 스키마와 데이터는 Oracle 문법과 호환됩니다. `schema-h2.sql`, `data-h2.sql` 파일로 자동 로드됩니다.
4. 테스트 클래스에는 반드시 `@ActiveProfiles("test")` 어노테이션을 선언하여 H2 설정을 적용해야 합니다.
5. IDE에서 테스트 클래스 우클릭 → **Run As → JUnit Test** 

### 3. 테스트 결과

1. 테스트가 통과하면 초록색 바(✅)로 표시됩니다.
2. 테스트 실패 시 콘솔 로그에 원인이 표시되며, SQL 오류, Bean 주입 문제, 데이터 누락 등 원인을 확인할 수 있습니다.
3. 테스트 실행 후 데이터베이스는 자동으로 롤백되어 항상 초기 상태를 유지합니다.
4. Pull Request 생성 또는 push할 경우, **GitHub Actions**가 자동으로 테스트를 실행합니다.

### 4. PR

1. 현재는 오류가 있어도 PR 가능합니다. 
2. 주요 기능 완성되면 테스트 오류 없이 초록색 결과 나올 때만 PR 가능하도록 변경 예정입니다.

### 5. 테스트에 사용하는 주요 어노테이션

| 어노테이션 | 설명 |
| --- | --- |
| `@SpringBootTest` | 스프링 전체 컨텍스트를 로드하여 통합 테스트 수행 |
| `@MybatisTest` | Mapper, SQL 등 Repository 계층만 테스트 |
| `@AutoConfigureMockMvc(addFilters = false)` | MockMvc 객체 자동 주입 (필터 비활성화) |
| `@Transactional` | 테스트 실행 후 DB 상태 자동 롤백 |
| `@ActiveProfiles("test")` | H2 테스트 환경 설정 사용 |
| `@DisplayName("테스트 설명")` | 테스트 목적을 명확하게 표시 |

### 6. 기본 테스트 구조  예시(Given - When - Then)

모든 테스트는 다음 세 단계로 작성합니다.

```java
@Test
@DisplayName("회원 등록 테스트")
void registerMember() {
    // Given (입력값 준비)
    MemberRequest dto = new MemberRequest("홍길동", "남", "010-1234-5678", "test@test.com");

    // When (기능 실행)
    MemberResponse result = memberService.registerMember(dto);

    // Then (결과 검증)
    assertNotNull(result);
    assertEquals("홍길동", result.getName());
}

```

### 7. 참고 및 주의사항

1. 테스트 클래스명은 반드시 `*Test.java`로 끝나야 합니다.
2. spring.sql.init.mode=always 설정이 반드시 필요합니다.
    
    (이 설정이 있어야 H2 스키마 및 데이터가 자동 반영됩니다.) (현재 추가해놨습니다)
