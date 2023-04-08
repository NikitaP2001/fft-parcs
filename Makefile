SRC_DIR = src
OBJ_DIR = build

APP = $(OBJ_DIR)/Application.jar

JR = C:\Program Files\Java\jdk-19\bin\jar.exe
RM = rm -f
JC = C:\Program Files\Java\jdk-19\bin\javac.exe

PJAR = $(OBJ_DIR)/parcs.jar
SRC = $(wildcard $(SRC_DIR)/*.java)
OBJ := $(SRC:%.java=%.class)

all: run

clean:
	$(RM) $(APP)

$(APP): $(PJAR) $(OBJ)
	$(JR) cf $@ $(OBJ)
	$(RM) $(OBJ)

$(OBJ): $(SRC)
	$(JC) -cp $(PJAR) $^

build: $(APP)

run: $(APP)
	cd build && java -cp 'parcs.jar;Application.jar' Application