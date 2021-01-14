package c0.tokenizer;

public enum TokenType
{
    /**
     * auxiliary
     */
    NONE,       // empty
    IDENT,      // identifier
    COMMENT,    // comment
    EOF,        // end of file

    /**
     * data type
     */
    INT64,      // int64
    STR,        // string
    CHAR,       // char
    DOUBLE,     // float64
    VOID,       // void
    FUNC,       // function

    /**
     * keywords
     */
    FN_KW,          // fn
    LET_KW,         // let
    CONST_KW,       // const
    AS_KW,          // as
    WHILE_KW,       // while
    IF_KW,          // if
    ELSE_KW,        // else
    RETURN_KW,      // return
    BREAK_KW,       // break
    CONTINUE_KW,    // continue

    /**
     * operators
     */
    PLUS,       // +
    MINUS,      // -
    MUL,        // *
    DIV,        // /
    ASSIGN,     // =
    EQ,         // ==
    NEQ,        // !=
    LT,         // <
    LE,         // <=
    GT,         // >
    GE,         // >=
    L_PAREN,    // (
    R_PAREN,    // )
    L_BRACE,    // {
    R_BRACE,    // }
    ARROW,      // ->
    COMMA,      // ,
    COLON,      // :
    SEMICOLON,  // ;
}
