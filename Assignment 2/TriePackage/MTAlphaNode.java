// CS 1501 Summer 2019
//Stefon Miller REC Monday 2:30
// MultiWay Trie Node implemented as an external class which
// implements the TrieNodeInt InterfaceAddress.  For this
// class it is assumed that all characters in any key will
// be letters between 'a' and 'z'.

package TriePackage;
import java.util.*;
import TriePackage.DLBNode.Nodelet;

public class MTAlphaNode<V> implements TrieNodeInt<V>
{
	private static final int R = 26;	//26 letters in alphabet
    protected V val;					//value stored in node
    protected TrieNodeInt<V> [] next;	//array of letters
	protected int degree;				//# of children this node has
	
	//Default constructor, initialize array and set values to default
	public MTAlphaNode()
	{
		 val = null;
		 degree = 0;
		 next = (TrieNodeInt<V> []) new TrieNodeInt<?>[R];
	}
	
	//Constructor w/ data, initialize array and set val to data 
	public MTAlphaNode(V data)
	{
		val = data;
		degree = 0;
		next = (TrieNodeInt<V> []) new TrieNodeInt<?>[R];
	}
	
	//Constructor converting DLBNode into MTAlphaNode
	//Iterates through the DLBNode, adding each entry 
	//To the appropriate index in next[]
	public MTAlphaNode(DLBNode<V> node)
	{
		//If the array is null, initialize it
		if(next == null)
		{
			next = (TrieNodeInt<V> []) new TrieNodeInt<?>[R];
		}
		//Temp variable to iterate through linked list
		Nodelet fr = node.front;
		//Iterate through linked list in data
		while(fr.rightSib != null)
		{
			//If next doesn't contain the character in fr, we have a character that wasn't originally in our array, increment degree
			if(next[(fr.cval - 97)] == null)
			{
				degree++;
				
			}
			else
			{
				//If data was already in array, we don't need to change degree
			}
			//Assign index in array to child of fr
			next[(fr.cval - 97)] = fr.child;
			//If we are at the end of our list, fr.rightSib will be null
			if(fr.rightSib != null)
			{
				fr = fr.rightSib;
			}
		}
		//We will exit the above loop without processing the last entry, so loop 1 additional time
		if(next[(fr.cval - 97)] == null)
		{
			degree++;
			
		}
		else
		{
			
		}
		next[(fr.cval - 97)] = fr.child;
	}
	
	/**
	 * Get the next entry of char c in our node
	 */
	public TrieNodeInt<V> getNextNode(char c) 
	{
		//If the char doesn't exist in our node, return null
		if(this.next[(c - 97)] == null)
		{
			return null;
		}
		//If the char exists, return the entry
		else
		{
			return next[(c - 97)];
		}
	}
	
	/**
	 * Set child at index c to node
	 */
	public void setNextNode(char c, TrieNodeInt<V> node) 
	{
		//If index c in array next is null, we have a new entry and should icrement degree
		if(next[(c - 97)] == null)
		{
			degree++;
		}
		else
		{	
			//if index c isn't null, we have a duplicate entry and don't need to increment degree
		}
		//Set index c of next to node. Note 97 is subtracted from c b/c lowercase ascii chars start at 97
		next[(c - 97)] = node;
		
	}
	
	/**
	 * Return data of node
	 */
	public V getData() 
	{
		return val;
	}
	
	/**
	 * Set data of node
	 */
	public void setData(V data) 
	{
		val = data;
	}
	
	/**
	 * Return degree of node
	 */
	public int getDegree() 
	{
		return degree;
	}
	
	/**
	 * Size of MTAlphaNode is fixed b/c we are using an array
	 * thus, it is always 26 pointers for the array and 2 more
	 * for variables (26*4) + 8 = 112 bytes
	 */
	public int getSize() 
	{
		return (4 + 4 + (26*4));
	}
	
	/**
	 * Return iterable collection of children of current node
	 */
	public Iterable<TrieNodeInt<V>> children() 
	{
		//Add all children into a queue retaining order
		Queue<TrieNodeInt<V>> q = new LinkedList<TrieNodeInt<V>>();
		for(int i = 0; i < R; i++)
		{
			//If index is null, don't add it to the queue
			if(next[i] == null)
			{
				
			}
			else
			{
				q.add(next[i]);
			}
		}
		//If there are no entries, return null
		if(q.isEmpty())
		{
			return null;
		}
		else
		{
			return q;
		}
	}
}
