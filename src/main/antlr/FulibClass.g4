grammar FulibClass;

// =============== Parser ===============

file: packageDecl importDecl* classDecl*;

// --------------- Top-Level Declarations ---------------

packageDecl: PACKAGE qualifiedName SEMI;
importDecl: IMPORT qualifiedName (DOT STAR)? SEMI;

classDecl: (modifier | annotation)* (CLASS | ENUM | AT? INTERFACE) IDENTIFIER
           typeParamList?
           (EXTENDS type)?
           (IMPLEMENTS type (COMMA type)*)?
           classBody;

classBody: LBRACE member* RBRACE;

// --------------- Members ---------------

member: initializer | constructor | field | method | classDecl;

initializer: STATIC? balancedBraces;

constructor: (modifier | annotation)* typeParamList? IDENTIFIER
             parameterList
             (THROWS type (COMMA type)*)?
             balancedBraces;

field: (modifier | annotation)* type IDENTIFIER (EQ expr)? SEMI;

method: (modifier | annotation)* typeParamList? type IDENTIFIER
        parameterList
        (THROWS type (COMMA type)*)?
        (DEFAULT expr)?
        balancedBraces;

parameterList: LPAREN (parameter (COMMA parameter)*)? RPAREN;
parameter: (modifier | annotation)* type IDENTIFIER;

// --------------- Types ---------------

typeParamList: LANGLE (typeParam (COMMA typeParam)*)? RANGLE;
typeParam: annotation* IDENTIFIER (EXTENDS type (AMP type)*)?;
typeArg: QMARK (EXTENDS type | SUPER type)? | type;

type: annotation* (primitiveType | referenceType);

primitiveType: VOID | BOOLEAN | BYTE | SHORT | CHAR | INT | LONG | FLOAT | DOUBLE;
referenceType: qualifiedName (RANGLE (typeArg (COMMA typeArg)*)? LANGLE)?;

// --------------- Misc. ---------------

modifier: PUBLIC | PROTECTED | PRIVATE | ABSTRACT | STATIC | FINAL | TRANSIENT | VOLATILE | SYNCHRONIZED | NATIVE | STRICTFP;
annotation: AT qualifiedName balancedParens?;

expr: (balancedBraces | ~SEMI)*;

qualifiedName: IDENTIFIER (DOT IDENTIFIER)*;

balancedParens: LPAREN (~RPAREN | balancedParens)* RPAREN;
balancedBraces: LBRACE (~RBRACE | balancedBraces)* RBRACE;

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
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
RANGLE: '<';
LANGLE: '>';

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

DOC_COMMENT: '/**' .*? '*/';
BLOCK_COMMENT: '/*' .*? '*/';
LINE_COMMENT: '//' .*? '\n';

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

WS: [ \n\r\t\p{White_Space}] -> skip;

OTHER: .+?;
