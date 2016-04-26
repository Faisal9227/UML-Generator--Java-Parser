package org.umlparser.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.umlparser.module.Classlist;
import org.umlparser.module.Dependencies;
import org.umlparser.module.Functionlist;
import org.umlparser.module.InterfaceList;
import org.umlparser.module.Usesdep;
import org.umlparser.module.Varlist;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class Extract {
	File file;
	String classname;
	File[] listOfFiles;
	List<Classlist> cl = new ArrayList<Classlist>();
	List<Varlist> vl = new ArrayList<Varlist>();
	List<InterfaceList> il = new ArrayList<InterfaceList>();
	List<Functionlist> fl = new ArrayList<Functionlist>();
	List<Usesdep> udl = new ArrayList<Usesdep>();
	Classlist cltemp = new Classlist();
	Varlist vltemp = new Varlist();
	List<String> vrlist = new ArrayList<String>();
	List<String> clist = new ArrayList<String>();
	InterfaceList iltemp = new InterfaceList();
	Functionlist fltemp = new Functionlist();
	String dependencies = "";
	String uselist = "";
	String grammer = "@startuml\n";
	List<String> conbody = new ArrayList<String>();
	List<Dependencies> dl = new ArrayList<Dependencies>();
	String uses;
	MethodDeclaration functionbody;

	Extract() {
	}

	Extract(String filename,String savepath) throws Exception {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		};
		File folder = new File(filename);
		listOfFiles = folder.listFiles(filter);
		for (int i = 0; i < listOfFiles.length; i++) {
			file = listOfFiles[i];
			CompilationUnit cu;
			cu = JavaParser.parse(file);
			new COrI().visit(cu, null);
		}
		for (int i = 0; i < listOfFiles.length; i++) {
			file = listOfFiles[i];
			CompilationUnit cu;
			cu = JavaParser.parse(file);
			new COrI().visit(cu, null);
			new ClassOrInterfaceVisitor().visit(cu, null);
		}
		for (int j = 0; j < cl.size(); j++) {
			classprinter(cl.get(j));
			varprinter(cl.get(j).getName(), vl);
			funcprinter(cl.get(j).getName(), fl);
			grammer = grammer + "\n}\n";
		}
		dependencyadjusted();
		usesanddepadjusted();
		for (int i = 0; i < dl.size(); i++) {
			if (!dl.contains(dl.get(i).getData(cl))) {
				dependencies = dependencies + dl.get(i).getData(cl);
			}
		}
		for (int j = 0; j < cl.size(); j++) {
			dependencyprinter(cl.get(j));
		}
		for (int j = 0; j < udl.size(); j++) {
			if (!uselist.contains(udl.get(j).getData(cl))) {
				uselist = uselist + udl.get(j).getData(cl);
			}
		}
		grammer = grammer + dependencies + "\n" + uselist + "\n@enduml";
		StringBuilder plantUmlSource = new StringBuilder();
		plantUmlSource.append(grammer);
		SourceStringReader reader = new SourceStringReader(plantUmlSource.toString());
		FileOutputStream output = new FileOutputStream(new File(filename+"/"+savepath+".png"));
		reader.generateImage(output, new FileFormatOption(FileFormat.PNG, false));

	}

	private void usesanddepadjusted() {

		for (int i = 0; i < udl.size(); i++) {
			if (udl.get(i).getClassname().equals(udl.get(i).getDestclass())) {
				udl.remove(i);
				i = i - 1;
			}
		}
		for (int i = 0; i < udl.size(); i++) {
			for (int j = i + 1; j < udl.size(); j++) {
				if (udl.get(i).getClassname().equals(udl.get(j).getClassname())
						&& udl.get(i).getDestclass().equals(udl.get(j).getDestclass())) {
					udl.remove(j);
					j = j - 1;
				}
			}
		}
//		for (int i = 0; i < dl.size(); i++) {
//			for (int j = 0; j < udl.size(); j++) {
//				if (dl.get(i).getClassname().equals(udl.get(j).getClassname())
//						&& dl.get(i).getDestclass().equals(udl.get(j).getDestclass())) {
//					udl.remove(j);
//					j = j - 1;
//				} else if (dl.get(i).getClassname().equals(udl.get(j).getDestclass())
//						&& dl.get(i).getDestclass().equals(udl.get(j).getClassname())) {
//					udl.remove(j);
//					j = j - 1;
//				}
//			}
//		}

	}

	private void classprinter(Classlist cl) {
		if (cl.getType()) {
			grammer = grammer + "interface " + cl.getName() + " {";
		} else {
			grammer = grammer + "class " + cl.getName() + " {";
		}
	}

	private void dependencyadjusted() {
		String source;
		String dest;
		int temp = dl.size();
		for (int i = 0; i < dl.size(); i++) {
			for (int j = i + 1; j < dl.size(); j++) {
				if (dl.get(i).getClassname().equals(dl.get(j).getClassname())
						&& dl.get(i).getDestclass().equals(dl.get(j).getDestclass())) {
					if (dl.get(i).getSource().equals(dl.get(j).getSource())
							&& dl.get(i).getDest().equals(dl.get(j).getDest()) && dl.get(j).getDest().equals("1")) {
						dl.remove(i);
						dl.add(i, new Dependencies(dl.get(i).getClassname(), dl.get(i).getSource(),
								dl.get(i).getDestclass(), "4"));
						dl.remove(j);
						j = j - 1;
					}
				}
			}
		}
		for (int k = 0; k < dl.size(); k++) {
			for (int i = 0; i < dl.size(); i++) {
				for (int j = 0; j < dl.size(); j++) {
					if (dl.get(i).getClassname().equals(dl.get(j).getDestclass())
							&& dl.get(i).getDestclass().equals(dl.get(j).getClassname())) {
						if (dl.get(i).getSource().equals("4") || dl.get(j).getDest().equals("4")) {
							source = "4";
						} else {
							source = "1";
						}
						if (dl.get(j).getSource().equals("4") || dl.get(i).getDest().equals("4")) {
							dest = "4";
						} else {
							dest = "1";
						}
						dl.add(new Dependencies(dl.get(i).getClassname(), source, dl.get(i).getDestclass(), dest));
						dl.remove(j);
						temp = temp - 1;
						dl.remove(i);
						temp = temp - 1;
						break;
					}
				}
			}
		}
//		for (int i = 0; i < cl.size(); i++) {
//			for (int j = 0; j < dl.size(); j++) {
//				if (cl.get(i).getImplementslist() != null) {
//					if (cl.get(i).getName().equals(dl.get(j).getClassname())
//							&& cl.get(i).getImplementslist().toString().contains(dl.get(j).getDestclass())) {
//						dl.remove(j);
//						j = j - 1;
//					} else if (cl.get(i).getName().equals(dl.get(j).getDestclass())
//							&& cl.get(i).getImplementslist().toString().contains(dl.get(j).getClassname())) {
//						dl.remove(j);
//						j = j - 1;
//
//					}
//				}
//			}
//		}
	}

	private String bracketremover(String targetstr) {
		if (targetstr.contains("=")) {
			targetstr = targetstr.replace("[", "").replace("]", "").substring(0, targetstr.indexOf("=") - 1);
		} else {
			targetstr = targetstr.replace("[", "").replace("]", "");
		}
		return targetstr;
	}

	private void varprinter(String cl, List<Varlist> vl) {
		for (int i = 0; i < vl.size(); i++) {
			if (vl.get(i).getClassname() == cl) {
				if (vl.get(i).getModifier().equals("+") || vl.get(i).getModifier().equals("-")) {
					grammer = grammer + "\n" + vl.get(i).getModifier() + " "
							+ bracketremover(vl.get(i).getVariablename().toString()) + " : " + vl.get(i).getType();
				}
			}
		}
	}

	private void funcprinter(String cl, List<Functionlist> fl) {
		
		for (int i = 0; i < fl.size(); i++) {
			if (fl.get(i).getClassname() == cl && fl.get(i).getModifier()!="-") {
				if (fl.get(i).getParameter() != null) {
					grammer = grammer + "\n" + fl.get(i).getModifier() + " " + fl.get(i).getFunctionname() + "("
							+ bracketremover(parameterprinter(fl.get(i).getParameter())) + ") ";
					if (!fl.get(i).getType().equals(" ")) {
						grammer = grammer + ": " + fl.get(i).getType();
					}
				} else {
					grammer = grammer + "\n" + fl.get(i).getModifier() + " " + fl.get(i).getFunctionname() + "() ";
					if (!fl.get(i).getType().equals(" ")) {
						grammer = grammer + ": " + fl.get(i).getType();
					}
				}
			}
		}
	}

	private String parameterprinter(List<Parameter> parameter) {
		String temp = "";
		for (int i = 0; i < parameter.size(); i++) {
			temp = temp + parameter.get(i).getId() + " : " + parameter.get(i).getType();
			if (i < (parameter.size() - 1)) {
				temp = temp + ",";
			}
		}
		return temp;
	}

	private void dependencyprinter(Classlist cl) {
		if (cl.getExtendslist() != null)
			grammer = grammer + "\nclass " + cl.getName() + " extends "
					+ bracketremover(cl.getExtendslist().toString());
		if (cl.getImplementslist() != null)
			grammer = grammer + "\nclass " + cl.getName() + " implements "
					+ bracketremover(cl.getImplementslist().toString());
	}

	private class VarVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(FieldDeclaration vn, Object arg) {
			for (int i = 0; i < vn.getVariables().size(); i++) {
				vrlist.add(bracketremover(vn.getVariables().get(i).toString()));
			}
			if (clist.contains(
					vn.getType().toString().substring(vn.getType().toString().indexOf("<") + 1).replace(">", ""))) {
				Dependencies tempdep = new Dependencies(classname, "1",
						vn.getType().toString().substring(vn.getType().toString().indexOf("<") + 1).replace(">", ""),
						"4");
				if (!dl.contains(tempdep)) {
					if (vn.getType().toString().contains("<") || vn.getType().toString().contains("[")) {
						dl.add(new Dependencies(classname, "1", vn.getType().toString()
								.substring(vn.getType().toString().indexOf("<") + 1).replace(">", ""), "4"));
					} else {
						dl.add(new Dependencies(
								classname, "1", vn.getType().toString()
										.substring(vn.getType().toString().indexOf("<") + 1).replace(">", ""),
								String.valueOf(vn.getVariables().size())));

					}
				}

			} else {
				vl.add(new Varlist(classname, vn.getVariables(), vn.getType(), vn.getModifiers()));
			}
		}
	}

	private class MethodsVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration fn, Object arg) {
			if (fn.getName().startsWith("get") || fn.getName().startsWith("set")) {
				if (vrlist.contains(
						bracketremover(fn.getName().toString().replace("get", "").replace("set", "").toLowerCase())
								.replace(" ", ""))) {
				} else {
					fl.add(new Functionlist(classname, fn.getName(), fn.getParameters(), fn.getType().toString(),
							fn.getModifiers()));
					if (fn.getBody() != null) {
						if (fn.getBody().getStmts() != null) {
							for (int i = 0; i < fn.getBody().getStmts().size(); i++) {
								if (fn.getBody().getStmts().get(i).toString().contains("new")) {
									String temp = fn.getBody().getStmts().get(i).toString().substring(0,
											fn.getBody().getStmts().get(i).toString().indexOf(" "));
									for (int j = 0; j < clist.size(); j++) {
										if (temp.contains(clist.get(j))) {
											udl.add(new Usesdep(classname, clist.get(j)));
										}
									}
								} else {
									for (int j = 0; j < clist.size(); j++) {
										if (fn.getBody().getStmts().get(i).toString().contains(clist.get(j))) {
											udl.add(new Usesdep(classname, clist.get(i)));
										}
									}
								}
							}
						}
					}
					if (fn.getParameters() != null) {
						for (int i = 0; i < fn.getParameters().size(); i++) {
							if (clist.contains(fn.getParameters().get(i).getType().toString())) {
								int temp = clist.indexOf(fn.getParameters().get(i).getType().toString());
								udl.add(new Usesdep(classname, clist.get(temp)));
							}
						}
					}
				}
			} else {
				fl.add(new Functionlist(classname, fn.getName(), fn.getParameters(), fn.getType().toString(),
						fn.getModifiers()));
				if (fn.getBody() != null) {
					if (fn.getBody().getStmts() != null) {
						for (int i = 0; i < fn.getBody().getStmts().size(); i++) {
							if (fn.getBody().getStmts().get(i).toString().contains("new")) {
								String temp = fn.getBody().getStmts().get(i).toString().substring(0,
										fn.getBody().getStmts().get(i).toString().indexOf(" "));
								for (int j = 0; j < clist.size(); j++) {
									if (temp.contains(clist.get(j))) {
										udl.add(new Usesdep(classname, clist.get(j)));
									}
								}
							} else {
								for (int j = 0; j < clist.size(); j++) {
									if (fn.getBody().getStmts().get(i).toString().contains(clist.get(j))) {
										udl.add(new Usesdep(classname, clist.get(i)));
									}
								}
							}
						}
					}
				}

				if (fn.getParameters() != null) {
					for (int i = 0; i < fn.getParameters().size(); i++) {
						if (clist.contains(fn.getParameters().get(i).getType().toString())) {
							int temp = clist.indexOf(fn.getParameters().get(i).getType().toString());
							udl.add(new Usesdep(classname, clist.get(temp)));
						}
					}
				}
			}
		}
	}

	private class ClassOrInterfaceVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(ClassOrInterfaceDeclaration cn, Object arg) {
			classname = cn.getName().toString();
			cl.add(new Classlist(cn.getName().toString(), cn.getExtends(), cn.getImplements(), cn.isInterface()));
			new VarVisitor().visit(cn, null);
			new ConstructorVisitor().visit(cn, null);
			new MethodsVisitor().visit(cn, null);
		}
	}

	
	private class ConstructorVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(ConstructorDeclaration cn, Object arg) {
			fl.add(new Functionlist(classname, cn.getName(), cn.getParameters(), " ", cn.getModifiers()));
			if (cn.getBlock() != null) {
				if (cn.getBlock().getStmts() != null) {
					for (int i = 0; i < cn.getBlock().getStmts().size(); i++) {
						if (cn.getBlock().getStmts().get(i).toString().contains("new")) {
							String temp = cn.getBlock().getStmts().get(i).toString().substring(0,
									cn.getBlock().getStmts().get(i).toString().indexOf(" "));
							for (int j = 0; j < clist.size(); j++) {
								if (temp.contains(clist.get(j))) {
									udl.add(new Usesdep(classname, clist.get(j)));
								}
							}
						} else {
							for (int j = 0; j < clist.size(); j++) {
								if (cn.getBlock().getStmts().get(i).toString().contains(clist.get(i))) {
									udl.add(new Usesdep(classname, clist.get(j)));
								}
							}
						}
					}
				}
			}
			if (cn.getParameters() != null) {
				for (int i = 0; i < cn.getParameters().size(); i++) {
					if (clist.contains(cn.getParameters().get(i).getType().toString())) {
						int temp = clist.indexOf(cn.getParameters().get(i).getType().toString());
						udl.add(new Usesdep(classname, clist.get(temp)));
					}
				}
			}
		}
	}

	private class COrI extends VoidVisitorAdapter {

		@Override
		public void visit(ClassOrInterfaceDeclaration cn, Object arg) {
			classname = cn.getName().toString();
			clist.add(classname);
			classname = "";
		}
	}
}
