//0807 ĵ���� �ҽ� ���� ����
// - ���󱹿��� r�� ������ �ڵ����� ���µǵ��� ���α׷� ����(������ �� ������ ���� ����)
// - ������ġ �߰��� ���� ���� �Ӱ谪 ����(PITCH ���� ����)
// - �ҽ� ����

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>	//atan�� ����Լ�


#define END_IMU 29
#define MAX_BUFFER_SIZE 80	// ���� 50���� ���̰� 54�� �����ϹǷ� 60���� �ٲ�

#define YAW		&imu_buffer[0]
#define PITCH	&imu_buffer[2]
#define ROLL	&imu_buffer[4]
#define LATI_DEG		&gps_buffer[17]
#define LATI_SEC		&gps_buffer[22]	//22?
#define LONGI_DEG		&gps_buffer[30]
#define LONGI_SEC		&gps_buffer[36]	//
#define ALTI			&gps_buffer[54]	//����

#define LATI_DEG_LEN	2
#define LATI_MIN_LEN	8
#define LONGI_DEG_LEN	3
#define LONGI_MIN_LEN	8

#define CAMERA		0
#define IMU			2
#define GPS			3
#define ECT			4

#define IDLE		1
#define SHOT		2
#define CALL_SIZE	3

#define TRUE  1
#define FALSE 0

static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);

unsigned char gps_buffer[MAX_BUFFER_SIZE] = "";				// gps ���Ű� ����
unsigned char imu_buffer[MAX_BUFFER_SIZE] = "";				// imu ���Ű� ����
static char gps_buffer_flag;								// gps ���� �Ϸ� �÷���
static char imu_buffer_flag;								// imu ���� �Ϸ� �÷���
static char gps_load_flag;									// gps ���� �÷���
static char imu_load_flag;									// imu ���� �÷���
static unsigned char gps_buffer_cnt;						// gps ���� �ε���
static unsigned char imu_buffer_cnt;						// imu ���� �ε���

int dnleh_eh;	//����_��
long dnleh_qns;


int rudeh_eh;	//�浵_��
long rudeh_qns;

int Cansat_start_flag = 0;	//***ĵ���� ���� �÷���***

char Station_char_lati[7] = "";		//�������� ���� �޴� �� ->float ������ ��ȯ���� �������� ��ȯ��Ŵ
char Station_char_long[7] = "";

float Cansat_lati_min;	//***ĵ������ ����_��***
float Cansat_long_min;	//***ĵ������ �浵_��***
float Station_lati_min = 36.39929;	//***������ ����_�� ��ǥ - �ҽ��ڵ�� �Է��Ұ�***X
float Station_long_min = 17.30197;	//***������ �浵_�� ��ǥ - �ҽ��ڵ�� �Է��Ұ�***X
int Station_altitude = 10;	//***������ �� - �ҽ��ڵ�� �Է��Ұ�***X

long latitude;
long longitude;
int altitude = 300;	//��
/**/	

int Yaw_flag = 0;
int Pitch_flag = 0;	//PC ���ͷ�Ʈ�� ���� Yaw, Pitch �÷���

int Camera_flag = 0;	//Camera_flag �� 0�� ���� Camera off | 1�� ���� Camera on
int o_flag = 1;	// o_�÷��װ� 0���� ����.

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



int value[3] = {0};	//imu passing �� ����
char data_uart0;
char data_uart1;
char data_uart2;
char data_uart3;

int connect_flag = FALSE;

int i=0;	//s ���� ���� ī��Ʈ ����

void port_init(void)
{
	DDRD &= 0xFE;
	EIMSK = 0x01;	//INT0 ���
	EICRA = 0b00000010;	// �ϰ� ����(���� ����) ���
}
 
static int putchar1(char c)	//ī�޶� ��ɾ� ����
{
	while(!(UCSR0A & 0x20));
		UDR0 = c;
	return 0;
}

static int Putchar(char c, FILE *stream)//FILE ������, �۽�, avr->��ǻ��
{
	if(c == '\n')
		putchar('\r');

 while(!(UCSR1A & 0x20)); // UDRE, data register empty        
   UDR1 = c;
 return 0;
}
 
