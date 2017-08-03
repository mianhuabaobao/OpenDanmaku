    /*
    if ((access("/data/local/tmp/.handjoy/abc", F_OK)) == 0) {
        printf("aaaaaaaaaaaaaa!\n"); 
    } else {
        printf("bbbbbbbbbbbbb!\n"); 

        if (mkdir("/data/local/tmp/.handjoy/abc",   0777) == 0) {
            printf("ccccccccccccccc!\n"); 
        } else {
            printf("dddddddddddddd!\n"); 
        }
    }
    exit(0);
    */

    /*
    printf( "BO process, my process id is %d\n",getpid()); 

    //sleep(5);
    
    char * argv[]={"touchx","/data/local/tmp/.handjoy", "com.handjoy.touch.EchoServer", "0", NULL};
    //char * envp[]={"CLASSPATH=/data/local/tmp/.handjoy/htouch.jar", "ANDROID_DATA=/data/local/tmp/.handjoy", NULL};
    
    putenv("CLASSPATH=/data/local/tmp/.handjoy/htouch.jar");
    putenv("ANDROID_DATA=/data/local/tmp/.handjoy");

    //printenv(environ);
    

    //execve("/data/local/tmp/.handjoy/hjserver", argv, env);
    //execve("/data/local/tmp/.handjoy/htouch", argv, env);
    //execve("/system/bin/app_process", argv, env);
    
    execv("/system/bin/app_process", argv);
    //execv(HSERVER, argv);
    
    printf(">>>>>>>>>>>>>>>>>>>>>>>>> B EXIT: %s\n", strerror(errno));
    */

    /*
    int status = system("./hjserver");

    if(status < 0)
    {
        printf("cmd: ./hjserver\t error: %s", strerror(errno)); 
        return status;
    }

    if(WIFEXITED(status))
    {
        printf("normal termination, exit status = %d\n", WEXITSTATUS(status)); 
    }
    else if(WIFSIGNALED(status))
    {
        printf("abnormal termination,signal number =%d\n", WTERMSIG(status)); 
    }
    else if(WIFSTOPPED(status))
    {
        printf("process stopped, signal number =%d\n", WSTOPSIG(status));
    }
    */

    

    /*
    int port=7899; 
    int sin_len;  
    char message[256];  
  
    int socket_descriptor;  
    struct sockaddr_in sin;  
    printf("HELLO Waiting for data form sender \n");  
  
    bzero(&sin,sizeof(sin));  
    sin.sin_family=AF_INET;  
    sin.sin_addr.s_addr=htonl(INADDR_ANY);  
    sin.sin_port=htons(port);  
    sin_len=sizeof(sin);  
  
    socket_descriptor=socket(AF_INET,SOCK_DGRAM,0);  
    bind(socket_descriptor,(struct sockaddr *)&sin,sizeof(sin));  
  
    while(1)  
    {  
        recvfrom(socket_descriptor,message,sizeof(message),0,(struct sockaddr *)&sin,&sin_len);  
        printf("Response from server:%s\n",message);  
        if(strncmp(message,"stop",4) == 0)
        {  
  
            printf("Sender has told me to end the connection\n");  
            break;  
        }  
    }  
  
    close(socket_descriptor);  
    exit(0);  
  
    */