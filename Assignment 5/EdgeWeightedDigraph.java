/*
 * Stefon Miller
 * CS 1501 Assignment 5
 * This class was modified from the author's original version
 */

import java.util.ArrayList;
import java.util.Iterator;

public class EdgeWeightedDigraph 
{
    private final int V;															//Number of vertices
    private int E;																	//Number of edges
    public ArrayList<DirectedEdge>[] adj;											//Adjacency list
    boolean[] online;																//Status of each vertex
    private int d;																	//Number of paths
    
    /**
     * Create an empty edge-weighted digraph with V vertices.
     */
    public EdgeWeightedDigraph(int V) 
    {
        if (V < 0) throw new RuntimeException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = (ArrayList<DirectedEdge>[]) new ArrayList[V];
        for (int v = 0; v < V; v++)
            adj[v] = new ArrayList<DirectedEdge>();
    }

   /**
     * Create a edge-weighted digraph with V vertices and E edges.
     * Modified to add inverse edges
     */
    public EdgeWeightedDigraph(int V, int[] Vf, int[] Vt, int E, int[] W) 
    {
        this(V);
        online = new boolean[V];
        if (E < 0) throw new RuntimeException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) 
        {
            int v = Vf[i];
            int w = Vt[i];
            double weight = W[i];
            //Add inverse of each edge we are adding
            DirectedEdge e = new DirectedEdge(v, w, weight, true);
            DirectedEdge eInv = new DirectedEdge(w,v,weight, true);
            addEdge(e);
            addEdge(eInv);
            
        }
        //Initialize array of vertex status
        for(int j = 0; j < V; j++)
        {
        	online[j] = true;
        }
    }
    
    //Return status of vertices
   public boolean[] getStatus()
   {
	   return online;
   }
   /**
     * Return the number of vertices in this digraph.
     */
    public int V() {
        return V;
    }

   /**
     * Return the number of edges in this digraph.
     */
    public int E() {
        return E;
    }


   /**
     * Add the edge e to this digraph.
     */
    public void addEdge(DirectedEdge e) 
    {
        int v = e.from();
        adj[v].add(e);
        E++;
    }
    
    /**
     * Used to get every path from v1 to v2 under weight x
     * @param v1 starting vertex
     * @param v2 ending vertex
     * @param x weight limit
     */
    public void allPaths(int v1, int v2, int x)
    {
    	boolean[] visited = new boolean[V];
    	ArrayList<DirectedEdge> paths = new ArrayList<>(V);
    	d = 0;
    	if(online[v1] == false || online[v2] == false)
    	{
    		System.out.println("One or more vertices are down");
    		return;
    	}
    	recursivePaths(v1,v2,x,visited,paths, 0);
    	System.out.println("\n\n");
    }
    
    /**
     * Recursive utility method for allPaths
     * @param v1 starting vertex
     * @param v2 ending vertex
     * @param x max weight
     * @param visited array keeping track of what vertices we visit
     * @param paths list of edges representing the path we are taking
     * @param totWeight total running weight
     */
    public void recursivePaths(Integer v1, Integer v2, int x, boolean[] visited, ArrayList<DirectedEdge> paths, int totWeight)
    {
    	//Set v1 to visited and proceed
    	visited[v1] = true;
    	//Base case, if we are at the end and our weight is less than x, print out the path and return
    	if(v1.equals(v2) && totWeight <= x)
    	{
    		if(totWeight <= x)
    		{
    			System.out.println("Path " + d + ":");
    			d++;
    			System.out.print("\t" + paths);
    			System.out.println("\n\tTotal weight of this path: " + totWeight);
    			visited[v1] = false;
    		}
    		return;
    	}
    	//Loop through edges of v1 and determine where to go
    	for(DirectedEdge e: adj[v1])
    	{
    		//If an edge hasn't been visited and is online, add it to the path and recurse
    		if((!visited[e.to()]) && e.online == true)
    		{
    			paths.add(e);
    			totWeight += e.weight();
    			recursivePaths(e.to(), v2, x, visited, paths, totWeight);
    			//After recursion, remove last edge from our path
    			paths.remove(e);
    			totWeight -= (int)e.weight();
    		}
    	}
    	visited[v1] = false;
    }
    
    /**
     * Remove an edge from our graph
     * @param i starting vertex
     * @param j ending vertex
     */
    public void removeEdge(int i, int j)
    {
    	int v = i;
    	int w = j;
    	//Create an iterator on the adjacency list of i 
    	Iterator<DirectedEdge> it = adj[v].iterator();
    	//Loop through adj and look for any edge between i and j
    	while(it.hasNext())
    	{
    		DirectedEdge ed = it.next();
    		//If an edge is found, remove it and its reciprocal
    		if(ed.from() == v && ed.to() == w)
    		{
    			it.remove();
    			it = adj[w].iterator();
    			while(it.hasNext())
    			{
    				ed = it.next();
    				if(ed.from() == w && ed.to() == v)
    				{
    					it.remove();
    				}
    			}
    		}
    		
    	}    	
    	E-=2;
    }

   /**
     * Return the edges leaving vertex v as an Iterable.
     * To iterate over the edges leaving vertex v, use foreach notation:
     * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
     */
    public Iterable<DirectedEdge> adj(int v) 
    {
        return adj[v];
    }

   /**
     * Return all edges in this graph as an Iterable.
     * To iterate over the edges, use foreach notation:
     * <tt>for (DirectedEdge e : graph.edges())</tt>.
     */
    public Iterable<DirectedEdge> edges() {
        ArrayList<DirectedEdge> list = new ArrayList<DirectedEdge>();
        for (int v = 0; v < V; v++) {
            for (DirectedEdge e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    } 

   /**
     * Return number of edges leaving v.
     */
    public int outdegree(int v) {
        return adj[v].size();
    }



   /**
     * Return a string representation of this graph.
     */
    public String toString() 
    {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        for (int v = 0; v < V; v++) 
        {
            s.append(v + ": ");
            for (DirectedEdge e : adj[v]) 
            {
            	if(e.online)
            	{
            		s.append(e + "  ");
            	}
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

}
