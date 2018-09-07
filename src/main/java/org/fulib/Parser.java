package org.fulib;

import de.uniks.networkparser.list.SimpleKeyValueList;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.sdmlib.CGUtil;
import org.sdmlib.StrUtil;
import org.sdmlib.codegen.LocalVarTableEntry;
import org.sdmlib.codegen.StatementEntry;
import org.sdmlib.codegen.SymTabEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Parser
{

   private static final String ANNOTATION = "annotation";

   public static final char EOF = Character.MIN_VALUE;

   public static final String VOID = "void";

   public static final String CLASS = "class";

   public static final String INTERFACE = "interface";

   public static final String ENUM = "enum";

   public static final char COMMENT_START = 'c';

   public static final String PACKAGE = "package";

   public static final char LONG_COMMENT_END = 'd';

   public static final String CONSTRUCTOR = "constructor";

   public static final String ATTRIBUTE = "attribute";

   public static final String ENUMVALUE = "enumvalue";

   public static final String METHOD = "method";

   public static final String METHOD_END = "methodEnd";

   public static final String IMPORT = "import";

   public static final String CLASS_BODY = "classBody";

   public static final String CLASS_END = "classEnd";

   public static final String NAME_TOKEN = "nameToken";

   public static final String LAST_RETURN_POS = "lastReturnPos";

   public static final String IMPLEMENTS = "implements";

   public static final String QUALIFIED_NAME = "qualifiedName";

   public static final String EXTENDS = "extends";

   public static char NEW_LINE = '\n';

   private StringBuilder fileBody = null;

   private boolean fileBodyHasChanged = false;

   public boolean isFileBodyChanged()
   {
      return fileBodyHasChanged;
   }

   private Token lookAheadToken = null;

   private Token currentToken;

   private char currentChar;

   private int index;

   private SimpleKeyValueList<String, SymTabEntry> symTab;
   private FileFragmentMap fragmentMap;


   public SimpleKeyValueList<String, SymTabEntry> getSymTab()
   {
      return symTab;
   }

   private LinkedHashMap<String, LocalVarTableEntry> localVarTable;

   private StatementEntry currentParentStatement;

   private StatementEntry currentStatement = null;


   public LinkedHashMap<String, LocalVarTableEntry> getLocalVarTable()
   {
      return localVarTable;
   }

   public HashMap<StatementEntry, Integer> getReturnStatements()
   {
      return returnStatements;
   }

   private boolean verbose = false;

   private int endPos;

   private char lookAheadChar;

   private int lookAheadIndex;

   private String searchString;

   private int methodBodyStartPos;

   private String classModifier;


   public static FileFragmentMap parse(String fileName)
   {
      return new Parser()
            .doParse(fileName);
   }

   public FileFragmentMap  doParse(String fileName)
   {
      this.setFileName(fileName);

      loadFile(fileName);

      if (fileBody != null)
      {
         indexOf(CLASS_END);
      }

      return fragmentMap;
   }

   private void loadFile(String fileName)
   {
      // load file
      fragmentMap = new FileFragmentMap(fileName);

      Path path = Paths.get(fileName);
      if (Files.exists(path))
      {
         try
         {
            byte[] bytes = Files.readAllBytes(path);
            this.withFileBody(new StringBuilder(new String(bytes)));
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }



   class SearchStringFoundException extends RuntimeException
   {
      private static final long serialVersionUID = 1L;
   }

   public Parser withFileBody(StringBuilder fileBody)
   {
      this.fileBody = fileBody;
      return this;
   }

   private Parser init(int startPos, int endPos)
   {
      symTab = new SimpleKeyValueList<String, SymTabEntry>();

      if (localVarTable == null)
      {
         localVarTable = new LinkedHashMap<String, LocalVarTableEntry>();
      }
      else
      {
         localVarTable.clear();
      }

      if (returnStatements == null)
      {
         returnStatements = new HashMap<>();
      }
      else
      {
         returnStatements.clear();
      }

      currentParentStatement = new StatementEntry();

      methodBodyQualifiedNames.clear();

      currentChar = 0;

      index = startPos - 1;
      lookAheadIndex = startPos - 1;
      lastFragmentEndPos = startPos - 1;

      this.endPos = endPos;

      nextChar();
      nextChar();

      currentToken = new Token();
      lookAheadToken = new Token();
      previousToken = new Token();

      nextToken();
      nextToken();

      currentRealToken = new Token();
      lookAheadRealToken = new Token();
      previousRealToken = new Token();

      nextRealToken();
      nextRealToken();

      return this;
   }


   public int indexOf(String searchString)
   {
      indexOfResult = -1;

      init(0, fileBody.length());

      this.searchString = searchString;

      try
      {
         parseFile();
      } catch (Parser.SearchStringFoundException e)
      {
         // found it, return indexOfResult
      } catch (Exception e)
      {
         // problem with parsing. Return not found
         e.printStackTrace();
      }

      return indexOfResult;
   }

   private void parseFile()
   {
      // [packagestat] importlist classlist
      if (currentRealTokenEquals(PACKAGE))
      {
         parsePackageDecl();
      }

      int startPos = currentRealToken.startPos;

      while (currentRealTokenEquals(IMPORT))
      {
         parseImport();
      }

      parseClassDecl();
   }

   private void parseClassDecl()
   {
      int preCommentStartPos = currentRealToken.preCommentStartPos;
      int preCommentEndPos = currentRealToken.preCommentEndPos;

      // FIXME skip all Annotations
      int startPosAnnotations = currentRealToken.startPos;
      while ("@".equals(currentRealWord()))
      {
         String annotation = parseAnnotations();
      }

      // modifiers class name classbody
      int startPosClazz = currentRealToken.startPos;
      classModifier = parseModifiers();

      // skip keyword
      // skip ("class");

      // class or interface or enum
      String classTyp = parseClassType();
      className = currentRealWord();

      // skip name
      nextRealToken();

      parseGenericTypeSpec();

      // extends
      if ("extends".equals(currentRealWord()))
      {
         int startPos = currentRealToken.startPos;

         skip("extends");

         // skip superclass name
         parseTypeRef();

      }

      // implements
      if ("implements".equals(currentRealWord()))
      {
         int startPos = currentRealToken.startPos;

         skip("implements");

         while (!currentRealKindEquals(EOF) && !currentRealKindEquals('{'))
         {
            // skip interface name
            nextRealToken();

            if (currentRealKindEquals(','))
            {
               nextRealToken();
            }
         }
      }

      addCodeFragment("class", startPosAnnotations, currentRealToken.endPos);

      parseClassBody();
   }

   private void parseGenericTypeSpec()
   {
      // genTypeSpec < T , T, ...>
      if (currentRealKindEquals('<'))
      {
         skipTo('>');

         nextRealToken();
      }
   }

   private String parseClassType()
   {
      classType ="";
      if (CLASS.equals(currentRealWord()))
      {
         classType = "class";
      }else if (INTERFACE.equals(currentRealWord()))
      {
         classType = INTERFACE;
      }
      else if (ENUM.equals(currentRealWord()))
      {
         classType = ENUM;
      }
      if(classType.isEmpty() == false) {
         skip(classType);
      }
      return classType;
   }

   private void parseClassBody()
   {
      // { classBodyDecl* }
      skip("{");

      while (!currentRealKindEquals(EOF) && !currentRealKindEquals('}'))
      {
         parseMemberDecl();
      }

      if (currentRealKindEquals('}'))
      {
         addCodeFragment(CLASS_END, currentRealToken.startPos, currentRealToken.endPos);
      }

      addCodeFragment("gap:", currentRealToken.endPos+1, fileBody.length()-1);

   }

   private void parseMemberDecl()
   {
      // annotations modifiers (genericsDecl) ( typeRef name [= expression ] |
      // typeRef name '(' params ')' | classdecl ) ;

      // (javadoc) comment?
      int preCommentStartPos = currentRealToken.preCommentStartPos;
      int preCommentEndPos = currentRealToken.preCommentEndPos;


      // annotations
      int annotationsStartPos = currentRealToken.startPos;

      String annotations = parseAnnotations();

      int startPos = currentRealToken.startPos;

      String modifiers = parseModifiers();

      if (currentRealTokenEquals("<"))
      {
         // generic type decl
         skip("<");
         while (!currentRealTokenEquals(">"))
         {
            nextRealToken();
         }
         skip(">");
      }

      if (currentRealTokenEquals(CLASS) || currentRealTokenEquals(INTERFACE))
      {
         // parse nested class
         // throw new RuntimeException("class " + className +
         // " has nested class. " + " Can't parse it.");
         // System.err.println("class " + fileName +
         // " has nested class in line " +
         // getLineIndexOf(currentRealToken.startPos) +
         // "  Can't parse it. Skip it.");
         while (!currentRealTokenEquals("{"))
         {
            nextRealToken();
         }
         skipBody();

         return;
         // if (currentRealTokenEquals("}"))
         // {
         // return;
         // }
         // modifiers = parseModifiers();
      }
      else if (currentRealTokenEquals(ENUM))
      {
         // skip enum name { entry, ... }
         skip(ENUM);
         nextRealToken(); // name
         skipBody();

         return;

         // if (currentRealTokenEquals("}"))
         // {
         // return;
         // }
         // modifiers = parseModifiers();
      }

      if (currentRealTokenEquals(className) && lookAheadRealToken.kind == '(')
      {
         // constructor
         skip(className);

         String params = parseFormalParamList();

         // skip throws
         if (currentRealTokenEquals("throws"))
         {
            skipTo('{');
         }
         methodBodyStartPos = currentRealToken.startPos;
         parseBlock();

         String constructorSignature = org.sdmlib.codegen.Parser.CONSTRUCTOR + ":" + className + params;

      }
      else
      {
         String type = parseTypeRef();

         String memberName = currentRealWord();
         verbose("parsing member: " + memberName);

         nextRealToken();
         // Switch between Enum Value and Attributes
         if (currentRealKindEquals('='))
         {
            // field declaration with initialisation
            skip("=");

            parseExpression();

            addCodeFragment(ATTRIBUTE + ":" + memberName, annotationsStartPos, currentRealToken.endPos);

            skip(";");

         }
         else if (currentRealKindEquals(';') && !",".equals(memberName))
         {
            // field declaration
            addCodeFragment(ATTRIBUTE + ":" + memberName, annotationsStartPos, currentRealToken.endPos);

            skip(";");
         }
         else if (currentRealKindEquals('('))
         {

            String params = parseFormalParamList();

            // FIXME : skip annotations
            if (type.startsWith("@"))
            {
               return;
            }

            // skip throws
            String throwsTags = null;
            if (currentRealTokenEquals("throws"))
            {
               int temp = currentRealToken.startPos;
               skipTo('{');
               throwsTags = fileBody.substring(temp, currentRealToken.startPos);
            }

            methodBodyStartPos = currentRealToken.startPos;

            if (currentRealKindEquals('{'))
            {
               parseBlock();
            }

            else if (currentRealKindEquals(';'))
            {
               skip(';');
            }

            String methodSignature = org.sdmlib.codegen.Parser.METHOD + ":" + memberName + params;
            addCodeFragment(methodSignature, annotationsStartPos, previousRealToken.endPos);
         }
         else if (ENUM.equals(classType))
         {
            if (",".equalsIgnoreCase(memberName) || ";".equalsIgnoreCase(memberName) || !";".equals(type)
                  && currentRealKindEquals(EOF))
            {
               String enumSignature = org.sdmlib.codegen.Parser.ENUMVALUE + ":" + type;
            }
            else
            {
               String enumSignature = org.sdmlib.codegen.Parser.ENUMVALUE + ":" + type;

               skipTo(';');
               skip(";");
            }
         }
      }
   }

   private String parseAnnotations()
   {
      String result = "";

      while ("@".equals(currentRealWord()))
      {
         result += currentRealWord();
         nextRealToken();
         result += currentRealWord();
         nextRealToken();
         while (currentRealWord().equals("."))
         {
            result += currentRealWord();
            nextRealToken();
            result += currentRealWord();
            nextRealToken();
         }
         // result += parseQualifiedName();
         // result += currentRealWord();

         if ("(".equals(currentRealWord()))
         {
            result += currentRealWord();
            nextRealToken();

            while (!")".equals(currentRealWord()))
            {
               result += currentRealWord();
               nextRealToken();
            }
            result += currentRealWord();
            nextRealToken();
         }
      }

      // if (!result.isEmpty()) System.out.println(result);
      return result;
   }

   private void skipTo(char c)
   {
      while (!currentRealKindEquals(c) && !currentRealKindEquals(EOF))
      {
         nextRealToken();
      }
   }

   private void skipBody()
   {
      int index = 1;
      // nextRealToken();
      while (index > 0 && !currentRealKindEquals(EOF))
      {
         nextRealToken();
         if (currentRealTokenEquals("{"))
            index++;
         else if (currentRealTokenEquals("}"))
            index--;
      }
      nextRealToken();
   }

   private void verbose(String string)
   {
      if (verbose)
      {
         System.out.println(string);
      }
   }

   private void parseExpression()
   {
      // ... { ;;; } ;
      while (!currentRealKindEquals(EOF) && !currentRealKindEquals(';'))
      {
         if (currentRealKindEquals('{'))
         {
            parseBlock();
         }
         else
         {
            nextRealToken();
         }
      }
   }

   private void parseBlock()
   {
      // { stat ... }
      skip("{");

      while (!currentRealKindEquals(EOF) && !currentRealKindEquals('}'))
      {
         if (currentRealKindEquals('{'))
         {
            parseBlock();
         }
         else
         {
            nextRealToken();
         }
      }

      skip("}");
   }

   private String parseTypeRef()
   {
      StringBuilder typeString = new StringBuilder();

      // (void | qualifiedName) <T1, T2> [] ...
      String typeName = VOID;
      if (currentRealTokenEquals(VOID))
      {
         // skip void
         nextRealToken();
      }
      else
      {
         typeName = parseQualifiedName();
      }

      typeString.append(typeName);

      if (currentRealKindEquals('<'))
      {
         parseGenericTypeDefPart(typeString);
      }

      if (currentRealKindEquals('['))
      {
         typeString.append("[]");
         skip("[");
         while (!"]".equals(currentRealWord()) && !currentRealKindEquals(EOF))
         {
            nextRealToken();
         }
         skip("]");
      }

      if (currentRealKindEquals('.'))
      {
         typeString.append("...");
         skip(".");
         skip(".");
         skip(".");
      }

      if ("extends".equals(lookAheadRealToken.text.toString()))
      {
         typeString.append(currentRealToken.text);
         nextRealToken();
         typeString.append(currentRealToken.text);
         nextRealToken();
         typeString.append(currentRealToken.text);
         nextRealToken();
         typeString.append(currentRealToken.text);

      }
      if ("@".equals(typeString.toString()))
      {
         typeString.append(currentRealToken.text);
      }
      // phew
      return typeString.toString();
   }

   private void parseGenericTypeDefPart(StringBuilder typeString)
   {
      // <T, T, ...>
      skip("<");
      typeString.append('<');

      while (!currentRealKindEquals('>') && !currentRealKindEquals(EOF))
      {
         if (currentRealKindEquals('<'))
         {
            parseGenericTypeDefPart(typeString);
         }
         else
         {
            typeString.append(currentRealWord());
            nextRealToken();
         }
      }

      // should be a < now
      typeString.append(">");
      skip(">");
   }

   private String parseFormalParamList()
   {
      StringBuilder paramList = new StringBuilder().append('(');

      // '(' (type name[,] )* ') [throws type , (type,)*]
      skip("(");

      while (!currentRealKindEquals(EOF) && !currentRealKindEquals(')'))
      {
         int typeStartPos = currentRealToken.startPos;

         parseTypeRef();

         int typeEndPos = currentRealToken.startPos - 1;

         paramList.append(fileBody.substring(typeStartPos, typeEndPos));

         // parameter ends
         if (currentRealKindEquals(')'))
            break;

         // skip param name
         nextRealToken();

         if (currentRealKindEquals(','))
         {
            skip(",");
            paramList.append(',');
         }
      }

      skip(")");

      paramList.append(')');

      return paramList.toString();
   }

   private boolean currentRealKindEquals(char c)
   {
      return currentRealToken.kind == c;
   }

   private String currentRealWord()
   {
      return currentRealToken.text.toString();
   }

   private boolean currentRealTokenEquals(String word)
   {
      return StrUtil.stringEquals(currentRealWord(), word);
   }

   private String parseModifiers()
   {
      // names != class
      String result = "";
      String modifiers = " public protected private static abstract final native synchronized transient volatile strictfp ";
      while (modifiers.indexOf(" " + currentRealWord() + " ") >= 0)
      {
         result += currentRealWord() + " ";
         nextRealToken();
      }

      return result;
   }

   private void parseImport()
   {
      // import qualifiedName [. *];
      int startPos = currentRealToken.startPos;
      nextRealToken();

      String modifier = parseModifiers();

      // if (!modifier.isEmpty())
      // System.out.println("static import");

      String importName = parseQualifiedName();

      if (currentRealToken.kind == '*')
      {
         skip("*");
      }
      // if (currentRealToken.kind == '$'){
      // nextRealToken();
      // importName += "$"+currentRealWord();
      // nextRealToken();
      // }

      skip(";");

      addCodeFragment(IMPORT + ":" + importName, startPos, previousRealToken.endPos);
   }

   private void parsePackageDecl()
   {
      int startPos = currentRealToken.startPos;
      nextRealToken();
      String packageName = parseQualifiedName();
      skip(";");

      addCodeFragment(PACKAGE, startPos, previousRealToken.endPos);
   }

   private int lastFragmentEndPos;

   private void addCodeFragment(String key, int startPos, int endPos)
   {
      if (endPos < startPos)
      {
         endPos = startPos - 1;
      }
      // add gap fragement
      CodeFragment gap = new CodeFragment().setKey("gap:").setText(fileBody.substring(lastFragmentEndPos + 1, startPos));

      fragmentMap.add(gap);

      // add code fragment
      CodeFragment codeFragment = new CodeFragment()
            .setKey(key)
            .setText(fileBody.substring(startPos, endPos+1));

      fragmentMap.add(codeFragment);

      lastFragmentEndPos = endPos;
   }

   private void checkSearchStringFound(String foundElem, int startPos)
   {
      if (StrUtil.stringEquals(searchString, foundElem))
      {
         indexOfResult = startPos;
         throw new Parser.SearchStringFoundException();
      }
   }

   private String parseQualifiedName()
   {
      // return dotted name
      int startPos = currentRealToken.startPos;
      int endPos = currentRealToken.endPos;

      checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), currentToken.startPos);
      nextRealToken();

      while (currentRealKindEquals('.') && !(lookAheadRealToken.kind == '.') && !currentRealKindEquals(EOF))
      {
         skip(".");

         // read next name
         endPos = currentRealToken.endPos;
         checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), currentToken.startPos);
         nextRealToken();
      }

      return fileBody.substring(startPos, endPos + 1);
   }

   private void skip(char c)
   {
      if (currentRealKindEquals(c))
      {
         nextRealToken();
      }
      else
      {
         System.out.println("Parser Problem: \'" + currentRealToken.kind + "\' :"
               + " but \'" + c + "\' expected in " + className + ".java  at line "
               + getLineIndexOf(currentRealToken.startPos)
               + "\n" + getLineForPos(currentRealToken.startPos));
         // throw new RuntimeException("parse error");
      }
   }

   private void skip(String string)
   {
      if (currentRealTokenEquals(string))
      {
         nextRealToken();
      }
      else
      {

         System.err.println("Parser Error: expected token " + string + " found " + currentRealWord()
               + " at pos " + currentRealToken.startPos + " at line "
               + getLineIndexOf(currentRealToken.startPos, fileBody)
               + " in file \n" + fileName);

         throw new RuntimeException("parse error");
      }
   }

   private long getLineIndexOf(int startPos, StringBuilder fileBody)
   {
      long count = 1;
      String substring = fileBody.substring(0, startPos);
      for (int index = 0; index < substring.length() - 1; ++index)
      {
         final char firstChar = substring.charAt(index);
         if (firstChar == NEW_LINE)
            count++;
      }
      return count;
   }

   public long getLineIndexOf(int startPos)
   {
      if (startPos < 0)
         return -1;
      long count = 1;
      String substring = fileBody.substring(0, startPos);
      for (int index = 0; index < substring.length() - 1; ++index)
      {
         final char firstChar = substring.charAt(index);
         if (firstChar == NEW_LINE)
            count++;
      }
      return count;
   }

   public Token currentRealToken;
   public Token lookAheadRealToken;
   public Token previousRealToken;

   public int indexOfResult;

   private Token previousToken;

   private String className;
   private String classType;


   public int lastIfStart;
   public int lastIfEnd;

   private int lastReturnStart;

   private LinkedHashMap<String, Integer> methodBodyQualifiedNames = new LinkedHashMap<String, Integer>();

   private HashMap<StatementEntry, Integer> returnStatements = new HashMap<>();




   private void nextRealToken()
   {
      Token tmp = previousRealToken;
      previousRealToken = currentRealToken;
      currentRealToken = lookAheadRealToken;
      lookAheadRealToken = tmp;
      lookAheadRealToken.kind = EOF;
      lookAheadRealToken.preCommentStartPos = 0;
      lookAheadRealToken.preCommentEndPos = 0;
      lookAheadRealToken.text.delete(0, lookAheadRealToken.text.length());

      // parse comments and skip new lines
      while (currentToken.kind == COMMENT_START || currentToken.kind == NEW_LINE)
      {
         if (currentToken.text.indexOf("/*") == 0)
         {
            parseLongComment();
         }
         else if (currentToken.text.indexOf("//") == 0)
         {
            parseLineComment();
         }
         else
         {
            nextToken();
         }
      }

      // parse string constants as one real token
      if (currentToken.kind == '"')
      {
         int constStartPos = currentToken.startPos;

         parseStringConstant();

         lookAheadRealToken.kind = '"';
         lookAheadRealToken.text.append(fileBody.substring(constStartPos, previousToken.startPos + 1));
         lookAheadRealToken.startPos = constStartPos;
         lookAheadRealToken.endPos = previousToken.startPos;
      }
      else if (currentToken.kind == '\'')
      {
         int constStartPos = currentToken.startPos;

         parseCharConstant();

         lookAheadRealToken.kind = '\'';
         lookAheadRealToken.text.append(fileBody.substring(constStartPos, previousToken.startPos + 1));
         lookAheadRealToken.startPos = constStartPos;
         lookAheadRealToken.endPos = previousToken.startPos;
      }
      else if (currentToken.kind == '9')
      {
         // TODO IS IT RIGHT
         lookAheadRealToken.kind = currentToken.kind;
         lookAheadRealToken.text.append((int) currentToken.value);
         lookAheadRealToken.startPos = currentToken.startPos;
         lookAheadRealToken.endPos = currentToken.endPos;

         nextToken();
      }
      else
      {
         lookAheadRealToken.kind = currentToken.kind;
         lookAheadRealToken.text.append(currentToken.text.toString());
         lookAheadRealToken.startPos = currentToken.startPos;
         lookAheadRealToken.endPos = currentToken.endPos;

         nextToken();
      }
   }

   private void parseCharConstant()
   {
      // " 'c' or '\c' "
      skipBasicToken('\'');

      // skip \
      if (currentToken.kind == '\\')
      {
         nextToken();
      }

      // skip c
      nextToken();

      skipBasicToken('\'');
   }

   private void parseLineComment()
   {
      // '//' ... \n

      lookAheadRealToken.preCommentStartPos = currentToken.startPos;
      // skip //
      nextToken();

      while (currentToken.kind != EOF && currentToken.kind != '\n')
      {
         nextToken();
      }

      lookAheadRealToken.preCommentEndPos = currentToken.endPos;
      // skip \n
      nextToken();
   }

   private void parseStringConstant()
   {
      // " ... \" ... "
      skipBasicToken('"');

      // read until next "
      while (currentToken.kind != EOF && currentToken.kind != '"')
      {
         if (currentToken.kind == '\\')
         {
            // escape next char
            nextToken();
         }
         nextToken();
      }

      skipBasicToken('"');
   }

   private void skipBasicToken(char s)
   {
      if (currentToken.kind == s)
      {
         nextToken();
      }
   }

   private void parseLongComment()
   {
      // parse /* ... */ (nested?)

      // skip /*
      lookAheadRealToken.preCommentStartPos = currentToken.startPos;

      nextToken();
      while (currentToken.kind != EOF && currentToken.kind != LONG_COMMENT_END)
      {
         nextToken();
      }

      lookAheadRealToken.preCommentEndPos = currentToken.endPos;
      // skip */
      nextToken();
   }

   private void nextToken()
   {
      Token tmp = previousToken;
      previousToken = currentToken;
      currentToken = lookAheadToken;

      lookAheadToken = tmp;
      lookAheadToken.kind = EOF;
      lookAheadToken.text.delete(0, lookAheadToken.text.length());

      char state = 'i';

      while (true)
      {
         switch (state)
         {
            case 'i':
               if (Character.isLetter(currentChar) || (currentChar == '_'))
               {
                  state = 'v';
                  lookAheadToken.kind = 'v';
                  lookAheadToken.text.append(currentChar);
                  lookAheadToken.startPos = index;
               }
               else if (currentChar == EOF)
               {
                  lookAheadToken.kind = EOF;
                  lookAheadToken.startPos = index;
                  lookAheadToken.endPos = index;
                  return;
               }
               else if (Character.isDigit(currentChar))
               {
                  state = '9';
                  lookAheadToken.kind = '9';
                  lookAheadToken.value = currentChar - '0';
                  lookAheadToken.startPos = index;
               }
               else if (currentChar == '/' && (lookAheadChar == '*' || lookAheadChar == '/'))
               {
                  // start of comment
                  lookAheadToken.kind = COMMENT_START;
                  lookAheadToken.startPos = index;
                  lookAheadToken.text.append(currentChar);
                  nextChar();
                  lookAheadToken.text.append(currentChar);
                  lookAheadToken.endPos = index;
                  nextChar();
                  return;
               }
               else if (currentChar == '*' && lookAheadChar == '/')
               {
                  // start of comment
                  lookAheadToken.kind = LONG_COMMENT_END;
                  lookAheadToken.startPos = index;
                  lookAheadToken.text.append(currentChar);
                  nextChar();
                  lookAheadToken.text.append(currentChar);
                  lookAheadToken.endPos = index;
                  nextChar();
                  return;
               }
               else if ("+-*/\\\"'~=()><{}!.,@[]&|?;:#".indexOf(currentChar) >= 0)
               {
                  lookAheadToken.kind = currentChar;
                  lookAheadToken.text.append(currentChar);
                  lookAheadToken.startPos = index;
                  lookAheadToken.endPos = index;
                  nextChar();
                  return;
               }
               else if (currentChar == NEW_LINE)
               {
                  lookAheadToken.kind = NEW_LINE;
                  lookAheadToken.startPos = index;
                  lookAheadToken.endPos = index;
                  nextChar();
                  return;
               }
               else if (Character.isWhitespace(currentChar))
               {
               }

               break;

            case '9':
               if (Character.isDigit(currentChar))
               {
                  lookAheadToken.value = lookAheadToken.value * 10 + (currentChar - '0');
               }
               else if (currentChar == '.')
               {
                  state = '8';
               }
               else
               {
                  lookAheadToken.endPos = index - 1;
                  return;
               }
               break;

            case '8':
               if (!Character.isDigit(currentChar))
               {
                  lookAheadToken.endPos = index - 1;
                  return;
               }
               break;

            case 'v':
               if (Character.isLetter(currentChar)
                     || Character.isDigit(currentChar)
                     || currentChar == '_')
               {
                  // keep reading
                  lookAheadToken.text.append(currentChar);
               }
               else
               {
                  lookAheadToken.endPos = index - 1;
                  return; // <==== sudden death
               }
               break;

            default:
               break;
         }

         nextChar();
      }
   }

   private void nextChar()
   {
      currentChar = lookAheadChar;
      index = lookAheadIndex;
      lookAheadChar = 0;

      while (lookAheadChar == 0 && lookAheadIndex < endPos - 1)
      {
         lookAheadIndex++;

         lookAheadChar = fileBody.charAt(lookAheadIndex);
      }
   }




   private void parseBlockDetails()
   {
      // parse method and generate statement index
      skip('{');

      parseInnerBlockDetails();

      skip('}');
   }

   private void parseSimpleStatementDetails()
   {
      currentStatement = new StatementEntry()
            .withKind("simple")
            .withStartPos(currentRealToken.startPos)
            .withParent(currentParentStatement);

      parseExpressionDetails();

      skip(';');

   }

   private void parseForLoopDetails()
   {
      readToken("for");

      readToken('(');

      // should start with declartion of loop variable
      String type = parseTypeRef();

      String varname = currentRealWord();
      localVarTable.put(varname, new LocalVarTableEntry().withName(varname).withType(type));
      currentStatement.setAssignTargetVarName(varname);

      readToken();

      // might be a for (:) loop or the classical for (;;)
      if (currentRealKindEquals(':'))
      {
         readToken(':');
         parseExpressionDetails();
      }
      else
      {
         // loop var should be initialized
         readToken('=');
         parseExpressionDetails();
         readToken(';');
         parseExpressionDetails();
         readToken(';');
         parseExpressionDetails();
      }
      readToken(')');

      currentParentStatement = currentStatement;
      parseBlockDetails();
      currentParentStatement = currentParentStatement.getParent();
   }

   private void readToken(String string)
   {
      skip(string);

      if (currentStatement != null)
      {
         currentStatement.withToken(string, currentRealToken.endPos);
      }
   }

   private void parseInnerBlockDetails()
   {
      while (!currentRealKindEquals(EOF) && !currentRealKindEquals('}'))
      {
         int startPos = currentRealToken.startPos;
         if (" if while catch ".indexOf(" " + currentRealWord() + " ") >= 0)
         {
            lastIfStart = startPos;

            currentStatement = new StatementEntry().withKind(currentRealWord()).withParent(currentParentStatement)
                  .withStartPos(currentRealToken.startPos);

            readToken();

            parseBracketExpressionDetails();
            currentParentStatement = currentStatement;

            if (currentRealKindEquals('{'))
            {
               parseBlockDetails();
            }
            else
            {
               checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), startPos);

               parseSimpleStatementDetails();
            }
            currentParentStatement = currentParentStatement.getParent();

            lastIfEnd = previousRealToken.startPos;
         }
         else if (currentRealTokenEquals("for"))
         {
            currentStatement = new StatementEntry().withKind("for").withParent(currentParentStatement)
                  .withStartPos(currentRealToken.startPos);
            parseForLoopDetails();
         }
         else if (currentRealTokenEquals("return"))
         {
            currentStatement = new StatementEntry().withKind("return").withParent(currentParentStatement)
                  .withStartPos(currentRealToken.startPos);

            lastReturnStart = startPos;

            readToken("return");

            parseExpressionDetails();

            returnStatements.put(currentStatement, lastReturnStart);

            skip(';');
         }
         else if (currentRealTokenEquals("new"))
         {
            parseSimpleStatementDetails();
         }
         else if (currentRealKindEquals('v')
               && (lookAheadRealToken.kind == 'v' || lookAheadRealToken.kind == '=' || lookAheadRealToken.kind == '<'))
         {
            // local var decl with simple type
            parseLocalVarDeclDetails();
         }
         else if (currentRealKindEquals('v')
               && lookAheadRealToken.kind == '{')
         {
            // skip keyword
            readToken();
            parseBlockDetails();
         }
         else if (currentRealKindEquals('v')
               && lookAheadRealToken.kind == ':')
         {
            // skip keyword
            readToken();

            // skip colon
            readToken();
         }
         else if (currentRealKindEquals('v'))
         {
            checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), startPos);

            parseSimpleStatementDetails();

            // String qualifiedName = parseQualifiedName();
            // methodBodyQualifiedNames.put(qualifiedName, startPos);
         }
         else if (currentRealKindEquals('{'))
         {
            parseBlockDetails();
         }
         else if (currentRealKindEquals(';'))
         {
            checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), startPos);
            skip(';');
         }
         else if (currentRealKindEquals('('))
         {
            parseBracketExpressionDetails();
         }
         else
         {
            nextRealToken();
         }
      }
   }

   private void parseLocalVarDeclDetails()
   {
      // skip static and or final
      while ("static final".indexOf(currentRealWord()) >= 0
            && !currentRealKindEquals(org.sdmlib.codegen.Parser.EOF))
      {
         nextRealToken();
      }

      // parse type
      String type = null;

      if (lookAheadRealToken.kind == 'v' || lookAheadRealToken.kind == '<')
      {
         type = parseTypeRef();
      }

      String varName = currentRealWord();

      checkSearchStringFound(NAME_TOKEN + ":" + varName, previousRealToken.startPos);

      nextRealToken();

      int startPos = currentRealToken.startPos;
      if (currentRealKindEquals('='))
      {
         currentStatement = new StatementEntry()
               .withKind("assign")
               .withAssignTargetVarName(varName)
               .withStartPos(currentRealToken.startPos)
               .withParent(currentParentStatement);

         skip('=');

         ArrayList<ArrayList<String>> initCallSequence = new ArrayList<ArrayList<String>>();

         while (!currentRealKindEquals(org.sdmlib.codegen.Parser.EOF)
               && !currentRealKindEquals(';'))
         {
            if (currentRealKindEquals('v'))
            {
               ArrayList<String> methodClassDetails = parseMethodCallDetails();
               if (!methodClassDetails.isEmpty())
               {
                  initCallSequence.add(methodClassDetails);
                  methodBodyQualifiedNames.clear();
               }
            }
            else if (!currentRealKindEquals(';'))
            {
               readToken();
            }
         }

         if (!initCallSequence.isEmpty())
         {
            LocalVarTableEntry initSequence =
                  new LocalVarTableEntry()
                        .withName(varName)
                        .withType(type)
                        .withInitSequence(initCallSequence)
                        .withStartPos(startPos)
                        .withEndPos(previousRealToken.endPos);
            localVarTable.put(varName, initSequence);
         }

         checkSearchStringFound(NAME_TOKEN + ":;", currentRealToken.startPos);

         skip(';');
      }

      else if (currentRealKindEquals('('))
      {
         ArrayList<ArrayList<String>> initElements = new ArrayList<ArrayList<String>>();
         skip('(');
         while (!currentRealKindEquals(org.sdmlib.codegen.Parser.EOF) && !currentRealKindEquals(')'))
         {
            int paramStartPos = currentRealToken.startPos;
            parseExpressionDetails();
            int paramEndPos = previousRealToken.endPos;

            String name = fileBody.substring(paramStartPos, paramEndPos + 1);
            ArrayList<String> nameList = new ArrayList<String>();
            nameList.add(name);
            initElements.add(nameList);

            if (currentRealKindEquals(','))
            {
               skip(',');
            }
         }
         skip(')');
         while (!currentRealKindEquals(org.sdmlib.codegen.Parser.EOF) && !currentRealKindEquals(';'))
         {
            if (currentRealKindEquals('.'))
            {
               skip('.');
            }
            ArrayList<String> methodInitDetails = parseInitCallDetails();
            if (!methodInitDetails.isEmpty())
            {
               initElements.add(methodInitDetails);
            }
         }

         LocalVarTableEntry initSequence =
               new LocalVarTableEntry()
                     .withName(initElements.get(0).get(0))
                     .withType(varName)
                     .withInitSequence(initElements)
                     .withStartPos(startPos)
                     .withEndPos(previousRealToken.endPos);

         String nameString = varName + "_" + initSequence.hashCode();
         localVarTable.put(nameString, initSequence);

      }
   }

   private ArrayList<String> parseInitCallDetails()
   {
      ArrayList<String> methodInitDetails = new ArrayList<String>();
      methodInitDetails.add(".");
      while (!currentRealKindEquals(org.sdmlib.codegen.Parser.EOF) && !currentRealKindEquals(')'))
      {
         String text = currentRealToken.text.toString();
         // System.out.println(text);
         methodInitDetails.add(text);
         nextRealToken();
      }
      methodInitDetails.add(")");
      skip(')');
      return methodInitDetails;
   }

   private ArrayList<String> parseMethodCallDetails()
   {
      ArrayList<String> methodCallElements = new ArrayList<String>();
      if ("new".equals(currentRealWord()))
      {
         // constructor call
         readToken("new");

         String type = parseTypeRef();

         methodCallElements.add("new " + type);
         currentStatement.withToken(type, currentRealToken.endPos);

         if (currentRealKindEquals('('))
         {
            readToken('(');

            while (!currentRealKindEquals(org.sdmlib.codegen.Parser.EOF)
                  && !currentRealKindEquals(')'))
            {
               int paramStartPos = currentRealToken.startPos;
               parseExpressionDetails();
               int paramEndPos = previousRealToken.endPos;

               methodCallElements.add(fileBody.substring(paramStartPos, paramEndPos + 1));

               if (currentRealKindEquals(','))
               {
                  readToken(',');
               }
            }

            readToken(')');
         }
      }
      else if (currentRealKindEquals('v'))
      {
         // its a word, might be a method name
         String qualifiedName = parseQualifiedName();
         currentStatement.withToken(qualifiedName, currentRealToken.endPos);

         if (currentRealKindEquals('('))
         {
            methodCallElements.add(qualifiedName);

            readToken('(');

            while (!currentRealKindEquals(org.sdmlib.codegen.Parser.EOF)
                  && !currentRealKindEquals(')'))
            {
               methodBodyQualifiedNames.clear();
               // int paramStartPos = currentRealToken.startPos;
               parseExpressionDetails();
               // int paramEndPos = previousRealToken.endPos;

               if (!(methodBodyQualifiedNames.keySet().isEmpty())
                     &&
                     !(methodBodyQualifiedNames.keySet().size() == 1
                           && methodBodyQualifiedNames.keySet().toArray()[0].equals(previousRealToken.text.toString())))
               {
                  methodCallElements.add("[");
                  methodCallElements.addAll(methodBodyQualifiedNames.keySet());
                  methodCallElements.add("]");
               }
               methodCallElements.add(previousRealToken.text.toString());

               if (currentRealKindEquals(','))
               {
                  readToken(',');
               }
               else
               {

                  return methodCallElements;
               }
            }

            readToken(')');
         }
      }
      else
      {
         // seems to be a number or something like this, keep it and stop
         parseExpressionDetails();
      }

      return methodCallElements;
   }

   private void parseExpressionDetails()
   {
      // ... { ;;; } ;
      while (!currentRealKindEquals(EOF)
            && !currentRealKindEquals(';')
            && !currentRealKindEquals(',')
            && !currentRealKindEquals(')'))
      {
         if (currentRealKindEquals('{'))
         {
            parseBlockDetails();
         }
         else if (currentRealKindEquals('('))
         {
            parseBracketExpressionDetails();
         }
         else if (currentRealKindEquals('v'))
         {
            checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), currentRealToken.startPos);

            String qualifiedName = parseQualifiedName();

            methodBodyQualifiedNames.put(qualifiedName, currentRealToken.startPos);
            currentStatement.withToken(qualifiedName, currentRealToken.endPos);
         }
         else
         {
            readToken();
         }
      }
   }

   private void parseBracketExpressionDetails()
   {
      readToken('(');

      while (!currentRealKindEquals(EOF)
            && !currentRealKindEquals(')'))
      {
         if (currentRealKindEquals('('))
         {
            parseBracketExpressionDetails();
         }
         else if (currentRealKindEquals('v'))
         {
            checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), currentRealToken.startPos);

            String qualifiedName = parseQualifiedName();

            methodBodyQualifiedNames.put(qualifiedName, currentRealToken.startPos);
            currentStatement.withToken(qualifiedName, currentRealToken.endPos);
         }
         else if (currentRealKindEquals('"'))
         {
            checkSearchStringFound(NAME_TOKEN + ":" + currentRealWord(), currentRealToken.startPos);

            String qualifiedName = parseQualifiedName();

            methodBodyQualifiedNames.put(qualifiedName, currentRealToken.startPos);
            currentStatement.withToken(qualifiedName, currentRealToken.endPos);
         }
         else
         {
            readToken();
         }
      }

      readToken(')');
   }

   private void readToken()
   {
      if (currentStatement != null)
      {
// TODO         currentStatement.withToken(currentRealToken);
      }
      nextRealToken();
   }

   private void readToken(char c)
   {
      skip(c);

      if (currentStatement != null)
      {
         currentStatement.withToken("" + c, currentRealToken.endPos);
      }
   }

   private String fileName;

   public String getFileName()
   {
      return fileName;
   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }



   public String getLineForPos(int currentInsertPos)
   {
      String part1 = fileBody.substring(0, currentInsertPos);
      String part2 = fileBody.substring(currentInsertPos);
      int startPos = 1 + part1.lastIndexOf("\n");
      int endPos = currentInsertPos + part2.indexOf("\n");

      String lineString = "\"" + fileBody.substring(startPos, endPos).toString() + "\"";

      int index = currentInsertPos - startPos;
      char[] chars1 = new char[index];
      Arrays.fill(chars1, ' ');
      String string1 = new String(chars1);

      String posString = "\n" + string1 + "^";
      return lineString + posString;
   }


}
