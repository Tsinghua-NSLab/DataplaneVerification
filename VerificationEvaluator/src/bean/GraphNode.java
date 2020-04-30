package bean;

import java.util.ArrayList;

public class GraphNode{
	int portID;
	public ArrayList<GraphNode> adjacent;
	
	public GraphNode(int portIP) {
		this.portID = portID;
	}
	
	/*public static void findPath(ArrayList<graphNode> graph, graphNode src, graphNode dst) {
		ArrayList<graphNode> path = new ArrayList<graphNode>();
		path.add(src);
		findPath(graph, path, dst);
	}
	
	public static void findPath(ArrayList<graphNode> graph, ArrayList<graphNode> path, graphNode dst) {
		graphNode last = path.get(path.size()-1);
		for(int i = 0; i < last.adjacent.size(); i++) {
			if(!path.contains(last.adjacent.get(i))) {
				ArrayList<graphNode> newPath = new ArrayList<graphNode>();
				newPath.addAll(path);
				newPath.add(last.adjacent.get(i));
				if(last.adjacent.get(i).equals(dst)) {
					printPath(newPath);
				}else {
					findPath(graph,newPath,dst);
				}
			}
		}
	}
	public static void printPath(ArrayList<graphNode> path) {
		System.out.print(path.get(0).getName());
		for(graphNode node: path) {
			System.out.print("->"+node.getName());
		}
		System.out.println();
	}*/
	
	
	
	public ArrayList<GraphNode> getAdjacent() {
		return adjacent;
	}
	public int getPortID() {
		return portID;
	}

	public void setPortID(int portID) {
		this.portID = portID;
	}

	public void setAdjacent(ArrayList<GraphNode> adjacent) {
		this.adjacent = adjacent;
	}
	
}