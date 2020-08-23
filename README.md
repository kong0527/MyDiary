## [ 프로젝트 소개 ]
 2018년 2학기 '모바일 응용' 수업에서 진행한 개인 프로젝트입니다.

일기를 기록할 수 있는 다이어리 애플리케이션입니다.

Android의 내장 DB, Google Maps Api와 네이버 지역 검색 Api를 이용했습니다.

<br/>

## [ 프로젝트 핵심기능 ]
1. 초기화면 (비밀번호 입력)

   🔑 비밀번호는 db에 저장되도록 구현되어 있습니다.

   🔑 비밀번호와 관련된 다양한 경고 메시지가 상황별로 나타납니다.

   ​      비밀번호가 생성되지 않았는데 비밀번호를 입력할 경우 / 수정시 일치하지 않을 경우 / 비밀번호가 존재하는데 생성을 할 경우

   ![image](https://user-images.githubusercontent.com/64277114/90981622-21ec9380-e59d-11ea-9569-651c3d825ba6.png)  ![image](https://user-images.githubusercontent.com/64277114/90981673-637d3e80-e59d-11ea-9683-9c738a30b181.png)

   <br/>

2. 일기 목록 조회

   🔑 비밀번호를 맞게 입력하면 일기 목록을 띄워줍니다.

   🔑 일기 목록은 onClick 하면 해당 일기의 상세정보를 보여주고, 상세정보에 들어가면 수정을 할 수 있습니다.

   🔑 일기 목록을 longClick 하면 삭제를 할 것이냐는 대화창을 띄웁니다.

   ![image](https://user-images.githubusercontent.com/64277114/90981641-3f216200-e59d-11ea-846e-58d7cc8b2cab.png) ![image](https://user-images.githubusercontent.com/64277114/90981657-519b9b80-e59d-11ea-872f-7a65a5acd8b4.png)

   <br/>

3. 일기 작성

   🔑 일기 목록에서 오른쪽 상단의 menu 버튼을 누르면 일기추가를 할 수 있습니다.

   🔑 날짜 위젯을 통해 날짜를 선택할 수 있습니다.

   🔑 ImageView를 클릭하면 사진찍기와 갤러리에서 불러오기 중 하나를 선택할 수 있습니다. 

   🔑 Naver 지역검색 API 와 Google Maps API 를 활용한 주소검색을 할 수 있습니다. 

   🔑 주소검색 listView에서 longClick을 하면 해당 주소를 지도에 마커에 표시하여 전화번호와 장소의 이름을 보여줍니다. 

   ![image](https://user-images.githubusercontent.com/64277114/90981720-a212f900-e59d-11ea-937f-90e728ebbdf7.png)  ![image](https://user-images.githubusercontent.com/64277114/90981743-b656f600-e59d-11ea-8e7a-06c8011a5dcd.png)

   <br/>

4. 일기 검색

   🔑 일기 목록에서 오른쪽 상단의 menu 버튼을 누르면 제목으로 검색을 할 수 있습니다.

![image](https://user-images.githubusercontent.com/64277114/90981797-ffa74580-e59d-11ea-86ee-fe3c95a0e4bc.png) 

<br/>

<br/>

💥 **해당 프로젝트는 2018년에 작성된 프로젝트로 현재와는 맞지 않는 버전이나 코드가 존재할 수 있습니다.**