static int Getchar(FILE *stream)//����, ��ǻ��->avr
{
 while(!(UCSR1A & 0x80));
 return UDR1;
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

void imu(){			//IMU Command (Get data)
 //printf("\r\nIMU  ");
 UDR2=0x73; _delay_ms(1);
 UDR2=0x6e; _delay_ms(1);
 UDR2=0x70; _delay_ms(1);
 UDR2=0x82; _delay_ms(1);
 UDR2=0x01; _delay_ms(1);
 UDR2=0x00; _delay_ms(1);
 UDR2=0x02; _delay_ms(1);
 UDR2=0xD3;

_delay_ms(100);
 UDR2=0x73; _delay_ms(1);
 UDR2=0x6e; _delay_ms(1);
 UDR2=0x70; _delay_ms(1);
 UDR2=0xA0; _delay_ms(1);
 UDR2=0x00; _delay_ms(1);
 UDR2=0x01; _delay_ms(1);
 UDR2=0xF1; _delay_ms(1);
}
 
void uart0_init(void) //Camera
{
 UCSR0B = 0x00; //disable while setting baud rate
 UCSR0A = 0x00;
 UCSR0C = 0x06;
 UBRR0L = 0x19; //set baud rate 38400
 UBRR0H = 0x00; 
 UCSR0B = 0b10011000; //�ۼ���
}

void uart1_init(void) // BlueTooth
{
 UCSR1B = 0x00; //disable while setting baud rate
 UCSR1A = 0x00;
 UCSR1C = 0x06;
 UBRR1L = 0x08; //set baud rate 115200
 //UBRR1L = 0x19; //set baud rate 38400
 //UBRR1L = 0x67; //set baud rate 9600
 UBRR1H = 0x00; 
 UCSR1B = 0b10011000; //�ۼ���
}

void uart2_init(void) //IMU
{
 UCSR2B = 0x00; //disable while setting baud rate
 UCSR2A = 0x00;
 UCSR2C = 0x06;
 UBRR2L = 0x08; //set baud rate 115200
 UBRR2H = 0x00; 
 UCSR2B = 0b10011000; //�ۼ���
}

void uart3_init(void) //GPS
{
 UCSR3B = 0x00; //disable while setting baud rate
 UCSR3A = 0x00;
 UCSR3C = 0x06;
 UBRR3L = 0x67;	  //set baud rate 9600
 UBRR3H = 0x00; 
 UCSR3B = 0b10011000; //�ۼ���
}

void init_camera()
{
	reset();	_delay_ms(10);
	resize();	_delay_ms(10);
	zip();		_delay_ms(10);
	set_rate();	_delay_ms(10);
	UBRR0L = 0x08; _delay_ms(100);	//Camera baud rate 115200
}

void init_devices(void)
{
  cli();						 //disable all interrupts
  port_init();
  uart0_init();  
  uart1_init();
  uart2_init();
  uart3_init();
  fdevopen(Putchar, Getchar);	//file stream 0
  sei();  
  init_camera();

	/* ���Ϳ� �������� ���� - 0725�߰� YAW */
  	
	
	DDRB = 0x00;	//B��Ʈ 4 - YAW , 6 - PITCH ��¼��� -> s�� ������ ��ũ�� ������ ����
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
	case IMU :
		for(i=0; i<MAX_BUFFER_SIZE; i++)
			imu_buffer[i] = 0x00;
		break;
	case GPS:
		for(i=0; i<MAX_BUFFER_SIZE; i++)
			gps_buffer[i] = 0x00;
		break;
	case ECT:
		gps_buffer_flag = 0;
		imu_buffer_flag = 0;
		gps_load_flag = 0;
		imu_load_flag = 0;
		gps_buffer_cnt = 0;
		imu_buffer_cnt = 0;
		break;
	}
}

void passing_imu(void){
	value[0] = (int)imu_buffer[7] << 8;
	value[0] |= (int)imu_buffer[8];
	value[1] = (int)imu_buffer[9] << 8;
	value[1] |= (int)imu_buffer[10];
	value[2] = (int)imu_buffer[11] << 8;
	value[2] |= (int)imu_buffer[12];
	imu_buffer_flag = 0;
}


