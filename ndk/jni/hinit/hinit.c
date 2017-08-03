#include "hinit.h"

void test() {
    pthread_exit(NULL);
}

int *thread_broadcast_hinit(void *arg) {
    const char *cmd = "am broadcast -a com.handjoy.action.HINIT_STATUS --es status \"start\"";
    int times = 10;
    do {
        printf("broadcast_hinit: times = %d\n", times);
        sh(cmd);
        sleep(1);
    } while(--times > 0);
    return NULL;
}

void broadcast_hinit() {
    broadcast_status();

    pid_t pid;

    if ((pid = fork()) < 0) {
        hlog("error in fork(broadcast_hinit 1)!\n"); 
    } else if (pid == 0) {
        if ((pid = fork()) < 0) {
            hlog("error in fork(broadcast_hinit 2)!\n"); 
        } else if (pid == 0) {
            thread_broadcast_hinit(NULL);
        }     
         exit(EXIT_SUCCESS);
    }

    if (waitpid(pid, NULL, 0) != pid) {
        hlog("error in waitpid(broadcast_hinit)!\n"); 
    }

    /*
    pthread_t id;
    if(pthread_create(&id, NULL, (void *)thread_broadcast_hinit, NULL)) {
        hlog("error in broadcast_hinit(pthread_create)!\n");
    }
    */

    //pthread_join(&id, NULL);
}

int getapk(const char *pkg, char *path, int size) {
    sprintf(path, "pm path %s", pkg);

    if (shell(path, path, size)) {
        hlog("error in getapk!\n");
        return RETURN_FAILURE;
    }

    return RETURN_SUCCESS;
}

/*
    path = pm path app's package
    export CLASSPATH=path;app_process /system/bin package.classname
*/
int hapk() {
    char apk[MAXLINE];
    if (!getapk("com.handjoy.xiaoy", apk, sizeof(apk))) {
        char *path = strstr(apk, "/");

        char classppath[MAXLINE];
        sprintf(classppath,"CLASSPATH=%s", path);

        char * argv[]={"htouch", "/system/bin", "com.handjoy.touch.EchoServer", "0", NULL};

        putenv(classppath);

        execv("/system/bin/app_process", argv);

        hlog("hapk: error is %d, errinfo is %s\n", errno, strerror(errno));
    }
    return RETURN_FAILURE;
}

int hlog(char *fmt, ...) {
    char buff[MAXLINE];
    va_list args;
    va_start(args, fmt);
    int n = vsprintf(buff, fmt, args);
    va_end(args);

    printf("%s", buff); 
    FILE* fp = fopen("/data/local/tmp/.handjoy/htouch.log","a+");  
    fwrite(buff, 1, strlen(buff), fp);  
    fclose(fp); 
    return RETURN_SUCCESS;
}

int start_activity() {
    return sh("am start -n com.handjoy.xiaoy/com.handjoy.gamehouse.GameHouseBegin --ei source 10");
}

int broadcast_status() {
    return sh("am broadcast -a com.handjoy.action.HINIT_STATUS --es status \"start\"");
}

int sh(const char *cmd) {
    char buff[MAXLINE];
    
    if (shell(cmd, buff, sizeof(buff))) {
        hlog("sh(%s) error!\n", cmd);
        return RETURN_FAILURE;
    }

    return RETURN_SUCCESS;
}

int createfile(const char * filename) {
    int fd = open(filename, O_RDWR|O_CREAT);
    if (fd == -1) {
        hlog("error in createfile(open)!\n");
        return RETURN_FAILURE;
    }
    
    if (close(fd) == -1) {
        hlog("error in createfile(close)!\n");
    }

    return RETURN_SUCCESS;
}

int waitfile(const char * filename, int freq) {
    while ((access(filename, F_OK)) == -1) {
        //printf("waitfile %s\n", filename); 
        sleep(freq);
    }

    if (remove(filename)) {
        hlog("error in waitfile(remove)!\n"); 
    }

    return RETURN_SUCCESS;
}

