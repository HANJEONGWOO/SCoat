/*
10. 4일 Timer ADC 작업내용
- 1. 타이머 오버플로 인터럽트 1을 이용하여 진동센서에 진동을 주도록 함(분주비 64)
- 2. 코드를 알아보기 쉽게 정리
- 3. 카메라 코드 추가할 예정
*/

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>
#define TEST	1

static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);

int val0=0, val1=0, val2 =0;		//좌, 우의 센서값 저장
int sensor_flag = 0;	//센서플래그
char right_h=0, left_h=0, front_h=0;

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

void uart0_init(void) //Camera
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

ISR(TIMER1_OVF_vect)
{
	ADCSRA = 0xFF;

	switch(sensor_flag)
	{
	case 0: 
	  ADMUX = 0xC0;	//// 내부 클럭 사용, 오른쪽 
	  while((ADCSRA & 0x10) == 0);
	  	val0 = ADCW;
		ADCW = 0;
		sensor_flag++;
	  break;
	case 1:
	  ADMUX = 0xC1;	//// 왼쪽
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

	ADCSRA = 0x00;
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
  ADCSRA = 0x00;	//처음에는 DISABLE -> 타이머 카운트를 이용하여 ENABLE 할 예정

  DDRA=0x07;	//A 0, 1번 핀 출력 설정(진동) || 2번 핀 출력 설정 (LED)
  
  TIMSK = 0x04;		// 타이머, 카운트 1 오버플로 인터럽트 허용
  TCCR1A = 0x00;	//
  TCCR1B = 0x03;	//분주비 64
}

int main(void)
{
	init_devices();

	printf("\r\n\r\nConnect with Timer_ADC\r\n");

	while(1)
	{

	// *** 센서부분 *** //
	if(val1 > 300)	{	//왼쪽
		PORTA |= 0x02;
		right_h = 1;
	}
	else if(right_h == 1)	{
		PORTA ^= 0x02;
		right_h = 0;
	}
	if(val0 > 300)	{	//오른쪽
		PORTA |= 0x01;
		left_h = 1;	//진동없는 상태 : 0, 진동있는 상태 : 1
	}
	else if(left_h == 1)	{
		PORTA ^= 0x01;
		left_h = 0;
	}
	if(val2 > 500)	{	//가슴
		PORTA |= 0x03;
		front_h = 1;
	}
	else if(front_h == 1)	{
		PORTA ^= 0x03;
		front_h = 0;
	}


	}	//while

	return 0;
}
