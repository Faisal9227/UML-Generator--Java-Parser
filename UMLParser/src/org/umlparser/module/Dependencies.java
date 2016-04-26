package org.umlparser.module;

import java.util.List;

public class Dependencies {
	String classname;
	String source="0";
	String destclass;
	String dest="0";
	
	
	public Dependencies() {
		// TODO Auto-generated constructor stub
	}
	
	public Dependencies(String classname, String source, String destclass, String dest) {
		this.classname = classname;
		if(Integer.parseInt(source)>1){
			this.source= "*";
		}
		else{
			this.source = source;
		}
		this.destclass = destclass;
		if(Integer.parseInt(dest)>1){
			this.dest = "*";
		}
		else{
			this.dest = dest;
		}
	}

	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getSource() {
		if(source=="*"){
			return "4";
		}
		return source;
	}
	public void setSource(String source) {
		if(Integer.parseInt(source)>1){
			this.source= "*";
		}
		else{
			this.source = source;
		}
	}
	public String getDestclass() {
		return destclass;
	}
	public void setDestclass(String destclass) {
		this.destclass = destclass;
	}
	public String getDest() {
		if(dest=="*"){
			return "4";
		}
		return dest;
	}
	public void setDest(String dest) {
		if(Integer.parseInt(dest)>1){
			this.dest = "*";
		}
		else{
			this.dest = dest;
		}
	}
	public String getData(List<Classlist> cl){;
		String temp="";
		for(int i=0;i<cl.size();i++){
			if(cl.get(i).getName().equals(classname)){
				if(cl.get(i).getType()){
					temp="\ninterface "+classname+" \""+ source +"\" -- \""+ dest +"\" ";
				}
				else{
					temp="\nclass "+classname+" \""+ source +"\" -- \""+ dest +"\" ";
				}					
				break;
			}
		}
		for(int i=0;i<cl.size();i++){
			if(cl.get(i).getName().equals(destclass)){
				if(cl.get(i).getType()){
					temp= temp + "interface "+destclass+"\n";
				}
				else{
					temp= temp + "class "+ destclass+"\n";
				}
				break;
			}
		}
		return temp;
	}

}
