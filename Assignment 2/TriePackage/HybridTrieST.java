// CS 1501 Summer 2019
//Stefon Miller REC Monday 2:30
// HybridTrieST<V> class

package TriePackage;
import java.util.*;
import java.io.*;

public class HybridTrieST<V> 
{
    private TrieNodeInt<V> root;
    int treeType = 0;
    	// treeType = 0 --> multiway trie
    	// treeType = 1 --> DLB
    	// treeType = 2 --> hybrid
    
    /**
     * Constructor for the trie 
     * @param i	type of trie being built
     */
    public HybridTrieST(int i)
	{
    	//Assign tree type depending on i
		treeType = i;
		//If type is MTAlpha only, root is an MTAlpha node
		if(treeType == 0)
		{
			root = new MTAlphaNode<V>();
		}
		//If treetype is DLB only or hybrid, always make root a DLBNode
		else if(treeType == 1 || treeType == 2)
		{
			root = new DLBNode<V>();
		}
	}
    
    /**
     * Put method supplied by TrieSTMT and modified
     * @param key key for decoding, in this case it is the same as value
     * @param val value being stored
     */
    public void put(String key, V val) 
    {
        root = put(root, key, val, 0);
    }
    /**
     * Recursive method called by put
     * @param x	TrieNodeInt being added to
     * @param key key used for decoding
     * @param val value being stored
     * @param d index in string wea re at
     * @return TrieNodeInt<V> with added string val
     */
    private TrieNodeInt<V> put(TrieNodeInt<V> x, String key, V val, int d) 
    {
    	//If x is null, simply create a new node depending on the tree type
        if (x == null)
        {
        	//If treetype is 0, create a MTNode
        	if(treeType == 0)
        	{
        		x = new MTAlphaNode<V>();
        	}
        	//If treetype is 1 or 2, create DLBNode to start
        	else if(treeType == 1 || treeType == 2)
        	{
        		x = new DLBNode<V>();
        	}
        }
        //If we are at the end of the string, set its value to val
        if (d == key.length()) 
        {
            x.setData(val);
            return x;
        }
        //set c to the new char index
        char c = key.charAt(d);
        //insert next character in x
        x.setNextNode(c, put(x.getNextNode(c), key, val, d+1));
        //Check if the degree of the DLB is more than 11, if so convert it to a MTAlphaNode
        if(x instanceof DLBNode<?> && treeType == 2)
        {
        	if(x.getDegree() > 11)
        	{
        		x = new MTAlphaNode<V>((DLBNode<V>) x);
        	}
        }
        return x;
    }
    
    
    /**
     * Get data for key given
     * @param key key given
     * @return data of node matching the key
     */
    public V get(String key) 
    {
    	//Find the node matching the given key
        TrieNodeInt<V> x = get(root, key, 0);
        //If not found, return null
        if (x == null)
        {
        	return null;
        }
        //If found, return its data
        return x.getData();
    }
    /**
     * Recursive method called by get
     * @param x Node we are looking in
     * @param key key used to find the value
     * @param d current character we are looking at in key
     * @return Node found from looking up key
     */
    private TrieNodeInt<V> get(TrieNodeInt<V> x, String key, int d) 
    {
    	//If x is null, return null
        if (x == null)
        {
        	return null;
        }
        //If we are at the end of the key, return x
        if (d == key.length())
        {
        	return x;
        }
        //set c to current char index
        char c = key.charAt(d);
        //Recursively call this method with next child containg char c
        return get(x.getNextNode(c), key, d+1);
    }
    
    /**
     * SearchPrefix method supplied by TrieSTMT
     * @param key string we are searching for
     * @return whether the key is a prefix, word, both, or none
     */
    public int searchPrefix(String key)
    {
    	//Initailize variables
    	int ans = 0;
		TrieNodeInt<V> curr = root;
		boolean done = false;
		int loc = 0;
		//While there are still nodes to be searched and we are not done
		while (curr != null && !done)
		{
			//If we are at the end of our string, test it and mark that we are done
			if (loc == key.length())
			{
				//If there is a value at this node, it is a word
				if (curr.getData() != null)
				{
					ans += 2;
				}
				//If the current node has children, it is a prefix
				if (curr.getDegree() > 0)
				{
					ans += 1;
				}
				done = true;
			}
			//If we are not at the end, keep going
			else
			{
				curr = curr.getNextNode(key.charAt(loc));
				loc++;
			}
		}
		//Return result
		return ans;
    }
    
    /**
     * Get size of trie in total
     * @return size of trie
     */
    public int getSize()
    {
    	//Temp pointer to root of trie
    	TrieNodeInt<V> curr = root;
    	//Get iterable collection of children
    	Iterable<TrieNodeInt<V>> i = curr.children();
    	//Call recursive method 
    	return getSize(root.getSize(), i, curr);
    	
    }
    /**
     * Recursive method called by getSize
     * @param s running total size
     * @param c children of node
     * @param n node
     * @return total size of trie
     */
    private int getSize(int s, Iterable<TrieNodeInt<V>> c, TrieNodeInt<V> n)
    {
    	//If there are no children, return current size 
    	if(c == null)
    	{
    		return s;
    	}
    	//If c is not null, iterate through the nodes children
    	else
    	{
    		//For each child, add their sizes to s and recursively call getsize for their children
    		for(TrieNodeInt<V> t: c)
    		{
    			s+= t.getSize();
    			n = t;
    			s = getSize(s,t.children(),n);
    		}
    		
    	}
    	//Return final size
    	return s;
    }
    
