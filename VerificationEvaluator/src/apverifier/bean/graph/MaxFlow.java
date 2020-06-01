package apverifier.bean.graph;

import java.util.ArrayList;

public class MaxFlow{
	public int maxV = Integer.MAX_VALUE;
	public int[][] capacity;//ͳ�Ƹ���ͼǰ��ߺͺ����ʣ������
	public int[][] graph;//���ڼ�¼��ǰͼ
	public int[] flow;//����ͳ�ƴ�Դ�㵽ͼ������һ����������ӵ�����
	public int[] pre;//���ڼ�¼��ǰ���ﶥ���ǰ�����
	public int result = 0;
	public int nodenumber = 0;
	public ArrayList<Vertex> nodelist;
	public ArrayList<Edge> edgelist;
	public Vertex Svertex;
	public Vertex Dvertex;
	public int Snodeindex;
	public int Dnodeindex;
	
	public MaxFlow(){
		
	}
	
	public MaxFlow(ArrayList<Vertex> nodelist, ArrayList<Edge> edgelist) {
	//public MaxFlow(DefaultDirectedWeightedGraph<Vertex,Edge> inputgraph){
		this.nodelist = nodelist;
		this.edgelist = edgelist;
		nodenumber = nodelist.size();
		capacity = new int[nodenumber][nodenumber];
		graph = new int[nodenumber][nodenumber];
		flow = new int[nodenumber];
		pre = new int[nodenumber];
		
		for(int i = 0; i<edgelist.size();i++){
			int Sindex = nodelist.indexOf(edgelist.get(i).getSrc());
			int Dindex = nodelist.indexOf(edgelist.get(i).getDst());
			if((Sindex==-1)||(Dindex==-1))
			{
				continue;
			}
			if(edgelist.get(i).getWeight()>10000)
			{
				capacity[Sindex][Dindex] = 10000;
				graph[Sindex][Dindex] = 10000;
			}else{
				capacity[Sindex][Dindex] = (int) (edgelist.get(i).getWeight());
				graph[Sindex][Dindex] = (int)(edgelist.get(i).getWeight());
			}
		}
	}
	
	public int bfs(int source, int dest, int weight) {  //ʹ��BFS������Ѱ�Ҹ���ͼ������·��
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(source);      //Դ��Ϊ����source
        for(int i = 0;i < nodenumber;i++) {
            pre[i] = -1;   //��ʼ�����ж����ǰ������Ϊ-1
        }
        pre[source] = source;     //Դ���ǰ�������趨Ϊ�Լ�
        flow[source] = maxV; //Դ���ǰ�����㵽Դ������������趨Ϊ�����
        while(!list.isEmpty()) {
            int index = list.get(0);
            list.remove(0);
            if(index == dest)
                break;
            for(int i = 0;i < capacity.length;i++) {
                if(capacity[index][i] > 0 && pre[i] == -1) {//������iδ�������ҵ��ﶥ��i��ʣ������ʱ
                    pre[i] = index;  //����i��ǰ������Ϊindex
                    flow[i] = Math.min(flow[index],Math.min( capacity[index][i],weight));
                    list.add(i);
                }
            }
        }
        if(pre[dest] != -1)
            return flow[dest];
        return -1;
    }
    
    public void getResult(Vertex source, Vertex dest){
    	Svertex = source;
    	Dvertex = dest;
    	Snodeindex = nodelist.indexOf(Svertex);
    	Dnodeindex = nodelist.indexOf(Dvertex);
    	result += getResult(nodelist.indexOf(source),nodelist.indexOf(dest),maxV);
    }
    
    public int getResult(int source, int dest,int weight) {
    	int tempresult = 0;
        int temp = bfs(source,dest,weight);
        while((temp != -1)&&(weight!=0)) {
            tempresult = tempresult + temp;
            int start = pre[dest];
            int end = dest;
            while(start != source) {
                capacity[start][end] -= temp;   //ǰ���ʣ����������temp
                capacity[end][start] += temp;   //�����ʣ����������temp
                end = start;
                start = pre[end];
            }
            capacity[source][end] -= temp;
            capacity[end][source] += temp;
            weight -= temp;
            temp = bfs(source,dest,weight);
        }
        //System.out.println(result);
        //System.out.println(Svertex);
        //System.out.println(Dvertex);
        return tempresult;
    }
    public static void main(String args[]) {
    	ArrayList<Vertex> nodeList = new ArrayList<Vertex>();
    	ArrayList<Edge> edgeList = new ArrayList<Edge>();
    	nodeList.add(new Vertex("S"));
    	nodeList.add(new Vertex("A"));
    	nodeList.add(new Vertex("B"));
    	nodeList.add(new Vertex("C"));
    	nodeList.add(new Vertex("D"));
    	nodeList.add(new Vertex("T"));
    	edgeList.add(new Edge(nodeList.get(0),nodeList.get(1),3));
    	edgeList.add(new Edge(nodeList.get(0),nodeList.get(2),4));
    	edgeList.add(new Edge(nodeList.get(1),nodeList.get(2),5));
    	edgeList.add(new Edge(nodeList.get(1),nodeList.get(3),1));
    	edgeList.add(new Edge(nodeList.get(1),nodeList.get(4),3));
    	edgeList.add(new Edge(nodeList.get(2),nodeList.get(4),6));
    	edgeList.add(new Edge(nodeList.get(3),nodeList.get(2),1));
    	edgeList.add(new Edge(nodeList.get(3),nodeList.get(5),3));
    	edgeList.add(new Edge(nodeList.get(4),nodeList.get(5),4));
    	MaxFlow maxFlow = new MaxFlow(nodeList,edgeList);
    	maxFlow.getResult(nodeList.get(0), nodeList.get(5));
    	System.out.print(maxFlow.result);
    }
}