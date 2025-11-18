# ImHere (Iamhere)

*해당 레포지토리는 `우아한테크코스 8기 프리코스 오픈미션`의 과제로 수행한 프로젝트를 관리하고 있습니다.*

## 목차

- [프로젝트 개요](#0-프로젝트-개요)
- [기술 스택](#1-기술-스택-및-선택-이유)
- [주요 기능](#2-주요-기능)
- [프로젝트 구조](#3-프로젝트-구조)
- [설치 및 설정 가이드](#4-설치-및-설정-가이드)
- [API 문서](#5-api-문서)
- [배포 가이드](#6-배포-가이드)
- [개발 가이드](#7-개발-가이드)

---

## 0. 프로젝트 개요

**ImHere**는 위치 기반 알림 서비스를 제공하는 모바일 애플리케이션입니다. 사용자가 특정 위치(지오펜스)에 진입하거나 이탈할 때 지정된 연락처로 자동으로 알림을 전송하는 기능을 제공합니다.

### 주요 특징

- **지오펜스 관리**: 특정 위치와 반경을 설정하여 지오펜스 영역을 등록하고 관리
- **위치 기반 알림**: 지오펜스 진입/이탈 시 자동 알림 발송
- **연락처 연동**: 기기 연락처와 연동하여 알림 수신자 관리
- **기록 관리**: 지오펜스 알림 발송 이력 조회
- **카카오 소셜 로그인**: 간편한 인증을 위한 카카오 OAuth2 로그인 지원

---

## 1. 기술 스택 및 선택 이유

### 설계

- **Figma**: UI/UX 디자인 및 프로토타이핑

### 백엔드 (Spring)

- **Kotlin (2.2.21)**: JVM 기반의 현대적이고 안전한 언어
- **Spring Boot (3.5.7)**: 엔터프라이즈급 애플리케이션 개발 프레임워크
- **Spring Security**: OAuth2 및 JWT 기반 인증/인가 처리
- **Spring Data JPA**: 데이터베이스 접근 추상화
- **Redis**: OIDC 공개키 캐싱 및 세션 관리
- **H2 Database**: 개발 및 테스트용 인메모리 데이터베이스
- **JWT (JJWT)**: 토큰 기반 인증
- **SOLAPI SDK**: SMS 발송 서비스 연동
- **Testcontainers**: 통합 테스트를 위한 컨테이너 기반 테스트

### 모바일 (Flutter)

- **Dart (3.8.1)**: Flutter 프레임워크의 기본 언어
- **Flutter (3.32.5)**: 크로스 플랫폼 모바일 앱 개발 프레임워크
- **Flutter Riverpod (3.0.3)**: 상태 관리
- **GoRouter (17.0.0)**: 선언적 라우팅
- **Kakao Flutter SDK (1.9.7+3)**: 카카오 로그인 연동
- **Flutter Naver Map (1.4.1+1)**: 네이버 지도 서비스 연동
- **Geolocator (14.0.2)**: 위치 정보 수집
- **Permission Handler (12.0.1)**: 권한 관리
- **Sqflite (2.4.2)**: 로컬 데이터베이스
- **Flutter Secure Storage (9.2.2)**: 보안 토큰 저장
- **Dio (5.9.0)**: HTTP 클라이언트

### 인프라

- **AWS EC2**: 서버 인스턴스
- **AWS ECR**: Docker 이미지 레지스트리
- **Docker**: 컨테이너화 및 배포
- **Redis**: 캐시 및 세션 저장소

### 개발 도구

- **Git & GitHub**: 버전 관리 및 협업
- **Gradle**: 빌드 도구
- **JaCoCo**: 코드 커버리지 측정

---

## 2. 주요 기능

### 백엔드 (Kotlin/Spring)

#### 인증 및 인가

- **카카오 OAuth2 로그인**
  - Spring Security를 이용한 OAuth2 인증
  - OIDC (OpenID Connect) 기반 사용자 인증
  - 카카오를 OAuth2 제공자로 사용
  - JWT 토큰 발급 및 재발급
  - Redis를 통한 OIDC 공개키 캐싱

  **예외 상황:**
  - `AUTH_COMMON_001`: OIDC ID 토큰이 유효하지 않은 경우
  - `AUTH_COMMON_002`: OIDC ID 토큰이 만료된 경우
  - `AUTH_COMMON_003`: 암호화 알고리즘을 찾을 수 없는 경우
  - `AUTH_COMMON_004`: 유효하지 않은 공개키 스펙인 경우
  - `AUTH_COMMON_005`: 잘못된 Base64 인코딩 값인 경우
  - `AUTH_KAKAO_001`: 카카오 서버에서 OIDC 공개 키를 가져오는데 실패한 경우
  - `AUTH_KAKAO_002`: Redis에서 OIDC 공개 키를 가져오는데 실패한 경우
  - `AUTH_KAKAO_003`: OIDC 공개 키 목록에서 공개키를 가져오는데 실패한 경우
  - `IMHERE_TOKEN_001`: 만료된 JWT 토큰인 경우
  - `IMHERE_TOKEN_002`: 잘못된 JWT 토큰인 경우
  - `USER_001`: 사용자를 찾을 수 없는 경우

#### 메시지 발송

- **SMS 발송 서비스**
  - SOLAPI를 통한 단일/다중 SMS 발송
  - 지오펜스 알림 시 자동 SMS 전송

  **예외 상황:**
  - SOLAPI 서비스 장애 시 SMS 발송 실패
  - 잘못된 전화번호 형식으로 인한 발송 실패
  - SOLAPI API 키/시크릿 오류로 인한 인증 실패

### 모바일 (Dart/Flutter)

#### 회원가입/로그인

- 카카오 SDK를 이용한 소셜 로그인
- 서버와의 연동을 통한 JWT 토큰 관리
- 보안 저장소를 통한 토큰 안전 보관

  **예외 상황:**
  - 카카오 로그인 취소 또는 실패
  - 네트워크 연결 실패로 인한 서버 통신 오류
  - 서버 인증 실패 (401 Unauthorized)
  - 토큰 만료 시 자동 재발급 실패
  - 보안 저장소 접근 권한 오류

#### 지오펜스 관리

- **지오펜스 목록 화면**: 등록된 지오펜스 조회
- **지오펜스 등록 화면**: 새로운 지오펜스 등록
  - 네이버 지도를 통한 위치 선택
  - 반경 설정
  - 지오펜스 이름 및 설명 입력

  **예외 상황:**
  - 위치 권한이 거부된 경우
  - 위치 권한이 영구적으로 거부된 경우
  - 위치 서비스가 비활성화된 경우
  - 네이버 지도 API 인증 실패
  - 네이버 지도 사용량 초과
  - 로컬 데이터베이스 저장 실패

#### 연락처 관리

- 기기 연락처에서 연락처 선택 및 불러오기
- 지오펜스 알림 수신자로 연락처 등록
- 등록된 연락처 목록 조회

  **예외 상황:**
  - 연락처 권한이 거부된 경우
  - 연락처 권한이 영구적으로 거부된 경우
  - 기기 정책으로 인한 연락처 접근 제한
  - 네이티브 플랫폼에서 연락처 선택 실패
  - 로컬 데이터베이스 저장/삭제 실패

#### 기록 관리

- 지오펜스 알림 발송 이력 조회
- 알림 발송 시간, 위치, 수신자 정보 확인

  **예외 상황:**
  - 로컬 데이터베이스 조회 실패
  - 기록 데이터가 없는 경우
  - 데이터베이스 연결 오류

---

## 3. 프로젝트 구조

### 전체 구조

```
openMission/
├── Flutter/              # Flutter 모바일 앱
│   ├── lib/
│   │   ├── auth/         # 인증 관련 (모델, 서비스, 뷰, 뷰모델)
│   │   ├── common/       # 공통 유틸리티 (라우터, 테마, 컴포넌트)
│   │   ├── contact/      # 연락처 관리
│   │   ├── geofence/     # 지오펜스 관리
│   │   ├── record/       # 기록 관리
│   │   └── main.dart     # 앱 진입점
│   ├── android/          # Android 네이티브 설정
│   ├── ios/              # iOS 네이티브 설정
│   ├── assets/           # 리소스 파일 (폰트, 이미지)
│   └── test/             # 테스트 코드
│
└── Spring/               # Spring Boot 백엔드 (Hexagonal Architecture)
    ├── src/
    │   ├── main/
    │   │   ├── kotlin/com/kdongsu5509/imhere/
    │   │   │   ├── auth/              # 인증 모듈
    │   │   │   │   ├── adapter/       # 외부 인터페이스 어댑터
    │   │   │   │   │   ├── in/        # 입력 어댑터 (웹 컨트롤러)
    │   │   │   │   │   │   └── web/   # AuthController
    │   │   │   │   │   ├── out/       # 출력 어댑터
    │   │   │   │   │   │   ├── persistence/  # JPA 어댑터
    │   │   │   │   │   │   ├── redis/         # Redis 어댑터
    │   │   │   │   │   │   ├── kakao/         # 카카오 OAuth 클라이언트
    │   │   │   │   │   │   └── jjwt/          # JWT 파서/검증 어댑터
    │   │   │   │   │   └── dto/       # 어댑터 레벨 DTO
    │   │   │   │   ├── application/   # 애플리케이션 레이어
    │   │   │   │   │   ├── port/      # 포트 인터페이스
    │   │   │   │   │   │   ├── in/    # 유스케이스 포트 (입력)
    │   │   │   │   │   │   └── out/   # 외부 의존성 포트 (출력)
    │   │   │   │   │   ├── service/   # 서비스 구현
    │   │   │   │   │   │   ├── jwt/   # JWT 토큰 서비스
    │   │   │   │   │   │   ├── oidc/  # OIDC 인증 서비스
    │   │   │   │   │   │   └── security/ # 보안 관련
    │   │   │   │   │   └── dto/       # 애플리케이션 DTO
    │   │   │   │   └── domain/        # 도메인 모델
    │   │   │   │       ├── User.kt
    │   │   │   │       ├── UserRole.kt
    │   │   │   │       └── OAuth2Provider.kt
    │   │   │   ├── message/           # 메시지 발송 모듈
    │   │   │   │   ├── adapter/
    │   │   │   │   │   ├── in/web/    # MessageController
    │   │   │   │   │   └── dto/       # 요청/응답 DTO
    │   │   │   │   ├── application/
    │   │   │   │   │   ├── port/      # 메시지 발송 유스케이스 포트
    │   │   │   │   │   ├── service/   # SOLAPI 서비스
    │   │   │   │   │   └── vo/        # 값 객체
    │   │   │   └── common/            # 공통 모듈
    │   │   │       ├── config/        # 설정 (Redis, Security)
    │   │   │       ├── exception/     # 예외 처리
    │   │   │       │   ├── ErrorCode.kt
    │   │   │       │   ├── BaseException.kt
    │   │   │       │   ├── ErrorResponse.kt
    │   │   │       │   ├── ImhereExceptionHandler.kt
    │   │   │       │   └── implementation/ # 구체적 예외 클래스
    │   │   │       └── annotation/     # 커스텀 어노테이션
    │   │   └── resources/
    │   │       ├── application.yaml
    │   │       └── application-secret.yaml
    │   └── test/                      # 테스트 코드
    ├── docker-compose.yml              # Docker Compose 설정
    └── Dockerfile                      # Docker 이미지 빌드 설정
```

### 아키텍처 패턴

#### Spring 백엔드: Hexagonal Architecture (포트-어댑터 패턴)

프로젝트는 **Hexagonal Architecture**를 기반으로 설계되었습니다:

- **Domain Layer**: 비즈니스 로직의 핵심 (User, UserRole 등)
- **Application Layer**: 유스케이스 및 서비스 구현
  - `port/in`: 애플리케이션에 들어오는 요청을 정의하는 인터페이스 (유스케이스)
  - `port/out`: 애플리케이션이 의존하는 외부 시스템을 정의하는 인터페이스
  - `service`: 유스케이스 구현체
- **Adapter Layer**: 외부 세계와의 인터페이스
  - `adapter/in`: 입력 어댑터 (웹 컨트롤러 등)
  - `adapter/out`: 출력 어댑터 (JPA, Redis, 외부 API 클라이언트 등)

이 구조를 통해 도메인 로직과 외부 의존성을 분리하여 테스트 용이성과 유지보수성을 향상시킵니다.

#### Flutter 모바일: MVVM 패턴

- **Model**: 데이터 모델 및 비즈니스 로직
- **View**: UI 컴포넌트
- **ViewModel**: Riverpod을 이용한 상태 관리 및 비즈니스 로직 처리

---

## 4. 설치 및 설정 가이드

### a. 필수 요구사항

#### 백엔드 개발 환경

- **JDK 21** 이상
- **Gradle 8.x** 이상
- **Docker** 및 **Docker Compose** (로컬 실행 시)
- **Redis** (로컬 실행 시)

#### 모바일 개발 환경

- **Flutter SDK 3.32.5** 이상
- **Dart SDK 3.8.1** 이상
- **Android Studio** 또는 **Xcode** (플랫폼별 빌드)
- **Android SDK** (Android 개발 시)
- **Xcode** (iOS 개발 시, macOS만)

### b. 환경 변수 설정

#### Spring 백엔드

`Spring/src/main/resources/application-secret.yaml` 파일을 생성하고 다음 내용을 설정합니다:

```yaml
# 카카오 OAuth2 설정
kakao:
  oauth2:
    client-id: ${KAKAO_CLIENT_ID}
    client-secret: ${KAKAO_CLIENT_SECRET}
    redirect-uri: ${KAKAO_REDIRECT_URI}
    oidc-issuer-url: https://kauth.kakao.com

# JWT 설정
jwt:
  secret: ${JWT_SECRET}
  access-token-validity: 3600000  # 1시간 (밀리초)
  refresh-token-validity: 86400000 # 24시간 (밀리초)

# SOLAPI 설정
solapi:
  api-key: ${SOLAPI_API_KEY}
  api-secret: ${SOLAPI_API_SECRET}
  sender-phone: ${SOLAPI_SENDER_PHONE}
```

#### Flutter 모바일

`Flutter/iam_here_flutter_secret.env` 파일을 생성하고 다음 내용을 설정합니다:

```env
# 카카오 SDK 설정
KAKAO_NATIVE_APP_KEY=your_kakao_native_app_key

# 네이버 지도 설정
NAVER_MAP_CLIENT_ID=your_naver_map_client_id

# 백엔드 API URL
API_BASE_URL=http://localhost:8080
```

### c. 의존성 설치 방법

#### Spring 백엔드

```bash
cd Spring
./gradlew build
```

#### Flutter 모바일

```bash
cd Flutter
flutter pub get
```

### d. 로컬 실행 명령어

#### Spring 백엔드

**Redis 실행 (Docker Compose 사용):**

```bash
cd Spring
docker-compose up -d redis
```

**애플리케이션 실행:**

```bash
cd Spring
./gradlew bootRun
```

또는 IDE에서 `ImhereApplication.kt`를 실행합니다.

기본 포트: `8080`

#### Flutter 모바일

**Android 실행:**

```bash
cd Flutter
flutter run
```

**iOS 실행 (macOS만):**

```bash
cd Flutter
flutter run -d ios
```

---

## 5. API 문서

### 인증 API

#### 카카오 로그인

```
POST /api/auth/kakao/login
Content-Type: application/json

Request Body:
{
  "idToken": "카카오 ID 토큰"
}

Response:
{
  "accessToken": "JWT 액세스 토큰",
  "refreshToken": "JWT 리프레시 토큰"
}
```

#### 토큰 재발급

```
POST /api/auth/refresh
Content-Type: application/json

Request Body:
{
  "refreshToken": "리프레시 토큰"
}

Response:
{
  "accessToken": "새로운 JWT 액세스 토큰",
  "refreshToken": "새로운 JWT 리프레시 토큰"
}
```

### 메시지 API

#### 단일 SMS 발송

```
POST /api/message/send
Authorization: Bearer {accessToken}
Content-Type: application/json

Request Body:
{
  "to": "수신자 전화번호",
  "text": "메시지 내용"
}
```

#### 다중 SMS 발송

```
POST /api/message/send-multiple
Authorization: Bearer {accessToken}
Content-Type: application/json

Request Body:
{
  "messages": [
    {
      "to": "수신자 전화번호1",
      "text": "메시지 내용1"
    },
    {
      "to": "수신자 전화번호2",
      "text": "메시지 내용2"
    }
  ]
}
```

---

## 6. 배포 가이드

### a. 배포 환경 설정 (AWS, Docker)

#### Docker 이미지 빌드

```bash
cd Spring
docker build -t iamhere:latest .
```

#### AWS ECR에 이미지 푸시

```bash
# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 518033442106.dkr.ecr.ap-northeast-2.amazonaws.com

# 이미지 태깅
docker tag iamhere:latest 518033442106.dkr.ecr.ap-northeast-2.amazonaws.com/iamhere:latest

# 이미지 푸시
docker push 518033442106.dkr.ecr.ap-northeast-2.amazonaws.com/iamhere:latest
```

#### Docker Compose로 배포

```bash
cd Spring
docker-compose up -d
```

---

## 7. 개발 가이드

### 코드 스타일

#### Kotlin

- Kotlin 코딩 컨벤션 준수
- Clean Architecture 패턴 적용
- Hexagonal Architecture 사용

#### Dart/Flutter

- Effective Dart 스타일 가이드 준수
- Riverpod을 이용한 상태 관리
- MVVM 패턴 적용

### 테스트

#### 백엔드 테스트 실행

```bash
cd Spring
./gradlew test
```

#### 코드 커버리지 확인

```bash
cd Spring
./gradlew jacocoTestReport
# 리포트는 build/jacocoHtml/index.html에서 확인 가능
```

#### Flutter 테스트 실행

```bash
cd Flutter
flutter test
```

#### Flutter 테스트 커버리지 확인

```bash
cd Flutter
flutter test --coverage
# 리포트는 coverage/html/index.html에서 확인 가능
```

### 커밋 메시지 컨벤션

- `AngularJS Git Commit Convention` 준수

```
[타입] 간단한 제목

상세 설명 (선택사항)

타입:
- feat: 새로운 기능 추가
- fix: 버그 수정
- docs: 문서 수정
- style: 코드 포맷팅, 세미콜론 누락 등
- refactor: 코드 리팩토링
- test: 테스트 코드 추가
- chore: 빌드 업무 수정, 패키지 매니저 설정 등
```
