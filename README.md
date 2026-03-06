# 2026_Prjt_Backend 리팩터링 샘플

이 파일은 공개 리포지토리의 현재 보이는 구조를 기준으로 재정리한 **서비스별 디렉토리 구조 샘플 프로젝트**입니다.

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
