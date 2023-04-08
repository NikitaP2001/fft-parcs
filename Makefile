SRC_DIR = src
OBJ_DIR = build

APP = $(OBJ_DIR)/Application.jar

JR = jar
RM = rm -f
JC = javac

PJAR = $(OBJ_DIR)/parcs.jar
SRC = $(wildcard $(SRC_DIR)/*.java)
OBJ := $(SRC:%.java=%.class)
JAR_OBJ := $(OBJ:$(SRC_DIR)/%.class= -C $(SRC_DIR) %.class)

all: run

clean:
	$(RM) $(APP)

$(APP): $(PJAR) $(OBJ)
	$(JR) cf $@ $(JAR_OBJ)
	$(RM) $(OBJ)

$(OBJ): $(SRC)
	$(JC) -cp $(PJAR) $^

build: $(APP)

ifeq ($(OS),Windows_NT)   
run: $(APP)
	cd build && java -cp 'parcs.jar;Application.jar' Application
else    
run: $(APP)
	cd build && java -cp 'parcs.jar:Application.jar' Application
endif
