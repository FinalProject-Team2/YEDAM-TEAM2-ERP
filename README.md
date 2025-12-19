# 🧾 구독형 ERP (그룹웨어) 프로젝트

![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring](https://img.shields.io/badge/Spring-Framework-green)
![JSP](https://img.shields.io/badge/JSP-View-blue)
![Oracle](https://img.shields.io/badge/Oracle-DB-red)
![MyBatis](https://img.shields.io/badge/MyBatis-Mapper-brightgreen)
![Status](https://img.shields.io/badge/Status-In_Progress-yellow)

> 기업이 **필요한 모듈만 선택해서 구독**할 수 있는 구독형 ERP/그룹웨어 시스템  
> 불필요한 모듈까지 함께 쓰는 비효율을 줄이고, 비용/운영 효율을 개선하는 것이 목표입니다.

---

## ✨ 핵심 기능 (예시)
- 🔐 로그인/권한(역할) 기반 메뉴 접근 제어
- 👥 사원/계정 관리 (등록/수정/잠금/활성 등)
- 🧩 모듈 구독/해지 및 구독 상태 기반 기능 제한
- 🧾 로그(SysLog) 적재 및 감사 추적
- 🧪 단위/통합 테스트 시나리오 기반 검증

> ⚠️ 실제 기능 목록은 프로젝트 진행에 따라 업데이트됩니다.

---

## 🛠 기술 스택
- **Backend**: Java, Spring, MyBatis
- **Frontend**: JSP, JavaScript, jQuery, HTML/CSS
- **DB**: Oracle
- **Etc**: (배포/CI 도구는 환경에 맞게 기재)

---

## 🧱 아키텍처 (간단)
- MVC 기반 (Controller → Service → Mapper → DB)
- 권한/세션 정보를 기반으로 화면/기능 제어
- 주요 이벤트는 SysLog로 기록하여 추적 가능

---

## 🗓 진행 일정

### ✅ 기존 일정(수업 일정표 기준)
- **12/23** PPT
- **12/24** 리허설
- **12/29** 프로젝트 발표 :contentReference[oaicite:1]{index=1}

### 🔄 변경된 일정(최신)
- **12/22** PPT
- **12/23 ~ 12/24** 리허설
- **12/26** 발표

> 일정 변경 사항은 위 “변경된 일정(최신)”을 기준으로 진행합니다.

---

## 🚀 실행 방법 (템플릿)
```bash
# 1) DB 세팅 (Oracle)
# 2) application 설정(DB 접속 정보 등)
# 3) 서버 실행
# 4) 브라우저 접속
