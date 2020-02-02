/*
10. 4�� Timer ADC �۾�����
- 1. Ÿ�̸� �����÷� ���ͷ�Ʈ 1�� �̿��Ͽ� ���������� ������ �ֵ��� ��(���ֺ� 64)
- 2. �ڵ带 �˾ƺ��� ���� ����
- 3. ī�޶� �ڵ� �߰��� ����
*/

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>
#define TEST	1

static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);

int val0=0, val1=0, val2 =0;		//��, ���� ������ ����
int sensor_flag = 0;	//�����÷���
char right_h=0, left_h=0, front_h=0;

void port_init(void)
{}

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

void uart0_init(void) //Camera
{
 UCSR0B = 0x00; //disable while setting baud rate
 UCSR0A = 0x00;
 UCSR0C = 0x06;
 UBRR0L = 0x08; //1152
 UBRR0H = 0x00; 
 UCSR0B = 0b10011000; //���Ÿ�
}

void uart1_init(void) //PC�� ���
{
 UCSR1B = 0x00; //disable while setting baud rate
 UCSR1A = 0x00;
 UCSR1C = 0x06;
 UBRR1L = 0x08; //set baud rate 115200
 UBRR1H = 0x00; 
 UCSR1B = 0b10011000; //�۽� ���ͷ�Ʈ ����
}

ISR(TIMER1_OVF_vect)
{
	ADCSRA = 0xFF;

	switch(sensor_flag)
	{
	case 0: 
	  ADMUX = 0xC0;	//// ���� Ŭ�� ���, ������ 
	  while((ADCSRA & 0x10) == 0);
	  	val0 = ADCW;
		ADCW = 0;
		sensor_flag++;
	  break;
	case 1:
	  ADMUX = 0xC1;	//// ����
	  while((ADCSRA & 0x10) == 0);
	  	val1 = ADCW;
		ADCW = 0;
		sensor_flag++;
	  break;
	case 2:
	  ADMUX = 0xC2; // ���� ���ܼ�
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

  DDRF=0xF8;	//F 0, 1, 2(����)�� �� �Է� ����
  ADCSRA = 0x00;	//ó������ DISABLE -> Ÿ�̸� ī��Ʈ�� �̿��Ͽ� ENABLE �� ����

  DDRA=0x07;	//A 0, 1�� �� ��� ����(����) || 2�� �� ��� ���� (LED)
  
  TIMSK = 0x04;		// Ÿ�̸�, ī��Ʈ 1 �����÷� ���ͷ�Ʈ ���
  TCCR1A = 0x00;	//
  TCCR1B = 0x03;	//���ֺ� 64
}

int main(void)
{
	init_devices();

	printf("\r\n\r\nConnect with Timer_ADC\r\n");

	while(1)
	{

	// *** �����κ� *** //
	if(val1 > 300)	{	//����
		PORTA |= 0x02;
		right_h = 1;
	}
	else if(right_h == 1)	{
		PORTA ^= 0x02;
		right_h = 0;
	}
	if(val0 > 300)	{	//������
		PORTA |= 0x01;
		left_h = 1;	//�������� ���� : 0, �����ִ� ���� : 1
	}
	else if(left_h == 1)	{
		PORTA ^= 0x01;
		left_h = 0;
	}
	if(val2 > 500)	{	//����
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
