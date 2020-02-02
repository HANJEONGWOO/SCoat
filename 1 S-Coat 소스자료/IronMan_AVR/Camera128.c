
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <stdio.h>
#include <string.h>
 
#define RESET 1
#define OP_SIZE 2
#define STOP 3
#define SHOT 4
#define CALL_SIZE 5
#define CALL_IMG 6
 
static int Putchar(char c, FILE *stream);
static int Getchar(FILE *stream);
 
unsigned buff[9];
int cnt;
int callflag;
 
void port_init(void)
{}
 
static int Putchar(char c, FILE *stream)//FILE 사용안함, 송신, avr->컴퓨터
{
 if(c == '\n')
  Putchar('\r', 0); 

 while(!(UCSR1A & 0x20)); // UDRE, data register empty        
   UDR1 = c;
 return 0;
      
}
 
static int Getchar(FILE *stream)//수신, 컴퓨터->avr
{
 while(!(UCSR1A & 0x80));
 return UDR1;
}

void reset(){
 printf("\r\nRESET  ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x26; _delay_ms(1);
 UDR0=0x00;
}
 
void resize(){
 printf("\r\nRESIZE  ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x31; _delay_ms(1);
 UDR0=0x05; _delay_ms(1);
 UDR0=0x04; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x19; _delay_ms(1);
 UDR0=0x11;
}
 
void set_rate(){
 printf("\r\nSET_RATE ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x24; _delay_ms(1);
 UDR0=0x03; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x0D; _delay_ms(1);
 UDR0=0xA6;
}
 
void idle(){
 printf("\r\nIDLE  ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x36; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x03;
}
 
void shot(){
 printf("\r\nSHOT  ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x36; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x00;
}
 
void call_size(){
 printf("\r\nCALL_SIZE ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x34; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x00;
}
 
void call_img(){
 printf("\r\nCALL_IMG ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x32; _delay_ms(1);
 UDR0=0x0c; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x0A; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=buff[7]; _delay_ms(1);
 UDR0=buff[8]; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x0A;
}
 
void zip(){
 printf("\r\nZIP  ");
 UDR0=0x56; _delay_ms(1);
 UDR0=0x00; _delay_ms(1);
 UDR0=0x31; _delay_ms(1);
 UDR0=0x05; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x01; _delay_ms(1);
 UDR0=0x12; _delay_ms(1);
 UDR0=0x04; _delay_ms(1);
 UDR0=0x36;
}
 
void uart0_init(void) //camera 값 수신
{
 UCSR0B = 0x00; //disable while setting baud rate
 UCSR0A = 0x00;
 UCSR0C = 0x06;
 UBRR0L = 0x19; //set baud rate 38400
 UBRR0H = 0x00; 
 UCSR0B = 0b10011000; //수신만
}
void uart1_init(void) //PC와 통신
{
 UCSR1B = 0x00; //disable while setting baud rate
 UCSR1A = 0x00;
 UCSR1C = 0x06;
 UBRR1L = 0x08; //set baud rate 115200
 UBRR1H = 0x00; 
 UCSR1B = 0b10011000; //송신 인터럽트 개방
}
 
void init_camera()
{
 reset(); _delay_ms(1000);
 resize(); _delay_ms(50);
 zip(); _delay_ms(50);
 set_rate(); _delay_ms(50);		//
 UBRR0L = 0x08; printf("\r\n chang boudrate"); _delay_ms(50);
 idle();
}
 
void init_devices(void)
{
  cli(); //disable all interrupts
  port_init();
  uart0_init();  
  uart1_init();
  fdevopen(Putchar, Getchar);//file stream 0
  sei(); 
}
 

void shot_camera()
{_delay_ms(50);
 shot();  
// call_size(); _delay_ms(50);
// call_img(); _delay_ms(100);
 idle(); 
} 
 
int main(void)
{
 init_devices();
 printf("\r\n\r\nConnect with 2560\r\n");
 init_camera();
 while(1)
 {
 }
 
 return 0;
}
 
ISR(USART0_RX_vect)
{  
 if(callflag==1)
 {
 	buff[cnt]=UDR0;
	cnt++;
 }
 else
 	UDR1=UDR0;
}
 
ISR(USART1_RX_vect)
{
 char ch=UDR1;
 if(ch=='l')
 {
 	callflag=0;
   shot_camera();
  }
 if(ch=='c')
  {
  cnt=0;
 	callflag=1;
  call_size();
  
  }
 if(ch=='s')
  {
 	callflag=0;
  shot(); 
  }
 if(ch=='i')
  {
 	callflag=0;
  idle();
  }
 if(ch=='k')
  {
 	callflag=0;
  call_img();
  }
 if(ch=='p')
  printf("size: %x %x %x %x %x %x %x %x %x ",buff[0],buff[1],buff[2],buff[3],buff[4],buff[5],buff[6],buff[7],buff[8]);
}


