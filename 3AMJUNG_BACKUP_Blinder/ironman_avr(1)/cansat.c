#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>

#define GPS 	1
#define IMU 	2
#define CAMERA 	3
#define SENSOR 	4
#define ECT 	5
#define	IDLE 	1
#define SHOT 	2
#define CALL_SIZE	3
#define ADC_VREF_TYPE 0x40

static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);

void load_buffer(int );
void SendToStation(int);
void loaddata(int );
void init_buffer(int);

unsigned char gps_buffer[50];
unsigned char imu_buffer[50];
unsigned char camera_buffer[7];
static char gps_buffer_flag;
static char imu_buffer_flag;
static char gps_load_flag;
static char imu_load_flag;
static unsigned char gps_buffer_cnt;
static unsigned char imu_buffer_cnt;
static char com_channel;
static char img_buffer[9];
static int img_cnt;
static int call_size_flag;
static int call_img_flag;
static int img_data_start_flag;
static int img_data_cnt;
static int idle_cnt;
static int shot_cnt;
static int call_size_cnt;
static int command;
int sensor_RH;
int sensor_SMOKE;

void port_init(void)
{
	ADMUX=ADC_VREF_TYPE & 0xff;
 	ADCSRA=0x84;
	DDRF=0x08;
}

unsigned int read_adc(unsigned char adc_input)     //AD 컨버팅 함수
{
	ADMUX=adc_input | (ADC_VREF_TYPE & 0xff);     
	_delay_us(10);
	ADCSRA|=0x40;                        //AVCC PIN으로 ADC 기준전압 설정 
	while ((ADCSRA & 0x10)==0);          //ADC 폴링상태로 대기 
	ADCSRA|=0x10;                        //ADCSRA 초기화 
	return ADCW;                         //ADCL , ADCH 각각 8비트를 합산하여 16비트로 사용 (헤더파일 참조)
}

void init_buffer(int select)	///
{
	int i;
	switch(select)
	{
		case GPS:
			for(i=0;i<50;i++)
				gps_buffer[i]=0;
			break;
		case IMU:
			for(i=0;i<50;i++)
				imu_buffer[i]=0;
			break;
		case CAMERA:
			for(i=0;i>9;i++)
				img_buffer[i]=0;
				break;
		case ECT:
			gps_buffer_flag=0;
			imu_buffer_flag=0;
			gps_load_flag=0;
			imu_load_flag=0;
			gps_buffer_cnt=0;
			imu_buffer_cnt=0;
			com_channel=0;
			break;
	}
}

static int putchar1(char c)	//카메라에 명령어 전송
{
	while(!(UCSR1A & 0x20));
	UDR1 = c;
	return 0;
}

static int Putchar(char c, FILE *stream)//FILE 사용안함, 송신, avr->컴퓨터
{
	if(c == '\n')
		Putchar('\r', 0);
	
	while(!(UCSR3A & 0x20)); // UDRE, data register empty        
  	UDR3 = c;
	return 0;
      
}

static int Getchar(FILE *stream)//수신, 컴퓨터->avr
{
	while(!(UCSR3A & 0x80));
	return UDR3;
}

void reset(){
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x26);
	putchar1(0x00);
	img_cnt=0;
}

void resize(){
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x31);
	putchar1(0x05);
	putchar1(0x04);
	putchar1(0x01);
	putchar1(0x00);
	putchar1(0x19);
	putchar1(0x11);
	img_cnt=0;
}

void set_rate(){
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x24);
	putchar1(0x03);
	putchar1(0x01);
	putchar1(0x0D);
	putchar1(0xA6);
	img_cnt=0;
}

void idle(){
	init_buffer(CAMERA);
	command=IDLE;
	idle_cnt=0;
	img_cnt=0;
	call_img_flag=0;
	img_data_start_flag=0;
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x36);
	putchar1(0x01);
	putchar1(0x03);
}

void shot(){
	command=SHOT;
	idle_cnt=0;
	shot_cnt=0;
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x36);
	putchar1(0x01);
	putchar1(0x00);
	img_cnt=0;
}

void call_size(){
	command=CALL_SIZE;
	shot_cnt=0;
	call_size_cnt=0;
	call_size_flag=1;
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x34);
	putchar1(0x01);
	putchar1(0x00);
	img_cnt=0;
}

void call_img(){
	command=0;
	call_size_flag=0;
	call_img_flag=1;
	img_data_start_flag=0;
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x32);
	putchar1(0x0C);
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
	img_cnt=0;
}

void zip(){
	putchar1(0x56);
	putchar1(0x00);
	putchar1(0x31);
	putchar1(0x05);
	putchar1(0x01);
	putchar1(0x01);
	putchar1(0x12);
	putchar1(0x04);
	putchar1(0xff);
	img_cnt=0;
}

void uart0_init(void)	//GPS와 통신
{
	UCSR0B = 0x00; //disable while setting baud rate
	UCSR0A = 0x00;
	UCSR0C = 0x06;
	UBRR0L = 0x67; //set baud rate 9,600
	UBRR0H = 0x00; 
	UCSR0B = 0x00;	//처음에서는 닫고
}

void uart1_init(void)	//camera 값 수신
{
	UCSR1B = 0x00; //disable while setting baud rate
	UCSR1A = 0x00;
	UCSR1C = 0x06;
	UBRR1L = 0x19; //set baud rate 38400
	UBRR1H = 0x00; 
	UCSR1B = 0b10011000; //송수신
}