void passing_gps(void){
	
	char buffer[6] = "";
	int i = 0;
	altitude = 0;

	dnleh_eh =   (gps_buffer[17]-'0')*10
				+(gps_buffer[18]-'0')*1;

	dnleh_qns =	 (gps_buffer[19]-'0')*1000000
				+(gps_buffer[20]-'0')*100000
				+(gps_buffer[22]-'0')*10000
				+(gps_buffer[23]-'0')*1000
				+(gps_buffer[24]-'0')*100
				+(gps_buffer[25]-'0')*10
				+(gps_buffer[26]-'0')*1;	//������

	Cansat_lati_min = (float) (dnleh_qns) / (float)10000.0;	//����
	
	while( *(ALTI+i) != '.' && *(ALTI+i) != '\0')
	{
		//buffer[i] = *(ALTI + i);
		//i++;

		altitude *= 10;
		altitude += *(ALTI+i) - '0';
		i++;
	}
	//buffer[i] = '\0';
	//altitude = atoi(buffer);
	
	rudeh_eh =   (gps_buffer[30]-'0')*100
				+(gps_buffer[31]-'0')*10
				+(gps_buffer[32]-'0')*1;

	rudeh_qns =	 (gps_buffer[33]-'0')*1000000
				+(gps_buffer[34]-'0')*100000
				+(gps_buffer[36]-'0')*10000
				+(gps_buffer[37]-'0')*1000
				+(gps_buffer[38]-'0')*100
				+(gps_buffer[39]-'0')*10
				+(gps_buffer[40]-'0')*1;	//������
	
	Cansat_long_min = (float) (rudeh_qns) / (float)10000.0;	//����

	//printf("lati : %x | longi : %x | alti : %x\n",dnleh_qns,rudeh_qns, altitude);
	gps_buffer_flag = 0;
}


ISR(TIMER0_OVF_vect)//Ÿ�̸� �����÷� ���ͷ�Ʈ	//
{
	PORTB = PORTB ^ 0x50;	//��Ʈ B  0x00
}

ISR(TIMER0_COMPA_vect)//�� ���ͷ�Ʈ
{
	PORTB |= 0x10;	//��ƮB YAW Motor 0x10 ����
}

ISR(TIMER0_COMPB_vect)
{
	PORTB |= 0x40;	//��Ʈ B PITCH Motor 0x40 ����
}

int main(void)
{
	init_devices();
	init_buffer(GPS);
	init_buffer(IMU);
	init_buffer(ECT);
	int loop_count=0;	//����ī��Ʈ������ ������ ��� ���Ҵ��� �˷��ش�.

	printf("Connect with 3AMJUNG CANSAT\n");
	imu();

	while(Cansat_start_flag == 0)	{	//IDLE MODE
		_delay_ms(100);
	}
	
	//Ÿ�̸� ī��Ʈ �������� ����
	TIMSK0 = 0x07;	//�����÷�, ��º� ���ͷ�Ʈ A : YAW , ��º� ���ͷ�Ʈ B : PITCH
	TCCR0A = 0b10000011;//FastPWM, 0Cn��� ���,
	TCCR0B = 0b00000100; //���ֺ� 256
	OCR0A = 105;		//��º� ���ͷ�Ʈ A(YAW) : 1053 ���� ĵ�������� ����
	OCR0B = 160;		//��º� ���ͷ�Ʈ B(PITCH) : 215 ���� PITCH ���� ����
	DDRB = 0x50;	//���Ϳ� ���� ������ ����

	UCSR2B = 0x00;
	
	while(1)
	{		
		
			if(gps_buffer_flag == 1){	//GPS
				SendToStation(GPS);		//�� ���ο� ���� �б��Ұ�
			}
			
			if(Camera_flag == 0 && loop_count == 30000 && o_flag == 1)
				UCSR2B = 0x98;
			

			if(imu_buffer_flag == 1 && loop_count >= 30000)	{	//IMU ������ �ʰԹޱ�
				passing_gps();
				passing_imu();
				
				Rotate_Yaw();
				Rotate_Pitch();	//�� ���ο� ���� �б��Ұ�
								
				SendToStation(IMU);	
				loop_count = 0;
			}

			loop_count++;
			
				//�� ���ο� ���� �Ľ�
				
				// ���� ȸ�� ���� ���
				// ���� ȸ��

		if(idle_cnt >= 3)	{
			_delay_ms(45);
			shot();

		}
	
		if(shot_cnt == 5)
			call_size();

		if(call_size_cnt == 9)	{
		   //printf("#%x#%x\r\n", img_buffer[7], img_buffer[8]);
			call_size_cnt = 0;
			_delay_ms(50);
			call_img();
		}
		
	}	//while(1);
	
	return 0;
}

ISR(INT0_vect)
{
	//printf("Trigger0 \n");
	
	if(connect_flag == 0 && EICRA | 0x02)
	{
		connect_flag = TRUE;
		EICRA &= 0xFC;
		EICRA |= 0x03;
	}
	else
	{
		connect_flag = FALSE;
		EICRA &= 0xFC;
		EICRA |= 0x02;
	}
	
}


