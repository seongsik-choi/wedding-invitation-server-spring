# 배포 가이드

## 서버 스펙

- **Java**: 17
- **Spring Boot**: 3.2.0
- **포트**: 8090 (환경 변수로 변경 가능)
- **데이터 저장**: JSON 파일 (`./data/` 디렉토리)
- **메모리**: 최소 512MB 권장
- **디스크**: 파일 시스템 접근 필요 (JSON 파일 저장)

## 추천 호스팅 서비스

### 1. Railway (⭐ 가장 추천)

**장점:**
- 무료 티어 제공 ($5 크레딧/월)
- Spring Boot 자동 감지 및 배포
- GitHub 연동으로 자동 배포
- 환경 변수 설정 간편
- 파일 시스템 접근 가능

**가격:**
- 무료: $5 크레딧/월 (소규모 프로젝트 충분)
- 유료: 사용량 기반

**배포 방법:**
1. Railway 계정 생성 및 GitHub 연동
2. 새 프로젝트 생성 → GitHub 레포지토리 선택
3. 환경 변수 설정:
   - `PORT` (Railway가 자동 할당, 설정 불필요)
   - `CORS_ALLOWED_ORIGIN` (프론트엔드 도메인)
   - `ADMIN_PASSWORD` (관리자 비밀번호)
   - `JSON_DATA_PATH` (선택사항, 기본값 `./data` 사용 가능)
4. 배포 완료

**참고:** Railway는 자동으로 워킹 디렉토리를 설정하므로, `JSON_DATA_PATH`를 설정하지 않으면 기본값 `./data`가 사용됩니다. 이 경우 Railway의 워킹 디렉토리(보통 프로젝트 루트) 기준으로 `data` 디렉토리가 생성됩니다.

**URL**: https://railway.app

---

### 2. 네이버 클라우드 플랫폼 (NCP)

**장점:**
- 한국 서버 (낮은 레이턴시)
- 한국어 지원
- 무료 크레딧 제공

**가격:**
- 무료: 크레딧 제공
- 유료: 사용량 기반

**URL**: https://www.ncloud.com

---

### 3. 카카오 i 클라우드

**장점:**
- 한국 서버
- 한국어 지원

**가격:**
- 사용량 기반

**URL**: https://cloud.kakao.com

---

## 배포 전 준비사항

### 1. application.properties 확인

현재 `application.properties`는 이미 환경 변수를 지원하도록 설정되어 있습니다:

```properties
server.port=${PORT:8090}
cors.allowed-origin=${CORS_ALLOWED_ORIGIN:http://localhost:5173}
admin.password=${ADMIN_PASSWORD:asdf}
json.data.path=${JSON_DATA_PATH:./data}
```

### 2. Dockerfile 생성 (선택사항)

