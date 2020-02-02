#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>

#define CAMERA		0

#define IDLE		1	//카메라 관련 매크로 상수
#define SHOT		2
#define CALL_SIZE	3

#define TEST	1

static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);

int val0=0, val1=0, val2 =0;		//좌, 우의 센서값 저장
int sensor_flag = 0;	//센서플래그
char right_h=0, left_h=0, front_h=0;

//10월 02일 작업내용
//1. ADC 인터럽트 때문에 블루투스, 외부 인터럽트를 이용한 명령어 전송이 되지 않음 -> 타이머 카운트 이용할 것
//2. 카메라 설정함수인 init_camera(); 호출시 프로그램이 멈추어버림 -> 원인 밝혀낼 것
//3. 소스코드 관리 잘 할 것!!! 


// ***카메라 관련 플래그 변수*** //
unsigned char img_buffer[9] = "";							//  call_size 저장용
static int img_cnt;
static int call_size_flag;
static int call_img_flag;
static int img_data_start_flag;
static int img_data_cnt;
static int idle_cnt;
static int shot_cnt;
static int call_size_cnt;
static int command;


ISR(ADC_vect)	//AD 컨버터 진동센서 관련 인터럽트 
{
	switch(sensor_flag)
	{
	case 0: 
	  ADMUX = 0xC0;	//// 내부 클럭 사용함
	  while((ADCSRA & 0x10) == 0);
	  	val0 = ADCW;
		ADCW = 0;
		sensor_flag++;
	  break;
	case 1:
	  ADMUX = 0xC1;	//// ""
	  while((ADCSRA & 0x10) == 0);
	  	val1 = ADCW;
		ADCW = 0;
		sensor_flag++;
	  break;
	case 2:
	  ADMUX = 0xC2; // 가슴 적외선
	  while((ADCSRA & 0x10) == 0);
	  val2 = ADCW;
	  ADCW = 0;
	  sensor_flag = 0;
	  break;
	  
	}
	
}

// *** 버튼 인터럽트 관련 부분 ***
ISR(INT4_vect)
{
	if(TEST)
		printf("INT4 Call!!!\n");
	
	
	_delay_ms(20);		//디바운싱
	while(~PINE & 0x10);	//스위치 누름을 기다림
	_delay_ms(20);		//디바운싱
		
	// 인터럽트 Disable 시키는 부분
  	EIMSK = 0x00;	//외부 인터럽트 DISABLE 
	ADCSRA = 0x00;	//AD 컨버팅 중지	

	idle();

	EIFR = 0x10;		//인터럽트 플래그 레지스터 리셋	- 실행후 리셋시켜야함
}

/*
ISR(INT0_vect)
{
	//printf("Trigger0 \n");
	
	if(connect_flag == 0 && EICRA | 0x02)
	{
		connect_flag = TRUE;
		EICRA &= 0xFC;
		EICRA |= 0x03;
	}
	else
	{
		connect_flag = FALSE;
		EICRA &= 0xFC;
		EICRA |= 0x02;
	}
}
*/

/*
ISR(INT0_vect)		//외부 인터럽트 0서비스루틴
{	
	int i;
	
	for(i=0; i<50; i++)	//딜레이 이용하여 0도로 움직임
	{					//딜레이 함수는 정확하지 않다.
		
		PORTB = 0x01;
		_delay_us(1000);
		PORTB = 0x00;
		_delay_us(19000);
	}

	_delay_ms(20);		//디바운싱
	while(PIND & 0x01);	//스위치 누름을 기다림
	_delay_ms(20);		//디바운싱

	EIFR = 0x01;		//인터럽트 플래그 레지스터 리셋
}
*/

static int putchar1(char c)	//카메라에 명령어 전송
{
	while(!(UCSR0A & 0x20));
		UDR0 = c;
	return 0;
}


