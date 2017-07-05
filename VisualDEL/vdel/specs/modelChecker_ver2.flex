/* JFlex example: part of Java language lexer specification */
import java_cup.runtime.*;

/**
 * This class is a simple example lexer.
 */
%%

%class ModelCheckerScanner
%unicode
%cup
%line
%column

%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}


Identifier = [:jletter:] [:jletterdigit:]*
DecIntegerLiteral = 0 | [1-9][0-9]*
DecDoubleLiteral = 0 | 0 \. 0 | [1-9][0-9]* (\.[0-9]+)?

%state KN_IDENTIFIER
%state AG_IDENTIFIER
%state EP_KN_IDENTIFIER
%state CONSTRAINT
%state EPISTEMIC_CONSTRAINT
%state PHISTATE
%state PHISTATE_KN_CONSTRAINT
%state KNOWS_STATE

%%

/* keywords */
<YYINITIAL> "begin"          {System.out.println("found begin "); return symbol(sym.BEGIN); }
<YYINITIAL> "end"            {System.out.println("found end "); return symbol(sym.END); }
<YYINITIAL> "boolean"        {System.out.println("found boolean "); yybegin(KN_IDENTIFIER); return symbol(sym.BOOLEAN); } // in essence, when we declare the type of a atom, we expect it to be followed by an identifier. When it does, we know that that identifier is a atom_identifier...
<YYINITIAL> "int"            {System.out.println("found int ");  yybegin(KN_IDENTIFIER); return symbol(sym.INT); }
<YYINITIAL> "enum"           {System.out.println("found enum ");  yybegin(KN_IDENTIFIER); return symbol(sym.ENUM); }
<YYINITIAL> "@"              {System.out.println("found knows ");  yybegin(KNOWS_STATE); return symbol(sym.KNOWS); }
<YYINITIAL> "agent"          {System.out.println("found agent ");  yybegin(AG_IDENTIFIER); return symbol(sym.AGENT); }  // likewise, when we declare an agent 'type', we expect it to be followed by an identifier which is an agent identifier...
//<YYINITIAL> "knows"          { System.out.println("found knows "); return symbol(sym.KNOWS); }
<YYINITIAL> "observes"       { System.out.println("found observes "); return symbol(sym.OBSERVES); }
<YYINITIAL> "true"           { System.out.println("found knows "); return symbol(sym.TRUE); }
<YYINITIAL> "false"          { System.out.println("found knows "); return symbol(sym.FALSE); }
<YYINITIAL> "sqrt"           { System.out.println("found sqrt"); return symbol(sym.SQUAREROOT); }
<YYINITIAL> "sqr"            { System.out.println("found sqr"); return symbol(sym.SQUARE); }
<YYINITIAL> "constraint"     { System.out.println("found constraint"); return symbol(sym.CONSTRAINT_KW); }
<YYINITIAL> "epistemic"      { System.out.println("found constraint"); return symbol(sym.EPISTEMIC_KW); }
<YYINITIAL> "dynamic"        { System.out.println("found dynamic"); return symbol(sym.DYNAMIC_KW); }
<YYINITIAL> "["              { System.out.println("[ "); yybegin(CONSTRAINT); } // No need to return any symbol here cus the [ is just to determine the begining of the expression so as to enter into a different state that will enable us to use a different symbol class other the default sym class
<YYINITIAL> "{"              {System.out.println("{ "); yybegin(EPISTEMIC_CONSTRAINT); } 


