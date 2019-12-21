// tested against
// https://github.com/antlr/grammars-v4/blob/b47fc22a9853d1565d1d0f53b283d46c89fc30e5/java/examples/AllInOne7.java
// and
// https://github.com/antlr/grammars-v4/blob/b47fc22a9853d1565d1d0f53b283d46c89fc30e5/java/examples/AllInOne8.java

grammar FulibClass;

// =============== Parser ===============

file: packageDecl? (importDecl | SEMI)* (classDecl | SEMI)* EOF;

// --------------- Top-Level Declarations ---------------

packageDecl: PACKAGE qualifiedName SEMI;
importDecl: IMPORT STATIC? qualifiedName (DOT STAR)? SEMI;

classDecl: (modifier | annotation)* classMember;
classMember: (CLASS | ENUM | AT? INTERFACE) IDENTIFIER
           typeParamList?
           (EXTENDS annotatedTypeList)?
           (IMPLEMENTS annotatedTypeList)?
           classBody;

classBody: LBRACE (enumConstants (SEMI (member | SEMI)*)? | (member | SEMI)*) RBRACE;

// --------------- Members ---------------

member: initializer | (modifier | annotation)* (constructorMember | fieldMember | methodMember | classMember);

initializer: STATIC? balancedBraces;

// constructor: (modifier | annotation)* constructorMember;
constructorMember: typeParamList? IDENTIFIER
             parameterList
             (THROWS annotatedTypeList)?
             balancedBraces;

enumConstants: enumConstant (COMMA enumConstant)*;
enumConstant: annotation* IDENTIFIER balancedParens? balancedBraces?;

// field: (modifier | annotation)* fieldMember;
fieldMember: type IDENTIFIER arraySuffix* (EQ expr)? SEMI;

// method: (modifier | annotation)* methodMember;
methodMember: (typeParamList annotatedType | type) IDENTIFIER
        parameterList
        arraySuffix*
        (THROWS annotatedType (COMMA annotatedType)*)?
        (DEFAULT expr)?
        (balancedBraces | SEMI);

parameterList: LPAREN (parameter (COMMA parameter)*)? RPAREN;
parameter: (modifier | annotation)* type ELLIPSIS? (IDENTIFIER | (IDENTIFIER DOT)? THIS);

// --------------- Types ---------------

typeParamList: LANGLE (typeParam (COMMA typeParam)*)? RANGLE;
typeParam: annotation* IDENTIFIER (EXTENDS annotatedType (AMP annotatedType)*)?;
typeArg: annotation* (QMARK (EXTENDS annotatedType | SUPER annotatedType)? | type);

type: (primitiveType | referenceType) arraySuffix*;
arraySuffix: annotation* LBRACKET RBRACKET;
annotatedType: annotation* type;
annotatedTypeList: annotatedType (COMMA annotatedType)*;

primitiveType: VOID | BOOLEAN | BYTE | SHORT | CHAR | INT | LONG | FLOAT | DOUBLE;
referenceType: qualifiedName (LANGLE (typeArg (COMMA typeArg)*)? RANGLE)?;

// --------------- Misc. ---------------

modifier: PUBLIC | PROTECTED | PRIVATE | ABSTRACT | STATIC | FINAL | TRANSIENT | VOLATILE | SYNCHRONIZED | NATIVE | STRICTFP | DEFAULT;
annotation: AT qualifiedName balancedParens?;

expr: (balancedBraces | ~SEMI)*;

qualifiedName: IDENTIFIER (DOT IDENTIFIER)*;

balancedParens: LPAREN (~(LPAREN | RPAREN) | balancedParens)*? RPAREN;
balancedBraces: LBRACE (~(LBRACE | RBRACE) | balancedBraces)*? RBRACE;

// =============== Lexer ===============

// --------------- Symbols ---------------

DOT: '.';
STAR: '*';
COMMA: ',';
SEMI: ';';
AT: '@';
AMP: '&';
QMARK: '?';
EQ: '=';
ELLIPSIS: '...';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
LANGLE: '<';
RANGLE: '>';
LBRACKET: '[';
RBRACKET: ']';

// --------------- Keywords ---------------

PACKAGE: 'package';
IMPORT: 'import';
CLASS: 'class';
ENUM: 'enum';
INTERFACE: 'interface';
EXTENDS: 'extends';
IMPLEMENTS: 'implements';
SUPER: 'super';
THROWS: 'throws';
DEFAULT: 'default';

PUBLIC: 'public';
PROTECTED: 'protected';
PRIVATE: 'private';
ABSTRACT: 'abstract';
STATIC: 'static';
FINAL: 'final';
TRANSIENT: 'transient';
VOLATILE: 'volatile';
SYNCHRONIZED: 'synchronized';
NATIVE: 'native';
STRICTFP: 'strictfp';

VOID: 'void';
BOOLEAN: 'boolean';
BYTE: 'byte';
SHORT: 'short';
CHAR: 'char';
INT: 'int';
LONG: 'long';
FLOAT: 'float';
DOUBLE: 'double';

THIS: 'this';

DOC_COMMENT: '/**' .*? '*/' -> channel(2);
BLOCK_COMMENT: '/*' .*? '*/' -> channel(2);
LINE_COMMENT: '//' .*? '\n' -> channel(2);

IDENTIFIER: JavaLetter JavaLetterOrDigit*;

// from https://github.com/antlr/grammars-v4/blob/b47fc22a9853d1565d1d0f53b283d46c89fc30e5/java8/Java8Lexer.g4#L450
fragment JavaLetter:
// these are the "java letters" below 0xFF
[a-zA-Z$_]
|
// covers all characters above 0xFF which are not a surrogate
~[\u0000-\u00FF\uD800-\uDBFF] {Character.isJavaIdentifierStart(_input.LA(-1))}?
|
// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
[\uD800-\uDBFF] [\uDC00-\uDFFF] {Character.isJavaIdentifierStart(Character.toCodePoint((char) _input.LA(-2), (char) _input.LA(-1)))}?
;

fragment JavaLetterOrDigit:
// these are the "java letters or digits" below 0xFF
[a-zA-Z0-9$_]
|
// covers all characters above 0xFF which are not a surrogate
~[\u0000-\u00FF\uD800-\uDBFF] {Character.isJavaIdentifierPart(_input.LA(-1))}?
|
// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
[\uD800-\uDBFF] [\uDC00-\uDFFF] {Character.isJavaIdentifierPart(Character.toCodePoint((char) _input.LA(-2), (char) _input.LA(-1)))}?
;

// --------------- Char and String Literals ---------------

// the main reason for them being here is that we properly handle cases like '{' or "  )  " in code blocks.

CharacterLiteral: '\'' SingleCharacter '\'' | '\'' EscapeSequence '\'';

fragment SingleCharacter: ~['\\\r\n];

StringLiteral:	'"' StringCharacters? '"';
fragment StringCharacters:	StringCharacter+;
fragment StringCharacter: ~["\\\r\n] | EscapeSequence;
fragment EscapeSequence: '\\' [btnfr"'\\] | OctalEscape | UnicodeEscape;

fragment OctalEscape: '\\' OctalDigit | '\\' OctalDigit OctalDigit | '\\' ZeroToThree OctalDigit OctalDigit;
fragment OctalDigit: [0-7];
fragment ZeroToThree: [0-3];

fragment UnicodeEscape: '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit;
fragment HexDigit: [0-9a-fA-F];

// --------------- Whitespace ---------------

WS: [ \n\r\t\p{White_Space}] -> skip;

OTHER: .+?;
