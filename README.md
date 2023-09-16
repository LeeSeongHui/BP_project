# 블루피플
<br>

# :books: <a name="outline">개요</a>
<br>
<img src="https://github.com/LeeSeongHui/BP_project/blob/master/app/src/main/res/drawable/portfolio/logo.png" width="70" height="100"/>


> **프로젝트** : 현지인 매칭 여행 플랫폼 '블루피플' 
>
> **기획 및 제작** : 블루피플
>
> **분류** : 팀 프로젝트
>
> **제작 기간** : 2020.03.02 ~ 2023.6.20
>
> **프로젝트 목적** : 여행 정보를 수집할 때 여행자의 검색 능력과 매체를 통한 정보습득 방법으로는 개인의 여행 취향에 맞는 정보의 질이 보장되지 않고 있다. 뿐만 아니라, 국내 여행지에 대한 콘텐츠 다양성 부족 또한 여행자들의 발걸음을 돌아서게 하고 있다. 이러한 문제점을 인식하여 신뢰할 수 있는 현지인을 통해, 합리적인 맞춤형 여행정보를 실시간으로 제공할 수 있는 플랫폼을 개발하게 되었다. ‘온라인 현지인 가이드’라는 특색으로 코로나 장기화 속 증가하고 있는 국내여행 수요와 변화된 여행 트렌드에 적합한 여행정보 서비스를 제공할 수 있다. 코로나 이후에도 국내여행객들이 꾸준히 증가할 수 있도록 서비스를 제공하여 궁극적으로 지역경제 활성화에 이바지하는 것을 목표로 하고 있다.
>
> **프로젝트 내용** : '블루피플'은 국내여행을 즐기고 싶은 자유여행객에게 1:1현지인 매칭을 통해 온라인 맞춤형 정보를 실시간으로 제공해 주는 애플리케이션이다. '블루오션', '잘 알려지지 않아 유망한 시장'이라는 뜻이 담겨있듯 '블루피플' 또한 현지인들만 아는, 잘 알려지지 않은 곳을 소개해 줄 수 있는 사람들이라는 뜻이 담겨 있다. 자유 여행객 수의 비율이 높고, 주로 지인을 통해 여행정보를 획득하는 근래 국내 관광시장의 상황에서 매칭 서비스 애플리케이션을 출시해 자유여행객들이 마치 현지에 있는 자신의 친구에게 물어보듯 편하게 여행에 관한 궁금증을 해결해 주도록 한다. 여행자는 해시태그와 지역정보를 기반으로 생성된 현지인 리스트에서 맞춤형 현지인을 매칭하고, 현지인과의 실시간 채팅을 통하여 양질의 정보를 얻을 수 있다. 또한 현지인은 여행자와의 동기화된 화면공유를 통해 여행정보를 효율적으로 전달이 가능하다.
<br />

# :bookmark_tabs: <a name="function">기능 구현 시연 영상</a>
<br/>

# <a name="fun1">1. 시작화면(회원가입, 로그인)
<br />

<div align="center">
  <img src="https://github.com/LeeSeongHui/BP_project/blob/master/app/src/main/res/drawable/portfolio/blue_01.gif" width="300" height="550" />
</div>
<br />
- 애플리케이션 실행 시 첫 화면은 로그인 화면으로 이동<br />
- 회원가입과 로그인 방법은 2가지로, 플랫폼 회원가입을 통한 로그인과 SNS연동 방법이 있음
<br />
<br />
<br />

# <a name="fun2">2. 화면구성((1)홈, (2)매칭, (3)채팅, (4)설정)
<br />
<div align="center">
  <img src="https://github.com/LeeSeongHui/BP_project/blob/master/app/src/main/res/drawable/portfolio/blue_02.gif" width="300" height="550" />
</div>
<br />
- 홈화면 하단 네비게이션바를 이용해 서비스 화면을 이동할 수 있음<br />
- 매칭화면에서 지역 및 해시태그 검색을 통해 현지인 리스트를 조회할 수 있고, 현지인 상세 프로필에서 기본정보(소개말, 리뷰 등)를 확인한 후 하단 '매칭 요청' 버튼을 통해 현지인에게 알림을 보낼 수 있음<br />
- 채팅화면에서 현지인이 매칭을 수락해 개설된 채팅방을 확인할 수 있음<br />
- 설정화면에서는 현지인등록, 즐겨찾기(현지인 상세 프로필 화면), 서비스 피드백 남기기 등의 기능을 제공함
<br />
<br />
<br />

# <a name="fun3">3. 채팅화면
<br />
<div align="center">
  <img src="https://github.com/LeeSeongHui/BP_project/blob/master/app/src/main/res/drawable/portfolio/blue_03.gif" width="300" height="550" />
</div>
<br />
- 개설된 채팅방에서 텍스트와 이미지를 전송하여 상대와 대화를 나눌 수 있음<br />
- 메시지 전송시 1대1 대화방의 상대에게 push알림을 전송함
<br />
<br />
<br />

# <a name="fun4">4. 지도 위치핀 정보 공유 서비스
<br />
<div align="center">
  <img src="https://github.com/LeeSeongHui/BP_project/blob/master/app/src/main/res/drawable/portfolio/blue_04.gif" width="300" height="550" />
</div>
<br />
- 채팅방 내부 지도 서비스를 통해 현지인은 추천하는 장소를 지도상에 위치핀으로 전달하고 싶은 내용과 함께 등록해  

<div align="center">
  <img src="https://github.com/LeeSeongHui/BP_project/blob/master/app/src/main/res/drawable/portfolio/blue_05.gif" width="300" height="550" />
</div>