<YYINITIAL> {
 /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
  /* identifiers */ 
  {Identifier}                   { System.out.println("found identifier " + yytext()); return symbol(sym.IDENTIFIER, yytext()); }
 // {DecIntegerLiteral}            { System.out.println("found Integer " + yytext()); return symbol(sym.INTEGER, new Integer(yytext())); }
  {DecDoubleLiteral}   		   { return symbol(sym.DOUBLE, new Double(yytext())); }
  ";"                            { System.out.println("found ;"); return symbol(sym.SEMI, ";"); }  
  "!"                            { System.out.println("found ~"); return symbol(sym.NOT, "!"); }
  "="                            { System.out.println("found ="); return symbol(sym.EQUALS, "="); }
  "("                            { System.out.println("found ("); return symbol(sym.LPAREN, "("); }
  ")"                            { System.out.println("found )"); return symbol(sym.RPAREN, ")"); }
  ","                            { System.out.println("found ,"); return symbol(sym.COMMA, ","); }
  "*"                            { System.out.println("found *"); return symbol(sym.MULTIPLY, "*"); }
  "/"                            { System.out.println("found /"); return symbol(sym.DIVIDE, "/"); }
  "|"                            { System.out.println("found |"); return symbol(sym.MOD, "|"); }
  "+"                            { System.out.println("found +"); return symbol(sym.PLUS, "+"); }
  "-"                            { System.out.println("found -"); return symbol(sym.MINUS, "-"); }
  "=="                           { System.out.println("found =="); return symbol(sym.DOUBLEEQUALS, "=="); }
  "<"                            { System.out.println("found <"); return symbol(sym.LESSTHAN, "<"); }
  "<="                           { System.out.println("found <="); return symbol(sym.LESSTHANEQUAL, "<="); }
  ">"                            { System.out.println("found >"); return symbol(sym.GREATERTHAN, ">"); }
  ">="                           { System.out.println("found >="); return symbol(sym.GREATERTHANEQUAL, ">="); }
  "!="                 		   { System.out.println("found !="); return symbol(sym.NOTEQUAL, "!="); }
  "&&"                 		   { System.out.println("found &&"); return symbol(sym.AND, "&&"); }
  "||"                 		   { System.out.println("found ||"); return symbol(sym.OR, "||"); }
  "->"                		   { System.out.println("found ->"); return symbol(sym.IMPLIES, "->"); }
 
 
  //{DecIntegerLiteral}            { return symbol(sym.INTEGER, yytext()); }
}

<KN_IDENTIFIER> {
  /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
  {Identifier}				   { System.out.println("found atom identifier" + yytext()); yybegin(YYINITIAL); 
                                   return symbol(sym.ATOM_IDENTIFIER, yytext()); }
  //the following square bracket is used to mark the beginning (and end - i.e. with the closing bracket) of the phi expression when it is being parsed by the main parser (parser.java)    
  "["                            { System.out.println("[ in KN_IDENTIFIER"); yybegin(PHISTATE); return symbol(sym.LBRACKET, "]");  }
  // the following % sign is used to mark the beginning and end of the the phi expression when it is being parsed by the epistemic constraint parser (EpistemicConstraintParser.java)
  //the seperation of both cases enables us to use different (appropriate) symbol classes to for returning the identified tokens
  
  "("                 		   { System.out.println("found ("); return symbol(sym.LPAREN, "("); }
  ")"                 		   { System.out.println("found )"); return symbol(sym.RPAREN, ")"); }
                        
}

<KNOWS_STATE> {
  /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
  {Identifier}				   { System.out.println("found atom identifier" + yytext()); yybegin(YYINITIAL); 
                                   return symbol(sym.IDENTIFIER, yytext()); }
  //the following square bracket is used to mark the beginning (and end - i.e. with the closing bracket) of the phi expression when it is being parsed by the main parser (parser.java)    
  "["                            { System.out.println("[ in KN_IDENTIFIER"); yybegin(PHISTATE); return symbol(sym.LBRACKET, "[");  }
  // the following % sign is used to mark the beginning and end of the the phi expression when it is being parsed by the epistemic constraint parser (EpistemicConstraintParser.java)
  //the seperation of both cases enables us to use different (appropriate) symbol classes to for returning the identified tokens
  
  "("                 		   { System.out.println("found ("); return symbol(sym.LPAREN, "("); }
  ")"                 		   { System.out.println("found )"); return symbol(sym.RPAREN, ")"); }
  
  
                        
}



