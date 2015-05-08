#include<stdlib.h>
#include <stdio.h>  
#include<string.h>
char* join3(char *s1, char *s2)  
{  
    char *result = malloc(strlen(s1)+strlen(s2)+2);
    if (result == NULL) exit (1);  
    strcpy(result, s1);  
    result[strlen(s1)]=' ';
    strcat(result, s2);  
    return result;  
}  
int main(int argc,char *argv[])
{
	char *a=join3(argv[1],argv[2]);
	char *b=join3(argv[3],argv[4]);
	a=join3(a,b);
	a=join3(a,argv[5]);
	a=join3("java com.tcp.TcpClient",a);
	system(a);
	return 0;
}
