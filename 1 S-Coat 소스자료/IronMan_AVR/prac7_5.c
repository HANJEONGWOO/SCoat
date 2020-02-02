//�ǽ� 7.5.1 �ܺ� ���ͷ�Ʈ INT0�� ����Ͽ� LED ���� �̵�

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
	while(~PIND & 0x01);	//����ġ ������ ��ٸ�
	_delay_ms(20);

	EIFR = (1 << INTF0);	//���ͷ�Ʈ �÷��� INTF0�� ����
}

int main()
{
	DDRA = 0xFF;	//��Ʈ A�� ��� ��Ʈ�� ����
	PORTA = pattern[0];	//ó�� �������� LED�� �Ҵ�.

	EICRA = (2 << ISC00);	// ISC01:0 =2 (�ϰ� �𼭸� Ʈ����)
	EIMSK = (1 << INT0);	// INT0 ��Ʈ ��Ʈ (INT0 ���)
	sei();
	while(1);	//���� ����
}
