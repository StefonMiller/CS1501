// CS 1501 Summer 2019
//Stefon Miller REC Monday 2:30
// DLB Trie Node implemented as an external class which
// implements the TrieNodeInt<V> Interface

package TriePackage;
import java.util.*;

public class DLBNode<V> implements TrieNodeInt<V>
{
    protected Nodelet front;						//Front pointer of linked list
    protected int degree;							//# of children in node
	protected V val;								//Value of node
	
	//Class for underlying nodelet structure
    protected class Nodelet				
    {
    	protected char cval;						//Character value in nodelet
    	protected Nodelet rightSib;					//Right sibling of nodelet	
    	protected TrieNodeInt<V> child;				//Child of nodelet
    	
    	/**
    	 * Constructor for Nodelet taking a char and sibling
    	 * @param cv	Character value
    	 * @param rS	Right sibling
    	 */
    	Nodelet(char cv, Nodelet rS)
    	{
    		cval = cv;
    		rightSib = rS;
    	}
    	/**
    	 * Default constructor for Nodelet
    	 */
    	Nodelet()
    	{
    		cval = 0;
    		rightSib = null;
    		child = null;
    	}
    }
    
    /**
     * Default constructor for DLBNode
     */
    public DLBNode()
	{
    	//Create new nodelet for front pointer and set all other values to default
    	front = new Nodelet();
    	val = null;
    	degree = 0;
	}
	
    /**
     * Get next instance of char c in our node
     */
	public TrieNodeInt<V> getNextNode(char c) 
	{
		//Create temp nodelet so we don't change front
		Nodelet fr = front;
		//While fr has siblings
		while(fr.rightSib != null)
		{
			//If the char value matches fr, we return that nodelet's child
			if(fr.cval == c)
			{
				return fr.child;
			}
			else
			{
				//If the char value wasn't a match, keep traversing
			}
			fr = fr.rightSib;
		}
		//While loop ends 1 entry early, so test last nodelet
		if(fr.cval == c)
		{
			return fr.child;
		}
		else
		{
			//If no siblings contain char c, return null
			return null;
		}
		
		
	}
	
	/**
	 * Set next node in list to char c w/ child node
	 */
	public void setNextNode(char c, TrieNodeInt<V> node) 
	{
		//Temp pointer to front
		Nodelet fr = front;
		//The front cval is 0, there are no nodelets used and we can simply fill the front nodelet
		if(fr.cval == 0)
		{
			fr.cval = c;
			fr.child = node;
			degree++;
			return;
		}
		//Iterate through all nodelets in this DLBNode
		for(int i = 0; i < degree; i++)
		{
			//If we find a nodelet that already has cval c, set its child to node and return
			if(fr.cval == c)
			{
				fr.child = node;
				return;
			}
			else
			{
				//If fr.cval does not match c, keep traversing the list
				if(fr.rightSib != null)
				{
					fr = fr.rightSib;
				}
			}
		}
		//If we reach this point in the method, we know c is not in our list and must add it
		//Reset temp pointer to the front
		fr = front;
		//Back pointer 1 position behind fr
		Nodelet back = null;
		//Iterate through linked list again, finding out where to add the node in alphabetical order
		for(int j = 0; j < degree; j++)
		{
			//If c is less than fr.cval, we should add it before fr to make it alphabetical
			if(c < fr.cval)
			{
				//If we are at the first pointer in the list, insert the new node at the beginning
				if(fr == front)
				{
					Nodelet beg = new Nodelet(c,fr);
					front = beg;
					beg.child = node;
					degree++;
					return;
				}
				//If we are not at the first pointer, insert the new node between fr and back
				else
				{
					Nodelet mid = new Nodelet(c,fr);
					back.rightSib = mid;
					mid.child = node;
					degree++;
					return;
				}
			}
			//If c isn't less than our current Nodelet, keep traversing the list
			back = fr;
			if(fr.rightSib!=null)
			{
				fr = fr.rightSib;
			}
		}
		//If we exit the loop, that means the char we are trying to add is higher than all other entries,
		//so we must add it to the end
		Nodelet end = new Nodelet(c,null);
		fr.rightSib = end;
		end.child = node;
		degree++;
		return;
	}
	
	/**
	 * Return data of this node
	 */
	public V getData() 
	{
		return val;
	}

	/**
	 * Set data of this node
	 */
	public void setData(V data) 
	{
		val = data;
	}
	
	/**
	 * Return degree of this node
	 */
	public int getDegree() 
	{
		return degree;
	}

	/**
	 * Return size of this node, note the size of the DLB nodes is dependent on the
	 * degree.  Total size of a DLB node is 1 primitive type and 2 references, totaling
	 * 12 bytes minimum.  Along with the variables is the nodelets, each taking up 9 bytes, 
	 * so the total size is (12 + 9*degree)
	 */
	public int getSize() 
	{
		return (12 + degree*(9));
	}
	
	/**
	 * Return iterable collection of all children of current node
	 */
	public Iterable<TrieNodeInt<V>> children() 
	{
		//Create queue to store children in sorted order whilst being iterable
		Queue<TrieNodeInt<V>> q = new LinkedList<TrieNodeInt<V>>();
		//Temp front pointer
		Nodelet fr = front;
		//Loop through all nodelets and add their children to the queue
		for(int i = 0; i < degree; i++)
		{
			q.add(fr.child);
			if(fr.rightSib != null)
			{
				fr = fr.rightSib;
			}
			
		}
		//Return null if queue is empty
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