ISR(USART3_RX_vect)
{
	char uart3_data = UDR3;
	if(uart3_data=='$' && gps_buffer_flag == 0)
		gps_load_flag = 1;

	if(gps_load_flag == 1)	{
		gps_buffer[gps_buffer_cnt] = uart3_data;
		gps_buffer_cnt++;
		
		if(gps_buffer_cnt == 5 && uart3_data != 'G') {		//���� ī��Ʈ�� 5�϶�,G�� �ƴϸ�
				gps_buffer_cnt = 0;	//����ī��Ʈ, �÷��� 0 �ʱ�ȭ
				gps_load_flag = 0;
		}
		
		if(uart3_data == '\n')	{	//���۰� ���๮���϶�
			gps_buffer_flag = 1;	//�÷��� 1
			gps_load_flag = 0;		//�ε� 0
			gps_buffer_cnt = 0;		//����ī��Ʈ 0
		}
	}
}


ISR(USART2_RX_vect) //IMU���
{  
	
	data_uart2 = UDR2;
	if(data_uart2=='s' && imu_buffer_flag ==0)
		imu_load_flag = 1;

	if(imu_load_flag == 1)	{
		imu_buffer[imu_buffer_cnt] = data_uart2;
		imu_buffer_cnt++;
		
		if(imu_buffer_cnt == 4 && data_uart2 != 0xb7) {		//���� ī��Ʈ�� 4�϶�,b7�� �ƴϸ�
				imu_buffer_cnt = 0;	//����ī��Ʈ, �÷��� 0 �ʱ�ȭ
				imu_load_flag = 0;
				return;
		}
		
		if(imu_buffer_cnt == 15)	{	//���۰� ���๮���϶�
			imu_buffer_flag = 1;	//�÷��� 1
			imu_load_flag = 0;		//�ε� 0
			imu_buffer_cnt = 0;		//����ī��Ʈ 0
		}
	}

}


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
			Camera_flag = 0;
			printf("\n");
			_delay_ms(100);
			init_buffer(CAMERA);
			init_buffer(IMU);
			init_buffer(GPS);
			init_buffer(ECT);
		}
		else
			img_data_cnt = 0;
	}

	if(call_img_flag == 1 && img_data_start_flag == 1)
		UDR1 = uart0_data;
}


void printf_buffer()	{
	int i;
	printf("\n");
	for(i=0; i<9; i++)
		printf("%x ", img_buffer[i]);
}


ISR(USART1_RX_vect)	//Computer
{
	char ch = UDR1;

	if(Yaw_flag == 1)	{
		OCR0A = ch;
		Pitch_flag = 1;
		Yaw_flag = 0;
	}
	else if(Pitch_flag == 1)	{
		OCR0B = ch;
		Pitch_flag = 0;
	}

	switch(ch)
	{
		case 'c':
			UCSR3B = 0x00; //_delay_ms(5);
			UCSR2B = 0x00;
			Camera_flag = 1;	//Camera_flag On ���� Set
			o_flag = 0;
			imu_buffer_flag = 0; //imu, gps buffer flag �� 0���� �������ν� �߰��� �����Ͱ� ������ ����.
			gps_buffer_flag = 0;	
			idle();
			_delay_ms(100);
			break;
		case 's':
			Cansat_start_flag = 1;	//call_img�� ���� Start_flag�� 1�� �������
			break;
		case '#':
			Yaw_flag = 1;
			break;
		case 'd'://
			printf("idle cnt : %d\n", idle_cnt);
			printf("shot_cnt : %d\n", shot_cnt);
			printf("call_size_cnt : %d\n", call_size_cnt);
			printf("start_flag : %d\n", Cansat_start_flag);
			break;
		case 'o':
			load_buffer(GPS);
			load_buffer(IMU);
			o_flag = 1;
			break;
		case 'i':	//imu load buffer
			load_buffer(IMU);
			break;	
		case 'g':	//gps load buffer
			load_buffer(GPS);
			break;
		case 'r':
			asm("jmp 0");
			break;
		case 'f':
			UCSR3B = 0x00; //_delay_ms(5);
			UCSR2B = 0x00;
			o_flag = 0;
			break;			
	}
}


void load_buffer(int select)	//�۽� ���ͷ�Ʈ�� ����ϴ� �Լ�
{
	switch(select)
	{
		case GPS:
			UCSR3B=0x98; _delay_ms(2);
			break;
		case IMU:
			UCSR2B=0x98; _delay_ms(2);
			break;
	}
}