void reset(){		//Camera Command(reset)
 printf("\r\nRESET  ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x26);
 putchar1(0x00);
 img_cnt = 0;
}
 
void resize(){		//Camera Command(resize)
 printf("\r\nRESIZE  ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x31);
 putchar1(0x05);
 putchar1(0x04);
 putchar1(0x01);
 putchar1(0x00);
 putchar1(0x19);
 putchar1(0x11);
 img_cnt = 0;
}
 
void set_rate(){	//Camera Command(set_rate 115200)
 printf("\r\nSET_RATE ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x24);
 putchar1(0x03);
 putchar1(0x01);
 putchar1(0x0D);
 putchar1(0xA6);
 img_cnt = 0;
}

void idle(){		//Camera Command(Idle)
 printf("\r\nIDLE  ");
 init_buffer(CAMERA);
 command = IDLE;
 idle_cnt = 0;
 img_cnt = 0;
 call_img_flag = 0;
 img_data_start_flag = 0;
 
 putchar1(0x56); _delay_ms(1);
 putchar1(0x00); _delay_ms(1);
 putchar1(0x36); _delay_ms(1);
 putchar1(0x01); _delay_ms(1);
 putchar1(0x03); _delay_ms(1);
 
 if(TEST)
 	printf("IDLE CALL!\\n");
}
 
void shot(){		//Camera Command(shot)
 printf("\r\nSHOT  ");
 command = SHOT;
 idle_cnt = 0;
 shot_cnt = 0;
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x36);
 putchar1(0x01);
 putchar1(0x00);
 img_cnt = 0;
}
 
void call_size(){	//Camera Command(call_size)
 printf("\r\nCALL_SIZE ");
 command = CALL_SIZE;
 shot_cnt = 0;
 call_size_cnt = 0;
 call_size_flag = 1;
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x34);
 putchar1(0x01);
 putchar1(0x00);
}
 
void call_img(){	//Camera Command(call_img)
 printf("\r\nCALL_IMG ");
 command = 0;
 call_size_flag = 0;
 call_img_flag = 1;
 img_data_start_flag = 0;
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x32);
 putchar1(0x0c);
 putchar1(0x00);
 putchar1(0x0A);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(img_buffer[7]);
 putchar1(img_buffer[8]);
 putchar1(0x00);
 putchar1(0x0A);
 img_cnt = 0;
}
 
void zip(){			//Camera Command(zip)
 //printf("\r\nZIP  ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x31);
 putchar1(0x05);
 putchar1(0x01);
 putchar1(0x01);
 putchar1(0x12);
 putchar1(0x04);
 putchar1(0xFF);
 img_cnt = 0;
}

void init_camera()
{
 if(TEST)
 	printf("INIT CAMERA CALL!\n");

 reset(); _delay_ms(1000);
 resize(); _delay_ms(50);
 zip(); _delay_ms(50);
 set_rate(); _delay_ms(50);		//
 UBRR0L = 0x08; printf("\r\n chang boudrate"); _delay_ms(50);
 idle();
}

void port_init(void)
{}
 
static int Putchar(char c, FILE *stream)//FILE 사용안함, 송신, avr->컴퓨터
{
 if(c == '\n')
  Putchar('\r', 0); 

 while(!(UCSR1A & 0x20)); // UDRE, data register empty
   UDR1 = c;
 return 0;
}
 
static int Getchar(FILE *stream)//수신, 컴퓨터->avr
{
 while(!(UCSR1A & 0x80));
 return UDR1;
}

void uart0_init(void) //
{
 UCSR0B = 0x00; //disable while setting baud rate
 UCSR0A = 0x00;
 UCSR0C = 0x06;
 UBRR0L = 0x08; //1152
 UBRR0H = 0x00; 
 UCSR0B = 0b10011000; //수신만
}

void uart1_init(void) //PC와 통신
{
 UCSR1B = 0x00; //disable while setting baud rate
 UCSR1A = 0x00;
 UCSR1C = 0x06;
 UBRR1L = 0x08; //set baud rate 115200
 UBRR1H = 0x00; 
 UCSR1B = 0b10011000; //송신 인터럽트 개방
}
 
void init_devices(void)
{
  cli(); //disable all interrupts
  port_init();
  uart0_init();  
  uart1_init();
  fdevopen(Putchar, Getchar);//file stream 0
  sei(); 

  DDRF=0xF8;	//F 0, 1, 2(가슴)번 핀 입력 설정
  //ADCSRA = 0xFF;

  DDRA=0x07;	//A 0, 1번 핀 출력 설정(진동) || 2번 핀 출력 설정 (LED)
  
  DDRE = 0x00;	//E 7, 6, 5, 4 입력 설정(인터럽트) -> e
  EICRB = 0x55;	//인터럽트 7, 6, 5, 4 하강,상승에지 인터럽트 요구 - O
  EIMSK |= 0xF0;	//INT7, 6, 5, 4인터럽트 요구

  /*
  7 : ADEN : A/D 컨버터 동작 허용
  6 : ADSC : 변환 시작
  5 : ADFR : 프리 런닝 모드
  4 : ADIF : 변환완료 인터럽트
  3 : ADIE : ***인터럽트 개별적으로 허용
  2~0 : 분주비 설정
  ADMUX = 0xC0;		//MUX 설정
  */

}

