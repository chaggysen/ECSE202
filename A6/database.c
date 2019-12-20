/*
 ============================================================================
 Name        : database.c
 Author      : Anthony Harissi Dagher (w/ Prof. Frank Ferrie's sample code)
 ID     	 : 260924250
 Copyright   : Your copyright notice
 Description : A simple program to manage a small database of student
             : records using B-Trees for storage.  (This version includes
             : the components to read in the database and create an object
             : for each record.  Nothing useful is done with these objects
             : other than to immediately print.  It should provide most
             : of the scaffolding you need to do Assignment 6
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <ctype.h>

// To make the "C" implementation completely analogous to Java, one has to create
// an object for each student record and attach it to a corresponding bNode
// object in a B-Tree data structure.  These objects are represented by the
// corresponding structure templates below.

#define MAXLEN 20
#define false 0
#define true !false

// Prototypes and templates should go in a file called sortFile.h which
// is subsequently included in sortFile.c.  For a small program like this one,
// a single file will do.

// Structure Templates
typedef struct SR {				// The student record object
    char Last[MAXLEN];
	char First[MAXLEN];
	int ID;
	int marks;
} SRecord;

typedef struct bN {				// The bNode object (not used
	struct SR *Srec;			// in this demo, but you will
	struct bN *left;			// need it for A6).
	struct bN *right;
} bNode;

// List of prototypes
bNode *addNode_Name(bNode *root, SRecord *data); 
bNode *addNode_ID(bNode *root, SRecord *data); 
bNode *makeNode(SRecord *data);
bNode *match; // Defines the static variable to assist with pointer-pointers

void inorder(bNode *root);
void search_Name(bNode *root, char *data); 
void search_ID(bNode *root, int ID);
void str2upper(char *string);
void help();


// Main entry point is here.  Program uses the standard Command Line Interface
int main(int argc, char *argv[]) {

	// Internal declarations
    FILE *NamesIDs;        // File descriptor (an object)!
	FILE *marks;           // Will have two files open

    bNode *root_N;   		// Pointer to names B-Tree
    bNode *root_I;   		// Pointer to IDs B-Tree
    SRecord *Record;   		// Pointer to current record read in

	int NumRecords;
	char cmd[MAXLEN], sName[MAXLEN];
	int sID;

	// Argument check
    if (argc != 3) {
        printf("Usage: sdb [Names+IDs] [marks] \n");
        return -1;
    }
	// Attempt to open the user-specified file.  If no file with
	// the supplied name is found, exit the program with an error
	// message.

    if ((NamesIDs=fopen(argv[1],"r"))==NULL) {
        printf("Can't read from file %s\n",argv[1]);
        return -2;
    }

    if ((marks=fopen(argv[2],"r"))==NULL) {
        printf("Can't read from file %s\n",argv[2]);
        return -2;
    }

	// Initialize B-Trees by creating the root pointers

    root_N=NULL;
	root_I=NULL;

	//  Read through the NamesIDs and marks files, record by record.

	NumRecords=0;

	printf("Building database...\n");

	while(true) {

		// 	Allocate an object to hold the current data

		Record = (SRecord *)malloc(sizeof(SRecord));
		if (Record == NULL) {
			printf("Failed to allocate object for data in main\n");
			return -1;
		}

		//  Read in the data.  If the files are not the same length, the shortest one
		//  terminates reading.

		int status = fscanf(NamesIDs,"%s%s%d",Record->First,Record->Last,&Record->ID);
		if (status == EOF) break;
		status = fscanf(marks,"%d",&Record->marks);
		if (status == EOF) break;
		NumRecords++;

		//	Add the record just read in to 2 B-Trees - one ordered
		//  by name and the other ordered by student ID.

		root_N=addNode_Name(root_N,Record); 
		root_I=addNode_ID(root_I,Record);

		//	For this demo we'll simply list each record as we receive it

		printf("\nStudent Name:\t\t%s %s\n",Record->First,Record->Last);
		printf("Student ID:\t\t%d\n",Record->ID);
		printf("Total Grade:\t\t%d\n",Record->marks);
	}

	// Close files once we're done
	fclose(NamesIDs);
	fclose(marks);

	printf("Finished, %d records found...\n",NumRecords);

	//  Simple Command Interpreter: - This is commented out until you implement the functions listed above
	while (1) {

	    printf("sdb> ");
	    scanf("%s",cmd);					  // read command
	    str2upper(cmd);

		// List by Name
		if (strncmp(cmd,"LN",2)==0) {         // List all records sorted by name
			printf("Student Record Database sorted by Last Name\n\n");
			inorder(root_N); // Applies inorder on names
			printf("\n");
		}

		// List by ID
		else if (strncmp(cmd,"LI",2)==0) {    // List all records sorted by ID
			printf("Student Record Database sorted by Student ID\n\n");
			inorder(root_I); // Applies inorder on the ID
			printf("\n");
		}

		// Find record that matches Name
		else if (strncmp(cmd,"FN",2)==0) {    // List record that matches name
			printf("Enter name to search: ");
			scanf("%s",sName);
			match=NULL;
			search_Name(root_N,sName);
			if (match==NULL) printf("There is no student with that name.\n");
	        else {
			  	if (strlen(match->Srec->First)+strlen(match->Srec->Last)>15) {
				printf("\nStudent Name:\t%s %s\n",match->Srec->First,match->Srec->Last);
			  	} 
			  	else {
					printf("\nStudent Name:\t\t%s %s\n",match->Srec->First,match->Srec->Last);
			  	}
			  printf("Student ID:\t\t%d\n",match->Srec->ID);
			  printf("Total Grade:\t\t%d\n\n",match->Srec->marks);
	        }
		}

		// Find record that matches ID
		else if (strncmp(cmd,"FI",2)==0) {    // List record that matches ID
			printf("Enter ID to search: ");
			scanf("%d",&sID);
			match=NULL;
			search_ID(root_I,sID);
			if (match==NULL){
			  printf("There is no student with that ID.\n");
			}
	        else {
			  if (strlen(match->Srec->First)+strlen(match->Srec->Last)>15) {
				printf("\nStudent Name:\t%s %s\n",match->Srec->First,match->Srec->Last);
			  } 
			  else {
			  	  printf("\nStudent Name:\t\t%s %s\n",match->Srec->First,match->Srec->Last);
			  }
			printf("Student ID:\t\t%d\n",match->Srec->ID);
			printf("Total Grade:\t\t%d\n\n",match->Srec->marks);
	      }
		}

		// Help
		else if (strncmp(cmd,"H",1)==0) {  // Help
			help();
		}

		else if (strncmp(cmd,"?",2)==0) {  // Help
			help();
		}

		// Quit

		else if (strncmp(cmd,"Q",1)==0) {  // Help
			printf("Program terminated...\n");
			return 0;
		}

		// Command not understood
		else {
			printf("Command not understood.\n");
		}
	}
}

// Write and insert the functions listed in the prototypes here.

// AddNode method for names
bNode *addNode_Name(bNode *root, SRecord *data){

	bNode *current;
	// The following code will establish/form the tree when the bNode is null
	if(root == NULL){
		root=makeNode(data); // If null, create the node
		return root;
	}
	else{
		current=root;
		while (true){
			if (strcmp(data->Last, current->Srec->Last)<0){ // This statement compares character length
				if(current->left == NULL){ // If leaf node
					current->left = makeNode(data);  // Attaches the new node
					return root;
				}
				else{
					current=current->left; // If not, continue down tree
				}
			}
			else{
				if(current->right == NULL){ // If leaf node, but on right
					current->right = makeNode(data); // Attaches the new node
					return root;
				}
				else{
					current=current->right; // If not, continue down tree
				}
			}
		}
	}
}

// AddNode method for IDs
bNode *addNode_ID(bNode *root, SRecord *data){
	
	bNode *current;
	// The following code will establish/form the tree when the bNode is null
	if(root == NULL){
		root=makeNode(data); // If null, create the node
		return root;
	}
	else{
		current=root;
		while(true){
			if (data->ID < current->Srec->ID){ // This statement checks if the ID given is smaller than the root ID
				if(current->left == NULL){ // If leaf node
					current->left = makeNode(data);  // Attaches the new node
					return root; // Returns root
				}
				else{
					current=current->left; // If not, continue down tree
				}
			}
			else{
				if(current->right == NULL){ // If leaf node, but on right
					current->right = makeNode(data); // Attaches the new node
					return root; // Returns root
				}
				else{
					current=current->right; // If not, continue down tree
				}
			}
		}	
	}
}

// Method for creating instances of bNodes
bNode *makeNode(SRecord *data){

	bNode *node = (bNode*)malloc(sizeof(bNode)); // Creates the new object
	node->Srec = data; // Initializes the data field
	node->left = NULL; // Sets left to null
	node->right = NULL; // Sets right to null
	return node; // Returns node
}

// The following code is the inorder method, essentially identical to the bTree assignment.
void inorder(bNode *root){

	if(root->left != NULL) inorder(root->left); // Left first
	// To print the data, in this case the full name, ID & marks.
	printf("%-15s %-12s %-5d %-2d \n", root->Srec->First, root->Srec->Last, root->Srec->ID, root->Srec->marks); 
	if(root->right != NULL) inorder(root->right); // Right third
}

// Method to search by name
void search_Name(bNode *root, char *data){

	bNode *current; // Sets a new variable
	current = root; // Sets current to variable root

	// The following while loop compares strings to search for the name
	while(true){

		if (strcasecmp(current->Srec->Last,data)>0){ // If smaller, go left on the tree
			if(current->left ==NULL){
				break; // Breaks if the left of the tree is null, meaning it has found the name
			}
			current = current->left; // If not, continue down the tree on the left
		}
		if (strcasecmp(current->Srec->Last, data)<0){ // If larger, go right on the tree
			if (current->right == NULL){
				break; // Breaks if the right of the tree is null, meaning it has found the name
			}
			current = current->right; // If not, continue down the tree on the right
		}
		if(strcasecmp(current->Srec->Last, data)==0){ // If they are equal words, the current name is the name
			match = current; // Equates match variable to current variable
			break; // Breaks after associating match variable to current root
		}
	}
}
// Method to search by ID
void search_ID(bNode *root, int ID){

	bNode *current; // Sets a new variable
	current = root; // Sets current to variable root

	// The following while loop compares ID values (int) to search for the ID
	while(true){
		if (current->Srec->ID>ID){ // If smaller, go left on the tree
			if(current->left ==NULL){
				break; // Breaks if the left of the tree is null, meaning it has found the ID
			}
			current = current->left; // If not, continue down the tree on the left
		}
		if (current->Srec->ID<ID){ // If larger, go right on the tree
			if (current->right == NULL){
				break; // Breaks if the right of the tree is null, meaning it has found the ID
			}
			current = current->right; // If not, continue down the tree on the right
			}
		if(current->Srec->ID== ID){ // If they are equal words, the current ID is the ID
			match = current; // Equates match variable to current variable
			break; // Breaks after associating match variable to current root
		}
	}
}
//  Convert a string to upper case
void str2upper (char *string) {
    int i;
    for(i=0;i<strlen(string);i++)
       string[i]=toupper(string[i]);
     return;
}

// Help
// prints command list
void help() {
	printf("LN List all the records in the database ordered by last name.\n");
	printf("LI List all the records in the database ordered by student ID.\n");
	printf("FN Prompts for a name and lists the record of the student with the corresponding name.\n");
	printf("FI Prompts for a name and lists the record of the student with the Corresponding ID.\n");
	printf("HELP Prints this list.\n");
	printf("? Prints this list.\n");
	printf("Q Exits the program.\n\n");
}


