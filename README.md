# 모바일 청첩장 서버 (Spring Boot)

이 프로젝트는 모바일 청첩장 웹 애플리케이션의 백엔드 서버입니다. Spring Boot를 사용하여 구현되었으며, 데이터는 JSON 파일로 관리됩니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web**
- **Spring Data JPA** (참고용, 현재는 JSON 파일 사용)
- **Jackson** (JSON 처리)
- **Lombok**
- **Gradle**

## 프로젝트 구조

```
wedding-invitation-server-spring/
├── src/main/java/com/seongsikchoi/weddinginvitationserver/
│   ├── config/              # 설정 클래스
│   │   ├── SeongsikConfig.java      # CORS 설정
│   │   └── SecurityConfig.java      # Spring Security 설정
│   ├── controller/          # REST API 컨트롤러
│   │   ├── GuestbookController.java
│   │   └── AttendanceController.java
│   ├── service/             # 비즈니스 로직
│   │   ├── GuestbookService.java
│   │   └── AttendanceService.java
│   ├── dto/                  # 데이터 전송 객체
│   │   ├── GuestbookGetResponse.java
│   │   ├── GuestbookPostForCreate.java
│   │   ├── GuestbookPostForDelete.java
│   │   ├── GuestbookPostForGet.java
│   │   ├── AttendanceCreate.java
│   │   ├── GuestbookJson.java
│   │   └── AttendanceJson.java
│   ├── util/                 # 유틸리티 클래스
│   │   ├── JsonFileManager.java    # JSON 파일 읽기/쓰기
│   │   └── PasswordUtil.java       # 비밀번호 해싱
│   └── WeddingInvitationServerApplication.java
├── data/                     # JSON 데이터 파일 저장 위치
│   ├── guestbook.json       # 방명록 데이터
│   └── attendance.json      # 참석 여부 데이터
└── src/main/resources/
    └── application.properties
```

## 데이터 저장 구조

### JSON 파일 기반 저장

데이터는 SQLite 대신 **JSON 파일**로 관리됩니다.

- **저장 위치**: `./data/` 디렉토리
- **파일명**:
  - `guestbook.json` - 방명록 데이터
  - `attendance.json` - 참석 여부 데이터

### 동작 방식

1. **저장 이벤트 발생 시**:
   - JSON 파일 존재 여부 확인
   - 파일이 없으면 빈 배열(`[]`)로 자동 생성
   - 기존 데이터 읽기 → 새 데이터 추가 → JSON 파일에 저장

2. **조회 시**:
   - JSON 파일에서 데이터 읽기
   - 필터링/정렬/페이징 처리
   - DTO로 변환하여 응답

### JSON 데이터 구조

#### guestbook.json
```json
[
  {
    "id": 1,
    "name": "홍길동",
    "content": "축하합니다!",
    "password": "$2a$14$...",
    "timestamp": 1763095520,
    "valid": true
  }
]
```

#### attendance.json
```json
[
  {
    "id": 1,
    "side": "groom",
    "name": "김철수",
    "meal": "yes",
    "count": 2,
    "timestamp": 1763095555
  }
]
```

## API 엔드포인트

### Base URL
```
http://localhost:8090
```

### 1. 방명록 (Guestbook) API

#### GET `/api/guestbook` - 방명록 조회
방명록을 페이징하여 조회합니다.

**Query Parameters:**
- `offset` (선택, 기본값: `0`) - 시작 위치
- `limit` (선택, 기본값: `20`) - 조회 개수

**Request Example:**
```http
GET /api/guestbook?offset=0&limit=20
```

**Response (200 OK):**
```json
{
  "posts": [
    {
      "id": 1,
      "name": "홍길동",
      "content": "축하합니다!",
      "timestamp": 1763095520
    }
  ],
  "total": 10
}
```

**Response Fields:**
- `posts`: 방명록 게시글 배열
  - `id`: 게시글 ID
  - `name`: 작성자 이름
  - `content`: 내용
  - `timestamp`: 작성 시간 (Unix timestamp)
- `total`: 전체 유효한 게시글 수

---

#### POST `/api/guestbook` - 방명록 작성
새로운 방명록을 작성합니다.

**Request Body:**
```json
{
  "name": "홍길동",
  "content": "축하합니다!",
  "password": "mypassword"
}
```

**Request Fields:**
- `name` (필수): 작성자 이름
- `content` (필수): 내용
- `password` (필수): 삭제용 비밀번호

**Response:**
- `200 OK`: 작성 성공
- `500 Internal Server Error`: 서버 오류

