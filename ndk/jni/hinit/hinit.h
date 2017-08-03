#include <stdio.h>  
#include <stdlib.h>
#include <string.h>  
#include <sys/socket.h>  
#include <netinet/in.h>  
#include <arpa/inet.h>  
#include <netdb.h>
#include <sys/wait.h> 
#include <errno.h> 
#include <unistd.h>
#include<sys/types.h>
#include<sys/stat.h>
#include <fcntl.h>
#include <sys/file.h>
#include <stdarg.h>
#include <signal.h>
#include <pthread.h>
#include <ctype.h>
#include <dirent.h>

#ifndef _HINIT_H_  
#define _HINIT_H_

#define RETURN_SUCCESS 0
#define RETURN_FAILURE -1
#define MAXLINE 1024
#define TRUE 1
#define FALSE 0

extern char **environ;

int hlog(char *fmt, ...);
int printenv(char** env);
int copy(const char *src, const char *dest);
int hfiles();
int hserver();
int htouch();
int hapk();
int shell(const char *cmd, char *buffer, int size);
int sh(const char *cmd);
int getapk(const char *pkg, char *path, int size);
int broadcast_status();
void kill_process(char *name);
void kill_processes();

#endif