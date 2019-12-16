grammar FulibClass;

// =============== Parser ===============

file: packageDecl importDecl*;

packageDecl: PACKAGE qualifiedName SEMI;
importDecl: IMPORT qualifiedName (DOT STAR) SEMI;

qualifiedName: IDENTIFIER (DOT IDENTIFIER)*;

// =============== Lexer ===============

// --------------- Symbols ---------------

DOT: '.';
STAR: '*';
SEMI: ';';

// --------------- Keywords ---------------

PACKAGE: 'package';
IMPORT: 'import';

IDENTIFIER: [a-zA-Z_$][a-zA-Z0-9_$]*; // TODO JavaIdentifier

WS: [ \n\r\t\p{White_Space}] -> skip;
