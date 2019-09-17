/*
 * Stefon Miller
 * CS 1501 Assignment 5
 * REC Tuesday 2:30PM
 */

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

public class Assignment5 
{
	public static EdgeWeightedDigraph graph;												//EdgeWeightedDigraph object representing our graph
	public static void main(String[] args)
	{
		String fName = "";																	//Filename
		int nVertices;																		//Number of vertices in our graph
		int nEdges;																			//Number of edges in our graph\
		int[] fVertices;																	//Vertices edges are coming from
		int[] tVertices;																	//Vertices edges are going to
		int[] weights;																		//Edge weights
		String command;																		//User input
		Scanner keyboard;																	//Keyboard for user input
		String[] commands;																	//Command string split into an array
		
		
		//If no file is input, exit.  Else set fName to the string entered in command line
		if(args.length < 1)
		{
			System.out.println("No filename entered");
			System.exit(0);
		}
		else
		{
			fName = args[0];
		}
		//Try/catch for IOException
		try 
		{
			//Open a scanner on the input file and read in the number of vertices, edges, and weights
			Scanner reader = new Scanner(new File(fName));
			nVertices = reader.nextInt();
			nEdges = reader.nextInt();
			System.out.println("Input file: " + fName + "\n\n");
			tVertices = new int[nEdges];
			fVertices = new int[nEdges];
			weights = new int[nEdges];
			reader.nextLine();
			
			//Read in each edge and add it to our graph
			for(int i = 0; i < nEdges; i++)
			{
				String line = reader.nextLine();
				String[] strs = line.split(" ");
				fVertices[i] = Integer.parseInt(strs[0]);
				tVertices[i] = Integer.parseInt(strs[1]);
				weights[i] = Integer.parseInt(strs[2]);
			}
			//Close reader when done and create graph object 
			reader.close();
			graph = new EdgeWeightedDigraph(nVertices, tVertices, fVertices, nEdges, weights);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//Open keyboard scanner and display commands to user
		keyboard = new Scanner(System.in);
		System.out.println("Command List:\nR:\t\tCurrent active network\nM:\t\tMinimum spanning tree\nS i j:\t\tShortest path from i to j\nP i j x:\tEach distinct path from i to j with weight equal to or less than x\nD i:\t\tMake node i go down\nU i:\t\tMake node i go down\nC i j x:\tChange weight of edge i,j to x\nQ:\t\tExit\nL:\t\tList all comands");
		while(true)
		{	
			//Get keyboard command input and sterilize it for processing
			System.out.println("-------------------------------------");
			System.out.println("\nPlease enter a command");
			command = keyboard.nextLine();
			command = command.toUpperCase();
			System.out.println("Command " + command);
			System.out.println("-------------------------------------");
			command = command.toLowerCase();
			commands = new String[4];
			commands = command.split(" ");
			switch(commands[0].charAt(0))
			{
				case 'l':
				{
					//Simple command list to reduce clutter on console
					System.out.println("Command List:\nR:\t\tCurrent active network\nM:\t\tMinimum spanning tree\nS i j:\t\tShortest path from i to j\nP i j x:\tEach distinct path from i to j with weight equal to or less than x\nD i:\t\tMake node i go down\nU i:\t\tMake node i go down\nC i j x:\tChange weight of edge i,j to x\nQ:\t\tExit\nL:\t\tList all comands");
					break;
				}
				case 'r':
				{
					//Teset if the graph is connected or not and output proper response
					if(testConnection())
					{
						System.out.println("The graph is currently connected");
					}
					else
					{
						System.out.println("The graph is currently disconnected");
					}
					//Get an array describing which nodes are online and which arent
					boolean[] status = graph.getStatus();
					System.out.print("\nThe following nodes are online:\n\t");
					//Display all online nodes
					int online = 0;
					for(int j = 0; j < status.length; j++)
					{
						if(status[j] == true)
						{
							System.out.print(j + " ");
							online++;
						}
					}
					//Display all offline nodes
					System.out.println("\n");
					System.out.print("The following nodes are offline:\n\t");
					for(int j = 0; j < status.length; j++)
					{
						if(status[j] == false)
						{
							System.out.print(j + " ");
						}
					}
					//Hack if no nodes are offline
					if(online == status.length)
					{
						System.out.println("None");
					}
					System.out.println("\n");
					//Print a visual representation of our graph
					printGraph();
					break;
				}
				case 'm':
				{
					//Create primmsttrace object and print out all edges in it
					PrimMSTTrace mst = new PrimMSTTrace(graph);
					System.out.println("Minimum spanning tree has weight " + mst.weight());
					System.out.println("The MST has the following edges:");
					for (DirectedEdge e : mst.edges())
					{
						System.out.println("\t" + e);
					}
					break;
				}
				case 's':
				{
					//Get i and j from string.split method
					int i = 0;
					int j = 0;
					try
					{
						i = Integer.parseInt(commands[1]);
						j = Integer.parseInt(commands[2]);
					}
					catch(Exception e)
					{
						System.out.println("Invalid/wrong number of chars input for command s");
						System.exit(0);
					}
					//Create DijkstraP object from our graph and i
					DijkstraSP sp = new DijkstraSP(graph, i);
					//Prints all paths from s, print only path to j
					System.out.println("Shortest path from " + i + " to " + j);
				    if (sp.hasPathTo(j)) 
				    {
				        System.out.printf("%d to %d (%.2f)  ", i, j, sp.distTo(j));
				        for (DirectedEdge e : sp.pathTo(j)) 
				        {
				            System.out.print(e + "   ");
				        }
				        System.out.println();
				    }
				    else 
				    {
				        System.out.println("No path exists from " + i + " to " + j);
				    }
				    break;
				}
				case 'p':
				{
					int i = 0;
					int j = 0;
					int x = 0;
					
					try
					{
						i = Integer.parseInt(commands[1]);
						j = Integer.parseInt(commands[2]);
						x = Integer.parseInt(commands[3]);
						
					}
					catch(Exception e)
					{
						System.out.println("Invalid input for command p");
						System.exit(0);
					}
					//Parse all ints into i,j,and x, then call allPaths
					graph.allPaths(i,j,x);
					break;
				}
				case 'd':
				{
					int i = 0;
					try
					{
						 i = Integer.parseInt(commands[1]);
					}
					catch(Exception e)
					{
						System.out.println("Invalid input for command d");
						System.exit(0);
					}
					//Set node's status to false and set its reciprocal edge to false as well
					System.out.println("Node " + i + " has gone down");
					graph.online[i] = false;
					for(DirectedEdge e: graph.adj(i))
					{
						e.online = false;
						//Set all incident edges from i to offline
						for(DirectedEdge f: graph.adj(e.to()))
						{
							if(f.to() == i)
							{
								f.online = false;
							}
						}
					}
					break;
				}
				case 'u':
				{
					int i = 0;
					try
					{
						 i = Integer.parseInt(commands[1]);
					}
					catch(Exception e)
					{
						System.out.println("Invalid input for command u");
						System.exit(0);
					}
					//Set node's status to true and set its reciprocal node to true as well
					System.out.println("Node " + i + " has gone up");
					graph.online[i] = true;
					for(DirectedEdge e: graph.adj(i))
					{
						//Make sure both vertices are online before restoring edges between them
						if(graph.online[e.to()])
						{
							e.online = true;
							graph.online[e.from()] = true;
							for(DirectedEdge k: graph.adj(e.to()))
							{
								//Set any edges coming from i to online as long as both vertices are online
								if(k.to() == e.from())
								{
									k.online = true;
								}
							}
							
						}
					}
					break;
				}
				case 'c':
				{
					int i = 0;
					int j = 0;
					int x = 0;
					try
					{
						i = Integer.parseInt(commands[1]);
						j = Integer.parseInt(commands[2]);
						x = Integer.parseInt(commands[3]);
					}
					catch(Exception e)
					{
						System.out.println("Invalid input for command c");
						System.exit(0);
					}
					//If x <= 0, remove edge from our graph
					if(x <= 0)
					{
						graph.removeEdge(i,j);
						System.out.println("Edge " + i + ", " + j + " removed from graph\n\n");
					}
					else
					{
						boolean flag = false;
						//If we find the edge we are looking for, set its and its reciprocals weights to x
						for(DirectedEdge e: graph.adj(i))
						{
							if(e.to() == j)
							{
								e.setWeight(x);
								for(DirectedEdge ed: graph.adj(j))
								{
									if(ed.to() == i)
									{
										ed.setWeight(x);
									}
								}
								System.out.println("Edge weight changed\n\n");
								flag = true;
							}
						}
						//If the edge wasn't found, add it to our graph
						if(!flag)
						{
							System.out.println("Adding edge " + i + ", " + j);
							graph.addEdge(new DirectedEdge(i,j,x,true));
							graph.addEdge(new DirectedEdge(j,i,x,true));
						}
					}
					break;
				}
				case 'q':
				{
					//Exit if user picks q
					System.out.println("Thank you for using my program");
					keyboard.close();
					System.exit(0);
				}
			}
			
		}
		
	}
	/**
	 * Method to test whether our graph is connected using DFS, if all nodes are visited then it is connected
	 * @return boolean referring to whether or not graph is connected
	 */
	public static boolean testConnection()
	{
		boolean[] visited = new boolean[graph.V()];
		testConnectionDFS(visited, 0);
		for(int i = 0; i < visited.length; i++)
		{
			if(visited[i] == false)
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * Recursive utility for testConnection utilizing the recursive DFS algorithm
	 * @param b boolean array of vertices visited
	 * @param v beginning vertex
	 */
	public static void testConnectionDFS(boolean[] b, int v)
	{
		b[v] = true;
		Iterator<DirectedEdge> i = graph.adj[v].listIterator();
		while(i.hasNext())
		{
			DirectedEdge e = i.next();
			if((!b[e.to()]) && e.online == true)
			{
				testConnectionDFS(b,e.to());
			}
		}
	}
	/**
	 * Method to print our graph for r command
	 */
	public static void printGraph()
	{
		//Create boolean array to see which vertices we visit
		boolean[] visited = new boolean[graph.V()];
		int components = 0;
		System.out.println("Component " + components + ":\n");
		//Get stringbuilder all edges from our vertex
		StringBuilder[] strings = printDFS(visited, 0, new StringBuilder[graph.V()]);
		//Print out all vertices and their edges
		for(int i = 0; i < strings.length; i++)
		{
			//If we did not visit a vertex some indices will be null
			if(strings[i] != null)
			{
				System.out.println(strings[i].toString());
			}
		}
		//If some indices in our boolean array are false, then our graph is disconnected
		while(!testArray(visited))
		{
			//Loop through the boolean array and call printDFS on any index that is false
			for(int i = 0; i < visited.length; i++)
			{
				//If the vertex is online and hasn't been visited, we have a new connected component
				if(visited[i] == false && graph.online[i])
				{
					components++;
					System.out.println("Component " + components++ + ":");
					//Call printDFS on any vertex that is online and not visited, the print it out
					StringBuilder[] s = printDFS(visited, i, new StringBuilder[graph.V()]);
					for(int k = 0; k < s.length; k++)
					{
						if(s[k] != null)
						{
							System.out.println(s[k].toString());
						}
					}
				}
				//If the vertex wasn't visited but is offline, set its value to true but dont print any of its edges
				else if(visited[i] == false)
				{
					visited[i] = true;
				}
			}
		}
	}
	/**
	 * Utility for printGraph using DFS
	 * @param b boolean array keeping track of our visited vertices
	 * @param v beginning vertex
	 * @param s running string of our edges
	 * @return stringbuilder containing a string representation of our current connected component
	 */
	public static StringBuilder[] printDFS(boolean[] b, int v, StringBuilder[] s)
	{
		//DFS but when we find a node we haven't visited, append all of its edges to our stringbuilder
		b[v] = true;
		s[v] = new StringBuilder();
		if(graph.online[v])
		{
			s[v].append(v + ":\t");
		}
		//Add all edges to our sb
		for(DirectedEdge e: graph.adj(v))
		{
			if(e.online)
			{
				s[v].append(e.from() + "-" + e.to() + " " + e.weight() + "\t\t");
			}
		}
		s[v].append("\n");
		//Simple DFS to traverse our graph
		Iterator<DirectedEdge> i = graph.adj[v].listIterator();
		while(i.hasNext())
		{
			DirectedEdge e = i.next();
			//Make sure the vertex is online before we recurse
			if((!b[e.to()]) && e.online == true)
			{
				printDFS(b,e.to(),s);
			}
		}
		return s;
	}
	/**
	 * Test whether we have visited every vertex
	 * @param b boolean array
	 * @return t/f whether or not we have visited every vertex
	 */
	public static boolean testArray(boolean[] b)
	{
		for(int i = 0; i < b.length; i++)
		{
			if(b[i] == false)
			{
				return false;
			}
		}
		return true;
	}
	
}
