package com.example.android.calc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    private String str = "";
    private String str2 = "";
    boolean ifcaled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View v) {
        String tag = v.getTag().toString();

        if (tag.equals("AC")) {
            str = "";
            str2 = "";
        } else if(tag.equals("DEL") && str.length()>= 0){
            if(str.length() == 0){
                str = "";
                str2 = "";
            }else {
                str = str.substring(0, str.length() - 1);
                str2 = str2.substring(0, str2.length() - 1);
            }
        }else{
            if(ifcaled){
                str = "";
                str2 = "";
                display();
                ifcaled = false;
            }
            if(tag.equals("*")){
                str2+="×";
                str+= tag;
            }else if(tag.equals("/")){
                str2+="÷";
                str+= tag;
            }else{
                str += tag;
                str2 += tag;
            }
        }
        display();
    }

    public void calc(View v) {
        if(str.charAt(str.length()-1) == '+' || str.charAt(str.length()-1) == '-' || str.charAt(str.length()-1) == '*' || str.charAt(str.length()-1) == '/'){
            return;
        }

        try{
            eval();
            double a = Double.parseDouble(str2);
            if(a == (int)a) {
                str2 = String.valueOf((int)a);
            }
            ifcaled = true;
            display();
        }catch(Exception e){

        }
    }

    public void display() {
        TextView temp = (TextView) findViewById(R.id.bot_screen);
        temp.setText(str2);
    }

    //Below code is revised from Boann in stackoverflow: https://stackoverflow.com/a/26227947/11679813
    public void eval() {
        str2 = String.valueOf(new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse());
    }
}



