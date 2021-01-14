package c0;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import c0.analyser.Analyser;
import c0.error.CompileError;
import c0.instruction.Instruction;
import c0.tokenizer.Token;
import c0.tokenizer.TokenType;
import c0.tokenizer.Tokenizer;
import c0.util.StringIter;
import c0.util.console.BetterLogger;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App
{
    private static Scanner scanner;
    private static InputStream input;
    private static PrintStream output;
    private static Namespace result;

    public static void main(String[] args) throws CompileError
    {
        // welcome msg
        BetterLogger.notify("Starting ...");

        argumentParse(args);

        streamSetup();

        Scanner scanner = new Scanner(input);
        var iter = new StringIter(scanner);

        var tokenizer = new Tokenizer(iter);
        if (result.getBoolean("tokenize"))
        {
            tokenize(tokenizer);
        }

        var analyser = new Analyser(tokenizer);
        if (result.getBoolean("analyse"))
        {
            analyse(analyser);
        }

        // end msg
        BetterLogger.success("Complete!");
    }

    private static void analyse(Analyser analyser)
    {
        List<Instruction> instructions;
        try
        {
            instructions = analyser.analyse();
        } catch (CompileError compileError)
        {
            BetterLogger.error(compileError.toString());
            System.exit(1);
            return;
        }
        for (Instruction instruction : instructions)
        {
            output.println(instruction.toString());
        }
    }

    private static void tokenize(Tokenizer tokenizer) throws CompileError
    {
        BetterLogger.notify("Tokenizing...");
        var tokens = new ArrayList<Token>();
        try
        {
            while (true)
            {
                var token = tokenizer.nextToken();
                if (token.getTokenType().equals(TokenType.EOF))
                {
                    break;
                }
                tokens.add(token);
            }
        } catch (Exception e)
        {
            BetterLogger.error(e.toString());
            System.exit(1);
            return;
        }
        for (Token token : tokens)
        {
            output.println(token.toString());
        }
    }

    private static void streamSetup()
    {
        var inputFileName = result.getString("input");
        var outputFileName = result.getString("output");

        if (inputFileName.equals("-"))
        {
            input = System.in;
        }
        else
        {
            try
            {
                input = new FileInputStream(inputFileName);
            } catch (FileNotFoundException e)
            {
                BetterLogger.error("Cannot find input file!");
                e.printStackTrace();
                System.exit(2);
                return;
            }
        }

        if (outputFileName.equals("-"))
        {
            output = System.out;
        }
        else
        {
            try
            {
                output = new PrintStream(new FileOutputStream(outputFileName));
            } catch (FileNotFoundException e)
            {
                BetterLogger.error("Cannot open output file!");
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    private static void argumentParse(String[] args)
    {
        var argParser = getArgParser();
        try
        {
            result = argParser.parseArgs(args);
        } catch (ArgumentParserException e)
        {
            argParser.handleError(e);
            System.exit(2);
        }
    }

    private static ArgumentParser getArgParser()
    {
        var builder = ArgumentParsers.newFor("java-c0");
        var parser = builder.build();
        parser.addArgument("-t", "--tokenizer")
                .help("Tokenize the input")
                .action(Arguments.storeTrue());
        parser.addArgument("-l", "--analyse")
                .help("Analyse the input")
                .action(Arguments.storeTrue());
        parser.addArgument("-o", "--output")
                .help("Set the output file")
                .required(true)
                .dest("output")
                .action(Arguments.store());
        parser.addArgument("file")
                .help("Input File")
                .required(true)
                .dest("input")
                .action(Arguments.store());

        return parser;
    }
}
