package org.umlparser.module;

import java.util.List;

public class Usesdep {
	String classname;
	String destclass;
	int inter;
	int classs=0;
	public Usesdep(){
		
	}
	public Usesdep(String clasname, String destclass) {
		super();
		this.classname = clasname;
		this.destclass = destclass;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String clasname) {
		this.classname = clasname;
	}
	public String getDestclass() {
		return destclass;
	}
	public void setDestclass(String destclass) {
		this.destclass = destclass;
	}
	public String getData(List<Classlist> cl){;
	String temp="";
	inter=0;
	for(int i=0;i<cl.size();i++){
		if(cl.get(i).getName().equals(classname)){
			if(cl.get(i).getType()){
				temp="interface "+classname+" \"uses\"  ..> ";
				inter=1;
				classs=0;
			}
			else{
				temp="class "+classname+" \"uses\" ..> ";
				inter=0;
				classs=1;
			}					
			break;
		}
	}
	for(int i=0;i<cl.size();i++){
		if(cl.get(i).getName().equals(destclass)){
			if(cl.get(i).getType()){
				temp= temp + "interface "+destclass+"\n";
				inter=inter+1;
				classs=0;
			}
			else{
				temp= temp + "class "+ destclass+"\n";
				inter=0;
				classs=classs+1;
			}
			break;
		}
	}
		if(inter==2){
			inter=0;
			return "";
		}
		if(classs==2){
			classs=0;
			return "";
		}
	return temp;
	}				

}
