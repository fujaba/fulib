package org.fulib;

import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.fulib.util.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.fulib.Parser.CLASS_END;
import static org.fulib.Parser.PACKAGE;

public class TypeScriptParser
{
   public static final char EOF = Character.MIN_VALUE;
   public static final String CLASS = "class";
   public static char NEW_LINE = '\n';
   public static final char COMMENT_START = 'c';
   public static final char LONG_COMMENT_END = 'd';
   public static final String GAP = "gap:";
   public static final String IMPORT = "import";

   private FileFragmentMap fragmentMap;
   private StringBuilder fileBody = null;
   private String fileName;
   private int indexOfResult;
   private char currentChar;
   private int index;
   private int lookAheadIndex;
   private int lastFragmentEndPos;
   private int endPos;
   private char lookAheadChar;
   private Token currentToken;
   private Token lookAheadToken;
   private Token previousToken;
   private String searchString;
   private Token currentRealToken;
   private Token lookAheadRealToken;
   private Token previousRealToken;

   public String getFileName()
   {
      return fileName;
   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }


   public TypeScriptParser withFileBody(StringBuilder fileBody)
   {
      this.fileBody = fileBody;
      return this;
   }



   public static FileFragmentMap parse(String classFileName)
   {
      return new TypeScriptParser()
            .doParse(classFileName);
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



   private TypeScriptParser init(int startPos, int endPos)
   {
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
      // importlist classlist

      int startPos = currentRealToken.startPos;

      while (currentRealTokenEquals(IMPORT))
      {
         parseImport();
      }

      parseClassDecl();
   }



   private void parseImport()
   {
      // import SEClass from "@/model/seClass";
      int startPos = currentRealToken.startPos;
      nextRealToken();

      String importName = currentRealToken.text.toString();
      nextRealToken();

      if (currentRealTokenEquals("from"))
      {
         skip("from");
      }

      String moduleName = currentRealToken.text.toString();
      nextRealToken();

      skip(";");

      addCodeFragment(IMPORT + ":" + importName, startPos, previousRealToken.endPos);
   }



   private void parseClassDecl()
   {
      int startPos = currentRealToken.startPos;

      skip("export");
      skip("default");
      skip("class");

      String className = currentRealWord();
      nextRealToken();

      addCodeFragment(CLASS, startPos, currentRealToken.endPos);

      parseClassBody();
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

      addCodeFragment(GAP, currentRealToken.endPos+1, fileBody.length()-1);
   }



   private void parseMemberDecl()
   {
      int startPos = currentRealToken.startPos;

      String modifiers = parseModifiers();

      String memberName = currentRealWord();
      nextRealToken();

      if ("get set".indexOf(memberName) >= 0)
      {
         memberName += " " + currentRealWord();
         nextRealToken();
      }

      if (currentRealTokenEquals(":"))
      {
         // attribute
         skip(":");

         String type = parseUntil(" ; ");
         skip(";");

         addCodeFragment("attribute:" + memberName, startPos, previousRealToken.endPos);
      }
      else if (currentRealTokenEquals("("))
      {
         // function
         String paramSignature = parseParamDeclList();

         if (":".equals(currentRealWord()))
         {
            parseUntil(" { ");
         }

         parseUntilMatching("{", "}");

         addCodeFragment(Parser.METHOD + ":" + memberName + paramSignature, startPos, previousRealToken.endPos);
      }

   }

   private String parseParamDeclList()
   {
      String signature = "(";

      skip("(");

      while (!currentRealKindEquals(EOF) && !currentRealKindEquals(')'))
      {
         // name : type
         parseUntil(":");
         skip(":");

         String typeName = parseUntil(",)");
         signature += typeName;
         if (",".equals(currentRealWord())) signature += ",";
      }

      skip(")");

      signature += ")";

      return signature;
   }

   private void parseUntilMatching(String openingBrace, String closingBrace)
   {
      skip(openingBrace);

      while (!currentRealKindEquals(EOF))
      {
         if (openingBrace.equals(currentRealWord()))
         {
            parseUntilMatching(openingBrace, closingBrace);
         }
         else if (closingBrace.equals(currentRealWord()))
         {
            skip(closingBrace);
            break;
         }
         else
         {
            nextRealToken();
         }
      }
   }


   private String parseUntil(String stopTokens)
   {
      int startPos = currentRealToken.startPos;

      while (!currentRealKindEquals(EOF) && stopTokens.indexOf(currentRealWord()) < 0)
      {
         nextRealToken();
      }

      String result = fileBody.substring(startPos, previousRealToken.endPos+1);
      return result;
   }


   private String parseModifiers()
   {
      // names != class
      String result = "";
      String modifiers = " public protected private readonly ";
      while (modifiers.indexOf(" " + currentRealWord() + " ") >= 0)
      {
         result += currentRealWord() + " ";
         nextRealToken();
      }

      return result;
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



   private void skip(char c)
   {
      if (currentRealKindEquals(c))
      {
         nextRealToken();
      }
      else
      {
         System.out.println("Parser Problem: \'" + currentRealToken.kind + "\' :"
               + " but \'" + c + "\' expected in " + fileName + " at line "
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
         if (currentToken.text.indexOf("//") == 0)
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



   private void skipBasicToken(char s)
   {
      if (currentToken.kind == s)
      {
         nextToken();
      }
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



   private void addCodeFragment(String key, int startPos, int endPos)
   {
      if (endPos < startPos)
      {
         endPos = startPos - 1;
      }
      // add gap fragement
      CodeFragment gap = new CodeFragment().setKey(GAP).setText(fileBody.substring(lastFragmentEndPos + 1, startPos));

      fragmentMap.add(gap);

      // add code fragment
      CodeFragment codeFragment = new CodeFragment()
            .setKey(key)
            .setText(fileBody.substring(startPos, endPos+1));

      fragmentMap.add(codeFragment);

      lastFragmentEndPos = endPos;
   }

}
