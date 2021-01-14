package c0.analyser;

import c0.error.AnalyseError;
import c0.error.*;
import c0.instruction.Instruction;
import c0.tokenizer.Token;
import c0.tokenizer.TokenType;
import c0.tokenizer.Tokenizer;
import c0.util.Position;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analyser
{
    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;

    Token peekedToken = null;

    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer)
    {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> analyse() throws CompileError
    {
        analyseProgram();
        return instructions;
    }

    private Token peek() throws TokenizeError
    {
        if (peekedToken == null)
        {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    private Token next() throws TokenizeError
    {
        if (peekedToken != null)
        {
            var token = peekedToken;
            peekedToken = null;
            return token;
        }
        else
        {
            return tokenizer.nextToken();
        }
    }

    private boolean check(TokenType tt) throws TokenizeError
    {
        var token = peek();
        return token.getTokenType() == tt;
    }

    private Token nextIf(TokenType tt) throws TokenizeError
    {
        var token = peek();
        if (token.getTokenType() == tt)
        {
            return next();
        }
        return null;

    }

    private Token expect(TokenType tt) throws CompileError
    {
        var token = peek();
        if (token.getTokenType() == tt)
        {
            return next();
        }
        throw new UnexpectedTokenError(tt, token);

    }

    private int getNextVariableOffset()
    {
        return this.nextOffset++;
    }

    private void addSymbol(String name, boolean isInitialized, boolean isConstant, Position curPos) throws AnalyseError
    {
        if (this.symbolTable.get(name) != null)
        {
            throw new AnalyseError(ErrorCode.DuplicatedDeclaration, curPos);
        }
        else
        {
            this.symbolTable.put(name, new SymbolEntry(isConstant, isInitialized, getNextVariableOffset()));
        }
    }

    private void initializeSymbol(String name, Position curPos) throws AnalyseError
    {
        SymbolEntry entry = this.symbolTable.get(name);
        if (entry == null)
        {
            throw new AnalyseError(ErrorCode.NoDeclaration, curPos);
        }
        else
        {
            entry.setInitialized(true);
        }
    }

    private int getOffset(String name, Position curPos) throws AnalyseError
    {
        SymbolEntry entry = this.symbolTable.get(name);
        if (entry == null)
        {
            throw new AnalyseError(ErrorCode.NoDeclaration, curPos);
        }
        else
        {
            return entry.getStackOffset();
        }
    }

    private boolean isConstant(String name, Position curPos) throws AnalyseError
    {
        SymbolEntry entry = this.symbolTable.get(name);
        if (entry == null)
        {
            throw new AnalyseError(ErrorCode.NoDeclaration, curPos);
        }
        else
        {
            return entry.isConstant();
        }
    }

    private void analyseProgram() throws CompileError
    {
        // program -> item*
        while (check(TokenType.FN_KW)
                || check(TokenType.CONST_KW)
                || check(TokenType.LET_KW))
        {
            analyseItem();
        }
    }

    private void analyseItem() throws CompileError
    {
        if (check(TokenType.FN_KW))
        {
            analyseFunction();
        }
        else if (check(TokenType.CONST_KW))
        {
            analyseConstDeclaration();
        }
        else if (check(TokenType.LET_KW))
        {
            analyseLetDeclaration();
        }
        else
        {
            throw new UnexpectedTokenError(new TokenType[]{TokenType.FN_KW, TokenType.CONST_KW, TokenType.LET_KW}, peek());
        }
    }

    private void analyseFunction()
    {
        /*
            function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
            function_param_list -> function_param (',' function_param)*
            function_param -> 'const'? IDENT ':' ty
         */

    }

    private void analyseConstDeclaration()
    {

    }

    private void analyseLetDeclaration()
    {

    }

}
