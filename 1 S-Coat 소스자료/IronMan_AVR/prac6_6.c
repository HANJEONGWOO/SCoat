#include <avr/io.h>
#include <util/delay.h>
#include <stdio.h>


#define DEBOUNCING_DELAY 20

unsigned char pattern[8] = {0xFE, 0xFD, 0xFB, 0xF7, 0xEF, 0xDF, 0xBF, 0x7F};

int main()
{
	int i=0;

	DDRA = 0xFF;	//��Ʈ A�� ������� ����
	DDRD = 0x00;	//��Ʈ D�� �Է����� ����
	PORTA = pattern[i];	//ó�� �������� LED�� �Ҵ�.

	while(1)
	{
		while( !(~PIND & 0x01) );	//����ġ ������ ��ٸ�
		_delay_ms(20);	//�ð� ����

		if(++i==8) i=0;	//������ ���Ͽ��� �ε��� ����
		PORTA = pattern[i];

		while(~PIND & 0x01);	//����ġ �������� ��ٸ�
		_delay_ms(20);	//�ð� ����
	}
}
