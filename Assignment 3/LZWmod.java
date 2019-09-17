//Stefon Miller
//CS 1501 Summer 2019
//Assignment 3

public class LZWmod 
{

    private static final int R = 256;								//# of possible ascii vals
    private static int L = 65536;									//Max codewords (2^16)
    private static int W = 9;										//Width of current codeword
    private static int L2 = 512;									//Temp max codewords (2^W)
    private static boolean reset;									//Flag determining whether or not we reset the dictionary
    
    /**
     * Compress file specified by user which is directed to our input stream
     */
    public static void compress() 
    {
    	
        TrieST<Integer> dict = new TrieST<Integer>();				//Symbol table containing dictionary
        StringBuilder temp = new StringBuilder();					//temporary stringbuilder that gets appended to build
        StringBuilder build = new StringBuilder();					//Stringbuilder used for searching our dictionary
        
        //Original LZW code by the author
        for (int i = 0; i < R; i++) 
        {
            dict.put(new StringBuilder().append((char) i), i);
        }
        int code = R + 1;
        
        //Write an initial bit to our output file signalling whether or not we are resetting our dictionary
        if(reset)
        {
        	BinaryStdOut.write(true);
        }
        else
        {
        	BinaryStdOut.write(false);
        }
        //Attempt to read initial char in our input file and append it to build
        try 
        {
            char ci = BinaryStdIn.readChar();
            build.append((char) ci);
        } 
        //If the stream is empty or some other exception is thrown, set build to null
        catch (Exception e) 
        {
            build = null;
        }
        
        //If the stream is initially empty, build will be null and thus our file is empty
        while (build != null) 
        {
            //Search our dictionary for the longest prefix of build
            StringBuilder pre = dict.longestPrefixOf(build);

            //if pre and build are the same length, keep appending chars to build and searching for its longest prefix
            while (pre.length() == build.length()) 
            {
            	//Reset temp at the start of every loop
                temp = new StringBuilder();
                //Try to append the next char in our file to temp
                try 
                {
                    char c = BinaryStdIn.readChar();
                    temp.append((char) c);
                } 
                //Set temp to null if we are at the end of the file
                catch (Exception e) 
                {
                    temp = null;
                }
                //Append the next char in our file to build and find the longest prefix of it
                build.append(temp);
                pre = dict.longestPrefixOf(build);
            }
            
            //Print out whatever didn't match build
            BinaryStdOut.write(dict.get(pre), W);

            //Once we write to the file, check whether or not we are at the end of our file or we have reached max codewords
            if (temp != null && code < L2) 
            {
            	//If neither are the case, add build to our dictionary and increment code
                dict.put(build, code);
                code++;
                //If code is equal to our max number of codewords, check if w is at its max value
                if (code == L2) 
                {
                	//If w is not at its max value, increment it and adjust our temp max codeword value
                	if(W < 16) 
                    {
                    	W++;
                        L2 = (int)Math.pow(2, W);
                    }
                	//If we are at the max bitwidth(W=16) of codewords, check reset
                	else
                	{
                		if(reset)
                		{
                			//If we are resetting, initialize a new dictionary, populate it with ascii chars
                			//and reset our bitwidth and max codeword variables
                			dict = new TrieST<Integer>();
                			W = 9;
                			L2 = (int)Math.pow(2, W);
                			for (int i = 0; i < R; i++) 
                	        {
                	            dict.put(new StringBuilder().append((char) i), i);
                	        }
                			//Reset our code index
                	        code = R + 1;
                		}
                	}
                	
                }
            }

            //set build to temp
            build = temp;
        }
        //after we exit the loop, write a terminating character and close the stream
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
    
    /**
     * Expands file specified by user which is directed to our input stream
     */
    public static void expand() 
    {
        String[] dict = new String[L];							//Dictionary of codewords
        int code;												//Index of our dictionary for new codewords
        String out;												//String we are writing to output
        int cw;													//Current codeword
        String temp;											//Placeholder string for manipulation
        
        //Fill dictionary with all ascii chars
        for (code = 0; code < R; code++) 
        {
            dict[code] = "" + (char) code;
        }
        dict[code++] = "";
        
        //Set reset to first bit in our file
        reset = BinaryStdIn.readBoolean();
        
        //Read in # of bytes specified by w as our codeword
        cw = BinaryStdIn.readInt(W);
        
        //If we are at end of file, return
        if(cw == R)
        {
        	return;
        }
        
        //Assign our output string to whatever string is indexed by our codeword
        out = dict[cw];
        
        while (true) 
        {
        	//Write our output string to the output file
            BinaryStdOut.write(out);
            
            //If we are at the max temp codewords, but not true max codewords, increment W and recalculate L2
            if(((code + 1) == L2) && (W!=16)) 
            {
            	W++;
                L2 = (int)Math.pow(2, W);
            }
            //if we are at the true max codewords, determine if we should reset or not
        	if (code == L) 
            {
            	//If we are to reset, empty dict and reset W and L2
            	if(reset)
            	{
            		dict = new String[L];
                    //Fill dictionary with all ascii chars
                    for (code = 0; code < R; code++) 
                    {
                        dict[code] = "" + (char) code;
                    }
                    dict[code++] = "";
                    W = 9;
                    L2 = (int)Math.pow(2,W);
            	}
            	//If we are not to reset, keep writing 16 bit words without adding to dictionary
            	else
            	{
            		while (cw != R) 
            		{
            			BinaryStdOut.write(dict[cw]);
            			cw = BinaryStdIn.readInt(W);
            		}
                    break;
            	}
            }
        	 //Set codeword to the next W bytes read in from input file
            cw = BinaryStdIn.readInt(W);
            
            //If we are at the end of the file, exit the loop
            if(cw == R)
            {
            	break;
            }
            
            //Set a temporary string to the prefix at index cw
            temp = dict[cw];
            
            
            //Special case when the string we are looking at is the same as the string we just added to our dictionary
            if (code == cw) 
            {
            	//Set temp to the string we just added + its first character, as discussed in class
                temp = out + out.charAt(0);
            }
            
            //if we are not at the end of the word limit for 16 bitwidth codewords, add a new entry to our dictionary
            if((code + 1) < L) 
            {
            	//Add whatever string was previously found plus the first char in our temp string to the dictionary
                dict[code++] = out + temp.charAt(0);
            }
            
            
            //Set our output string to temp
            out = temp;
        }
        //Close stream at end of loop
        BinaryStdOut.close();
    }
    
    public static void main(String[] args) 
    {
    	//If no character was entered for reset, we do nothing
    	if(args.length <= 1)
    	{
    		reset = false;
    	}
    	//If a character was entered, test if it was 'r'
    	else
    	{
    		reset = testFlag(args[1]);
    	}
    	//Determine compression/decompression
        if (args[0].equals("-")) 
        {
            compress();
        } 
        else if (args[0].equals("+")) 
        {
            expand();
        } 
        else 
        {
            throw new RuntimeException("Illegal command line argument");
        }

        System.exit(0);
    }
    
    /**
     * Determines flag to signal for dictionary resets
     * @param s string containing user choice
     * @return boolean of whether or not we reset dictionary
     */
    public static boolean testFlag(String s)
    {
    	//If char is r, set flag to signal dictionary resets
    	if(s.equals("r"))	
    	{
    		return true;
    	}
    	return false;
    }
}
