## Mice Visualization Program

The Mice Visualization Program is a JavaFX software application designed to visualize data collected from a series of mice behavior experiments. The data contains the various locations of individual mice within a 25 sector grid and their corresponding timestamps over time. The program parses this data and is capable of representing the mice activity with three types of maps (heat, vector, and overlay) using two methods of visualization generation (static and animated). 

## Project Background and Developers

The Mice Visualization Program is the result of a semester-long project for our Software Engineering class. Our client, an interdisciplinary team of researchers, needed a method of visualizing experiment data for the purpose of ascertaining whether or not a social hierarchy exists among the mice.

### Developers
#### Parker Rowland (parkercode)
User interface, session handling, data parsing, heat map algorithm, animation skeleton code, animation export, visualization options, help documentation.

#### Josh Mwandu (jmwandu)
Vector map algorithm, animated vector map, mice colors

#### Alex Brown (Alpr1010)
Image export, help documentation

## Installation

Since this program was developed using NetBeans, the easiest way to run the program involves downloading NetBeans and using the Git features built into NetBeans to fetch the program's code. The following steps outline this process:

<ol>
  <li>Download and install the latest version of NetBeans that supports Java SE from https://netbeans.org/downloads/.</li>
  <li>Use NetBean's Git features, detailed in https://netbeans.org/kb/docs/ide/git.html, to clone the code from this repository</li>
  <li>Build and run the code from within NetBeans</li>
  <li>If the program will not build, try changing the File -> Project Properties settings to reflect the settings that the development team used:
    <ul>
      <li>Souce/ Binary Format: JDK8</li>
      <li>Profile: Full JRE</li>
      <li>Encoding: UTF8</li>
      <li>Java Platform: JDK 1.8 (Default)</li>
    </ul>
  </li>
</ol>

## Data set format

The program validates CSV data sets according to certain rules. In the CSV file, the data must conform to the following format:

Column headers ...

10/10/2010 10:10:10.100,mouseIdString,mouseLabelString,RFID10,1010

...

The column headers are ignored. The first column in the data row contains the timestamp, which must be in the format MM/dd/yyyy HH:mm:ss.SSS, the second column represents the unique identifier that each mouse should have throughout the data set, the third column contains the label associated with the identifier in the preceding column, the fourth column contains the label for the grid sector, which must be in the format "XXXX00" (where 'X' is an alphanumeric character and '0' is a digit), and the fifth column contains the duration, in milliseconds, of how long the mouse remained in the grid sector. There is no limit on the number of data rows that the program can process.

## Documentation

The documentation for the program, excluding its installation procedure, is contained within the program, available by navigating to Help -> Documentation in the main menu.
