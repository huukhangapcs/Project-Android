package com.example.moneyconverter;

public class Calculator {
    public int flag =-1;
    public long operate(long num1, long num2)
    {
        if (flag == 0)
            num1 += num2;
        else if (flag == 1)
            num1 = num2 - num1;
        else if (flag == 2)
            num1 *= num2;
        else if (flag ==3)
        {
            if (num1 == 0)
                num1 = (long) -1;
            else num1 = num2/num1;
        }
        return num1;
    }
    public void setFlag(String s) {
        if (s.equals("+")) flag = 0;
        else if (s.equals("-")) flag =1;
        else if (s.equals("x")) flag = 2;
        else if (s.equals("\u00F7")) flag =3;
    }
    public void resetFlag()
    {
        flag = -1;
    }
    public int getFlag(){return flag;}
}
