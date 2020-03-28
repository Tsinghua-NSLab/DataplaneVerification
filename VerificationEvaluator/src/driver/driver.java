package driver;

import java.util.ArrayList;

import bean.Link;
import bean.Network;

public class driver{
	public void init() {
		Network stanfordBackbone = new Network();	
//		stanfordBackbone.getRouters().add("bbra_rtr");
//		stanfordBackbone.getRouters().add("bbrb_rtr");
//		stanfordBackbone.getRouters().add("boza_rtr");
//		stanfordBackbone.getRouters().add("bozb_rtr");
//		stanfordBackbone.getRouters().add("coza_rtr");
//		stanfordBackbone.getRouters().add("cozb_rtr");
//		stanfordBackbone.getRouters().add("goza_rtr");
//		stanfordBackbone.getRouters().add("gozb_rtr");
//		stanfordBackbone.getRouters().add("poza_rtr");
//		stanfordBackbone.getRouters().add("pozb_rtr");
//		stanfordBackbone.getRouters().add("roza_rtr");
//		stanfordBackbone.getRouters().add("rozb_rtr");
//		stanfordBackbone.getRouters().add("soza_rtr");
//		stanfordBackbone.getRouters().add("sozb_rtr");
//		stanfordBackbone.getRouters().add("yoza_rtr");
//		stanfordBackbone.getRouters().add("yozb_rtr");
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te7/3","goza_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te7/3","pozb_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te1/3","bozb_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te1/3","yozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te1/3","roza_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te1/4","boza_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te1/4","rozb_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te6/1","gozb_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te6/1","cozb_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te6/1","poza_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te6/1","soza_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te7/2","coza_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te7/2","sozb_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te6/3","yoza_rtr","te1/3"));
		stanfordBackbone.getLinks().add(new Link("bbra_rtr","te7/1","bbrb_rtr","te7/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te7/4","yoza_rtr","te7/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te1/1","goza_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te1/1","pozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te6/3","bozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te6/3","roza_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te6/3","yozb_rtr","te1/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te1/3","boza_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te1/3","rozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te7/2","gozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te7/2","cozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te7/2","poza_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te7/2","soza_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te6/1","coza_rtr","te3/1"));
		stanfordBackbone.getLinks().add(new Link("bbrb_rtr","te6/1","sozb_rtr","te2/1"));
		stanfordBackbone.getLinks().add(new Link("boza_rtr","te2/3","bozb_rtr","te2/3"));
		stanfordBackbone.getLinks().add(new Link("coza_rtr","te2/3","cozb_rtr","te2/3"));
		stanfordBackbone.getLinks().add(new Link("goza_rtr","te2/3","gozb_rtr","te2/3"));
		stanfordBackbone.getLinks().add(new Link("poza_rtr","te2/3","pozb_rtr","te2/3"));
        stanfordBackbone.getLinks().add(new Link("roza_rtr","te2/3","rozb_rtr","te2/3"));
        stanfordBackbone.getLinks().add(new Link("soza_rtr","te2/3","sozb_rtr","te2/3"));
        stanfordBackbone.getLinks().add(new Link("yoza_rtr","te1/1","yozb_rtr","te1/3"));
        stanfordBackbone.getLinks().add(new Link("yoza_rtr","te1/2","yozb_rtr","te1/2"));
	}
}