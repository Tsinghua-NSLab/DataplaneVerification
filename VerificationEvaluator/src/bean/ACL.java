package bean;

import java.io.Serializable;

public class ACL implements Serializable{
	boolean action = true; //false: deny, true: permit
	int src_ip = 0;
	int src_ip_mask = 32;
	int dst_ip = 0;
	int dst_ip_mask = 32;
	int ip_protocol = 0;
	int transport_src_begin = 0;
	int transport_src_end = 0xffff;
	int transport_dst_begin = 0;
	int transport_dst_end = 0xffff;
	int transport_ctrl_begin = 0;
	int transport_ctrl_end = 0xff;
	int line = -1;
	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public int getIp_protocol() {
		return ip_protocol;
	}

	public void setIp_protocol(int ip_protocol) {
		this.ip_protocol = ip_protocol;
	}

	public int getSrc_ip() {
		return src_ip;
	}

	public void setSrc_ip(int src_ip) {
		this.src_ip = src_ip;
	}

	public int getSrc_ip_mask() {
		return src_ip_mask;
	}

	public void setSrc_ip_mask(int src_ip_mask) {
		this.src_ip_mask = src_ip_mask;
	}

	public int getDst_ip() {
		return dst_ip;
	}

	public void setDst_ip(int dst_ip) {
		this.dst_ip = dst_ip;
	}

	public int getDst_ip_mask() {
		return dst_ip_mask;
	}

	public void setDst_ip_mask(int dst_ip_mask) {
		this.dst_ip_mask = dst_ip_mask;
	}

	public int getTransport_src_begin() {
		return transport_src_begin;
	}

	public void setTransport_src_begin(int transport_src_begin) {
		this.transport_src_begin = transport_src_begin;
	}

	public int getTransport_src_end() {
		return transport_src_end;
	}

	public void setTransport_src_end(int transport_src_end) {
		this.transport_src_end = transport_src_end;
	}

	public int getTransport_dst_begin() {
		return transport_dst_begin;
	}

	public void setTransport_dst_begin(int transport_dst_begin) {
		this.transport_dst_begin = transport_dst_begin;
	}

	public int getTransport_dst_end() {
		return transport_dst_end;
	}

	public void setTransport_dst_end(int transport_dst_end) {
		this.transport_dst_end = transport_dst_end;
	}

	public int getTransport_ctrl_begin() {
		return transport_ctrl_begin;
	}

	public void setTransport_ctrl_begin(int transport_ctrl_begin) {
		this.transport_ctrl_begin = transport_ctrl_begin;
	}

	public int getTransport_ctrl_end() {
		return transport_ctrl_end;
	}

	public void setTransport_ctrl_end(int transport_ctrl_end) {
		this.transport_ctrl_end = transport_ctrl_end;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
}