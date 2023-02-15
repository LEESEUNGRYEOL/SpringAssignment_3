# SpringAssignment_3

스프링 2주차 개인과제 (LEVEL 3)
## ERD
<img width="841" alt="image" src="https://user-images.githubusercontent.com/96409909/219075307-b07cec6c-993a-410e-8575-252272a2fe63.png">

## 요구사항

**1. 회원 가입 API**

- username, password를 Client에서 전달받기
- username은  `최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)`로 구성되어야 한다.
- password는  `최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자`로 구성되어야 한다.
- DB에 중복된 username이 없다면 회원을 저장하고 Client 로 성공했다는 메시지, 상태코드 반환하기
- 회원 권한 부여하기 (ADMIN, USER) - ADMIN 회원은 모든 게시글, 댓글 수정 / 삭제 가능

**2. 로그인 API**

- username, password를 Client에서 전달받기
- DB에서 username을 사용하여 저장된 회원의 유무를 확인하고 있다면 password 비교하기
- 로그인 성공 시, 로그인에 성공한 유저의 정보와 JWT를 활용하여 토큰을 발급하고,
  발급한 토큰을 Header에 추가하고 성공했다는 메시지, 상태코드 와 함께 Client에 반환하기

**3. 댓글 작성 API**

- 토큰을 검사하여, 유효한 토큰일 경우에만 댓글 작성 가능
- 선택한 게시글의 DB 저장 유무를 확인하기
- 선택한 게시글이 있다면 댓글을 등록하고 등록된 댓글 반환하기

**4. 댓글 수정 API**

- 토큰을 검사한 후, 유효한 토큰이면서 해당 사용자가 작성한 댓글만 수정 가능
- 선택한 댓글의 DB 저장 유무를 확인하기
- 선택한 댓글이 있다면 댓글 수정하고 수정된 댓글 반환하기

**5. 댓글 삭제 API**

- 토큰을 검사한 후, 유효한 토큰이면서 해당 사용자가 작성한 댓글만 삭제 가능
- 선택한 댓글의 DB 저장 유무를 확인하기
- 선택한 댓글이 있다면 댓글 삭제하고 Client 로 성공했다는 메시지, 상태코드 반환하기

**6. 예외 처리**

- 토큰이 필요한 API 요청에서 토큰을 전달하지 않았거나 정상 토큰이 아닐 때는 "토큰이 유효하지 않습니다." 라는 에러메시지와 statusCode: 400을 Client에 반환하기
- 토큰이 있고, 유효한 토큰이지만 해당 사용자가 작성한 게시글/댓글이 아닌 경우에는 “작성자만 삭제/수정할 수 있습니다.”라는 에러메시지와 statusCode: 400을 Client에 반환하기
- DB에 이미 존재하는 username으로 회원가입을 요청한 경우 "중복된 username 입니다." 라는 에러메시지와 statusCode: 400을 Client에 반환하기
- 로그인 시, 전달된 username과 password 중 맞지 않는 정보가 있다면 "회원을 찾을 수 없습니다."라는 에러메시지와 statusCode: 400을 Client에 반환하기

**7. 전체 게시글 목록 조회 API**

- 제목, 작성자명(username), 작성 내용, 작성 날짜를 조회하기
- 작성 날짜 기준 내림차순으로 정렬하기
- 각각의 게시글에 등록된 모든 댓글을 게시글과 같이 Client에 반환하기
- 댓글은 작성 날짜 기준 내림차순으로 정렬하기

**8. 게시글 작성 API**

- 토큰을 검사하여, 유효한 토큰일 경우에만 게시글 작성 가능
- 제목, 작성자명(username), 작성 내용을 저장하고
- 저장된 게시글을 Client 로 반환하기

**9. 선택한 게시글 조회 API**

- 선택한 게시글의 제목, 작성자명(username), 작성 날짜, 작성 내용을 조회하기
  (검색 기능이 아닙니다. 간단한 게시글 조회만 구현해주세요.)
- 선택한 게시글에 등록된 모든 댓글을 선택한 게시글과 같이 Client에 반환하기
- 댓글은 작성 날짜 기준 내림차순으로 정렬하기

**10. 선택한 게시글 수정 API**

- 토큰을 검사한 후, 유효한 토큰이면서 해당 사용자가 작성한 게시글만 수정 가능
- 제목, 작성 내용을 수정하고 수정된 게시글을 Client 로 반환하기

**11. 선택한 게시글 삭제 API**

- 토큰을 검사한 후, 유효한 토큰이면서 해당 사용자가 작성한 게시글만 삭제 가능
- 선택한 게시글을 삭제하고 Client 로 성공했다는 메시지, 상태코드 반환하기

## API 명세표
https://quartz-journey-1b0.notion.site/f1f720440bf04cc69db51e22f6b4c798?v=472c5c6894b4453cb83f0b55a841fe2b

