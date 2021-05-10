/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.manticore.jsqlformatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.snt.inmemantlr.GenericParser;
import org.snt.inmemantlr.listener.DefaultTreeListener;
import org.snt.inmemantlr.tree.ParseTree;
import org.snt.inmemantlr.tree.ParseTreeNode;

/** @author are */
public class JavaTools {
  private static final Logger LOGGER = Logger.getLogger(JavaTools.class.getName());
	
	private static class LocalVariableDeclaration {
		public String label=null;
		public String typeString=null;
		public StringBuilder sqlBuilder = new StringBuilder();
		public ArrayList<String> parameters = new ArrayList<>();

	}

  public static void main(String[] args) {
    try {
      File lexerFile =
          new File(ClassLoader.getSystemResource("com/manticore/antlr/JavaLexer.g4").toURI());
      File parserFile =
          new File(ClassLoader.getSystemResource("com/manticore/antlr/JavaParser.g4").toURI());

//			String columnName="";
//			String tableName="";
//			
//			String test2 = new StringBuilder("SELECT ").append(columnName).append(" from ").append(tableName).append(";").toString();
			
      // String test = "SELECT " + columnName + " from " + tableName + ";";
      final String[] escaped = {
				"\"SELECT \" + columnName + \" from \" + noVariableAssigned + \";\";"
        , "String test = \"SELECT \" + columnName + \" from \" + tableName + \";\";"
			  , "String test2 = new StringBuilder(\"SELECT \").append(columnName).append(\" from \").append(tableName).append(\";\").toString();"
			  , "\"SELECT \" + columnName2 + \" from \" + noVariableAssigned2 + \";\";" 
				, "assertSqlCanBeParsedAndDeparsed(\"WITH split (word, str, hascomma) AS (VALUES ('', 'Auto,A,1234444', 1) UNION ALL SELECT substr(str, 0, CASE WHEN instr(str, ',') THEN instr(str, ',') ELSE length(str) + 1 END), ltrim(substr(str, instr(str, ',')), ','), instr(str, ',') FROM split WHERE hascomma) SELECT trim(word) FROM split WHERE word != ''\");"
      };

      final String className = "TMP" + System.currentTimeMillis();

      final StringBuilder source =
          new StringBuilder(100 + escaped.length * 20)
              .append("public class ")
              .append(className)
              .append("{\n")
              .append("\tpublic static void mock() {")
              .append("\t\t\n");
      for (String string : escaped) {
        source.append("\t\t").append(string).append("\n");
      }
      source.append("\t}\n}\n");

      DefaultTreeListener t = new DefaultTreeListener();
      GenericParser gp = new GenericParser(lexerFile, parserFile);
      gp.setListener(t);
      try {
        gp.compile();
        gp.writeAntlrAritfactsTo("/tmp/grammar");
        gp.store("/tmp/grammar/gp.out", true);

        System.out.println(source);

        ParseTree parseTree;
        gp.parse(source.toString(), GenericParser.CaseSensitiveType.NONE);
        parseTree = t.getParseTree();

        StringBuilder builder = new StringBuilder();
        int indent = 0;
				
				ArrayList<LocalVariableDeclaration> declarations = new ArrayList<>();

        ParseTreeNode root = parseTree.getRoot();
        append(builder, root, indent, declarations);
				
				for (LocalVariableDeclaration declaration:declarations) {
					if (declaration.label!=null && declaration.label.length()>1) {
						System.out.print(declaration.typeString);
						System.out.print(" ");
						System.out.print(declaration.label);
						System.out.print(" = ");
					}
					System.out.println(
            "String.format(\n"
                + JSQLFormatter.formatToJava(declaration.sqlBuilder.toString(), 8));
					
					for (int j=0; j< 8 - 2 ; j++) {
						System.out.print(" ");
				  }
					System.out.println(
                ", new Object[]{"
                + Arrays.deepToString(declaration.parameters.toArray(new String[declaration.parameters.size()]))
                + "});\n");
				}
      } catch (Exception ex) {
				Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
    } catch (URISyntaxException ex) {
      Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
	
	public static String formatJava(String javaCode, String... options) throws Exception {
		StringBuilder formatted = new StringBuilder();
		
      File lexerFile =
          new File(ClassLoader.getSystemResource("com/manticore/antlr/JavaLexer.g4").toURI());
      File parserFile =
          new File(ClassLoader.getSystemResource("com/manticore/antlr/JavaParser.g4").toURI());

      final String className = "TMP" + System.currentTimeMillis();

      final StringBuilder source =
          new StringBuilder()
              .append("public class ")
              .append(className)
              .append("{\n")
              .append("\tpublic static void mock() {")
              .append("\t\t\n");
      source.append("\t\t").append(javaCode).append("\n");
      source.append("\t}\n}\n");

      DefaultTreeListener t = new DefaultTreeListener();
      GenericParser gp = new GenericParser(lexerFile, parserFile);
      gp.setListener(t);
      try {
        gp.compile();
        gp.writeAntlrAritfactsTo("/tmp/grammar");
        gp.store("/tmp/grammar/gp.out", true);

        ParseTree parseTree;
        gp.parse(source.toString(), GenericParser.CaseSensitiveType.NONE);
        parseTree = t.getParseTree();

        StringBuilder builder = new StringBuilder();
        int indent = 0;
				
				ArrayList<LocalVariableDeclaration> declarations = new ArrayList<>();

        ParseTreeNode root = parseTree.getRoot();
        append(builder, root, indent, declarations);
				
				for (LocalVariableDeclaration declaration:declarations) {
					formatted.append("\n").append(JSQLFormatter.format(declaration.sqlBuilder.toString(),options));
					
				}
      } catch (Exception ex) {
				throw new Exception("Could not parse Java Code:\n" + javaCode, ex);
      }
		return formatted.toString();
  }

  private static void append(
      StringBuilder builder,
      ParseTreeNode p,
      int indent,
      ArrayList<LocalVariableDeclaration> declarations) {
    for (int i = 0; i < indent; i++) builder.append("    ");

    builder.append(p.toString()).append("\n");
		if (p.getRule().equalsIgnoreCase("blockStatement")) {
			declarations.add(new LocalVariableDeclaration());
			
		} else if (p.getRule().equalsIgnoreCase("localVariableDeclaration")) {
			declarations.get(declarations.size()-1).typeString = p.getChild(0).getLabel();
			
		} else if (p.getRule().equalsIgnoreCase("variableDeclaratorId")) {
			declarations.get(declarations.size()-1).label=p.getLabel();
			
		} else if (p.getRule().equalsIgnoreCase("primary")) {
			LocalVariableDeclaration declaration = declarations.get(declarations.size()-1);
      String label = p.getLabel();
      if (p.hasChildren()) {
        declaration.sqlBuilder.append(label.substring(1, label.length()-1)).append(" ");
      } else {
        declaration.sqlBuilder.append(" $").append(label);
        declaration.parameters.add(label);
      }
    }

    for (ParseTreeNode n : p.getChildren()) {
      append(builder, n, indent + 1, declarations);
    }
  }
}
