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
    	
    	BufferedReader input = null;
    	try {
    		input = new BufferedReader(new FileReader(file));
    		imp.importGraph(familyTree, input);
    	}
    	catch (FileNotFoundException e){
    		System.out.println("Could not find the specified file.");
    		return null;
    	}
    	catch (ImportException e) {
    		System.out.println("Could not import a graph from the specified file.");
    		return null;
    	}
    	finally {
    		try {
    			if (input != null) input.close();
    		}
    		catch (IOException e) {
    			System.out.println("Something bad happened.");
    			return null;
    		}
    	}
    	
    	//Eliminate marriages since they don't reflect an ancestral relation
    	for (String u : familyTree.vertexSet()) {
    		for (String v : familyTree.vertexSet()) {
    			if ((familyTree.containsEdge(u,v)) && (familyTree.containsEdge(v,u))) {
    				familyTree.removeEdge(u,v);
    				familyTree.removeEdge(v,u);
    			}
    		}	
    	}
    	
    	//If the graph still contains cycles at this point we can't do anything to fix it
    	CycleDetector<String, DefaultEdge> C = new CycleDetector<>(familyTree);
    	if (C.detectCycles()) {
    		System.out.println("The graph contains a cycle.");
    		return null;
    	}
    	
    	return familyTree;
    }
    
    public static void main( String[] args ) {
        String file = args[0];
        String person1 = args[1];
        String person2 = args[2];
        Graph<String, DefaultEdge> familyTree = getFamilyTree(file);
        if (familyTree != null) {
        	NaiveLcaFinder<String, DefaultEdge> lca = new NaiveLcaFinder<>(familyTree);
        	for (String p : lca.findLcas(person1, person2)) {
        		System.out.println(p);
        	}	
        }
    }
}
