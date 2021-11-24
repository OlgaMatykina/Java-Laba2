import java.math.BigDecimal;
import java.util.*;


/**
 * This class allows you to check if the expression is correct and calculate the value
 */

public class Calculater {


    final int NONE = 0;
    final int DELIMITER = 1;
    final int VARIABLE = 2;
    final int NUMBER = 3;

    final String SYNTAX = "Syntax Error";
    final String UNPAIR_BRACKETS = "Unpair Brackets Error";
    final String NO_EXP = "No Expression Presented";
    final String DIV_BY_ZERO = "Division by Zero";

    final String end_of_exp = "\0";
    private String exp;
    private int exp_index;
    private String cur_char;
    private String pre_char;
    private int cur_char_type;
    private int pre_char_type;



    /**
     * analyses current character in expression
     */
    private void getCurrentCharacter() throws Exception{
        cur_char = "";
        cur_char_type = NONE;
        if (exp_index == exp.length()) {
            pre_char=cur_char;
            cur_char = end_of_exp;
            return;
        }
        while (exp_index < exp.length() && Character.isWhitespace(exp.charAt(exp_index))) //пропускаем пробелы
            ++exp_index;
        if (exp_index == exp.length()) {
            cur_char = end_of_exp;
            return;
        }
        if (isDelimiter(exp.charAt(exp_index))) {
            if(exp_index==exp.length()-1 && exp.charAt(exp_index)!=')') throw new Exception(SYNTAX);
            else {
                cur_char = Character.toString(exp.charAt(exp_index));
                exp_index++;
                cur_char_type = DELIMITER;
            }
        } else if (Character.isLetter(exp.charAt(exp_index)) || exp.charAt(exp_index)=='_' || exp.charAt(exp_index)=='$') { //если переменная
            while (Character.isLetter(exp.charAt(exp_index)) || Character.isDigit(exp.charAt(exp_index)) || exp.charAt(exp_index)=='_' || exp.charAt(exp_index)=='$') {
                cur_char += exp.charAt(exp_index);
                exp_index++;
                if (exp_index >= exp.length())
                    break;
            }
            cur_char_type = VARIABLE;
        } else if (Character.isDigit(exp.charAt(exp_index))) {
            boolean beforeComma=true;
            while (Character.isDigit(exp.charAt(exp_index)) || (beforeComma && exp.charAt(exp_index)==',' ) ){
                if (exp.charAt(exp_index)==',') beforeComma=false;
                cur_char += exp.charAt(exp_index);
                exp_index++;
                if (exp_index >= exp.length())
                    break;
            }
            cur_char_type = NUMBER;
        } else {
            throw new Exception(SYNTAX);
        }
    }

    /**
     * analyses if parameter is delimiter
     * @param c type char
     * @return true if c is delimiter and false otherwise
     */
    private boolean isDelimiter(char c) {
        return "+-/*^()".indexOf(c) != -1;
    }

    /**
     * analyses if parameter is binary operator
     * @param str type String
     * @return true if c is binary operator and false otherwise
     */

    private boolean isBinaryOperator(String str){
        return (str.equals("+") || str.equals("-") || str.equals("/") || str.equals("*") || str.equals("^"));
    }

    /**
     * converts infix expression to postfix
     * @param str type String
     * @return postfix expression
     * @throws Exception
     */