<EP_KN_IDENTIFIER> {
// this state is arrived to from the EPISTEMIC_CONSTRAINT state

  /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
// {Identifier}				   { System.out.println("found atom identifier" + yytext()); yybegin(EPISTEMIC_CONSTRAINT); 
//                                   return symbol(MCSym.ATOM_IDENTIFIER, yytext()); }

 
                                   
                                   
  "%"              		        { System.out.println("% "); yybegin(PHISTATE_KN_CONSTRAINT); return symbol(MCSym.BRACKET, "%");}   // not really LBRACKET SYMBOL, but makes more sense when it is read as LBRACKET on the scanner   
  
  "("                 		   { System.out.println("found ("); return symbol(MCSym.LPAREN, "("); }
  ")"                 		   { System.out.println("found )"); return symbol(MCSym.RPAREN, ")"); }   
  "*"                  { System.out.println("found *"); return symbol(MCSym.MULTIPLY, "*"); }
  "/"                  { System.out.println("found /"); return symbol(MCSym.DIVIDE, "/"); }
  "|"                  { System.out.println("found |"); return symbol(MCSym.MOD, "|"); }
  "+"                  { System.out.println("found +"); return symbol(MCSym.PLUS, "+"); }
  "-"                  { System.out.println("found -"); return symbol(MCSym.MINUS, "-"); }
  "=="                 { System.out.println("found =="); return symbol(MCSym.DOUBLEEQUALS, "=="); }
  "<"                  { System.out.println("found <"); return symbol(MCSym.LESSTHAN, "<"); }
  "<="                 { System.out.println("found <="); return symbol(MCSym.LESSTHANEQUAL, "<="); }
  ">"                  { System.out.println("found >"); return symbol(MCSym.GREATERTHAN, ">"); }
  ">="                 { System.out.println("found >="); return symbol(MCSym.GREATERTHANEQUAL, ">="); }
  "!="                 { System.out.println("found !="); return symbol(MCSym.NOTEQUAL, "!="); }
  "&&"                 { System.out.println("found &&"); return symbol(MCSym.AND, "&&"); }
  "||"                 { System.out.println("found ||"); return symbol(MCSym.OR, "||"); }
  "->"                 { System.out.println("found ->"); return symbol(MCSym.IMPLIES, "->"); }
  "!"                  { System.out.println("found !"); return symbol(MCSym.NOT, "!"); }
  ";"                  { System.out.println("found ;"); return symbol(MCSym.SEMI, ";"); } 
  "true"           	   { System.out.println("found 'true' "); return symbol(MCSym.TRUE, "true"); }
  "false"              { System.out.println("found 'false' "); return symbol(MCSym.FALSE, "false"); }
  "sqrt"               { System.out.println("found sqrt"); return symbol(MCSym.SQUAREROOT, "sqrt"); }
  "sqr"                { System.out.println("found sqr"); return symbol(MCSym.SQUARE, "sqr"); }
  "@"                  { System.out.println("found knows "); yybegin(EP_KN_IDENTIFIER); return symbol(MCSym.KNOWS, "@"); } 
  "#"                  { System.out.println("found everybody knows"); yybegin(EP_KN_IDENTIFIER); return symbol(MCSym.EVERYBODYKNOWS, "#"); } 
  ":>"                  { System.out.println("found distributed knowledge"); yybegin(EP_KN_IDENTIFIER); return symbol(MCSym.DISTKNOWLEDGE, ":>"); }       
  
  {Identifier}				   { System.out.println("found atom identifier" + yytext()); yybegin(EPISTEMIC_CONSTRAINT); 
                                   return symbol(MCSym.IDENTIFIER, yytext()); }                       
}

<AG_IDENTIFIER> {
   /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
  {Identifier}				   { System.out.println("found agent identifier" + yytext()); yybegin(YYINITIAL); 
                                   return symbol(sym.AGENT_IDENTIFIER, yytext()); }
  {DecDoubleLiteral}   		   { return symbol(sym.DOUBLE, new Double(yytext())); }
 // {DecIntegerLiteral}            { return symbol(sym.INTEGER, new Integer(yytext())); }
  "("                 		   { System.out.println("found ("); return symbol(sym.LPAREN, ")"); }
  ")"                 		   { System.out.println("found )"); return symbol(sym.RPAREN, "("); }
}

<CONSTRAINT> {
  
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  {DecDoubleLiteral}  		   { System.out.println("found " + yytext()); return symbol(CESym.DOUBLE, new Double(yytext())); }
  "*"                  		   { System.out.println("found *"); return symbol(CESym.MULTIPLY, "*"); }
  "/"                  		   { System.out.println("found /"); return symbol(CESym.DIVIDE, "/"); }
  "|"                            { System.out.println("found |"); return symbol(CESym.MOD, "|"); }
  "+"                  		   { System.out.println("found +"); return symbol(CESym.PLUS, "+"); }
  "-"                  		   { System.out.println("found -"); return symbol(CESym.MINUS, "-"); }
  "=="                 		   { System.out.println("found =="); return symbol(CESym.DOUBLEEQUALS, "=="); }
  "<"                  		   { System.out.println("found <"); return symbol(CESym.LESSTHAN, "<"); }
  "<="                 		   { System.out.println("found <="); return symbol(CESym.LESSTHANEQUAL, "<="); }
  ">"                  		   { System.out.println("found >"); return symbol(CESym.GREATERTHAN, ">"); }
  ">="                 		   { System.out.println("found >="); return symbol(CESym.GREATERTHANEQUAL, ">="); }
  "!="                		   { System.out.println("found !="); return symbol(CESym.NOTEQUAL, "!="); }
  "&&"                 		   { System.out.println("found &&"); return symbol(CESym.AND, "&&"); }
  "||"                 		   { System.out.println("found ||"); return symbol(CESym.OR, "||"); }
  "->"                 		   { System.out.println("found ->"); return symbol(CESym.IMPLIES, "->"); }
  "!"                 		   { System.out.println("found !"); return symbol(CESym.NOT, "!"); }
  ";"                 		   { System.out.println("found ;"); return symbol(CESym.SEMI, ";"); } 
  "true"           	   		   { System.out.println("found 'true' "); return symbol(CESym.TRUE, "true"); }
  "false"              		   { System.out.println("found 'false' "); return symbol(CESym.FALSE, "false"); }
  "sqrt"               		   { System.out.println("found sqrt"); return symbol(CESym.SQUAREROOT, "sqrt"); }
  "sqr"               		   { System.out.println("found sqr"); return symbol(CESym.SQUARE, "sqr"); }
  "("                 		   { System.out.println("found ("); return symbol(CESym.LPAREN, "("); }
  ")"                 		   { System.out.println("found )"); return symbol(CESym.RPAREN, ")"); }
  "]"                  		   { System.out.println("]"); yybegin(YYINITIAL); }   // returns to the INITIAL state, as usual no need to return anything as ] is just a helper marker
  {Identifier}		             { System.out.println("found atom identifier" + yytext()); return symbol(CESym.IDENTIFIER, yytext()); }  
}

