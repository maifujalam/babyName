
import edu.duke.*;
import org.apache.commons.csv.*;
import java.io.*;
import java.util.*;

public class BabyNames {
    /*
        * This method returns the total number of births males and females in a file
    */
    public static void show() {
        FileResource fr=new FileResource();
        CSVParser parser = fr.getCSVParser(false);
        for(CSVRecord i:parser)
        {
            String currName = i.get(0);
            String g = i.get(1);
            int numBorn = Integer.parseInt(i.get(2));
            String gg=(g.equals("M")?"Male":"Female");
            System.out.printf("Name: %12s    Gender: %6s    Number of Born: %5d",currName,gg,numBorn);
            System.out.println();
        }
        System.out.println();
    }
    public static void totalBirths() {
        FileResource fr=new FileResource();
        int totalBirths = 0;
        int totalGirls = 0;
        int totalBoys = 0;
        CSVParser parser = fr.getCSVParser(false);

        for(CSVRecord record : parser) {
            int numBorn = Integer.parseInt(record.get(2));
            String gender = record.get(1);
            totalBirths += numBorn;
            if(gender.equals("M")) {
                totalBoys += numBorn;
            } else {
                totalGirls += numBorn;
            }
        }

        System.out.println("Total: " + totalBirths);
        System.out.println("Boys: " + totalBoys);
        System.out.println("Girls: " + totalGirls);
        System.out.println();
    }

    /*
    * This method returns the rank of the name in the file for the given gender, 
    * where rank 1 is the name with the largest number of births. 
    * If the name is not in the file, then -1 is returned.
    */
    public static long getRank(String name) {
        long rank = -1;
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser(false);

        for(CSVRecord record : parser) {
            String currName = record.get(0);
            //String currGender = record.get(1);
            
            if(currName.equals(name)) {
                rank = record.getRecordNumber();
            }
        }
        return rank;
    }

    /*
    * This method returns the name of the person in the file at this rank, 
    * for the given gender, where rank 1 is the name with the largest number of births. 
    * If the rank does not exist in the file, then “NO NAME” is returned.
    */
    public static String getName(int rank) {
        String name = "";
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser(false);

        for(CSVRecord record : parser) {
            long currRank = record.getRecordNumber();
            //String currGender = record.get(1);
            String currName = record.get(0);

            if(currRank == rank ) {
                name = currName;
            }
        }

        if(name != "") {
            return name;
        } 
        else {
            return "NO NAME";
        }
    }

    /*
    * This method determines what name would have been named if they were born 
    * in a different year, based on the same popularity.
    */
    public static void whatIsNameInYear(String name, int year, int newYear) {
        FileResource fr = new FileResource("us_babynames_test/yob" + year + "short.csv");
        FileResource newFr = new FileResource("us_babynames_test/yob" + newYear + "short.csv");
        CSVParser parserOld = fr.getCSVParser(false);
        CSVParser parserNew = newFr.getCSVParser(false);
        String newName = "";
        long popularity = 0;

        for(CSVRecord record : parserOld) {
            String currName = record.get(0);
            String currGender = record.get(1);

            if(currName.equals(name) ) {
                popularity = record.getRecordNumber();
            }
        }

        for(CSVRecord record : parserNew) {
            String currGender = record.get(1);
            long currPopularity = record.getRecordNumber();

            if(popularity == currPopularity) {
                newName = record.get(0);
            }
        }

        System.out.println(name + " born in " + year + " would be " + newName + " if she was born in " + newYear);
    }

    /*
    * This method selects a range of files to process and returns an integer, 
    * the year with the highest rank for the name and gender. 
    * If the name and gender are not in any of the selected files, it should return -1.
    */
    public static int yearOfHighestRank(String name, String gender) {
        long highestRank = 0;
        int yearOfHighestRank = -1;
        String fileName = "";
        DirectoryResource dr = new DirectoryResource();
        
        // Iterate through all files
        for(File f : dr.selectedFiles()) {
            FileResource fr = new FileResource(f);
            CSVParser parser = fr.getCSVParser(false);
            
            // Iterate through all records in file
            for(CSVRecord record : parser) {
                String currName = record.get(0);
                String currGender = record.get(1);

                if(currName.equals(name) && currGender.equals(gender)) {
                    long currRank = record.getRecordNumber();
                    
                    if(highestRank == 0) {
                        highestRank = currRank;
                        fileName = f.getName();
                    } 
                    else {
                        if(highestRank > currRank) {
                            highestRank = currRank;
                            fileName = f.getName();
                        }
                    }
                }
            }
        }

        // Remove all non-numeric characters from the filename
        fileName = fileName.replaceAll("[^\\d]", "");
        
        // Convert String fileName to Integer
        yearOfHighestRank = Integer.parseInt(fileName);

        return yearOfHighestRank;
    }
  