    private ArrayList<String> infixToPostfix (String str) throws Exception{
        Map<String, Integer> priorities = new HashMap<String, Integer>();
        priorities.put("-u",5);
        priorities.put("^",4);
        priorities.put("*",3);
        priorities.put("/",3);
        priorities.put("+",2);
        priorities.put("-",2);
        priorities.put("(",1);

        Stack<String> operators = new Stack<String>();
        ArrayList<String> postExp = new ArrayList<String>();
        exp = str;
        exp_index = 0;
        pre_char="";
        pre_char_type=NONE;

        getCurrentCharacter();
        if (cur_char.equals(end_of_exp))
            throw new Exception(NO_EXP);
        else {
            while (!cur_char.equals(end_of_exp)){
                switch (cur_char_type){
                    case VARIABLE -> {
                        if (!(pre_char_type==NUMBER || pre_char_type==VARIABLE)) {
                            postExp.add(cur_char);
                        }
                        else throw new Exception(SYNTAX);
                    }
                    case NUMBER -> {
                        if (!(pre_char_type==NUMBER || pre_char_type==VARIABLE)) postExp.add(cur_char);
                        else throw new Exception(SYNTAX);
                    }
                    case DELIMITER -> {
                        switch (cur_char){
                            case "-","+" -> {
                                if (pre_char_type==NUMBER || pre_char_type==VARIABLE) {//бинарные операции
                                    while (!operators.empty() && priorities.get(cur_char) <= priorities.get(operators.peek()))
                                        postExp.add(operators.pop());
                                    operators.push(cur_char);
                                }
                                else if (pre_char_type==NONE || pre_char.equals("("))//унарные операции
                                    operators.push(cur_char+'u');
                                else throw new Exception(SYNTAX);
                            }
                            case "*","/","^"-> {
                                if (pre_char_type==NUMBER || pre_char_type==VARIABLE || pre_char.equals(")")) {//бинарные операции
                                    while (!operators.empty() && priorities.get(cur_char) <= priorities.get(operators.peek()))
                                        postExp.add(operators.pop());
                                    operators.push(cur_char);
                                }
                                else throw new Exception(SYNTAX);
                            }
                            case "(" -> {
                                if (pre_char_type==DELIMITER || pre_char.equals(""))
                                    operators.push(cur_char);
                                else throw new Exception(UNPAIR_BRACKETS);
                            }
                            case ")" ->{
                                if (pre_char_type==NUMBER || pre_char_type==VARIABLE){
                                    while (!operators.empty() && !operators.peek().equals("(")) postExp.add(operators.pop());
                                    if (operators.empty()) throw new Exception(SYNTAX);
                                    operators.pop();
                                }
                                else throw new Exception(SYNTAX);
                            }


                        }
                    }
                }
                pre_char=cur_char;
                pre_char_type=cur_char_type;
                getCurrentCharacter();
            }
            while(!operators.empty()) {
                if (operators.peek().equals("("))
                    throw new Exception(UNPAIR_BRACKETS);
                postExp.add(operators.pop());
            }
        }
        return postExp;
    }

    /**
     * calculates the value of expression if it is possible
     * @param str type String, scanner type Scanner
     * @return value of expression
     * @throws Exception
     */


    public BigDecimal calculate (String str) throws Exception{
        ArrayList<String> postExp = infixToPostfix(str);

         // System.out.println(toString(postExp));

        Scanner scanner = new Scanner(System.in);
        BigDecimal op1, op2;
        Stack<BigDecimal> operands = new Stack<BigDecimal>();
        Map<String, BigDecimal> variables = new HashMap<String, BigDecimal>();
        for (int i=0; i< postExp.size(); i++){
            String curElemOfPostfix = postExp.get(i);
            if (isBinaryOperator(curElemOfPostfix)){
                op2=operands.pop();
                op1=operands.pop();
                switch (curElemOfPostfix){
                    case "+" -> operands.push(op1.add(op2));
                    case "-" -> operands.push(op1.subtract(op2));
                    case "*" -> operands.push(op1.multiply(op2));
                    case "^" -> {
                        try{
                            operands.push(op1.pow(op2.intValueExact()));
                        } catch(NumberFormatException exc) {
                            throw new Exception(SYNTAX);
                        }
                    }
                    case "/" -> {
                        if (!op2.equals(BigDecimal.valueOf(0))) operands.push(op1.divide(op2));
                        else throw new Exception(DIV_BY_ZERO);
                    }
                }
            }
            else if (curElemOfPostfix.equals("-u")){
                op1=operands.pop();
                operands.push(op1.multiply(BigDecimal.valueOf(-1)));
            }
            else if (Character.isDigit(curElemOfPostfix.charAt(0))) {
                try {
                    op1 = new BigDecimal(curElemOfPostfix.replace(",", "."));
                } catch (NumberFormatException exc) {
                    throw new Exception(SYNTAX);
                }
                operands.push(op1);
            }
            else{
                if (!variables.containsKey(curElemOfPostfix)) {
                    System.out.println("Введите значение для переменной " + curElemOfPostfix); //вопрос по использованию консоли
                    try {
                        op1 = scanner.nextBigDecimal();
                    }catch (InputMismatchException exc) {
                        throw new Exception(SYNTAX);
                    }
                    variables.put(curElemOfPostfix,op1);
                }
                else op1=variables.get(curElemOfPostfix);
                operands.push(op1);
            }
        }
        return operands.pop().setScale(2);
    }

    private String toString(ArrayList<String> postExp) {
        String result = new String();

        for (int i = 0; i < postExp.size(); i++) {
            result+=postExp.get(i)+" ";

        }
        return result;
    }
}