<EPISTEMIC_CONSTRAINT> {
  
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  {DecDoubleLiteral}   { System.out.println("found " + yytext()); return symbol(MCSym.DOUBLE, new Double(yytext())); }
  "*"                  { System.out.println("found *"); return symbol(MCSym.MULTIPLY, "*"); }
  "/"                  { System.out.println("found /"); return symbol(MCSym.DIVIDE, "/"); }
  "|"                  { System.out.println("found |"); return symbol(MCSym.MOD, "|"); }
  "+"                  { System.out.println("found +"); return symbol(MCSym.PLUS, "+"); }
  "-"                  { System.out.println("found -"); return symbol(MCSym.MINUS, "-"); }
  "=="                 { System.out.println("found =="); return symbol(MCSym.DOUBLEEQUALS, "=="); }
  "<"                  { System.out.println("found <"); return symbol(MCSym.LESSTHAN, "<"); }
  "<="                 { System.out.println("found <="); return symbol(MCSym.LESSTHANEQUAL, "<="); }
  ">"                  { System.out.println("found >"); return symbol(MCSym.GREATERTHAN, ">"); }
  ">="                 { System.out.println("found >="); return symbol(MCSym.GREATERTHANEQUAL, ">="); }
  "!="                 { System.out.println("found !="); return symbol(MCSym.NOTEQUAL, "!="); }
  "&&"                 { System.out.println("found &&"); return symbol(MCSym.AND, "&&"); }
  "||"                 { System.out.println("found ||"); return symbol(MCSym.OR, "||"); }
  "->"                 { System.out.println("found ->"); return symbol(MCSym.IMPLIES, "->"); }
  "!"                  { System.out.println("found !"); return symbol(MCSym.NOT, "!"); }
  ";"                  { System.out.println("found ;"); return symbol(MCSym.SEMI, ";"); } 
  "true"           	   { System.out.println("found 'true' "); return symbol(MCSym.TRUE, "true"); }
  "false"              { System.out.println("found 'false' "); return symbol(MCSym.FALSE, "false"); }
  "sqrt"               { System.out.println("found sqrt"); return symbol(MCSym.SQUAREROOT, "sqrt"); }
  "sqr"                { System.out.println("found sqr"); return symbol(MCSym.SQUARE, "sqr"); }
  "("                  { System.out.println("found ("); return symbol(MCSym.LPAREN, "("); }
  ")"                  { System.out.println("found )"); return symbol(MCSym.RPAREN, ")"); }
  "@"                  { System.out.println("found knows "); yybegin(EP_KN_IDENTIFIER); return symbol(MCSym.KNOWS, "@"); } 
  "#"                  { System.out.println("found everybody knows"); yybegin(EP_KN_IDENTIFIER); return symbol(MCSym.EVERYBODYKNOWS, "#"); } 
  ":>"                  { System.out.println("found distributed knowledge"); yybegin(EP_KN_IDENTIFIER); return symbol(MCSym.DISTKNOWLEDGE, ":>"); } 
  "}"                  { System.out.println("}"); yybegin(YYINITIAL); }   // returns to the INITIAL state, as usual no need to return anything as ] is just a helper marker
  {Identifier}		   { System.out.println("found atom identifier" + yytext()); return symbol(MCSym.IDENTIFIER, yytext()); }
}


