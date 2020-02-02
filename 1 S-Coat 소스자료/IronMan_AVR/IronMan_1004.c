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

#define TEST	0

#define CAMERA		0
#define IDLE		1	//ī�޶� ���� ��ũ�� ���
#define SHOT		2
#define CALL_SIZE	3

static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);

int val0=0, val1=0, val2 =0;		//��, ���� ������ ����
int sensor_flag = 0;	//�����÷���
char right_h=0, left_h=0, front_h=0;


// ***ī�޶� ���� �÷��� ����*** //
unsigned char img_buffer[9] = "";							//  call_size �����
static int img_cnt;
static int call_size_flag;
static int call_img_flag;
static int img_data_start_flag;
static int img_data_cnt;
static int idle_cnt;
static int shot_cnt;
static int call_size_cnt;
static int command;

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
  DDRF=0xF8;	//F 0, 1, 2(����)�� �� �Է� ����
  ADCSRA = 0x00;	//ó������ DISABLE -> Ÿ�̸� ī��Ʈ�� �̿��Ͽ� ENABLE �� ����

  DDRA=0x07;	//A 0, 1�� �� ��� ����(����) || 2�� �� ��� ���� (LED)

  TIMSK = 0x04;		// Ÿ�̸�, ī��Ʈ 1 �����÷� ���ͷ�Ʈ ���
  TCCR1A = 0x00;	//
  TCCR1B = 0x03;	//���ֺ� 64		
}

void uart0_init(void) //Camera
{
 UCSR0B = 0x00; //disable while setting baud rate
 UCSR0A = 0x00;
 UCSR0C = 0x06;
 UBRR0L = 0x19; //384
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
	
	if(TEST)
		printf("TIMER1_OVF_vect!\n");

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
  fdevopen(Putchar, Getchar); //file stream 0
  sei();
  port_init();

}

//*** ī�޶� ���� �Լ� ***//
static int putchar1(char c)	//ī�޶� ��ɾ� ����
{
	while(!(UCSR0A & 0x20));
		UDR0 = c;
	return 0;
}


void reset(){		//Camera Command(reset)
 //printf("\r\nRESET  ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x26);
 putchar1(0x00);
 img_cnt = 0;
}
 
void resize(){		//Camera Command(resize)
 //printf("\r\nRESIZE  ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x31);
 putchar1(0x05);
 putchar1(0x04);
 putchar1(0x01);
 putchar1(0x00);
 putchar1(0x19);
 putchar1(0x11);
 img_cnt = 0;
}
 
void set_rate(){	//Camera Command(set_rate 115200)
 //printf("\r\nSET_RATE ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x24);
 putchar1(0x03);
 putchar1(0x01);
 putchar1(0x0D);
 putchar1(0xA6);
 img_cnt = 0;
}

void idle(){		//Camera Command(Idle)
 //printf("\r\nIDLE  ");
 init_buffer(CAMERA);
 command = IDLE;
 idle_cnt = 0;
 img_cnt = 0;
 call_img_flag = 0;
 img_data_start_flag = 0;
 
 putchar1(0x56); _delay_ms(1);
 putchar1(0x00); _delay_ms(1);
 putchar1(0x36); _delay_ms(1);
 putchar1(0x01); _delay_ms(1);
 putchar1(0x03); _delay_ms(1);
 
 if(TEST)
 	printf("IDLE CALL!\\n");
}
 
void shot(){		//Camera Command(shot)
 //printf("\r\nSHOT  ");
 command = SHOT;
 idle_cnt = 0;
 shot_cnt = 0;
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x36);
 putchar1(0x01);
 putchar1(0x00);
 img_cnt = 0;
}
 
void call_size(){	//Camera Command(call_size)
 //printf("\r\nCALL_SIZE ");
 command = CALL_SIZE;
 shot_cnt = 0;
 call_size_cnt = 0;
 call_size_flag = 1;
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x34);
 putchar1(0x01);
 putchar1(0x00);
}
 
void call_img(){	//Camera Command(call_img)
 //printf("\r\nCALL_IMG ");
 command = 0;
 call_size_flag = 0;
 call_img_flag = 1;
 img_data_start_flag = 0;
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x32);
 putchar1(0x0c);
 putchar1(0x00);
 putchar1(0x0A);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(0x00);
 putchar1(img_buffer[7]);
 putchar1(img_buffer[8]);
 putchar1(0x00);
 putchar1(0x0A);
 img_cnt = 0;
}
 