## 질문사항

1. 처음 설계한 API 명세서에 변경사항이 있었나요?
   변경 되었다면 어떤 점 때문 일까요? 첫 설계의 중요성에 대해 작성해 주세요!

- 본인이 느끼기에는 가장 큰 차이점은 Client에서 무엇을 전달해 주느냐의 차이 인 것 같다.
- 처음에는 내가 원하는 값을 전부다 전달하면 되겠다고 생각을 했지만, 잘 생각해 보았을때 프론트부분에서도 조정을 할 수 있다고 판단.
- 따라서 불필요한 변수들은 최대한 안 받으려고 노력을 했다. (ex.id 부분)

2. ERD를 먼저 설계한 후 Entity를 개발했을 때 어떤 점이 도움이 되셨나요?

- 사실 처음에는 ERD를 설계하는 부분에 있어서 먼저 하지 않고 들어갔었다.
- 이때 연관관계 Mapping 에 있어서 어려움이 있었다고 이야기 할 수 있을 것 같다.
- 그리고 나중에 연관관계를 맺어주는 것은, 지금은 H2를 사용하고 있지만, 나중에 RDB를 사용하게된다면 문제점이 커질 수 있다는 사실도 인지했다.
3. JWT를 사용하여 인증/인가를 구현 했을 때의 장점은 무엇일까요?

- 서버에 저장소가 필요하지 않다.
- JSON 형식으로 다른 서버와 쉽게 공유할 수 있다.
- Client 가 JWT를 저장하여, 서버의 인증 및 인가 작업을 자유롭게 수행 가능.
- JWT는 self-contained 방식으로 토큰에 필요한 모든 정보가 포함되어 있으므로, 추가 DB 쿼리 없이도 토큰 내용 확인 가능.
4. 반대로 JWT를 사용한 인증/인가의 한계점은 무엇일까요?
- JWT가 조작되는 경우, 토큰 자체에 사용자 데이터가 포함, 보안상 취약점이 발생할 수 있다.
- 만료시간 혹은 사용자의 로그아웃이 아닌 이상 취소가 불가능
- 저장소가 없으므로, 만료시간을 변경하거나, 사용자 세션을 강제로 끊을 수 없다.
5. 만약 댓글 기능이 있는 블로그에서 댓글이 달려있는 게시글을 삭제하려고 한다면 무슨 문제가 발생할까요? Database 테이블 관점에서 해결방법이 무엇일까요?
- 현재 지금 만든 기능에서는 문제점이 발생하지 않는다, 왜냐하면 entity 간의 연관관계를 정확히 설정했기 때문입다.
- 예를들어서 연관관계를 만들지 않았다고 가정한다면, 게시글은 댓글들과 연관이 없으므로, 댓글정보들이 누락이 될 수 있다.
- 따라서 연관관계를 맺어줘야 하는데, 우리가 이번에 했던 과제들 중 Foreign Key 제약 조건으로 처리가 가능하다.

6. IoC / DI 에 대해 간략하게 설명해 주세요!

- 일단 WIL에서 잠깐 다루었지만 다시한번 다룬다면 다음과 같다.
- **DI (Dependency Injection)** : 의존성 주입이라는 한국말으로 이해하는 것 보다는 영어 자체를 이해하는 편이 편하다.
  - 객체 하나가 필요한 것들을 직접 생산을 하는 것이 아니라 다른곳에서 주입 받는 다는 것이다.
  - 중요한 것은 이는 객체가 생성할때, 이 객체가 사용할 의존성 객체를 외부에서 주입받아서 객체간에 결합도를 낮추고, 이 유연성과 확장성을 높이는 방법이다.
- **IoC (Inversion Of Control)** : 프로그램의 제어의 흐름 자체가 내가 아닌 이 스프링이라는 프레임워크나 Container 과 같이 외부에서 결정되는 것을 말한다.
  - 즉 객체생성, 관리, 의존성 주입, 이러한 제어들을 개발자인 내가 하는 것이 아니라 외부에서 수행하는 것이다.
  - 이또한 목표가 객체간의 관계를 느슨하게 하여서 유지보수 및 확장성을 높이게 함에 있다.
- **둘의 연관관계**: DI는 IoC를 구현하기 위한 방법중에 하나이다.
  - 결합도를 낮추는 부분은 DI를 통해서 (객체들간의 의존성 자체를 외부에서 주입받는 방식으로)
  - 객체간의 관계를 유연하게 만드는 부분은 IoC로 (프로그램의 제어 흐름을 외부에서 결정하는 방식으로)

## 실행결과

#### [POST 결과]

#### [GET 결과]

#### [PUT 결과]

#### [DELETE 결과]

#### [총 진행 결과 DB]

## 진행상황