void SendToStation(int select)
{
	int i;

	switch(select)
	{
		case GPS:
			printf("%s\n", gps_buffer);
			init_buffer(GPS);
			gps_buffer_flag = 0;

			// UCSR3B = 0x00;
			break;
		case IMU:

			for(i=0; i<15; i++)	{	//15�� ���� -> IMU DATA Length
				if(i==3)
					putchar('\n');	//snp ���� ���๮�ڸ� �������ν� �������� ���� �� �� �ִ�.
				putchar(imu_buffer[i]);
			}	printf("\n");


			init_buffer(IMU);
			imu_buffer_flag = 0;
			imu_buffer_cnt = 0;

			break;
	}
}

void Rotate_Yaw(void)
{
	if(value[0] > -180 && value[0] < 0)	{	//���� ���̳ʽ��� ��쿡�� 
		value[0] = (360 + value[0]) / 3.22;	//�ٸ� ������� ���ؼ� value[0] �� ���� �ٲپ��ش�.
	}
	else	{
		value[0] = value[0] / 3.22;	// ���ϵ���
	}

	double gps_degree = 0;	
	double sum_degree = 0;	//���� �����ϴ� ���Ǻ���
	
	printf("min %x %x\n",Cansat_lati_min, Cansat_long_min);
	
	gps_degree = (atan((double) (Cansat_lati_min - Station_lati_min) / (double) (Cansat_long_min - Station_long_min) ) * 180 / 3.141592 ) / 3.22;
	//atan�� 45 �̶�� �����Ͽ����� OCR���� 119�� �Ǿ�� �ϹǷ� 14�� ���� ����� ���� 3.22�� �������־� value ���� �����Ѵ�. 
	//�浵�� ���̰� �ظ��̹Ƿ� ������ ���� / �浵�� ���̷� ���Ѵ�

	
	if(Cansat_lati_min < Station_lati_min && Cansat_long_min < Station_long_min)	{	//ĵ������ ������ �� - 1��и� : �÷���
		sum_degree = 105 + value[0] + 82 + gps_degree;
	}
	else if(Cansat_lati_min < Station_lati_min && Cansat_long_min > Station_long_min)	{ //���� �� - 2��и� : ���̳ʽ�
		sum_degree = 105 + value[0] + 28 + gps_degree;
	}
	else if(Cansat_lati_min > Station_lati_min && Cansat_long_min > Station_long_min)	{	//���� �Ʒ� - 3��и� : �÷���
		sum_degree = 105 + value[0] + 28 + gps_degree;
	}
	else if(Cansat_lati_min > Station_lati_min && Cansat_long_min < Station_long_min)	{	//������ �Ʒ� - 4��и� : ���̳ʽ�
		sum_degree = 105 + value[0] + 82 + gps_degree;
	}
	
		if(sum_degree > 215)	{	//���� 215�� ���� ��� �ڷ� ȸ��
			sum_degree -= 215;
			OCR0A = 105 + sum_degree;
		}
		else if(sum_degree < 105)	{	//105 �̸��� ��쿡�� ������ ȸ��
			sum_degree = 105 - sum_degree;
			OCR0A = 215 - sum_degree;
		}
		else	{
			OCR0A = sum_degree;	//�ƴ� ��쿡�� �� ����
		}
}


void Rotate_Pitch(void)
{
	//������ ���� ���� ���� �Ϸ�		

	double DISTANCE = sqrt( pow((Cansat_lati_min - Station_lati_min)*1850 , 2) + pow((Cansat_long_min - Station_long_min)*1480, 2) );
	//�Ÿ��� �� ���� �̿��Ͽ� ����, ���ϱ⸦ �Ͽ� M�� ȯ���� ���� 1�� : �� 1.85km // �浵 1�� : �� 1.48km, 
	double HEIGHT =  altitude - Station_altitude;	//��
	double pitch_degree = ( atan( (double) HEIGHT / (double) DISTANCE ) * 180 / 3.141592) / 1.8;
	int result_pitch = 0;
	//atan ������ 0 ~ 90 ���� �����Ƿ� OCR���� ������ 55�� �����ϱ� ���ؼ� 1.65��� ���� �������ش�.

	//OCR0B =  160 - (int)(value[1] / 1.7) - (int)pitch_degree;
	//pitch_degree�� +�� ���־�� �Ǵ� �� ������ -�� �ϴ°��� �Ǿ��ϰ� ����
	// value[1] / 1.7 �� Pitch ������ // pitch_degree �� atan �� ���
	result_pitch = 160 - (int)(value[1] / 1.7) - (int)pitch_degree;
	
	if(result_pitch > 215)
		OCR0B = 215;
	else if(result_pitch < 160)
		OCR0B = 160;
	else
		OCR0B = result_pitch;
}