<PHISTATE> {
 
  "!"                            { return symbol(sym.NOT, "!"); }
  "("                            { return symbol(sym.LPAREN, "("); }
  ")"                            { return symbol(sym.RPAREN, ")"); }
  "*"                            { return symbol(sym.MULTIPLY, "*"); }
  "/"                            { return symbol(sym.DIVIDE, "/"); }
  "|"                 		   { return symbol(sym.MOD, "|"); }
  "+"                            { return symbol(sym.PLUS, "+"); }
  "-"                            { return symbol(sym.MINUS, "-"); }
  "=="                           { return symbol(sym.DOUBLEEQUALS, "=="); }
  "<"                            { return symbol(sym.LESSTHAN, "<"); }
  "<="                           { return symbol(sym.LESSTHANEQUAL, "<="); }
  ">"                            { return symbol(sym.GREATERTHAN, ">"); }
  ">="                           { return symbol(sym.GREATERTHANEQUAL, ">="); }
  "!="                 		   { return symbol(sym.NOTEQUAL, "!="); }
  "&&"                 		   { return symbol(sym.AND, "&&"); }
  "||"                 		   { return symbol(sym.OR, "||"); }
  "->"                		   { return symbol(sym.IMPLIES, "->"); }
  "true"           			   { return symbol(sym.TRUE, "true"); }
  "false"          			   { return symbol(sym.FALSE, "false"); }
  "sqrt"           			   { return symbol(sym.SQUAREROOT, "sqrt"); }
  "sqr"            			   { return symbol(sym.SQUARE, "sqr"); }
  "]"            			   { System.out.println("out of phi state"); yybegin(YYINITIAL); return symbol(sym.RBRACKET, "]");  }

/* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
  /* identifiers */ 
  {Identifier}                   { return symbol(sym.IDENTIFIER, yytext()); }
  {DecDoubleLiteral}   		   { return symbol(sym.DOUBLE, new Double(yytext())); }
  //{DecIntegerLiteral}            { return symbol(sym.INTEGER, new Integer(yytext())); }
  
 

}

<PHISTATE_KN_CONSTRAINT> {
 
  "!"                            { return symbol(MCSym.NOT, "!"); }
  "("                            { return symbol(MCSym.LPAREN, "("); }
  ")"                            { return symbol(MCSym.RPAREN, ")"); }
  "*"                            { return symbol(MCSym.MULTIPLY, "*"); }
  "/"                            { return symbol(MCSym.DIVIDE, "/"); }
  "|"                            { return symbol(MCSym.MOD, "|"); }
  "+"                            { return symbol(MCSym.PLUS, "+"); }
  "-"                            { return symbol(MCSym.MINUS, "-"); }
  "=="                           { return symbol(MCSym.DOUBLEEQUALS, "=="); }
  "<"                            { return symbol(MCSym.LESSTHAN, "<"); }
  "<="                           { return symbol(MCSym.LESSTHANEQUAL, "<="); }
  ">"                            { return symbol(MCSym.GREATERTHAN, ">"); }
  ">="                           { return symbol(MCSym.GREATERTHANEQUAL, ">="); }
  "!="                 		   { return symbol(MCSym.NOTEQUAL, "!="); }
  "&&"                 		   { return symbol(MCSym.AND, "&&"); }
  "||"                 		   { return symbol(MCSym.OR, "||"); }
  "->"                		   { return symbol(MCSym.IMPLIES, "->"); }
  "true"           			   { return symbol(MCSym.TRUE, "true"); }
  "false"          			   { return symbol(MCSym.FALSE, "false"); }
  "sqrt"           			   { return symbol(MCSym.SQUAREROOT, "sqrt"); }
  "sqr"            			   { return symbol(MCSym.SQUARE, "sqr"); }
  "%"            			   { System.out.println("out of phi state"); yybegin(EPISTEMIC_CONSTRAINT); return symbol(MCSym.BRACKET, "%");  }  
 

/* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
  
  /* identifiers */ 
  {Identifier}                   { return symbol(MCSym.IDENTIFIER, yytext()); }
  {DecDoubleLiteral}   		   { return symbol(MCSym.DOUBLE, new Double(yytext())); }
//  {DecIntegerLiteral}            { return symbol(MCSym.INTEGER, new Integer(yytext())); }
  
 

}


/* error fallback */
.|\n                             { throw new Error("Illegal character <"+
                                                    yytext()+ " at line " + yyline + " at column " + yycolumn + ">"); }
