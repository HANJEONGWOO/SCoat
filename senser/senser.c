#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/signal.h>
#include <util/delay.h>

void printnum(int a);
int numlen(int a);

	int val;
ISR(ADC_vect){
	val=ADCW;
}

int main(void){

	DDRA=0xFF;
	DDRF|=0b11100000;
//	ADCSRA|=0x11101011;
	ADMUX=0b11000000;
	
	while(1){
	
		ADCSRA = 0xF4; // 16분주, 폴링,
	//	 ADC 인에이블 , ADSC = 1
		PORTF=0b11100000;
		while ((ADCSRA & 0x10) == 0);
		val=ADCW;
		printnum(val);
		
	_delay_ms(1000);
		asm("nop"::);
	}
	return 0;
}
4
void printnum(int a){
	char NUM[10]={0x3F,0x06,0x5B,0x4F,0x66,0x6D,0x7D,0x27,0x7F,0x6F};
	int len=numlen(a);
	int temp=a%10;
	int i;
	
	DDRC=0xFF;
	DDRA=0xFF;
	
	PORTC=0b11110111;
	for(i=0;i<len;i++){
		PORTA=NUM[temp];
		//if(i==1) PORTA|=0b10000000;
		_delay_ms(1);
		PORTC=(0b00001000) | (PORTC>>1);
		a/=10;
		temp=a%10;
	}
	//PORTA=0x00;
	_delay_ms(1000);
}

int numlen(int a){
	int res=1;
	while(a>=10){
		a/=10;
		res++;
	}
	return res;
}
