package org.umlparser.module;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.Parameter;

public class Functionlist {
	String classname;
	String functionname;
	List<Parameter> parameter=new ArrayList<Parameter>();
	String type;
	int modifier;
	public Functionlist(){
		
	}
	public Functionlist(String classname,String functionname,List<Parameter> parameter,String type, int modifier){
		this.classname=classname;
		this.functionname=functionname;
		this.parameter=parameter;
		this.type=type;
		this.modifier=modifier;
	}
	
	public String getFunctionname() {
		return functionname;
	}
	public void setFunctionname(String functionname) {
		this.functionname = functionname;
	}
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public List<Parameter> getParameter() {
		return parameter;
	}
	public void setParameter(List<Parameter> parameter) {
		this.parameter=parameter;
	}
	public String getType() {
		return type;
	}
	public void setType(String parametertype) {
		this.type=type;
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
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

}
