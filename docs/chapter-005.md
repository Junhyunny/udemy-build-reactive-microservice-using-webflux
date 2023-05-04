
## Chapter 005. Why Reactive Programming? - Part 1

* 시스템 아키텍처에 대한 변화
    * 과거 10~15년 전
        * 모놀리스 어플리케이션
        * 어플리케이션 서버에 배포
        * 분산 시스템 수용하지 않음
    * 현재
        * 마이크로서비스 어플리케이션
        * 클라우드 환경에 배포
        * 분산 시스템 수용
* 어플리케이션에 대한 기대 사항들
    * 수밀리초 안에 응답
    * 다운 타임(down-time)이 없음
    * 부하에 대해 자동으로 스케일업(scaleup) 가능
* REST API in Spring boot
    * 기존 사용자 요청마다 톰캣에서 관리되는 스레드 풀의 스레드가 한 개씩 매칭됨
    * thread per request model
    * 이 API 요청은 블록킹 방식
* Spring MVC limitations
    * 내장 톰캣의 스레드 풀 사이즈는 200
    * 스레드 풀 사이즈를 필요에 제한적으로 늘릴 수 있다.
* Thread's limitations
    * 스레드는 비싼 자원
    * 1MB 힙(heap) 메모리 영억을 차지
    * 많은 수의 스레드는 많은 메모리를 차지한다는 것을 의미
    * 프로세스에서 사용할 수 있는 힙 메모리 영역이 부족해짐
