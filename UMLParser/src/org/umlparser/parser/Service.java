package org.umlparser.parser;

import java.io.File;

public class Service {

	public static void main(String[] args) throws Exception {
		    String filename = null;
		   // File file = new File(filename);
			String savepath = null;
				  filename = args[0];
				  savepath = args[1];
			Extract e =new Extract(filename,savepath);
	  }
}
