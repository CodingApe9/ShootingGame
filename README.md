# ShootingGame
게임 룰
1. 초기상태 = 대기상태(남은 시간은 1분 30초) 
	게임을 시작하려면 아무 키나 누르기
2. esc를 누르면 대기상태로 전환 (화면에 YOU DIE 출력)
3. 목숨은 3개, 상대의 총알을 한 번 맞으면 1 감소(1회 깜빡임)
	목숨이 0이 되면 게임 종료(3회 깜빡임, 화면에 YOU DIE 출력)
	그리고 대기상태로 전환
4. 상대는 아래로 전진하며 좌우(무작위)로 회피 기동을 함.
	그리고 총알을 랜던하게 발사
5. 1분이 지나면 상대의 숫자가 많아짐(생성량 증가)
6. 남은 시간이 0초가 되면 게임 클리어(화면에 CLEAR 출력)
	게임이 끝나도 점수를 확인 가능
	대기상태로 전환, 다시 키를 눌러서 게임을 플레이 가능.
7. 최대한 많은 점수를 얻는 것이 목표

배경이 위로 조금씩 움직임. 
비행기가 실제로 날아가는 듯한 착시현상.
내 캐릭터의 총알 무제한, 연사 가능.
상대(적기)의 두 가지 중에 이미지를 랜덤하게 선택
목숨 3개 부여

캐릭터 조종
1. 방향키로 상, 하, 좌, 우 이동
2. 스페이스바를 이용해 총알 발사. (연사 가능)
	상대에게 총알을 맞추면 세 번 깜빡이고 사라짐
	그리고 점수는 10 증가.
![image](https://github.com/CodingApe9/ShootingGame/assets/117576404/a6660f98-8b3c-42e4-a9f1-79b4b7ff0f1b)
