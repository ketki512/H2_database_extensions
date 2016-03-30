1. This project is intended to add 2 new features to the existing design of H2 database.
2. The features added are: 
	a. First() and Last() command. 
	b. Kmeans clustering on numerical data.
3. In order to run the files, 
	i. Open a console and go to the project directory
	ii. Follow 3 commands: build.bat, build clean and build jar
	iii. This creates an executable file in the bin folder that can be used to open the H2 console. 
4. The project can also be launched in Eclipse and can be run. Open the file Console.java under src/main/org/tools/Console.java
5. On running this the H2 console opens up. 
6. Query for First: SELECT FIRST(column_name) FROM table_name;
	- returns the first entry of the column in the table
7. Query for Last: SELECT LAST(column_name) FROM table_name;
	- returns the last entry of the column in the table
8. Query for Kmeans: SELECT KMEANS(column_name) FROM table_name;
	- Calculates the SSE for each of the clusters and calculates the efficient number of clusters this data could have. 
	- Also creates a graph plot of SSE vs No. of clusters, which is stored in the project directory. 