    /**
     * Get distribution of nodes based on their degrees
     * @return distribution of nodes based on degree
     */
    public int[] degreeDistribution()
    {
    	//Create int array size k+1 as specified by HybridTrieTest
    	int[] dist = new int[27];
    	//Temp pointer to root of trie
    	TrieNodeInt<V> curr = root;
    	//Iterable collection of current node's children
    	Iterable<TrieNodeInt<V>> i = curr.children(); 
    	//Before calling the recursive method, put the root node in our distribution
    	dist[curr.getDegree()]++;
    	//Recursive call
    	degreeDistribution(dist, i, curr);
    	//Return the distribution
    	return dist;
    }
    /**
     * Recursive method called by degreeDistribuiton
     * @param d int array of distribuiton
     * @param c children of current node
     * @param n current node
     */
    private void degreeDistribution(int[] d, Iterable<TrieNodeInt<V>> c, TrieNodeInt<V> n)
    {
    	//If there are no children, backtrack
    	if(c == null)
    	{
    		return;
    	}
    	//If there are children, iterate through all of them
    	else
    	{
    		//For each child, increment the index corresponding to their degree by 1
    		//And recursively call degreeDistribuition for its children
    		for(TrieNodeInt<V> t: c)
    		{
    			n = t;
    			d[n.getDegree()]++;
    			degreeDistribution(d,t.children(),n);
    		}
    	}
    }
    
    /**
     * Count all nodes in the trie of certain type
     * @param type type of trie nodes we are looking for
     * @return all nodes of the certain type in the tire
     */
    public int countNodes(int type)
    {
    	//If type == 1, we are looking for MTAlpha nodes
    	if(type == 1)
    	{
    		//Temp pointer to root
    		TrieNodeInt<V> curr = root;
    		//Get iterable collection of current node's children
    		Iterable<TrieNodeInt<V>> i = curr.children();
    		//If curr is the type of node we are looking for, pass in an initial value of 1 instead of 0
    		//into the recursive method
    		if(curr instanceof MTAlphaNode<?>)
    		{
        		return countNodes(1 , i, curr, type);
    		}
    		else
    		{
    			return countNodes(0 , i, curr, type);
    		}
    	}
    	/*If type is 2, we are looking for DLB nodes, so
    	*we do the same process as above but instanceof checks
    	*for DLBNodes not MTALphaNodes
    	*/
    	else if(type == 2)
    	{
    		TrieNodeInt<V> curr = root;
    		Iterable<TrieNodeInt<V>> i = curr.children();
    		if(curr instanceof DLBNode<?>)
    		{
        		return countNodes(1 , i, curr, type);
    		}
    		else
    		{
    			return countNodes(0 , i, curr, type);
    		}
    	}
    	//Any other int besides 1 or 2 is invalid
    	else
    	{
    		System.out.println("Invalid type");
    		return 0;
    	}
    }
    /**
     * Recursive method called by countNodes
     * @param d total of all nodes of type type
     * @param c children of current node
     * @param n current node
     * @param type type of node we are counting
     * @return total number of nodes we are counting
     */
    private int countNodes(int d, Iterable<TrieNodeInt<V>> c, TrieNodeInt<V> n, int type)
    {
    	//If there are no children, backtrack
    	if(c == null)
    	{
    		return d;
    	}
    	//If there are children, iterate through them
    	else
    	{
    		/*
    		 * For each child, check if it is an instance of the node we are looking
    		 * for and if so increment d
    		 */
    		for(TrieNodeInt<V> t: c)
    		{
    			if(type == 1)
    			{
    				if(t instanceof MTAlphaNode<?>)
        			{
        				d++;
        			}
        			else
        			{
        				
        			}
    			}
    			else if(type == 2)
    			{
    				if(t instanceof DLBNode<?>)
        			{
        				d++;
        			}
        			else
        			{
        				
        			}
    			}
    			//Recursively call countNodes with current child's children after checking it
    			n = t;
    			d = countNodes(d,t.children(),n, type);
    		}
    	}
    	//Return total
		return d;
    }
    
    /**
     * Save trie to output file
     * @param file output file specified
     * @throws IOException
     */
    public void save(String file) throws IOException
    {
    	//Create printwriter to print to file
    	FileWriter fw = new FileWriter(new File(file));
    	PrintWriter pw = new PrintWriter(fw);
    	//Temp pointer to root of trie
    	TrieNodeInt<V> curr = root;
    	//Iterable collection of current node's children
		Iterable<TrieNodeInt<V>> i = curr.children();
		//If curr has data, print it to the file
		if(curr.getData() != null)
		{
			pw.println((String)curr.getData());
		}
		else
		{
			
		}
		//Call recursive method and close print and file writers
		save(pw, i, curr);
		fw.close();
		pw.close();
    }
    /**
     * Recursive method called by save
     * @param p printwriter object
     * @param c children of current node
     * @param n current node
     * @throws IOException
     */
    private void save(PrintWriter p, Iterable<TrieNodeInt<V>> c, TrieNodeInt<V> n) throws IOException
    {
    	//If there are no children, backtrack
    	if(c == null)
    	{
    		return;
    	}
    	//If there are children, iterate through all of them
    	else
    	{
    		/*
    		 * For each child, if it has data print it to the file
    		 * and recursively call save for its children. Note this will
    		 * be in alphabetical order because we are traversing the trie in order
    		 */
    		for(TrieNodeInt<V> t: c)
    		{
    			if(t.getData() != null)
    			{
    				p.println((String)t.getData());
    			}
    			else
    			{
    				
    			}
    			n = t;
    			save(p,t.children(),n);
    		}
    	}
		return;
    }
	// You must supply the methods for this class.  See test program
	// HybridTrieTest.java for details on the methods and their
	// functionality.  Also see handout TrieSTMT.java for a partial
	// implementation.
}
