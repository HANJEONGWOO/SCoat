//�ǽ� 7.5.1 �ܺ� ���ͷ�Ʈ INT0�� ����Ͽ� LED ���� �̵�

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

static unsigned char pattern[8] = {0xFE, 0xFD, 0xFB, 0xF7, 0xEF, 0xDF, 0xBF, 0x7F};
/*
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


ISR(INT5_vect)
{
	static int i=0;
	if(--i== -1) i=7;

	PORTA = pattern[i];

	_delay_ms(20);
	while(~PINE & 0x20);	//����ġ ������ ��ٸ�
	_delay_ms(20);

	EIFR = 0x20;	//���ͷ�Ʈ �÷��� INT5�� ����
}

int main()
{
	DDRA = 0xFF;	//��Ʈ A�� ��� ��Ʈ�� ����
	PORTA = pattern[0];	//ó�� �������� LED�� �Ҵ�.

	EICRB = 0x0A;	// ISC41:0 =2 - �� ���� ������ �ϰ� �𼭸��� ���ͷ�Ʈ �䱸
	EIMSK = 0x30;	// INT4 ��Ʈ ��Ʈ (INT4 ���)
	sei();
	while(1);	//���� ����
}
*/

//  10. 11�� �۾� ����
//- 1) ���ͷ�Ʈ�� �̿����� �ʰ� while�� �ȿ� Ű�Է��� �޾Ҵ��� Ȯ���ϴ� ���� �ۼ�
//- 2) �ܺ� ���ͷ�Ʈ�� ��ø���� �� ��� ���۵� Ȯ�� - ���� ������ ���� �ʾҴµ��� �����߻�
//- 3) �� �ҽ��� ���뿹��
int i = 0;

int main(void)
{
	DDRA = 0xFF;	//��Ʈ A ��� ����
	DDRB = 0x00;	//��Ʈ B �Է� ����
	PORTA = pattern[i];
	
	while(1)
	{
		if(PINB == 0x01)	{
			_delay_ms(2000);
			if(++i >= 8)
				i=0;
			_delay_ms(2000);
		}
		else if(PINB == 0x02)	{
			_delay_ms(2000);
			if(--i <= -1)
				i=7;
			_delay_ms(2000);
		}

		PORTA = pattern[i];
		
		_delay_ms(10);
	}

}
