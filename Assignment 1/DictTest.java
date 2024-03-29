// CS 1501
// Simple program to test the TrieSTNew class.  This may help you
// to get started with Assignment 1.

import java.io.*;
import java.util.*;

public class DictTest
{
	public static void main(String [] args) throws IOException
	{
		Scanner fileScan = new Scanner(new FileInputStream("dictionary.txt"));
		
		// Make TrieSTNew object of String, then read strings from file and
		// put each one into the trie.
		TrieSTNew<String> D = new TrieSTNew<String>();
		String st;
		while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.put(st, st);
		}

		// Some sample test strings
		String [] tests = {"abc", "abe", "abet", "abx", "ace", "acid", "hives",
						   "iodin", "inval", "zoo", "zool", "zurich"};
		// This loop is testing for membership of the string within the trie
		System.out.println("Testing get() method...");
		for (int i = 0; i < tests.length; i++)
		{
			String val = D.get(tests[i]);
			if (val != null)
				System.out.println(val + " is in the dictionary");
			else
				System.out.println(tests[i] + " is not in the dictionary");
		}
		System.out.println();
		// This loop is testing the searchPrefix method.  Note the difference in
		// the outcomes from the loop above.  This method is key to the pruning
		// in your anagram algorithm.
		System.out.println("Testing searchPrefix() method...");
		for (int i = 0; i < tests.length; i++)
		{
			int ans = D.searchPrefix(tests[i]);
			System.out.print(tests[i] + " is ");
			switch (ans)
			{
				case 0: System.out.println("not found");
					break;
				case 1: System.out.println("a prefix");
					break;
				case 2: System.out.println("a word");
					break;
				case 3: System.out.println("a word and prefix");
			}
		}
	}
}


