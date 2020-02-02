#include <avr/io.h>
#include <avr/interrupt.h>
static unsigned char pattern[8]
	= {0xFE, 0xFD, 0xFB, 0xF7, 0xEF, 0xDF, 0xBF, 0x7F};

ISR(INT4_vect)
{

}

ISR(TIMER0_OVF_vect)	//Ÿ�̸� 0 �����÷� ���ͷ�Ʈ ���񽺷�ƾ
{
	static int index = 0;	//���� �ε���
	static char n_enter = 0;	//���ͷ�Ʈ Ƚ��

	TCNT0 = 0;	//10msec �Ŀ� ���ͷ�Ʈ �߻�
	n_enter++;	//���ͷ�Ʈ Ƚ�� ����

	if(n_enter == 20)	//200msec
	{
		n_enter = 0;
		PORTA = pattern[index++];	//���� �̵�
		if(index==8) index = 0;
	}
}

int main()
{
	DDRA = 0xFF;	//A��Ʈ�� ������� ����
	PORTA = 0xFF;	//LED�� ����.

	//Ÿ�̸� / ī���� 0 ����
	TCCR0 = 0x00;	//ǥ�� ���, Ÿ�̸� ����
	TCNT0 = 100;	//Ÿ�̸� �ʱⰪ ����

	//���ͷ�Ʈ ����
	TIMSK = (1 << TOIE0);	//Ÿ�̸� 0 �����÷� ���ͷ�Ʈ ���
	sei();

	TCCR0 |= (7<<CS00);	//���ֺ� 1024�� Ÿ�̸� ����

	while(1);
}
