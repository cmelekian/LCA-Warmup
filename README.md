# LCA Warmup
Short exercise to try out JGraphT. Given a graph (in .dot format) and two nodes, computes their lowest common ancestor. Given the input, I suppose we can flavor it as searching a family tree for the most recent common ancestors.

##Installation
Navigate to the directory and run
```
mvn package
```
This will produce an executable jar containing all dependencies.

##Functionality
Takes three arguments, in order, on the command line:

* The path of a file in .dot format containing the graph to be searched. If the graph contains any cycles of length 2, they will be removed from the input since there is no possible valid ancestral relation.
In the case of cycles of length 3 or more, produces no output. Accordingly, it may not be suitable for the family trees of ancient Romans.

* Two names of nodes in the graph that will have their most recent ancestors computed.

If an appropriate graph was sucessfully imported, all most recent common ancestors will be printed. Otherwise, a "helpful" error message will be printed.
