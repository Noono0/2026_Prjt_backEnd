# prjt-backend-operational

Spring Boot 기반 운영 API 서버입니다. MyBatis·JPA 혼용, Spring Security(세션 + DB 역할·메뉴), springdoc으로 OpenAPI를 노출합니다.

## 기술 스택

| 구분 | 내용 |
|------|------|
| 언어·런타임 | Java 17 (Gradle toolchain) |
| 프레임워크 | Spring Boot 3.3.x (Web, Security, Validation, Data JPA, Mail, Actuator) |
| 데이터 | MySQL, MyBatis 3.x, Hibernate(JPA) |
| API 문서 | [springdoc-openapi](https://springdoc.org/) (OpenAPI 3) — Swagger UI + 정적 Scalar 페이지 |
| 기타 | p6spy(개발 SQL 로그), Bucket4j·Caffeine(레이트 리밋), AWS SDK v2(S3 호환 스토리지) 등 |

## 로컬 실행

```bash
# Windows
.\gradlew.bat bootRun

# macOS / Linux
./gradlew bootRun
```

- 기본 포트: **8080** (`server.port`)
- 기본 프로필: **local** (`spring.profiles.default`)
- 다른 프로필: `SPRING_PROFILES_ACTIVE=dev` 등

### 디버그 포트(선택)

Windows PowerShell 예시 — 포트는 5005 대신 5006 등으로 바꿔도 됩니다.

```powershell
$env:JAVA_TOOL_OPTIONS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'
.\gradlew.bat bootRun
```

### API 문서 (springdoc이 켜진 환경)

| 구분 | 경로 |
|------|------|
| OpenAPI JSON | `GET /v3/api-docs` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Scalar | `http://localhost:8080/scalar/index.html` |

`application-prod.yml` 등에서 `springdoc.api-docs.enabled` / `swagger-ui.enabled` 가 `false` 이면 스펙·Swagger UI는 비활성화됩니다. Scalar 정적 HTML은 남아 있어도 스펙 URL을 불러오지 못할 수 있습니다.

---

## (참고) 리팩터링 샘플 메모

아래는 공개 리포지토리 구조를 기준으로 재정리한 **서비스별 디렉토리 구조 샘플** 설명입니다.

## 분석 요약
원본 리포지토리에서는 다음 구조가 확인됐습니다.

- 루트 패키지: `prjt.backend.operational`
- 상위 모듈: `audit`, `auth`, `bootstrap`, `common`, `domain`, `member`, `menu`, `paging`, `repo`, `security`, `web`
- `member` 아래에 `controller`, `domain` 이 있었고, `AdminCatalogController`, `AdminOrderController` 가 함께 있어 역할이 섞여 있음
- `menu` 는 이미 `dto`, `mapper`, `service`, `controller` 성격으로 분리되어 있음
- `resources/mybatis/menu/MenuMapper.xml` 형태로 MyBatis XML 매퍼 사용 중

## 리팩터링 방향
혼재된 패키지를 기능 단위로 재배치했습니다.

### 권장 구조
- `auth`
- `member`
- `menu`
- `product`
- `order`
- `common`

각 서비스 하위는 아래처럼 통일합니다.

- `domain` : JPA Entity
- `bean` : 검색 조건, 요청 조건용 Bean
- `dto` : 응답/전달용 DTO
- `mapper` : MyBatis 인터페이스
- `service` : 서비스 인터페이스
- `serviceImpl` : 서비스 구현체
- `controller` : API 엔드포인트

## 원본 → 권장 이동 예시
- `member/controller/AdminCatalogController` → `product/controller/ProductController`
- `member/controller/AdminOrderController` → `order/controller/OrderController`
- `member/domain/AdminMember` → `member/domain/Member`
- `domain/Product` → `product/domain/Product`
- `domain/Category` → `product/domain/Category` 또는 `category/domain/Category`
- `menu/*` → 현재 구조 유지하되 `serviceImpl` 추가

## 핵심 포인트
1. 조회는 MyBatis로 통일해도 됨
2. 저장/수정은 JPA 유지 가능
3. 서비스 인터페이스와 구현체를 분리해서 추후 트랜잭션/AOP 적용이 쉬움
4. 프론트 API 경로도 도메인 기준으로 정리하기 쉬움

## 포함된 샘플
- Member 조회 API + MyBatis XML
- Menu 권한 조회 API + MyBatis XML
- Product 조회 API + MyBatis XML
- Order 조회 API + MyBatis XML
- Auth 로그인 샘플 구조

## 다음 추천 작업
1. 원본 엔티티/컬럼명 기준으로 DTO와 XML 컬럼명을 실제 테이블에 맞게 치환
2. `Category`, `Role`, `RoleMenu`, `RefreshToken` 도 같은 규칙으로 분리
3. `security`, `audit`, `exception` 도 `common` 또는 전용 모듈로 재배치
4. 기존 컨트롤러 URL을 새 구조에 맞춰 매핑 정리



## 역할–메뉴 매핑 화면에 메뉴가 안 나올 때

- 매핑 API는 **`MENU` 테이블**에서 `USE_YN='Y'` 인 행을 전부 읽습니다. **메뉴가 한 건도 없으면** 그리드가 비어 있습니다.
- 최초 기동 시 `src/main/resources/data.sql` 이 기본 메뉴(MEMBER, MENU, ROLE, CODE_GROUP, CODE_DETAIL 등)를 `INSERT IGNORE` 로 넣습니다. 적용하려면 `application.yml` 의 `spring.jpa.defer-datasource-initialization` / `spring.sql.init.mode` 가 켜져 있어야 합니다.
- 이미 DB가 있는 경우 수동으로 `MENU` 에 행을 넣거나, **메뉴관리** 화면에서 등록해도 됩니다.

## 회원 권한 체계 (ROLE / GRADE / STATUS)

| 구분 | 저장 위치 | 공통코드 그룹 | 설명 |
|------|-----------|----------------|------|
| **ROLE** (시스템 권한) | `MEMBER_ROLE` 테이블 (회원당 다중) | `MEMBER_ROLE` | `ROLE.ROLE_CODE`·`ROLE_MENU`와 동일 코드. Spring Security `GrantedAuthority`(메뉴 CRUD + `ROLE_*`) |
| **GRADE** (등급) | `member.grade_code` | `MEMBER_GRADE` | VIP 등 **비즈니스 등급** (권한과 분리) |
| **STATUS** (계정 상태) | `member.status_code` | `MEMBER_STATUS` | `ACTIVE`만 로그인 허용, 탈퇴 시 `WITHDRAWN` 등 |

- 신규/초기 데이터: `src/main/resources/data.sql` 에 `ROLE`(USER, ADMIN), 공통코드 그룹/상세, `ROLE_MENU` 샘플 매핑이 포함됩니다.
- 기존 DB 이관은 `src/main/resources/db/manual-migration-member-role-grade-status.sql` 참고.
- 로그인 시 `MEMBER_ROLE` 이 비어 있으면 권한 조회용으로 `USER` 를 기본 사용합니다 (`CustomUserDetailsService`).

## 보안 (Spring Security + DB 역할–메뉴 권한)

- `AuthorityBuilder` 가 `ROLE_MENU` 조회 결과로 `MEMBER_READ`, `MENU_UPDATE` 형태의 `GrantedAuthority` 를 부여합니다.
- `MenuAuthorities` 상수(`MEMBER`, `MENU`, `ROLE`, `CODE_GROUP`, `CODE_DETAIL`)는 **MENU.MENU_CODE** 와 동일해야 합니다.
- 컨트롤러는 `@PreAuthorize("@securityExpressions.canRead('MEMBER')")` 처럼 메뉴 코드별 조회/등록/수정/삭제를 검사합니다.
- `app.security.permit-all` (기본 `true`): 로컬에서 기존처럼 전체 허용 + 메서드 권한도 `SecurityExpressions` 가 통과시킵니다. **운영 전 `false`** 로 두고 `/api/auth/login` 으로 세션 로그인 후 호출하세요.
- `false` 일 때: 미인증 → JSON 401, 권한 부족 → JSON 403 (`JsonAuthenticationEntryPoint`, `JsonAccessDeniedHandler`).
- 로그인은 `AuthenticationManager` 로 `SecurityContext` 를 세팅합니다. 프론트는 `credentials: 'include'` 로 JSESSIONID 를 보내도록 `defaultApiRequestInit` 을 사용합니다.

## 배포방법

### GitHub Actions → Lightsail (자동)
- 워크플로: `.github/workflows/deploy-lightsail.yml` — `main` 에 push 시 Gradle `test` 후 SSH 로 서버에서 `git pull` + `docker compose up -d --build`.
- GitHub 저장소 **Settings → Secrets and variables → Actions** 에 등록:
  - `LIGHTSAIL_HOST`, `LIGHTSAIL_USER`, `LIGHTSAIL_SSH_KEY`(개인키 전체 PEM), `LIGHTSAIL_PORT`(대부분 `22`)
- (선택) **Variables** 에 `LIGHTSAIL_DEPLOY_PATH` — 서버上的 clone 경로(기본 `/home/ubuntu/prjt-backend-operational`).
- Lightsail 인스턴스에 clone 된 저장소가 GitHub 에서 `git pull` 할 수 있어야 함(Deploy key 또는 저장된 자격 증명). `.env.docker` 는 기존처럼 서버에만 둠.

### 수동 배포
내 소스 git push후

ssh -i "C:\Users\khe90\Downloads\sideprojectSSH_KEY.pem"  ubuntu@13.124.250.113 // 이걸로 서버접속
서버에서 
cd ~/prjt-backend-operational
git pull origin main    # 브랜치명은 본인 것
docker compose up -d --build



.env.docker 수정배포시
PowerShell에서 한 줄:
cd "C:\dev\2026 new prjt\real\prjt-backend-operational"; .\scripts\scp-env-docker.ps1 -PemPath "C:\Users\khe90\Downloads\sideprojectSSH_KEY.pem" -ServerHost "13.124.250.113"

스크립트 없이 scp만 쓰려면:
scp -i "C:\Users\khe90\Downloads\sideprojectSSH_KEY.pem" "C:\dev\2026 new prjt\real\prjt-backend-operational\.env.docker" ubuntu@13.124.250.113:/home/ubuntu/prjt-backend-operational/.env.docker

전송후 서버에서
cd ~/prjt-backend-operational
docker compose up -d --build

혹은 force로
docker compose up -d --force-recreate app 

서버 쿼리 log보는법 
docker logs -f prjt-backend-operational-app-1
