CoinChange
-----
This app takes in a file containing amounts owed and amounts paid, and returns an output file dictating the amount of change to be paid by denomination with the minimum amount of physical change. However, if the owed amount is divisible by 3, the amount of change per denomination will be randomly generated (while still adding up to the correct amount).

This app implements both the greedy method and the dynamic programming method of calculating the optimal amount of change. The greedy method is used by default, as it is faster and less memory intensive. However, since this program has been designed with the possibility of adding additional currencies in the future, which may be non-canonical, the dynamic programming method will ensure that the optimal amount of change is always returned for those currencies.

Dependencies:
Download and Install these dependencies before building the program.
Java Development Kit - Recommend JDK 14
Apache Maven - Recommend Maven 3.6.3

Building the Program:
1. In the command line, from the directory containing this file, run the command "mvn clean install"
2. There should now be a child directory in this directory, named "target". That directory contains a jar file named CoinChange-1.0.jar.
3. Either place your input files in the target directory, or move the jar file to the directory with your input files.

Running the Program:
In command line, from the directory containing your input files and the jar file, run "java -jar .\CoinChange-1.0.jar <filename> <mode>"
- filename = name of the input file, with extension
- mode = 'greedy' or 'dp', depending on how you want the change to be calculated
If a filename is provided, but no mode, the default mode of 'greedy' will be used.
If you do not provide a filename in the command lines, do not provide a mode as well.
If no command line arguments are provided, the program will prompt the user for the name of the file, and the mode.

If using 'dp' mode for an amount of change greater than $100,000, the program may run out of heap space. If needed, additional heap space can be added by running the program with the flag '-Xmx6144m', where 6144m is the max amount of heap space, in this case 6 GB.

Input files must be organized in the following manner:
Each line must contain the amount owed and the amount paid separated by a comma (for example: 2.13,3.00)
<Amount owed 1>, <Amount paid 1>
<Amount owed 2>, <Amount paid 2>
Amount owed and amount paid in cents must be lower than the maximum int value.