**Response Example:**
```http
HTTP/1.1 200 OK
```

---

#### PUT `/api/guestbook` - 방명록 삭제 (Soft Delete)
방명록을 삭제합니다. 실제로는 `valid` 필드를 `false`로 변경합니다.

**Request Body:**
```json
{
  "id": 1,
  "password": "mypassword"
}
```

**Request Fields:**
- `id` (필수): 삭제할 게시글 ID
- `password` (필수): 작성 시 입력한 비밀번호 또는 관리자 비밀번호

**Response:**
- `200 OK`: 삭제 성공
- `403 Forbidden`: 비밀번호 불일치
- `500 Internal Server Error`: 서버 오류

**Response Example:**
```http
HTTP/1.1 200 OK
```

**참고:** 관리자 비밀번호(`admin.password`)를 사용하면 모든 방명록을 삭제할 수 있습니다.

---

### 2. 참석 여부 (Attendance) API

#### GET `/api/attendance` - 참석 여부 조회
모든 참석 정보를 조회합니다.

**Request Example:**
```http
GET /api/attendance
```

**Response (200 OK):**
```json
[
  {
    "side": "groom",
    "name": "김철수",
    "meal": "yes",
    "count": 2
  },
  {
    "side": "bride",
    "name": "이영희",
    "meal": "no",
    "count": 1
  }
]
```

**Response Fields:**
- `side`: 측 (예: "groom", "bride")
- `name`: 이름
- `meal`: 식사 여부 (예: "yes", "no", "undecided")
- `count`: 인원 수

---

#### POST `/api/attendance` - 참석 등록
새로운 참석 정보를 등록합니다.

**Request Body:**
```json
{
  "side": "groom",
  "name": "김철수",
  "meal": "yes",
  "count": 2
}
```

**Request Fields:**
- `side` (필수): 측 (예: "groom", "bride")
- `name` (필수): 이름
- `meal` (필수): 식사 여부 (예: "yes", "no", "undecided")
- `count` (필수): 인원 수

**Response:**
- `200 OK`: 등록 성공
- `500 Internal Server Error`: 서버 오류

**Response Example:**
```http
HTTP/1.1 200 OK
```

---

## 프론트엔드 연동

### CORS 설정

서버는 CORS(Cross-Origin Resource Sharing)를 지원합니다.

**현재 설정:**
- 허용 Origin: `http://localhost:5173` (Vite 기본 포트)
- 허용 메서드: `GET`, `POST`, `PUT`
- 허용 헤더: 모든 헤더 (`*`)
- Credentials: 허용

**설정 변경:**
`src/main/resources/application.properties` 파일에서 수정:
```properties
cors.allowed-origin=http://localhost:5173
```

### 프론트엔드 연동 예시

#### JavaScript/TypeScript (Fetch API)

```javascript
const API_BASE_URL = 'http://localhost:8090';

// 방명록 조회
async function getGuestbook(offset = 0, limit = 20) {
  const response = await fetch(
    `${API_BASE_URL}/api/guestbook?offset=${offset}&limit=${limit}`
  );
  const data = await response.json();
  return data;
}

// 방명록 작성
async function createGuestbook(name, content, password) {
  const response = await fetch(`${API_BASE_URL}/api/guestbook`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      name,
      content,
      password,
    }),
  });
  return response.ok;
}

// 방명록 삭제
async function deleteGuestbook(id, password) {
  const response = await fetch(`${API_BASE_URL}/api/guestbook`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      id,
      password,
    }),
  });
  
  if (response.status === 403) {
    throw new Error('비밀번호가 일치하지 않습니다.');
  }
  return response.ok;
}

// 참석 여부 조회
async function getAttendance() {
  const response = await fetch(`${API_BASE_URL}/api/attendance`);
  const data = await response.json();
  return data;
}

// 참석 등록
async function createAttendance(side, name, meal, count) {
  const response = await fetch(`${API_BASE_URL}/api/attendance`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      side,
      name,
      meal,
      count,
    }),
  });
  return response.ok;
}
```

#### Axios 사용 예시

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8090',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 방명록 조회
const getGuestbook = (offset = 0, limit = 20) => {
  return api.get('/api/guestbook', {
    params: { offset, limit },
  });
};

// 방명록 작성
const createGuestbook = (name, content, password) => {
  return api.post('/api/guestbook', {
    name,
    content,
    password,
  });
};

// 방명록 삭제
const deleteGuestbook = (id, password) => {
  return api.put('/api/guestbook', {
    id,
    password,
  });
};

// 참석 여부 조회
const getAttendance = () => {
  return api.get('/api/attendance');
};

