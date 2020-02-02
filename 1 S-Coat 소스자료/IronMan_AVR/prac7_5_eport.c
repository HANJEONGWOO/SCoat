//실습 7.5.1 외부 인터럽트 INT0을 사용하여 LED 패턴 이동

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

static unsigned char pattern[8] = {0xFE, 0xFD, 0xFB, 0xF7, 0xEF, 0xDF, 0xBF, 0x7F};

ISR(INT4_vect)
{
	static int i=0;
	if(++i==8) i=0;

	PORTA = pattern[i];
	
	_delay_ms(20);
	while(~PINE & 0x10);	//스위치 해제를 기다림
	_delay_ms(20);

	EIFR = 0x10;	//인터럽트 플래그 INTF4을 리셋
}

int main()
{
	DDRA = 0xFF;	//포트 A를 출력 포트로 설정
	PORTA = pattern[0];	//처음 패턴으로 LED를 켠다.

	EICRB = 0x02;	// ISC41:0 =2 - 두 샘플 사이의 하강 모서리가 인터럽트 요구
	EIMSK = 0x10;	// INT4 비트 세트 (INT4 허용)
	sei();
	while(1);	//무한 루프
}