void uart2_init(void)	//IMU와 통신
{
	UCSR2B = 0x00; //disable while setting baud rate
	UCSR2A = 0x00;
	UCSR2C = 0x06;
	UBRR2L = 0x67; //set baud rate 9,600
	UBRR2H = 0x00; 
	UCSR2B = 0x00;	//송신 인터럽트 개방
}

void uart3_init(void)	//PC와 통신
{
	UCSR3B = 0x00; //disable while setting baud rate
	UCSR3A = 0x00;
	UCSR3C = 0x06;
	UBRR3L = 0x08; //set baud rate 115,200
	UBRR3H = 0x00; 
	UCSR3B = 0x98;	//송수신 인터럽트 개방
}


void init_camera()
{
	reset(); _delay_ms(1000);
	resize();	_delay_ms(10);
	zip();	_delay_ms(10);
	set_rate();	_delay_ms(10);
	UBRR1L = 0x08; _delay_ms(100);
}

void init_devices(void)
{
	 cli(); //disable all interrupts
	 port_init();
	 uart0_init();
	 uart1_init(); 
	 uart2_init();
	 uart3_init();
	 fdevopen(Putchar, Getchar);//file stream 0
	 sei(); 
	 init_camera();
}

int main(void)
{
	init_devices();
	init_buffer(GPS);
	init_buffer(IMU);
	init_buffer(ECT);
	printf("\r\n\r\nStart KITSAT Flyprogram!\r\n");
	while(1)
	{	
		if(gps_buffer_flag==1)	//gps의 버퍼가 찼으면
			SendToStation(GPS);	//GPS의 값을 지상국으로 전송

		if(imu_buffer_flag==1)
			SendToStation(IMU);	//IMU의 ""
		
		if(idle_cnt==5){
			_delay_ms(45);
			shot();

		}

		if(shot_cnt==5)	{
			call_size();
	
		}

		
		if(call_size_cnt==9){
			printf("#%x#%x\r\n",img_buffer[7],img_buffer[8]);
			call_size_cnt=0;
		}
	}
	return 0;
}

ISR(USART0_RX_vect)	//gps의 수신완료 인터럽트
{
	char buf=UDR0;
	if(buf=='$' && gps_buffer_flag==0)
		gps_load_flag=1;
		
	if(gps_load_flag==1){
		gps_buffer[gps_buffer_cnt]=buf;
		gps_buffer_cnt++;
		if(gps_buffer_cnt==5)	//버퍼 카운트가 5일때
			if(buf!='G'){	//G가 아니면
				gps_buffer_cnt=0;	//버퍼카운트, 플래그 0 초기화
				gps_load_flag=0;
			}
		if(buf=='\n'){	//버퍼가 개행문자일때
			gps_buffer_flag=1;	//버퍼플래그 1
			gps_load_flag=0;	//로드 0
			gps_buffer_cnt=0;	//버퍼카운트 0
			UCSR0B=0x00;
		}
	}
}

ISR(USART2_RX_vect)	//imu의 수신완료 인터럽트
{	
	char ch = UDR2;
	if(ch=='*' && imu_buffer_flag==0)
		imu_load_flag=1;
	if(imu_load_flag==1){
		imu_buffer[imu_buffer_cnt]=ch;
		imu_buffer_cnt++;
		if(ch=='\n'){
			imu_buffer_flag=1;
			imu_load_flag=0;
			imu_buffer_cnt=0;
			UCSR2B=0x00;
		}
	}
}

ISR(USART1_RX_vect)	//Camera의 수신완료 인터럽트
{	
	char ch=UDR1;
	switch(command)
	{
		case IDLE :
			idle_cnt++;
			break;
		case SHOT :
			shot_cnt++;
			break;
		case CALL_SIZE:
			call_size_cnt++;
			break;
	}

	if(call_size_flag==1){
		img_buffer[img_cnt]=ch;
		img_cnt++;
	}
	if(ch==0xff)
		img_data_start_flag=1;

	if(ch==0xd9)
		img_data_cnt=1;
	else{
		if(ch==0x76 && img_data_cnt==1){
			img_data_start_flag=0;
			img_data_cnt=0;
			printf("\r\n");
			}
		else
			img_data_cnt=0;
	}

	if(call_img_flag==1 && img_data_start_flag==1)
		UDR3=ch;
}

void printf_buffer(){
	int i;
	printf("\r\n");
	for(i=0;i<9;i++)
		printf("%x ",img_buffer[i]);
}

//com
ISR(USART3_RX_vect)	//
{
	char ch = UDR3;
	
	switch(ch)
	{
		case 'g':
			load_buffer(GPS);
			break;
		case 'i':
			load_buffer(IMU);
			break;
		case 'c':
			idle();
			break;
		case 's':
			call_img();
			break;
		case 'r':
			load_buffer(SENSOR);
			SendToStation(SENSOR);
			break;
	}
}

//com
void load_buffer(int select)
{
	switch(select)
	{
		case GPS:
			UCSR0B=0x90;
			break;
		case IMU:
			UCSR2B=0x90;
			break;
		case SENSOR:
			sensor_RH=read_adc(0);
			sensor_SMOKE=read_adc(1);
			break;
	}
}


void SendToStation(int select)
{	
	
	switch(select)
	{
		case GPS:	//gps
			printf("%s",gps_buffer);
			init_buffer(GPS);
			gps_buffer_flag=0;
			break;
		case IMU:	//imu
			printf("%s",imu_buffer);
			init_buffer(IMU);
			imu_buffer_flag=0;
			break;
		case SENSOR:
			printf("@%d, %d\r\n",sensor_RH,sensor_SMOKE);
			break;
	}

}