// 참석 등록
const createAttendance = (side, name, meal, count) => {
  return api.post('/api/attendance', {
    side,
    name,
    meal,
    count,
  });
};
```

### 에러 처리

```javascript
try {
  const response = await fetch(`${API_BASE_URL}/api/guestbook`);
  if (!response.ok) {
    if (response.status === 403) {
      // 비밀번호 불일치
      console.error('비밀번호가 일치하지 않습니다.');
    } else if (response.status === 500) {
      // 서버 오류
      console.error('서버 오류가 발생했습니다.');
    }
    return;
  }
  const data = await response.json();
  // 데이터 처리
} catch (error) {
  console.error('네트워크 오류:', error);
}
```

## 설정

### application.properties

`src/main/resources/application.properties` 파일에서 다음 설정을 변경할 수 있습니다:

```properties
# 서버 포트
server.port=8090

# CORS 허용 도메인
cors.allowed-origin=http://localhost:5173

# 관리자 비밀번호 (방명록 삭제 시 사용)
admin.password=asdf

# JSON 파일 저장 경로
json.data.path=./data

# 로깅 설정
logging.level.root=INFO
logging.level.com.seongsikchoi.weddinginvitationserver=INFO
logging.level.com.seongsikchoi.weddinginvitationserver.controller=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### 주요 설정 설명

- `server.port`: 서버가 실행될 포트 번호
- `cors.allowed-origin`: 프론트엔드 도메인 (CORS 허용)
- `admin.password`: 관리자 비밀번호 (모든 방명록 삭제 가능)
- `json.data.path`: JSON 파일이 저장될 디렉토리 경로

## 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 7.x 이상 (또는 Gradle Wrapper 사용)

### 실행 방법

1. **프로젝트 클론:**
   ```bash
   git clone <repository-url>
   cd wedding-invitation-server-spring
   ```

2. **의존성 설치 및 빌드:**
   ```bash
   ./gradlew build
   ```

3. **서버 실행:**
   ```bash
   ./gradlew bootRun
   ```

   또는 IDE에서 `WeddingInvitationServerApplication.java`를 실행

4. **서버 확인:**
   - 서버는 `http://localhost:8090`에서 실행됩니다.
   - API 엔드포인트에 접속하여 확인:
     - `http://localhost:8090/api/guestbook`
     - `http://localhost:8090/api/attendance`

### 데이터 디렉토리

서버 실행 시 `./data/` 디렉토리가 자동으로 생성되며, 다음 파일들이 생성됩니다:
- `guestbook.json` - 방명록 데이터 (없으면 빈 배열로 생성)
- `attendance.json` - 참석 여부 데이터 (없으면 빈 배열로 생성)

## 로깅

모든 API 호출과 데이터 저장/조회 작업이 로그로 기록됩니다.

**로그 형식:**
```
2024-01-15 14:30:45.123 [http-nio-8090-exec-1] INFO  c.s.w.controller.GuestbookController - === API 호출: GET /api/guestbook - offset: 0, limit: 20 ===
2024-01-15 14:30:45.456 [http-nio-8090-exec-1] INFO  c.s.w.service.GuestbookService - >>> JSON READ: Guestbook - offset: 0, limit: 20
2024-01-15 14:30:45.789 [http-nio-8090-exec-1] INFO  c.s.w.service.GuestbookService - >>> JSON READ 완료: Guestbook - total: 10, returned: 10
```

## 보안

- 비밀번호는 BCrypt로 해싱되어 저장됩니다.
- 관리자 비밀번호로 모든 방명록을 삭제할 수 있습니다.
- CORS는 설정된 도메인만 허용합니다.

## 배포

### Railway 배포 시

환경 변수 설정:
- `CORS_ALLOWED_ORIGIN`: 프론트엔드 도메인
- `ADMIN_PASSWORD`: 관리자 비밀번호
- `JSON_DATA_PATH`: JSON 파일 저장 경로 (선택)

## 문제 해결

### JSON 파일이 생성되지 않는 경우

- `./data/` 디렉토리에 쓰기 권한이 있는지 확인
- `application.properties`의 `json.data.path` 설정 확인

### CORS 오류가 발생하는 경우

- `application.properties`의 `cors.allowed-origin`이 프론트엔드 도메인과 일치하는지 확인
- 프론트엔드에서 요청 시 `credentials: 'include'` 옵션 사용

### 포트 충돌

- 다른 애플리케이션이 8090 포트를 사용 중인지 확인
- `application.properties`에서 `server.port` 변경
