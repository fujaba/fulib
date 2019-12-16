grammar FulibClass;

// =============== Parser ===============

file: packageDecl importDecl*;

// --------------- Top-Level Declarations ---------------

packageDecl: PACKAGE qualifiedName SEMI;
importDecl: IMPORT qualifiedName (DOT STAR) SEMI;

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

parameterList: LPAREN parameter (COMMA parameter)* RPAREN;
parameter: (modifier | annotation)* type IDENTIFIER;

// --------------- Types ---------------

typeParamList: LANGLE typeParam (COMMA typeParam)* RANGLE;
typeParam: annotation* IDENTIFIER (EXTENDS type (AMP type)*)?;
typeArg: QMARK (EXTENDS type | SUPER type)? | type;

type: annotation* (primitiveType | referenceType);

primitiveType: VOID | BOOLEAN | BYTE | SHORT | CHAR | INT | LONG | FLOAT | DOUBLE;
referenceType: qualifiedName (RANGLE typeArg (COMMA typeArg)* LANGLE)?;

// --------------- Misc. ---------------

modifier: 'todo'; // TODO
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

STATIC: 'static';

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

IDENTIFIER: [a-zA-Z_$][a-zA-Z0-9_$]*; // TODO JavaIdentifier

WS: [ \n\r\t\p{White_Space}] -> skip;

OTHER: .+?;