void zip(){			//Camera Command(zip)
 //printf("\r\nZIP  ");
 putchar1(0x56);
 putchar1(0x00);
 putchar1(0x31);
 putchar1(0x05);
 putchar1(0x01);
 putchar1(0x01);
 putchar1(0x12);
 putchar1(0x04);
 putchar1(0xFF);
 img_cnt = 0;
}

void init_camera()
{
 if(TEST)
 	printf("INIT CAMERA CALL!\n");

	 reset(); _delay_ms(1000);
	 resize(); _delay_ms(50);
	 zip(); _delay_ms(50);
	 set_rate(); _delay_ms(50);		//
	 UBRR0L = 0x08; printf("\r\n chang boudrate"); _delay_ms(50);
	 //idle();
}



void init_buffer(int select)
{
	int i;
	
	switch(select)
	{
	case CAMERA:	//0
		for(i=0; i<9; i++)
			img_buffer[i] = 0x00;
		break;
	}
}


// *** USART ���ͷ�Ʈ
ISR(USART0_RX_vect)	//Camera�� ���ſϷ� ���ͷ�Ʈ
{
	char uart0_data = UDR0;

	switch(command)
	{
	case IDLE:
		idle_cnt++;
		break;
	case SHOT:
		shot_cnt++;
		break;
	case CALL_SIZE:
		call_size_cnt++;
		break;
	}
	
	if(call_size_flag == 1)	{
		img_buffer[img_cnt] = uart0_data;
		img_cnt++;
	}

	if(uart0_data == 0xff)
		img_data_start_flag = 1;
	
	if(uart0_data == 0xd9)
		img_data_cnt = 1;
	else	{
		if(uart0_data == 0x76 && img_data_cnt == 1)	{
			img_data_start_flag = 0;
			img_data_cnt = 0;
			printf("\n");
			_delay_ms(100);
			init_buffer(CAMERA);

			TIMSK = 0x04;	//Ÿ�̸� �ο��̺�

		}
		else
			img_data_cnt = 0;
	}

	if(call_img_flag == 1 && img_data_start_flag == 1)
		UDR1 = uart0_data;
}


ISR(USART1_RX_vect)	//Computer
{
	/*** ����� ��ǻ�ͷκ��� �޴� ������ ***/
	/*** ���߿��� ����Ʈ�����κ��� �޴� ������ ***/
	
	char ch = UDR1;

	switch(ch)
	{
			case 'c':
				TIMSK = 0x00;	//Ÿ�̸� �𽺿��̺�
				ADCSRA = 0x00;	//ADC ���ͷ�Ʈ �𽺿��̺�
				idle();
			break;
			case 'd'://
				printf("idle cnt : %d\n", idle_cnt);
				printf("shot_cnt : %d\n", shot_cnt);
				printf("call_size_cnt : %d\n", call_size_cnt);
			break;
	}
	
	if(TEST)
		printf("%c", UDR1);	
}


int main(void)
{
	init_devices();
	init_camera();

	printf("\r\n\r\nConnect with IronMan_Suit\r\n");

	while(1)
	{
	// *** ī�޶� ���� ī��Ʈ*** //
	if(idle_cnt >= 3)	{
		_delay_ms(45);
		shot();
		continue;
	}
	else if(shot_cnt == 5)	{
		call_size();
		continue;
	}
	else if(call_size_cnt == 9)	{
		call_size_cnt = 0;
		_delay_ms(50);
		call_img();
		continue;
	}
		

	// *** �����κ� *** //
	
	if(val0 > 300)	{	//����
		PORTA |= 0x01;
		right_h = 1;
	}
	else if(right_h == 1)	{
		PORTA ^= 0x01;
		right_h = 0;
	}
	if(val1 > 300)	{	//������
		PORTA |= 0x02;
		left_h = 1;	//�������� ���� : 0, �����ִ� ���� : 1
	}
	else if(left_h == 1)	{
		PORTA ^= 0x02;
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
	
	//printf("val0 : %3d, val1 : %3d, val2 : %3d\n", val0, val1, val2);
	//_delay_ms(1000);
		
	}	//while

	return 0;
}