int lockfile(int fp) {
    return flock(fp, LOCK_EX|LOCK_NB);
}

int unlockfile(int fp) {
    return flock(fp, LOCK_UN);
}

void lockprocess(const char * lock) {
    int fd = open(lock, O_RDWR|O_CREAT|O_EXCL);
    if (fd == -1) {
        hlog("error in lockprocess(open)!\n");
        exit(0);
    }
    
    if (close(fd) == -1) {
        hlog("error in lockprocess(close)!\n");
    }
}

void unlockprocess(const char * lock) {
    if (remove(lock)) {
        hlog("error in unlockprocess(remove)!\n"); 
    }
}

// adb shell "/data/local/tmp/.handjoy/hinit > /dev/null 2>&1 &"
int main(int argc, char** argv, char** env) {
    //test();
    const char *hpid = "/data/local/tmp/.handjoy/hpid";
    const char *hdaemon = "/data/local/tmp/.handjoy/hdaemon";

    signal(SIGHUP, SIG_IGN);

    lockprocess(hpid);

    kill_processes();

    pid_t pid = fork();

	if (pid < 0) {
		hlog("error in fork(1)!\n"); 
    } else if (pid == 0) {
        if ((pid = fork()) < 0) {
            hlog("error in fork(2)!\n"); 
        } else if (pid == 0) {
            if(daemon(0, 0) == -1) {
                hlog("error in daemon!\n");
                exit(EXIT_FAILURE);
            }
            //createfile(hdaemon);

            start_activity();

            broadcast_hinit();
            
            if (hfiles()) {
                hlog("error in hfiles!\n");
                exit(EXIT_FAILURE);
            }

            if ((pid = fork()) < 0) {
                hlog("error in fork(3)!\n"); 
            } else if (pid == 0) {
                hserver();
            }

            htouch();
        }

        exit(EXIT_SUCCESS);
	} 
    
    if (waitpid(pid, NULL, 0) != pid) {
        hlog("error in waitpid!\n"); 
    }

    //waitfile(hdaemon, 1);

    unlockprocess(hpid);

    return EXIT_SUCCESS;
}

int hfiles() {
    char * sources[]={"/data/data/com.handjoy.xiaoy/files/startservice", "/data/data/com.handjoy.xiaoy/files/touchservice.jar", "/data/data/com.handjoy.xiaoy/files/hjserver", "/data/data/com.handjoy.xiaoy/files/htouch.jar", "/data/data/com.handjoy.xiaoy/files/htouch", NULL};
    char * destinations[]={"/data/local/tmp/.handjoy/startservice", "/data/local/tmp/.handjoy/touchservice.jar", "/data/local/tmp/.handjoy/hserver", "/data/local/tmp/.handjoy/htouch.jar", "/data/local/tmp/.handjoy/htouch", NULL};
    
    char** src = sources;
    char** dest = destinations;

    while(*src != NULL) {
        if (copy(*src++, *dest)) {
            hlog("error in copy(%s)!\n", *--src);
            return RETURN_FAILURE;
        }

        if (chmod(*dest++, 0777)) {
            hlog("error in chmod(%s)!\n", *--dest);
            return RETURN_FAILURE;
        }
    }

    return RETURN_SUCCESS;
}

int hserver() {
    char * argv[]={"hserver",  NULL};

    execv("/data/local/tmp/.handjoy/hserver", argv);

    hlog("hserver: error is %d, errinfo is %s\n", errno, strerror(errno));

    return RETURN_FAILURE;
}

