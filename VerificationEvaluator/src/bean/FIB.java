package bean;

import java.util.ArrayList;

import bean.basis.Ip;

public class FIB{
	Ip ip;
	String port;
	String fileName;
	int lineCounter;
	ArrayList<FIB> compressedFIBs;
	
	public FIB(Ip ip, String port, String fileName, int lineCounter) {
		super();
		this.ip = new Ip();
		this.ip.setIP(ip.getIP());
		this.ip.setSubnet(ip.getSubnet());
		this.port = port;
		this.fileName = fileName;
		this.lineCounter = lineCounter;
	}
	
	public FIB(Ip ip, String port, ArrayList<FIB> compressedFIBs) {
		this.ip = new Ip();
		this.ip.setIP(ip.getIP());
		this.ip.setSubnet(ip.getSubnet());
		this.port = port;
		this.compressedFIBs = compressedFIBs;//TODO check whether need to use addAll
	}
	
	public Ip getIP() {
		return ip;
	}
	public void setIP(Ip ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getLineCounter() {
		return lineCounter;
	}
	public void setLineCounter(int lineCounter) {
		this.lineCounter = lineCounter;
	}
}