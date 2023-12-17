# Set compiler to use
CC=gcc
CFLAGS=-g -I. 
#CFLAGS=-g -I. -fpermissive
LDFLAGS=
DEBUG=0

all:: echoserv

echoserv: echoserv.o
	$(CC) -o $@ $^ $(CFLAGS) $(LDFLAGS)
	#$(CC) -o $@ $^ -std=c++11 $(CFLAGS) $(LDFLAGS)

%.o: %.c $(DEPS_CHAT)
	$(CC) -c -o $@ $< $(CFLAGS)

%.o: %.cpp $(DEPS_TET)
	$(CC) -c -o $@ $< $(CFLAGS)

clean: 
	rm -f *.exe 
	rm -f *.o *~ *.stackdump
