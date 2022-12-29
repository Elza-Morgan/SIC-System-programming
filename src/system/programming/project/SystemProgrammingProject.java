/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.programming.project;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SystemProgrammingProject {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        ArrayList< String> Labels=new ArrayList<String>();
        ArrayList<String> References=new ArrayList<String>();
        ArrayList<String> Instructions=new ArrayList<String>(); 
        
        ArrayList<String> LocationCounter=new ArrayList<String>();
        ArrayList<String> ObjectCode=new ArrayList<String>();
        ArrayList<String> HTR=new ArrayList<String>();
        
        FileReader read =  new FileReader("C:/Users/zizoj/OneDrive/Desktop/SIC-System programming\\sic program.txt");
        BufferedReader buffer = new BufferedReader(read); 
        String lineFromFile="";
        boolean checkLastElement=false;
        while((lineFromFile  = buffer.readLine())!=null) {
            lineFromFile = lineFromFile.trim();
            String []arr = lineFromFile.split("\\s+");
            for(int i = 0 ;i<arr.length;i++){
                if(arr[0].equalsIgnoreCase("end")){
                    checkLastElement = true;
                    break;
                }
            }
            if(!checkLastElement){
                switch (arr.length) {
                    case 3:
                        Labels.add(arr[0]);
                        Instructions.add(arr[1]);
                        References.add(arr[2]);
                        break;
                    case 2:
                        Labels.add("#");
                        Instructions.add(arr[0]);
                        References.add(arr[1]);
                        break;
                    default:
                        break;
                }
            }else{
                Labels.add("#");
                Instructions.add(arr[0]);
                References.add(arr[1]);
            }
        }
        
        /*These two are added compulsry because first excution always takes
        the first given address*/
        LocationCounter.add(0,References.get(0));
        LocationCounter.add(1,References.get(0));
        
        /*This main address is used to hold the address in order to do it function
        depend on directives*/
        String mainAddress = LocationCounter.get(0).trim();
        for(int i=2,j=1; i<Instructions.size(); i++,j++){
            int address=0;
            if(Instructions.get(j).equalsIgnoreCase("RESW"))
            {
               address = Integer.parseInt(References.get(j).trim());
               address=address*3;
               
               int temp = Integer.parseInt(mainAddress.trim(),16);
               
               temp = temp + address;
               mainAddress = Integer.toHexString(temp).trim();
               LocationCounter.add(i,mainAddress);
            }
            else if(Instructions.get(j).equalsIgnoreCase("BYTE"))
            {
                String byyte = References.get(j);
                
                if(byyte.charAt(0)=='C')
                {
                    int CharSize = 0;
                    CharSize = byyte.length()-3;
                    int temp = Integer.parseInt(mainAddress.trim(),16);
                
                    temp = temp+CharSize;
                
                    mainAddress = Integer.toHexString(temp).trim();
                    LocationCounter.add(i,mainAddress);
                }
                
                   else if(byyte.charAt(0)=='X')
                {
                    int HexaSize = 0;
                    HexaSize = (byyte.length()-3)/2;
                    
                    int temp = Integer.parseInt(mainAddress.trim(),16);
                    
                    temp = temp+HexaSize;
                
                    mainAddress = Integer.toHexString(temp).trim();
                    LocationCounter.add(i,mainAddress);
                }
            }
            else if(Instructions.get(j).equalsIgnoreCase("RESB"))
            {
                String byyte = References.get(j).trim();
                int byyte1 = Integer.parseInt(byyte);
                
                int temp = Integer.parseInt(mainAddress.trim(),16);
                    
                temp = temp+byyte1;
                    mainAddress = Integer.toHexString(temp).trim();
                    LocationCounter.add(i,mainAddress);
                    
            }else if(Instructions.get(j).equalsIgnoreCase("WORD")){
                String word = References.get(j).trim();
                String arr[] = word.split(",");
                
                int wordSize=  arr.length*3;
                int temp = Integer.parseInt(mainAddress.trim(),16);
                temp = temp +wordSize;
                mainAddress = Integer.toHexString(temp).trim();
                LocationCounter.add(i,mainAddress);
                
            }else{
                converter.initialize();
                for(int z=0 ; z<converter.OPTAB.length;z++)
                { String f="";
                    if(Instructions.get(j).trim().equalsIgnoreCase(converter.OPTAB[z][0])){
                        f=converter.OPTAB[z][1];
                        int temp = Integer.parseInt(mainAddress.trim(),16);
                        int f1 = Integer.parseInt(f);
                        temp+=f1;
                         mainAddress = Integer.toHexString(temp).trim();
                   LocationCounter.add(i,mainAddress);     
                    }
                    }
                }
            
        }
        /*This function is used to make the object code*/
         ObjectCode.add(0," ");
        for(int i = 1 ; i<Instructions.size();i++){
            String concat = "";
            String opcode = "";
            String address = "";
            boolean check =false;
                String arr[] = References.get(i).split(",");
                converter.initialize();
                /*j counter to search in list*/
                for(int j =0;j<converter.OPTAB.length;j++){
                    if(Instructions.get(i).equalsIgnoreCase(converter.OPTAB[j][0])){
                        opcode = converter.OPTAB[j][2];
                        if(converter.OPTAB[j][0].equalsIgnoreCase("RSUB")){
                            opcode = opcode+"0000";
                        }
                        check =true;
                        break;
                    }
                }
                if(check){
                    for(int j = 0;j<Labels.size();j++){
                        if(arr[0].trim().equalsIgnoreCase(Labels.get(j).trim())){
                            address = LocationCounter.get(j);
                            
                            break;
                        }
                    }
                    /*for index*/
                    if(arr.length == 2){
                        int x = Integer.parseInt(address);
                        x += 8000;
                        address = String.valueOf(x);
                    }
                    concat = opcode + address;
                    ObjectCode.add(i,concat);
                }
                else{
                    if(Instructions.get(i).equalsIgnoreCase("RESW")||
                            Instructions.get(i).equalsIgnoreCase("RESB")
                            || Instructions.get(i).equalsIgnoreCase("END")){
                        ObjectCode.add(i,"NoObjectCode");
                    }
                    else if(Instructions.get(i).equalsIgnoreCase("WORD")){
                        String word = References.get(i).trim();
                        
                        String arr2[] = word.split(",");
                        for(int h =0;h<arr2.length;h++){
                            int temp = Integer.parseInt(arr2[h].trim());
                            
                            String temp1 = Integer.toHexString(temp).trim();
                            
                            arr2[h]=("000000" + temp1).substring(temp1.length());
                        }
                        String concatTemp="";
                        for(int n=0 ; n< arr2.length;n++){
                             concatTemp =  concatTemp + arr2[n];
                        }
                        ObjectCode.add(i,concatTemp);
                        
                    }
                    else if(Instructions.get(i).equalsIgnoreCase("BYTE")){
                        
                       String Byte = References.get(i);
                        if(Byte.charAt(0)=='C'){
                            int number = 0;
                            String concatenation = "";
                            for(int h=2;h<Byte.length()-1;h++){
                               number = Byte.charAt(h);
                                String temp = Integer.toHexString(number);
                                concatenation = concatenation + temp;
                            }
                            ObjectCode.add(i,concatenation);
                        }
                        
                        if(Byte.charAt(0)=='X'){
                           String concatenation = "";
                           for(int h=2;h<Byte.length()-1;h++){
                              concatenation = concatenation + Byte.charAt(h);
                          }
                          ObjectCode.add(i,concatenation);
                        }
                    }
                }
        }
        //for printing all of the table
        printAllTable(Labels,Instructions,References,LocationCounter,ObjectCode);
        
         //for printing the symbol table
      printSymbolTable(LocationCounter, Labels);
      
      
      /*This is used to print the object code*/
     for(int i=0;i<ObjectCode.size();i++){
          System.out.println(ObjectCode.get(i));
        }
        /*HTR after the object code*/
        hteRecord(HTR,LocationCounter,Instructions,Labels,ObjectCode,References);
        
        
        
        //references and objectcode
           printReferenceAndObject(References, ObjectCode);


    }
     /*This is used to print the orginal table from the file a long with the 
    lables*/
    public static void printAllTable(ArrayList<String> Labels,ArrayList<String> Instructions,
            ArrayList<String> References,ArrayList<String> LocationCounter ,ArrayList<String> ObjectCode ){
        /*THis prints the subheading of the table*/
    System.out.format("%-20s%-15s%-15s%s%n","Location Counter","Labels","Instructions","References");
    for(int i=0 ; i<ObjectCode.size(); i++)
    {
        System.out.println(ObjectCode.get(i));
    }
    
    for(int i = 0; i<Labels.size();i++){
            System.out.format("%-20s%-15s%-15s%s%n", 
                    LocationCounter.get(i).trim(),Labels.get(i) ,
                    Instructions.get(i) , References.get(i));
        }
    }
    
    /*this function is used to print the symbol table*/
    public static void printSymbolTable(ArrayList<String> LocationCounter,
            ArrayList<String> Labels){
        System.out.format("%-30s%-24s%n","Labels","Location Counter");
        
        for(int i = 0;i<LocationCounter.size();i++){
            if(Labels.get(i).trim().charAt(0)!='#'){
            System.out.printf("%-30s%-24s%n",Labels.get(i),LocationCounter.get(i).trim());
            }
        } 
    }
    public static void printReferenceAndObject(ArrayList<String> References,
            ArrayList<String> ObjectCode){
        ObjectCode.add(18," ");
        System.out.format("%-30s%-24s%n","Reference","Object Code");
        
        for(int i = 0;i<References.size();i++){
            
            System.out.format("%-30s%-24s%n",References.get(i).trim(),ObjectCode.get(i));
            
        } 
    }
    public static String padding(String input, int length){
		String output = "000000"+input;
		return output.substring(output.length()-length).toUpperCase();
	}
    public static String getLength(String firstLoc, String lastLoc){
		int decFirstloc = Integer.parseInt(firstLoc.trim(), 16);
		int decLastloc = Integer.parseInt(lastLoc.trim(), 16);
		int decProgramLength = decLastloc - decFirstloc;
		String hexProgramLength = Integer.toHexString(decProgramLength);
		return hexProgramLength;	
	}
    public static void hteRecord(ArrayList<String> HTR,ArrayList<String> LocationCounter,ArrayList<String> Instructions
    	    ,ArrayList<String> Labels,ArrayList<String> ObjectCode,ArrayList<String> References){
                HTR.add(0,"H "+Labels.get(0).trim()+" "+padding(References.get(0).trim(), 6)+" "+padding(getLength(LocationCounter.get(0).trim(), LocationCounter.get(LocationCounter.size()-1)), 6));
                int j=1;
                String T = "";
		String temp = "";
		int cnt = 0;
		for(int i = 1; i<LocationCounter.size(); i++){
			if((ObjectCode.get(i).equals("NoObjectCode") && cnt > 0) || cnt == 10||Instructions.get(i).equalsIgnoreCase("END")){
				
				T += " "+padding(getLength(T.substring(2), LocationCounter.get(i).trim()), 2) + temp;
                                
                                HTR.add(j,T);
                                j++;
				T = temp = "";
				cnt = 0;
			}
			if(!ObjectCode.get(i).equals("NoObjectCode")){
				if(cnt == 0){
					T = "T "+padding(LocationCounter.get(i).trim(), 6);
				}
				if (cnt < 10){
					temp += " "+ObjectCode.get(i);
					cnt++;
				}
			}
		}
                HTR.add(j,"E" + " " + padding(LocationCounter.get(0), 6));
                for(int h=0;h<HTR.size();h++){
                    System.out.println(HTR.get(h));
                }
	}
}
