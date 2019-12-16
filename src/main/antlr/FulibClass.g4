grammar FulibClass;

// =============== Parser ===============

file: packageDecl importDecl*;

packageDecl: PACKAGE qualifiedName SEMI;
importDecl: IMPORT qualifiedName (DOT STAR) SEMI;

classDecl: (modifier | annotation)* (CLASS | ENUM | AT? INTERFACE) IDENTIFIER
           (LANGLE typeParam (COMMA typeParam)* RANGLE)?
           (EXTENDS type)?
           (IMPLEMENTS type (COMMA type)*)?
           classBody;

classBody: LBRACE member* RBRACE;

member: field | method | classDecl;

modifier: 'todo'; // TODO
annotation: AT qualifiedName (LPAREN balanced RPAREN)?;

field: 'todo'; // TODO
method: 'todo'; // TODO

typeParam: annotation* IDENTIFIER (EXTENDS type (AMP type)*)?;
typeArg: QMARK (EXTENDS type | SUPER type)? | type;

type: annotation* (primitiveType | referenceType);

primitiveType: VOID | BOOLEAN | BYTE | SHORT | CHAR | INT | LONG | FLOAT | DOUBLE;
referenceType: qualifiedName (RANGLE typeArg (COMMA typeArg)* LANGLE)?;

qualifiedName: IDENTIFIER (DOT IDENTIFIER)*;

balanced: 'todo'; // TODO

// =============== Lexer ===============

// --------------- Symbols ---------------

DOT: '.';
STAR: '*';
COMMA: ',';
SEMI: ';';
AT: '@';
AMP: '&';
QMARK: '?';
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

VOID: 'void';
BOOLEAN: 'boolean';
BYTE: 'byte';
SHORT: 'short';
CHAR: 'char';
INT: 'int';
LONG: 'long';
FLOAT: 'float';
DOUBLE: 'double';

IDENTIFIER: [a-zA-Z_$][a-zA-Z0-9_$]*; // TODO JavaIdentifier

WS: [ \n\r\t\p{White_Space}] -> skip;
