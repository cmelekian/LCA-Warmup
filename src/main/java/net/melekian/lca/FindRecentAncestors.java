package net.melekian.lca;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.buildVertex;
import java.util.*;
import java.io.*;

public class FindRecentAncestors {
    private FindRecentAncestors() {
    }
    
    private static Graph<String, DefaultEdge> getFamilyTree(String file) {
    	
    	VertexProvider<String> getName = new VertexProvider<String>() {
    		public String buildVertex(String id, Map<String, Attribute> attributes) {
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
    	}
    	catch (ImportException e) {
    		System.out.println("Could not import a graph from the specified file.");
    	}
    	finally {
    		try {
    			if (input != null) input.close();
    		}
    		catch (IOException e) {
    			System.out.println("Something bad happened.");
    		}
    	}
    	
    	/*
    	 * Eliminate marriages before returning
    	 */
    	for (String u : familyTree.vertexSet()) {
    		for (String v : familyTree.vertexSet()) {
    			if ((familyTree.containsEdge(u,v)) && (familyTree.containsEdge(v,u))) {
    				familyTree.removeEdge(u,v);
    				familyTree.removeEdge(v,u);
    			}
    		}
    		
    	}
    	
    	return familyTree;
    }
    
    public static void main( String[] args )
    {
        String file = args[0];
        String person1 = args[1];
        String person2 = args[2];
        Graph<String, DefaultEdge> familyTree = getFamilyTree(file);
        
        NaiveLcaFinder<String, DefaultEdge> lca = new NaiveLcaFinder<>(familyTree);
        for (String p : lca.findLcas(person1, person2)) {
        	System.out.println(p);
        }
    }
}
