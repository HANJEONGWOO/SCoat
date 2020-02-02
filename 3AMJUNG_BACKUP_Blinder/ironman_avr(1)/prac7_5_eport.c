//�ǽ� 7.5.1 �ܺ� ���ͷ�Ʈ INT0�� ����Ͽ� LED ���� �̵�

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
	while(~PINE & 0x10);	//����ġ ������ ��ٸ�
	_delay_ms(20);

	EIFR = 0x10;	//���ͷ�Ʈ �÷��� INTF4�� ����
}

int main()
{
	DDRA = 0xFF;	//��Ʈ A�� ��� ��Ʈ�� ����
	PORTA = pattern[0];	//ó�� �������� LED�� �Ҵ�.

	EICRB = 0x02;	// ISC41:0 =2 - �� ���� ������ �ϰ� �𼭸��� ���ͷ�Ʈ �䱸
	EIMSK = 0x10;	// INT4 ��Ʈ ��Ʈ (INT4 ���)
	sei();
	while(1);	//���� ����
}
