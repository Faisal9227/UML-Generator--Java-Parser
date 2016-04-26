package org.umlparser.module;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;

public class Varlist {
	String classname;
	List<VariableDeclarator> variablename=new ArrayList<VariableDeclarator>();
	Type type;
	int modifier;
	public Varlist(){
		
	}
	public Varlist(String classname, List<VariableDeclarator> variablename, Type type,int modifier){
			this.classname=classname;
			this.variablename=variablename;
			this.type=type;
			this.modifier=modifier;
		
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public List<VariableDeclarator> getVariablename() {
		return variablename;
	}
	public void setVariablename(List<VariableDeclarator> list) {
		this.variablename=variablename;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type2) {
		this.type = type2;
	}
	public String getModifier() {
		switch(modifier)
		{
		case 0: return "~";
		case 1: return "+";
		case 2: return "-";
		case 4: return "#";
		}
		return "+";
	}
	public void setModifier(int i) {
		this.modifier = i;
	}
	
}
