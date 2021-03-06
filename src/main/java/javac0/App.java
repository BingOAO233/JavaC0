package javac0;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import javac0.analyser.Analyser;
import javac0.compiler.Compiler;
import javac0.error.C0Error;
import javac0.error.CompileError;
import javac0.tokenizer.token.Token;
import javac0.tokenizer.token.TokenType;
import javac0.tokenizer.Tokenizer;
import javac0.util.program.structure.Program;
import javac0.util.tokenizer.StringIter;
import javac0.util.console.BetterLogger;
import javac0.vm.S0;
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

    public static void main(String[] args) throws C0Error
    {
        // welcome msg
        BetterLogger.notify("Starting ...");

        // arguments parse
        var argParser = getArgParser();
        try
        {
            result = argParser.parseArgs(args);
        } catch (ArgumentParserException e)
        {
            argParser.handleError(e);
            System.exit(2);
            return;
        }

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

        var compiler = new Compiler(analyse(analyser));
        var s0 = compile(compiler);
        if (result.getBoolean("binary"))
        {
            try
            {
                writeBinary(s0, output);
            } catch (IOException e)
            {
                BetterLogger.error("Unable to write file");
                e.printStackTrace();
            }
        }
        if (result.getBoolean("assembly"))
        {
            output.print(s0);
        }
        // end msg
        BetterLogger.success("Complete!");
    }

    private static S0 compile(Compiler compiler)
    {
        S0 s0;
        try
        {
            s0 = compiler.compile();
        } catch (CompileError compileError)
        {
            BetterLogger.error("error when compile");
            compileError.printStackTrace();
            System.exit(1);
            return null;
        }
        BetterLogger.success("S0 Compile Complete");
        return s0;

    }

    private static void writeBinary(S0 s0, PrintStream output) throws IOException, CompileError
    {
        s0.writeBinary(output);
    }

    private static Program analyse(Analyser analyser)
    {
        Program program;
        try
        {
            program = analyser.analyse();
        } catch (C0Error | CloneNotSupportedException c0Error)
        {
            BetterLogger.error(c0Error.toString());
            System.exit(1);
            return null;
        }
        BetterLogger.success("Syntax Analyse Complete");
        return program;
    }

    private static void tokenize(Tokenizer tokenizer) throws C0Error
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

    private static ArgumentParser getArgParser()
    {
        var builder = ArgumentParsers.newFor("java-c0");
        var parser = builder.build();
        parser.addArgument("-t", "--tokenize")
                .help("Tokenize the input")
                .action(Arguments.storeTrue());
        parser.addArgument("-l", "--analyse")
                .help("Analyse the input")
                .action(Arguments.storeTrue());
        parser.addArgument("-s", "--assembly")
                .help("To middle code")
                .action(Arguments.storeTrue());
        parser.addArgument("-c", "--compile")
                .help("Compile the input")
                .action(Arguments.storeTrue());
        parser.addArgument("-b", "--binary")
                .help("Compile to binary")
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
