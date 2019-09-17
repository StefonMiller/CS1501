// CS 1501 Summer 2019
// // Modification of Sedgewick Eager Prim Algorithm to show detailed trace

/******************************************************************************
 *  Compilation:  javac PrimMST.java
 *  Execution:    java PrimMST V E
 *  Dependencies: EdgeWeightedGraph.java Edge.java Queue.java IndexMinPQ.java
 *                UF.java
 *
 *  Prim's algorithm to compute a minimum spanning forest.
 *
 ******************************************************************************/

public class PrimMSTTrace {
    private DirectedEdge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ<Double> pq;

    public PrimMSTTrace(EdgeWeightedDigraph G) {
        edgeTo = new DirectedEdge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        pq = new IndexMinPQ<Double>(G.V());
        for (int v = 0; v < G.V(); v++) distTo[v] = Double.POSITIVE_INFINITY;

        for (int v = 0; v < G.V(); v++)      // run from each vertex to find
        {   
        	if (!marked[v]) 
        	{
        		prim(G, v);      			// minimum spanning forest
        	}
        }
        // check optimality conditions
        assert check(G);
    }

    // run Prim's algorithm in graph G, starting from vertex s
    private void prim(EdgeWeightedDigraph G, int s) {
        distTo[s] = 0.0;
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            scan(G, v);
        }
    }

    // scan vertex v
    private void scan(EdgeWeightedDigraph G, int v) {
        marked[v] = true;
        for (DirectedEdge e : G.adj(v)) {
            int w = e.to();
            if (marked[w]) 
            {
            	continue;         // v-w is obsolete edge
            }
            //Make sure e is online before scanning
            if ((e.weight() < distTo[w]) && e.online == true) 
            {
                distTo[w] = e.weight();
                edgeTo[w] = e;
                if (pq.contains(w)) 
                {
                		pq.change(w, distTo[w]);
                }
                else              
                {
                		pq.insert(w, distTo[w]);
                }
            }
            else
            {
            	
            }
        }
    }

    // return iterator of edges in MST
    public Iterable<DirectedEdge> edges() {
        Bag<DirectedEdge> mst = new Bag<DirectedEdge>();
        for (int v = 0; v < edgeTo.length; v++) {
            DirectedEdge e = edgeTo[v];
            if (e != null) {
                mst.add(e);
            }
        }
        return mst;
    }


    // return weight of MST
    public double weight() {
        double weight = 0.0;
        for (DirectedEdge e : edges())
            weight += e.weight();
        return weight;
    }


    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(EdgeWeightedDigraph G) {

        // check weight
        double weight = 0.0;
        for (DirectedEdge e : edges()) {
            weight += e.weight();
        }
        double EPSILON = 1E-12;
        if (Math.abs(weight - weight()) > EPSILON) {
            return false;
        }

        // check that it is acyclic
        UF uf = new UF(G.V());
        for (DirectedEdge e : edges()) {
            int v = e.from(), w = e.to();
            if (uf.connected(v, w)) {
                return false;
            }
            uf.union(v, w);
        }

        // check that it is a spanning forest
        for (DirectedEdge e : edges()) {
            int v = e.from(), w = e.to();
            if (!uf.connected(v, w)) {
                return false;
            }
        }

        // check that it is a minimal spanning forest (cut optimality conditions)
        for (DirectedEdge e : edges()) {
            int v = e.from(), w = e.to();

            // all edges in MST except e
            uf = new UF(G.V());
            for (DirectedEdge f : edges()) {
                int x = f.from(), y = f.to();
                if (f != e) uf.union(x, y);
            }

            // check that e is min weight edge in crossing cut
            for (DirectedEdge f : G.edges()) {
                int x = f.from(), y = f.to();
                if (!uf.connected(x, y)) {
                    if (f.weight() < e.weight()) {
                        return false;
                    }
                }
            }

        }

        return true;
    }

}