void init_buffer(int select)
{
	int i;
	
	switch(select)
	{
	case CAMERA:	//0
		for(i=0; i<9; i++)
			img_buffer[i] = 0x00;
		break;
	}
}
 
int main(void)
{
 init_devices();
 init_camera();
 
 printf("\r\n\r\nConnect with IronMan_Suit\r\n");

 if(TEST)
 	printf("MAIN CALL_ME!\n");
 while(1)
 {
 	/*
 	//ADCSRA = 0xFF;	//센서 개방

 	// *** 센서부분 *** //
	//센서부 값이 하나씩 밀렸기 때문에 값을 한번씩 밀어주기로 한다. -> 완료
	if(val1 > 300)	{	//왼쪽
		PORTA |= 0x02;
		right_h = 1;
	}
	else if(right_h == 1)	{
		PORTA ^= 0x02;
		right_h = 0;
	}
	if(val2 > 300)	{	//오른쪽
		PORTA |= 0x01;
		left_h = 1;	//진동없는 상태 : 0, 진동있는 상태 : 1
	}
	else if(left_h == 1)	{
		PORTA ^= 0x01;
		left_h = 0;
	}
	if(val0 > 500)	{	//가슴
		PORTA |= 0x03;
		front_h = 1;
	}
	else if(front_h == 1)	{
		PORTA ^= 0x03;
		front_h = 0;
	}
	//ADCSRA = 0x00;
	*/
	// *** 카메라 관련 카운트*** //
	if(idle_cnt >= 3)	{
		_delay_ms(45);
		shot();
	}
	
	if(shot_cnt == 5)
		call_size();

	if(call_size_cnt == 9)	{
	   //printf("#%x#%x\r\n", img_buffer[7], img_buffer[8]);
		call_size_cnt = 0;
		_delay_ms(50);
		call_img();
	}
	//_delay_ms(10);

	//printf("val0 : %3d, val1 : %3d, val2 : %3d\n", val0, val1, val2);
 }
 
 return 0;

}

ISR(USART0_RX_vect)	//Camera의 수신완료 인터럽트
{
	char uart0_data = UDR0;

	switch(command)
	{
	case IDLE:
		idle_cnt++;
		break;
	case SHOT:
		shot_cnt++;
		break;
	case CALL_SIZE:
		call_size_cnt++;
		break;
	}
	
	if(call_size_flag == 1)	{
		img_buffer[img_cnt] = uart0_data;
		img_cnt++;
	}

	if(uart0_data == 0xff)
		img_data_start_flag = 1;
	
	if(uart0_data == 0xd9)
		img_data_cnt = 1;
	else	{
		if(uart0_data == 0x76 && img_data_cnt == 1)	{
			img_data_start_flag = 0;
			img_data_cnt = 0;
			printf("\n");
			_delay_ms(100);
			init_buffer(CAMERA);
		}
		else
			img_data_cnt = 0;
	}

	if(call_img_flag == 1 && img_data_start_flag == 1)
		UDR1 = uart0_data;
}

ISR(USART1_RX_vect)	//Computer
{
	/*** 현재는 컴퓨터로부터 받는 데이터 ***/
	/*** 나중에는 스마트폰으로부터 받는 데이터 ***/
	
	char ch = UDR1;

	switch(ch)
	{
			case 'c':
			printf("c\n");
			ADCSRA = 0x00;	//ADC 인터럽트 디스에이블
			EIMSK = 0x00;	//외부 인터럽트 디스에이블
			idle();
			_delay_ms(100);
			break;
			
			case 'd'://
			printf("d");
			printf("idle cnt : %d\n", idle_cnt);
			printf("shot_cnt : %d\n", shot_cnt);
			printf("call_size_cnt : %d\n", call_size_cnt);
			break;
	}
	
	if(TEST)
		printf("%c", UDR1);	
		printf("USART1 RX CALL!\n");

}

