//실습 7.5.1 외부 인터럽트 INT0을 사용하여 LED 패턴 이동

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

static unsigned char pattern[8] = {0xFE, 0xFD, 0xFB, 0xF7, 0xEF, 0xDF, 0xBF, 0x7F};

ISR(INT0_vect)
{
	static int i=0;
	if(++i==8) i=0;

	PORTA = pattern[i];
	
	_delay_ms(20);
	while(~PIND & 0x01);	//스위치 해제를 기다림
	_delay_ms(20);

	EIFR = (1 << INTF0);	//인터럽트 플래그 INTF0을 리셋
}

int main()
{
	DDRA = 0xFF;	//포트 A를 출력 포트로 설정
	PORTA = pattern[0];	//처음 패턴으로 LED를 켠다.

	EICRA = (2 << ISC00);	// ISC01:0 =2 (하강 모서리 트리거)
	EIMSK = (1 << INT0);	// INT0 비트 세트 (INT0 허용)
	sei();
	while(1);	//무한 루프
}
