package driver;

import bean.Network;

public class driver{
	public void init() {
		Network stanfordBackbone = new Network();	
		stanfordBackbone.initStanford();
	}
	public static void main(String args[]) {
		driver test = new driver();
		test.init();
		System.out.println(test);
	}
}