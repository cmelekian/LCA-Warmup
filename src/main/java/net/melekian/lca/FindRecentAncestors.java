package net.melekian.lca;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import org.jgrapht.io.*;
import java.util.*;
import java.io.*;

public class FindRecentAncestors {
    private FindRecentAncestors() {
    }
    /**
     * Returns a simple directed graph containing the ancestry information read from the file argument.
     * Any cycles of length 2 will be removed. 
     * If an appropriate graph cannot be read from the file, returns null instead.
     * 
     * @param file a relative or absolute path to a .dot file containing the graph
     * @return a simple directed graph containing ancestry information
     */
    private static Graph<String, DefaultEdge> getFamilyTree(String file) {
    	
    	VertexProvider<String> getName = new VertexProvider<String>() {
    		public String buildVertex(String id, Map<String, Attribute> attributes) {
    			//Gets the correct name in case of regnal numbers or nicknames
    			if (attributes.containsKey("label")) {
    				return attributes.get("label").toString();
    			}
    			return id;
    		}
    	};
    	
    	EdgeProvider<String, DefaultEdge> defaultEdgeProvider = new EdgeProvider<String, DefaultEdge>() {
    		public DefaultEdge buildEdge(String from, String to, String label, Map<String, Attribute> attributes) {
    			return new DefaultEdge();
    		}
    	};

    	DOTImporter<String, DefaultEdge> imp = new DOTImporter<>(getName, defaultEdgeProvider);
    	Graph<String, DefaultEdge> familyTree = new SimpleDirectedGraph<>(DefaultEdge.class);
    	
    	try {
    		imp.importGraph(familyTree, new FileReader(file));
    	}
    	catch (FileNotFoundException e){
    		System.out.println("Could not find the specified file.");
    		return null;
    	}
    	catch (ImportException e) {
    		System.out.println("Could not import a graph from the specified file.");
    		return null;
    	}

    	//Eliminate marriages since they don't reflect an ancestral relation
    	List<DefaultEdge> removals = new ArrayList<>();
    	for (DefaultEdge e: familyTree.edgeSet()) {
    		if (familyTree.containsEdge(familyTree.getEdgeTarget(e),familyTree.getEdgeSource(e))) {
    			removals.add(e);
    			removals.add(familyTree.getEdge(familyTree.getEdgeTarget(e),familyTree.getEdgeSource(e)));
    		}
    	}
    	familyTree.removeAllEdges(removals);
    	
    	//If the graph still contains cycles at this point we can't do anything to fix it
    	CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(familyTree);
    	if (cycleDetector.detectCycles()) {
    		System.out.println("The graph contains a cycle.");
    		return null;
    	}
    	
    	return familyTree;
    }
    
    /**
     * Prints the most recent common ancestor of the specified nodes in the given graph.
     * Expects the path of a .dot file as the first argument,
     * and two nodes in the graph as the second and third.
     * 
     * @param args path to a .dot file, node, node
     */
    public static void main( String[] args ) {
    	if (args.length != 3) {
    		System.out.println("Incorrect number of arguments. Consult the readme for input details.");
    		return;
    	}
    	
        String file = args[0];
        String person1 = args[1];
        String person2 = args[2];
        
        
        Graph<String, DefaultEdge> familyTree = getFamilyTree(file);
        if (familyTree != null) {
        	if (!((familyTree.containsVertex(person1)) && (familyTree.containsVertex(person2)))) {
        		System.out.println("One or more of the specified nodes were not found in the graph.");
        		return;
        	}
        	
        	NaiveLcaFinder<String, DefaultEdge> lca = new NaiveLcaFinder<>(familyTree);
        	for (String p : lca.findLcas(person1, person2)) {
        		System.out.println(p);
        	}	
        }
    }
}
