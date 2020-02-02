#include <stdio.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#define TEST 1

int light_0;	//���� ���� adc_0
int infrared_1;	//���ܼ� ���� adc_1

int sensor_flag = 0;	//�����÷���

static int Putchar(char c, FILE *stream)//FILE ������, �۽�, avr->��ǻ��
{
 if(c == '\n')
  Putchar('\r', 0); 

 while(!(UCSR1A & 0x20)); // UDRE, data register empty
   UDR1 = c;
 return 0;
}
 
static int Getchar(FILE *stream)//����, ��ǻ��->avr
{
 while(!(UCSR1A & 0x80));
 return UDR1;
}

void port_init(void)
{
	DDRF = 0xFC;
	ADCSRA = 0x00;	//ó������ DISABLE

	TIMSK = 0x04;	//Ÿ�̸� ī��Ʈ 1 �����÷� ���ͷ�Ʈ ���
	TCCR1A = 0x00;
	TCCR1B = 0x03;	//���ֺ� 64
	
	DDRD = 0x01;	//D ��Ʈ ���;
	PORTD = 0x00;	//���
}


void init_devices(void)
{
  cli(); //disable all interrupts
  port_init();
  //uart0_init();  
  uart1_init();
  fdevopen(Putchar, Getchar); //file stream 0
  sei();
  port_init();

}

ISR(TIMER1_OVF_vect)
{
	ADCSRA = 0xFF;
	
	//if(TEST)
	//	printf("TIMER1_OVF_vect!\n");

	switch(sensor_flag)
	{
	case 0: 
	  ADMUX = 0xC0;	//// ���� ���� 
	  while((ADCSRA & 0x10) == 0);
	  	light_0 = ADCW;
		ADCW = 0;
		sensor_flag++;
	  break;
	case 1:
	  ADMUX = 0xC1;	//// ���ܼ� ����
	  while((ADCSRA & 0x10) == 0);
	  	infrared_1 = ADCW;
		ADCW = 0;
		sensor_flag=0;
	  break;
	}

	ADCSRA = 0x00;
	
}

void uart1_init(void) //PC�� ���
{
 UCSR1B = 0x00; //disable while setting baud rate
 UCSR1A = 0x00;
 UCSR1C = 0x06;
 //UBRR1L = 0x08; //set baud rate 115200
 UBRR1L = 0x19; //set baud rate 38400
 UBRR1H = 0x00; 
 UCSR1B = 0b10011000; //�۽� ���ͷ�Ʈ ����
}

int main(void)
{
	
	init_devices();

	while(1)
	{
		if(light_0 >= 1020 || infrared_1 >= 800)
			PORTD = 0x01;
		else if(light_0 < 900 && infrared_1 < 700)
			PORTD = 0x00;


		printf("light_0 : %d, infrared_1 : %d\n", light_0, infrared_1);
		_delay_ms(100); 
	}

	return 0;
}


/*
ISR(ADC0_vect)
{
	ADMUX = 0xC0;	//// ���� ���� 
	  while((ADCSRA & 0x10) == 0);
	  	light_0 = ADCW;
		ADCW = 0;
}

ISR(ADC1_vect)
{
	ADMUX = 0xC1;	//// ���ܼ� ����
	  while((ADCSRA & 0x10) == 0);
	  	infrared_1 = ADCW;
		ADCW = 0;
}
*/
