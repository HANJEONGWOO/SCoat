#include <avr/io.h>
#include <util/delay.h>
#include <stdio.h>


#define DEBOUNCING_DELAY 20

unsigned char pattern[8] = {0xFE, 0xFD, 0xFB, 0xF7, 0xEF, 0xDF, 0xBF, 0x7F};

int main()
{
	int i=0;

	DDRA = 0xFF;	//포트 A를 출력으로 설정
	DDRD = 0x00;	//포트 D를 입력으로 설정
	PORTA = pattern[i];	//처음 패턴으로 LED를 켠다.

	while(1)
	{
		while( !(~PIND & 0x01) );	//스위치 누름을 기다림
		_delay_ms(20);	//시간 지연

		if(++i==8) i=0;	//마지막 패턴에서 인덱스 리셋
		PORTA = pattern[i];

		while(~PIND & 0x01);	//스위치 떨어짐을 기다림
		_delay_ms(20);	//시간 지연
	}
}
