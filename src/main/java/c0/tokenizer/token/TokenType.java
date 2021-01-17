package c0.tokenizer.token;

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

//    /**
//     * literal type
//     */
    INT64_L,        // int64 literal
    STR_L,          // String literal
    CHAR_L,         // Char literal
    DOUBLE_L,       // float64 literal

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
