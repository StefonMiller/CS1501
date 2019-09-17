/*
 * Stefon Miller
 * CS 1501 MoWe 9:30-11:15AM
 * Assignment 1
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;

public class Assignment1 
{
	
	static TrieSTNew<String> trie;																	//Trie object used for searchprefix
	static ArrayList<String> solutions;																//Arraylist of solutions found
	public static void main(String[] args) throws IOException
	{
		String dictionary = "dictionary.txt";														//Dictionary file
		trie = new TrieSTNew<String>();																//Trie string object
		String inputFile = null;																	//file to be read from
		String outputFile = null;																	//file to write to
		BufferedReader b = null;																	//BufferedReader to read files
		String line = null;																			//Current line from inputFile
		solutions = new ArrayList<String>(10);														//ArrayList of solutions
		FileWriter fw;																				//FileWriter and PrintWriter for writing to outputFile
		PrintWriter pw;
		 
		// Check if the user entered an input AND output file, just an input file, or no files at all on command line
		if(args.length == 2)
		{
			//If user entered 2 filenames, assume first is input and last is output
			inputFile = args[0];
			outputFile = args[1];
		}
		else if(args.length ==1)
		{
			//If user entered 1 filename, assume it is an input file and create an output file
			inputFile = args[0];
			outputFile = "output.txt";
		}
		else
		{
			//If user didn't enter anything, the program cannot read anything
			System.out.println("No input file entered!");
			System.exit(0);
		}
		//Once we have an output file, create a printwriter to print to it after we are done
		fw = new FileWriter(outputFile);
		pw = new PrintWriter(fw);
		try 
		{
			//Read lines from dictionary using BufferedReader
			b = new BufferedReader(new FileReader(dictionary));
			line = b.readLine();
		} 
		catch (FileNotFoundException e) 
		{
			//If there is no dictionary in the directory, the user needs to supply one
			System.out.println("Please supply dictionary!");
			System.exit(0);
		}
		//Loop through the dictionary and populate the trie
		while(line != null)
		{
			//Omit blank lines as they are not words
			if(line.length() == 0)
			{
					
			}
			else
			{
				//put words into the trie assuming the words have no spaces
				trie.put(line, line);
				line = b.readLine();
			}
				
		}
		//Close stream when done
		b.close();
		
		try
		{
			//Read lines from inputFile until no lines are left
			b = new BufferedReader(new FileReader(inputFile));
			line = b.readLine();
			while(line != null)
			{
				//Again omit blank lines as they are not words
				if(line.equals(""))
				{
					line = b.readLine();
				}
				else
				{
					//If the line is valid, feed it into the program
					if(line!=null)
					{
						//Remove all white space from the input line and feed it into getAnagrams along with 2 empty stringbuilders
						line = line.replaceAll("\\s+", "");
			            getAnagrams(new StringBuilder(), new StringBuilder(line), new StringBuilder());
			            
			            //Write output of getAnagrams to output file
						pw.println("Here are the results for '" + line + "':");
						
						//Remove all duplicates from our solutions
						List<String> solutionFinal = solutions.stream().distinct().collect(Collectors.toList());
						
						//Sort our solutions alphabetically prior to sorting by number of words for future ease
						Collections.sort(solutionFinal);
						
						//Call custom printsolutions method to sort by # of words and alphabetically
						printSolutions(solutionFinal, pw);
						
						//Clear our solutions for the next call and read the next line
						solutionFinal.clear();
						solutions.clear();
						line = b.readLine();
					}
				}
			}
			//Flush pw buffer to output file
			pw.close();
		}
		
		catch(FileNotFoundException e)
		{
			
		}
		
	}
	/**
	 * For a given string, suffix, uses a temp stringbuilder, prefix, to recursively append valid words to build until all anagrams are found
	 * Utilizes searchPrefix to prune possibilities 
	 * @param prefix a temporary stringbuilder holding the current string we are permuting
	 * @param suffix the string fed in by our input file, as the loop progresses, chars from suffix will be appended to prefix in all valid permutations given by searchPrefix
	 * @param build The 'final' stringbuilder which will hold any and all valid single and multi-word strings
	 */
	public static void getAnagrams(StringBuilder prefix, StringBuilder suffix, StringBuilder build) 
	{
        //Loop through our entire suffix
        for (int i = 0; i < suffix.length(); i++) 
        {
            //Move character at position i from suffix to prefix and call searchPrefix
            prefix.append(suffix.charAt(i));
            suffix.deleteCharAt(i);
            int ans = trie.searchPrefix(prefix.toString());
            //Recursive case, word is a prefix and not in dictionary 
            if (ans == 1) 
            {
                //As long as there are still letters in the suffix, keep recursing
                if(suffix.length() > 0)
                {
                	getAnagrams(prefix, suffix, build);
                }
            } 
            //Word but not prefix
            else if(ans == 2)
            {
                //If all characters are used, append prefix and add word to solutions
                if(suffix.length() == 0)
                {
                	build.append(prefix);
                	solutions.add(build.toString());
                    //Once we return from adding the word to solutions, remove prefix from build and keep going
                    build.delete(build.length() - prefix.length(), build.length());
                }
                //If all characters are not used, append prefix onto build and look for multi word anagrams
                else if(suffix.length() > 0)
                {
                	build.append(prefix + " ");
                    getAnagrams(new StringBuilder(), suffix, build);
                    //We reach this once all valid multi word anagrams were found with the prefix we appended to build, so remove said prefix from build and keep going
                    build.delete(build.length() - prefix.length() - 1, build.length());

                }
            }
            //Word and a prefix
            else if(ans == 3)
            {
                //If all characters are used, append prefix and add it to dictionary 
                if(suffix.length() == 0)
                {
                	build.append(prefix);
                	solutions.add(build.toString());
                    //Once we return, remove prefix from build and keep going
                    build.delete(build.length() - prefix.length(), build.length());
                }
                //If there are still characters left, we have to do 2 recursive calls, one for single and one for multi word anagrams
                else if(suffix.length() > 0)
                {
                	getAnagrams(prefix, suffix, build);
                    getAnagrams(new StringBuilder(), suffix, build.append(prefix + " "));
                    //After our recursive calls, remove prefix from build if it was added
                    build.delete(build.length() - prefix.length() - 1, build.length());
                }
            }
            /*Any word/words with the prefix created at the beginning of our loop will have been in the dicitonary at this point
            *Return the char we moved from suffix to prefix back to suffix
            *This resets the chars before we increment the index
            */
            suffix.insert(i, prefix.charAt(prefix.length() - 1));
            prefix.deleteCharAt(prefix.length() - 1);
        }
       
    }
	/**
	 * Given a sorted list of strings, this method will sort said list by number of words, then alphabetically. 
	 * @param l sorted list of strings 
	 * @param pw printwriter used to format/write lines to outputFile
	 * @throws IOException
	 */
	 public static void printSolutions(List<String> l, PrintWriter pw) throws IOException
	 {
		 //Get max and min words for this list
		 int minWords = getMinWords(l);
		 int maxWords = getMaxWords(l);
		 //Create arraylist of final sorted solutions as well as a temporary arraylist
		 ArrayList<String> finalSols = new ArrayList<String>(l.size());
		 ArrayList<String> temp = new ArrayList<String>(l.size());
		 //Loop until we are out of words
		 while(minWords <= maxWords)
		 {
			 //Go through all words and if any are minWords in length, add them to temp
		 	for(int i = 0; i < l.size();i++)
		 	{
		 		if(l.get(i).split(" ").length == minWords)
		 		{
		 			temp.add(l.get(i));
		 		}
		 	}
		 	//Sort temp alphabetically after all of strings length minWords are added
		 	Collections.sort(temp);
		 	//Begin writing to file for all words size minWords
	 		pw.println("There were " + temp.size() + " " + minWords + "-word solutions:");
		 	for(int k = 0; k < temp.size(); k++)
		 	{
		 		//Print out all words of length minWords and add them to finalSols
		 		pw.println(temp.get(k));
		 		//Since temp was just sorted, these words will be entered alphabetically
		 		finalSols.add(temp.get(k));
		 	}
		 	//Clear temp for next loop and look for next number of words
		 	temp = new ArrayList<String>(l.size());
		 	minWords++;
		 }
		 //Write total solutions of list l at end of algorithm
		 pw.println("There were a total of " + finalSols.size() + " solutions");
		 pw.println();
		 
		 
	 }
	 /**
	  * Returns the minimum number of words of all strings in list l
	  * @param l list of strings
	  * @return fewest number of words of all strings in l
	  */
	 public static int getMinWords(List<String> l)
	 {
		 int minWords = 99;
		 for(int i = 0; i < l.size(); i++)
		 {
			 //Splits each string in l using space delimiter
			 int words = l.get(i).split(" ").length;
			 if(minWords > words)
			 {
				 minWords = words;
			 }
		 }
		 return minWords;
	 }
	 /**
	  * Returns the maximum number of words of all strings in list l
	  * @param l list of strings
	  * @return largest number of words of all strings in l
	  */
	 public static int getMaxWords(List<String> l)
	 {
		 int maxWords = 0;
		 for(int i = 0; i < l.size(); i++)
		 {
			//Splits each string in l using space delimiter
			 int words = l.get(i).split(" ").length;
			 if(maxWords < words)
			 {
				 maxWords = words;
			 }
		 }
		 return maxWords;
	 }
}

		
		
	
	
	

