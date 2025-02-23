package tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.* ;

public class GenerateAst {
    public static void main(String[]args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java GenerateAst <output-directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        List<String> list = Arrays.asList(
                "Assign     :Token name,Expr value",
                "Binary     :Expr left,Token operator,Expr right" ,
                "Logical    :Expr left,Token operator,Expr right",
                "Call       :Expr callee,Token paren,List<Expr> arguments",
                "Get        :Expr object,Token name",
                "Set        :Expr object,Token name,Expr value",
                "This       :Token keyword" ,
                "Grouping   :Expr expression" ,
                "Literal    :Object value" ,
                "Unary      :Token operator,Expr right",
                "Variable   :Token name"
        ) ;
        List<String> statementList = Arrays.asList(
                "Block      :List<Stmt> statements",
                "Expression :Expr expression" ,
                "Function   :Token name,List<Token> params,List<Stmt> body",
                "Class      :Token name,List<Stmt.Function> methods",
                "If         :Expr condition,Stmt thenBranch,Stmt elseBranch",
                "Return     :Token keyword,Expr value",
                "While      :Expr condition,Stmt body",
                "Print      :Expr expression",
                "Var        :Token name,Expr initializer"
        );
        defineAst(outputDir , "Expr" , list) ;
        defineAst(outputDir , "Stmt" , statementList);
    }
    @SuppressWarnings({"unused"})
    private static void defineAst(String outputDir ,  String baseName , List<String> types)  {
        String path =  outputDir + File.separator + baseName + ".java";
        try {
            PrintWriter writer = new PrintWriter(path , StandardCharsets.UTF_8);
            writer.println("package org.lox;") ;
            writer.println();
            writer.println("import java.util.*;") ;
            writer.println() ;
            writer.println("abstract class " + baseName + " {") ;
            defineVisitor(writer , baseName , types) ;
            for(String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer , baseName ,className , fields);
            }
            writer.println() ;
            writer.println("    abstract <R> R accept(Visitor<R> visitor);" );

            writer.println("}") ;
            writer.close();
        }catch(IOException e) {
            System.out.println(e + "I GIVE UP");
        }
    }
    private static void defineType(PrintWriter writer , String baseName , String className , String fields) throws IOException {
        writer.println("    static class " + className + " extends " + baseName + " {");
        writer.println("    " + className + "(" + fields + ") {") ;

        String[] fieldList = fields.split(",") ;
        for(String field : fieldList) {
            String fieldName = field.split(" ")[1];
            writer.println("    this." + fieldName + " = " + fieldName + ";");
        }
        writer.println("    }");
        writer.println();

        
        writer.println("   @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("    return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");
        writer.println(" ");
        for(String field : fieldList) {
            writer.println("    final " + field + ";");
        }
        writer.println("}");
    }
    private static void defineVisitor(PrintWriter writer , String baseName , List<String> types) throws IOException {
        writer.println("    interface Visitor<R> {") ;
        for(String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
    }

}
