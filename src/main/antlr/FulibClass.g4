grammar FulibClass;

// =============== Parser ===============

file: packageDecl importDecl*;

packageDecl: PACKAGE qualifiedName SEMI;
importDecl: IMPORT qualifiedName (DOT STAR) SEMI;

classDecl: (modifier | attribute)* (CLASS | ENUM | AT? INTERFACE) IDENTIFIER classBody;

classBody: LBRACE member* RBRACE;

member: field | method | classDecl;

modifier: 'todo'; // TODO
attribute: AT qualifiedName (LPAREN balanced RPAREN)?;

field: 'todo'; // TODO
method: 'todo'; // TODO

qualifiedName: IDENTIFIER (DOT IDENTIFIER)*;

balanced: 'todo'; // TODO

// =============== Lexer ===============

// --------------- Symbols ---------------

DOT: '.';
STAR: '*';
SEMI: ';';
AT: '@';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';

// --------------- Keywords ---------------

PACKAGE: 'package';
IMPORT: 'import';
CLASS: 'class';
ENUM: 'enum';
INTERFACE: 'interface';

IDENTIFIER: [a-zA-Z_$][a-zA-Z0-9_$]*; // TODO JavaIdentifier

WS: [ \n\r\t\p{White_Space}] -> skip;