// exec app_process /data/local/tmp/.handjoy com.handjoy.touch.EchoServer 0 > /dev/null 2>&1 &
int htouch() {
    char * argv[]={"htouch", "/data/local/tmp/.handjoy", "com.handjoy.touchserver.TouchServer", "0", NULL};
    putenv("CLASSPATH=/data/local/tmp/.handjoy/touchservice.jar");

    //char * argv[]={"htouch", "/data/local/tmp/.handjoy", "com.handjoy.touch.EchoServer", "0", NULL};

    //putenv("CLASSPATH=/data/local/tmp/.handjoy/htouch.jar");
    putenv("ANDROID_DATA=/data/local/tmp/.handjoy");

    execv("/system/bin/app_process", argv);

    hlog("htouch: error is %d, errinfo is %s\n", errno, strerror(errno));

    return RETURN_FAILURE;
}

void kill_processes() {
    kill_process("oy.touchservice");
    
    //kill_process(".handjoy.touchr");
    kill_process("hserver");
}

void kill_process(char *processname) {
	int READ_BUF_SIZE = 512;
	DIR *dir_proc;
	struct dirent *next;
	long pid = 0;
	int killstatus;

    dir_proc = opendir("/proc");
    if (!dir_proc){
        printf("Open /proc failed.\n");
    }
    while((next = readdir(dir_proc)) != NULL) {
        FILE *status;
        char filename[READ_BUF_SIZE];
        char buffer[READ_BUF_SIZE];
        char name[READ_BUF_SIZE];

        if (strcmp(next->d_name, ".") == 0 || strcmp(next->d_name, "..") == 0) {
            continue;
        }

        if (!isdigit(*next->d_name)) {
            continue;
        }

        sprintf(filename, "/proc/%s/status", next->d_name);
        if (!(status = fopen(filename, "r")) ) {
            continue;
        }
        if (fgets(buffer, READ_BUF_SIZE-1, status) == NULL) {
            fclose(status);
            continue;
        }
        fclose(status);

        sscanf(buffer, "%*s %s", name);
        //printf("name=%s\n", name);
        if (strcmp(name, processname) == 0) {
            pid = strtol(next->d_name, NULL, 0);
            //printf("pid = %ld, proc=%s\n",pid, processname);
            killstatus=kill(pid, SIGKILL);
            if(killstatus == -1){
                hlog("kill %ld failed\n", pid);
            }
            wait(&killstatus);
            break;
        }
    }
}

int shell(const char *cmd, char *buffer, int size) {
    FILE *fp = popen(cmd, "r"); 
    if (!fp) {
        return RETURN_FAILURE;
    }

    memset(buffer, 0, size);

    int offset = 0;
    int len = 0;
    
    while (fgets(buffer, size, fp) != NULL)  {
        //printf("%s", buffer);
        len = strlen(buffer);
        if (buffer[len - 1] == '\n') {
            buffer[len - 1] = '\0'; 
        }
    }

    pclose(fp); 
    return RETURN_SUCCESS;
}

int copy(const char *src, const char *dest) {
    const int BUFFER_SIZE = 4096;

	FILE *fps, *fpd;
	int lens, lend;
	char buffer[BUFFER_SIZE];
    
	if ((fps = fopen(src, "r")) == NULL) {
		hlog("The file '%s' can not be opened! \n", src);
		return RETURN_FAILURE;
	}

	if ((fpd = fopen(dest, "w")) == NULL) {
		hlog("The file '%s' can not be opened! \n", dest);
		fclose(fps);
		return RETURN_FAILURE;
	}

	memset(buffer, 0, BUFFER_SIZE);
	while ((lens = fread(buffer, 1, BUFFER_SIZE, fps)) > 0) {
		if ((lend = fwrite(buffer, 1, lens, fpd)) != lens) {
			hlog("Write to file '%s' failed!\n", dest);
			fclose(fps);
			fclose(fpd);
			return RETURN_FAILURE;
		}
		memset(buffer, 0, BUFFER_SIZE);
	}

	fclose(fps);
	fclose(fpd);
	return RETURN_SUCCESS;
}

int printenv(char** env) {
    printf( "printenv begin --------------\n"); 
    char** oenv = env;
    while(*oenv != NULL) {
        printf("%s\n", *oenv++);
    }
    printf( "printenv   end --------------\n"); 
    return RETURN_SUCCESS;
}
