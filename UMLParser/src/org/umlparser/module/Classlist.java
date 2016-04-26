package org.umlparser.module;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.type.ClassOrInterfaceType;

public class Classlist {
	String name;
	List<ClassOrInterfaceType> extendslist=new ArrayList<ClassOrInterfaceType>();
	List<ClassOrInterfaceType> implementslist=new ArrayList<ClassOrInterfaceType>();
	boolean type;
	public Classlist(String name, List<ClassOrInterfaceType> extendslist,List<ClassOrInterfaceType> implementlist,boolean type){
		this.name=name;
		this.extendslist=extendslist;
		this.implementslist=implementlist;
		this.type=type;
		
	}
	public Classlist(){
		
	}
	public boolean getType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ClassOrInterfaceType> getExtendslist() {
		return extendslist;
	}
	public void setExtendslist(List<ClassOrInterfaceType> extendslist) {
		this.extendslist = extendslist;
	}
	public List<ClassOrInterfaceType> getImplementslist() {
		return implementslist;
	}
	public void setImplementslist(List<ClassOrInterfaceType> implementslist) {
		this.implementslist = implementslist;
	}
}