  /*
    * This method returns the average rank of a name in multiple files
    */
    public static double getAverageRank(String name, String gender) {
        // Initialize a DirectoryResource
        DirectoryResource dr = new DirectoryResource();
        // Define rankTotal, howMany
        double rankTotal = 0.0;
        int howMany = 0;
        // For every file the directory add name rank to agvRank
        for(File f : dr.selectedFiles()) {
            FileResource fr = new FileResource(f);
            CSVParser parser = fr.getCSVParser(false);
            for(CSVRecord record : parser) {
                String currName = record.get(0);
                String currGender = record.get(1);
                if(currName.equals(name) && currGender.equals(gender)){
                    long currRank = record.getRecordNumber();
                    rankTotal += (double)currRank;
                    howMany += 1;
                }
            }
        }
        // Define avgRank = rankTotal / howMany
        double avgRank = rankTotal / (double)howMany;
        return avgRank;
    }
  
  /*
    * This method returns the total births of the same gender that are ranked higher
  * than the parameter name
    */
    public static int getTotalBirthsRankedHigher(int year, String name) {
        int numBorn = 0;
        long rank = getRank(name);
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser(false);
        for(CSVRecord record : parser) {
            int currBorn = Integer.parseInt(record.get(2));
            String currGender = record.get(1);
            long currRank = record.getRecordNumber();
            if(rank > currRank) {
                numBorn += currBorn;
            }
        }
        return numBorn;
    }
    
  /*
    * For testing the above methods,this method is used.
    */
    public static void testTotlaBirth() {           
        //FileResource fr = new FileResource();
        totalBirths();

        //long rank = getRank(2012, "Frank", "M");
        //System.out.println("Rank is: " + rank);

         //String name = getName(1982, 450, "M");
         //System.out.println("Name: " + name);
         //whatIsNameInYear("Isabella", 2012, 2014, "F");
         System.out.println(yearOfHighestRank("Mason", "M"));
        
         System.out.println(getAverageRank("Mason", "M"));
        
        // System.out.println(getTotalBirthsRankedHigher(2012, "Ethan", "M"));
         //System.out.println(""+getAverageRank("Robert","M"));
    }

    public static void main()throws IOException {
        
        Scanner in=new Scanner(System.in);
        int i=1;
        while(i!=0)
        {
            int ch;
            System.out.println("Press 1 to Show all details in  CSV File:");
            System.out.println("Press 2 to find Total Birth:");
            System.out.println("Press 3 to get Rank using Name:");
            System.out.println("Press 4 to get Name using Rank:");
            System.out.println("Press 5 to find year of heighest rank:");
            System.out.println("Press 6 to use Name Predictor:");
            System.out.println("Press 7 to get Average Rank:");
            System.out.println("Press 8 get total births ranked heigher:");
            System.out.println("Press 9 to Exit:");
            System.out.println("Press 10 to see Project Link:");
            ch=in.nextInt();
            switch(ch)
            {
                case 1:
                {
                    show();   //Shows all Names,Gender and Quantity
                    break;
                }
                case 2:
                {
                    totalBirths();    //Shows total with number of male and females.
                    break;
                }
                case 3:
                {
                    String name="Jacob";
                    long rank = getRank(name);   // name is given,find rank
                    System.out.println(name+" is present at "+ rank);
                    break;
                }
                case 4:
                {
                    int rank=7;
                    String name = getName(rank);           //Rank is Given ,find name
                    System.out.println("At Rank"+rank+"Name: " + name+" is present");
                    break;
                }
                case 5:
                {
                    System.out.println(yearOfHighestRank("Mason", "M"));    //Select multiple file and return the heighest rank
                    break;
                }
                case 6:
                {
                    whatIsNameInYear("Isabella", 2012, 2014);           //Find rank of name in 2012, then find the person using that rank in 2014.Like this coomon name is predicted.
                    break;
                }
                case 7:
                {
                    System.out.println(getAverageRank("Mason", "M"));     //Get Average rank from multiple file
                    break;
                }
                case 8:                                                  
                {
                    System.out.println(getTotalBirthsRankedHigher(2012, "Ethan"));   //Total births higher then the given names rank.
                    break;
                }
                case 9:                                               //Exit the Program
                {
                    i=0;
                    break;
                }
                case 10:                                               //Exit the Program
                {
                    System.out.println("Project Link:");
                    break;
                }
                default:                                             //invalid int input.
                {
                    System.out.println("Wrong Input!Try Again.");
                    break;
                }
            }
       }
  }
 } 