일부 플랫폼에서 필요할 수 있습니다:

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Gradle 빌드 결과물 복사
COPY build/libs/*.jar app.jar

# 데이터 디렉토리 생성
RUN mkdir -p /app/data

EXPOSE 8090

ENV PORT=8090
ENV JSON_DATA_PATH=/app/data

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3. 환경 변수 설정

배포 시 다음 환경 변수를 설정:

- `PORT`: 서버 포트 (일부 플랫폼은 자동 할당, 설정 불필요)
- `CORS_ALLOWED_ORIGIN`: 프론트엔드 도메인 (예: `https://yourdomain.com`)
- `ADMIN_PASSWORD`: 관리자 비밀번호
- `JSON_DATA_PATH`: JSON 파일 저장 경로 (선택사항, 기본값: `./data`)

**경로 설정 참고:**
- **로컬 개발**: `./data` (프로젝트 루트의 `data` 디렉토리)
- **배포 시**: 
  - `./data` 사용 가능 (플랫폼의 워킹 디렉토리 기준)
  - 절대 경로 사용 시: `/app/data` (Docker 컨테이너 내부) 또는 플랫폼별 경로
  - **대부분의 경우 `./data`를 그대로 사용해도 문제없습니다**

---

## 추천 순위

### 무료/저렴한 옵션 (개인 프로젝트)
1. **Railway** ⭐ - 가장 추천 (무료 크레딧, 간편한 배포)

### 프로덕션 환경
1. **AWS EC2** - 완전한 제어권
2. **DigitalOcean App Platform** - 안정적
3. **Google Cloud Run** - 서버리스
4. **Azure App Service** - 엔터프라이즈급

### 한국 서버 (낮은 레이턴시)
1. **네이버 클라우드 플랫폼 (NCP)**
2. **카카오 i 클라우드**

---

## Railway 배포 상세 가이드 (추천)

### 1단계: Railway 계정 생성
1. https://railway.app 접속
2. GitHub 계정으로 로그인

### 2단계: 프로젝트 생성
1. "New Project" 클릭
2. "Deploy from GitHub repo" 선택
3. `seongsik-choi/wedding-invitation-server-spring` 레포지토리 선택

### 3단계: 환경 변수 설정
Railway 대시보드에서 "Variables" 탭으로 이동하여 설정:

```
CORS_ALLOWED_ORIGIN=https://your-frontend-domain.com
ADMIN_PASSWORD=your-secure-password
```

**참고:**
- `PORT`는 Railway가 자동으로 할당하므로 설정 불필요
- `JSON_DATA_PATH`는 설정하지 않으면 기본값 `./data` 사용 (Railway 워킹 디렉토리 기준)
- 절대 경로가 필요한 경우에만 `JSON_DATA_PATH=/app/data` 설정

### 4단계: 배포 확인
- Railway가 자동으로 빌드 및 배포
- 배포 완료 후 제공되는 URL로 접속 확인
- 로그는 Railway 대시보드에서 확인 가능

### 5단계: 커스텀 도메인 설정 (선택사항)
- Railway 대시보드에서 "Settings" → "Domains"
- 원하는 도메인 추가

---

## Render 배포 상세 가이드

### 1단계: Render 계정 생성
1. https://render.com 접속
2. GitHub 계정으로 로그인

### 2단계: Web Service 생성
1. "New +" → "Web Service" 선택
2. GitHub 레포지토리 연결
3. 설정:
   - **Name**: `wedding-invitation-server`
   - **Environment**: `Java`
   - **Build Command**: `./gradlew build`
   - **Start Command**: `java -jar build/libs/*.jar`

### 3단계: 환경 변수 설정
```
CORS_ALLOWED_ORIGIN=https://your-frontend-domain.com
ADMIN_PASSWORD=your-secure-password
```

**참고:**
- `PORT`는 Render가 자동으로 할당하므로 설정 불필요
- `JSON_DATA_PATH`는 설정하지 않으면 기본값 `./data` 사용
- Render의 경우 프로젝트 루트가 워킹 디렉토리이므로 `./data`로 충분

### 4단계: 배포 확인
- Render가 자동으로 빌드 및 배포
- 제공되는 URL로 접속 확인

---

## 배포 체크리스트

- [ ] `application.properties` 환경 변수 지원 확인
- [ ] 포트 설정 (환경 변수 `PORT` 사용)
- [ ] CORS 설정 (프론트엔드 도메인)
- [ ] 관리자 비밀번호 설정
- [ ] JSON 파일 경로 설정
- [ ] 로그 확인 방법 확인
- [ ] 도메인/SSL 설정 (필요시)
- [ ] 데이터 백업 전략 수립

---

## 주의사항

### JSON 파일 경로 설정

**상대 경로 vs 절대 경로:**
- `./data`: 상대 경로, 현재 워킹 디렉토리 기준
  - 로컬: 프로젝트 루트의 `data` 디렉토리
  - 배포 시: 플랫폼의 워킹 디렉토리 기준 (대부분 프로젝트 루트)
  - **대부분의 경우 이 방식으로 충분합니다**
  
- `/app/data`: 절대 경로, Docker 컨테이너 내부 경로
  - Docker 기반 배포 시 사용
  - 플랫폼이 특정 경로를 요구하는 경우에만 필요

**권장사항:**
- Railway, Render 등: `JSON_DATA_PATH` 환경 변수 설정하지 않음 (기본값 `./data` 사용)
- Docker 기반 배포: `JSON_DATA_PATH=/app/data` 설정
- EC2 등 직접 서버 관리: `JSON_DATA_PATH=/var/app/data` 등 적절한 경로 설정

### JSON 파일 영구 저장

⚠️ **중요: Railway 재배포 시 데이터 초기화**

Railway는 컨테이너 기반 배포이므로, **재배포 시 컨테이너 내부의 JSON 파일이 초기화됩니다**. 데이터를 영구 보존하려면 다음 방법을 사용하세요:

#### 방법 1: Railway 볼륨(Volume) 사용 (권장)

1. Railway 대시보드에서 서비스 선택
2. **"Volumes"** 탭 클릭
3. **"Add Volume"** 클릭
4. 설정:
   - **Mount Path**: `/app/data`
   - **Volume Name**: `data-volume` (원하는 이름)
5. 저장 후 재배포

이제 `/app/data` 디렉토리가 볼륨에 마운트되어 재배포해도 데이터가 유지됩니다.

**환경 변수 설정:**
- `JSON_DATA_PATH=/app/data` (볼륨 마운트 경로와 일치)

#### 방법 2: 외부 데이터베이스 사용

- Railway PostgreSQL/MySQL 서비스 연결
- 데이터베이스에 데이터 저장 (JSON 파일 대신)

#### 방법 3: 외부 스토리지 사용

- AWS S3, Google Cloud Storage 등
- JSON 파일을 클라우드 스토리지에 저장

---

**다른 플랫폼:**
- **Render 무료 티어**: 파일 시스템이 영구적이지 않음 ❌
- **Render 유료 티어**: 파일 시스템 영구 저장 지원 ✅
- **Fly.io**: 볼륨(Volume) 사용 필요
- **AWS EC2**: 완전한 파일 시스템 접근 ✅
- **DigitalOcean**: 파일 시스템 영구 저장 지원 ✅

### 데이터 백업

JSON 파일 기반 저장이므로 정기적인 백업을 권장합니다:
- GitHub에 JSON 파일 커밋 (민감 정보 제외)
- 클라우드 스토리지에 백업
- 자동 백업 스크립트 설정

---

## 문제 해결

### 포트 오류
- 환경 변수 `PORT`가 설정되었는지 확인
- 플랫폼이 자동으로 할당하는 포트 사용 (예: Railway)

### CORS 오류
- `CORS_ALLOWED_ORIGIN` 환경 변수가 정확한지 확인
- 프론트엔드 도메인과 일치하는지 확인

### 파일 시스템 접근 오류
- `JSON_DATA_PATH` 경로가 올바른지 확인
- 디렉토리 생성 권한 확인
- 플랫폼의 파일 시스템 제한 확인

