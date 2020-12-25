package c0.analyser;

import java.util.Stack;

public class OPGAnalyser
{
    private final char[] source;
    private int pointer;
    private char peekChar;
    private Stack<Character> opStack = new Stack<>();
    private int[][] matrix = new int[][]{
            {-1, 0, -1, -1, -1, -2},//(
            {-2, 1, 1, 1, -2, 1},//)
            {-1, 1, 1, 1, -1, 1},//*
            {-1, 1, -1, 1, -1, 1},//+
            {-2, 1, 1, 1, -2, 1},//i
            {-1, -1, -1, -1, -1, -1}//#
    };
    //private HashMap<Character, Integer> cMap = new HashMap<>();

    OPGAnalyser(String seq)
    {
        seq = seq.replace('\r', '#');
        seq = seq.replace('\n', '#');
        //System.out.println(seq.toCharArray());
        source = seq.toCharArray();
        pointer = -1;
        peekChar = 0;
        opStack.push('#');
    }

    private char peekChar()
    {
        if (pointer + 1 < source.length)
        {
            if (peekChar == 0)
                peekChar = source[pointer + 1];
            return peekChar;
        }
        else
            return 0;
    }

    private char nextChar()
    {
        pointer++;
        peekChar = 0;
        return source[pointer];
    }

    private void stackPush(char c)
    {
        opStack.push(c);
        System.out.println("I" + c);
    }

    private boolean stackPopI()
    {
        opStack.pop();
        opStack.push('E');
        // TODO: OPG Reduction Operations
//        System.out.println('R');

        return true;
    }

    private boolean stackPopP()
    {
        if (opStack.peek() != 'E')
            return false;
        opStack.pop();
        if (opStack.peek() != '+' && opStack.peek() != '*')
            return false;
        opStack.pop();
        if (opStack.peek() != 'E')
            return false;
        opStack.pop();
        opStack.push('E');
        // TODO: OPG Reduction Operations
//        System.out.println('R');
        return true;
    }

    private boolean stackPopR()
    {
        if (opStack.peek() != ')')
            return false;
        opStack.pop();
        if (opStack.peek() != 'E')
            return false;
        opStack.pop();
        if (opStack.peek() != '(')
            return false;
        opStack.pop();
        opStack.push('E');
        // TODO: OPG Reduction Operations
//        System.out.println('R');
        return true;
    }

    private char stackTop()
    {
        if (judge(opStack.peek()))
            return opStack.peek();
        char temp = opStack.pop();
        char res = opStack.peek();
        opStack.push(temp);
        return res;
    }

    private int compareOp(char a, char b)
    {
        int ia = getIndex(a), ib = getIndex(b);
        return matrix[ia][ib];
    }

    private int getIndex(char c)
    {
        switch (c)
        {
            case '(':
                return 0;
            case ')':
                return 1;
            case '*':
                return 2;
            case '+':
                return 3;
            case 'i':
                return 4;
            case '#':
                return 5;
        }
        return -1;
    }

    private boolean judge(char c)
    {
        return c == '(' || c == ')' || c == '*' || c == '+' || c == 'i' || c == '#';
    }

    private void run()
    {
        while (peekChar() != '#')
        {
            char readIn = nextChar();
            stackPush(readIn);
            if (!judge(readIn) || !judge(peekChar()) || compareOp(stackTop(), peekChar()) == -2)
            {
                // TODO: Handler
//                System.out.println("E");
                return;
            }
            while (compareOp(stackTop(), peekChar()) == 1)
            {
                if (stackTop() == '+' || stackTop() == '*')
                    if (!stackPopP())
                    {
                        // TODO: Error Handler
//                        System.out.println("RE");
                        return;
                    }
                    else
                        continue;
                if (stackTop() == ')')
                    if (!stackPopR())
                    {
                        // TODO: Error Handler
//                        System.out.println("RE");
                        return;
                    }
                    else
                        continue;
                if (stackTop() == 'i')
                    stackPopI();
            }
        }
        if (compareOp(stackTop(), peekChar()) == -2)
        {
            // TODO: Handler
//            System.out.println("E");
            return;
        }
        while (stackTop() != '#')
        {
            if (stackTop() == '+' || stackTop() == '*')
                if (!stackPopP())
                {
                    // TODO: Error Handler
//                    System.out.println("RE");
                    return;
                }
            if (stackTop() == ')')
                if (!stackPopR())
                {
                    // TODO: Error Handler
//                    System.out.println("RE");
                    return;
                }
        }
    }

//
//    public static void main(String[] args) throws IOException {
//        OPGAnalyser c0.analyser = new OPGAnalyser(fileRead(args[0]));
//        c0.analyser.run();
//    }
//
//    public static String fileRead(String path) throws IOException {
//        StringBuilder buf = new StringBuilder();
//        //buf.append('#');
//        File file = new File(path);
//        FileReader fr = new FileReader(file);
//        long len = file.length();
//        char[] temp = new char[(int) len + 1];
//        fr.read(temp);
//        buf.append(String.valueOf(temp));
//        //buf.append('#');
//        fr.close();
//        //System.out.println(buf);
//        return buf.toString();
//    }
}