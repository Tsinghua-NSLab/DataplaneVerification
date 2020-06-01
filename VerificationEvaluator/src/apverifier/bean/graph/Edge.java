package apverifier.bean.graph;

public class Edge{
	Vertex src = null;
	Vertex dst = null;
	int weight = 0;
	
	public Edge() {
	}
	public Edge(Vertex src, Vertex dst) {
		this.src = src;
		this.dst = dst;
	}
	public Edge(Vertex src, Vertex dst, int weight) {
		this.src = src;
		this.dst = dst;
		this.weight = weight;
	}
	public Vertex getSrc() {
		return src;
	}
	public void setSrc(Vertex src) {
		this.src = src;
	}
	public Vertex getDst() {
		return dst;
	}
	public void setDst(Vertex dst) {
		this.dst = dst;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}