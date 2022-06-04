import java.io.*;
import java.util.*;

public class Lexical_analyser {
    int line=0;
    boolean aRowAnnotation=false;
    boolean someRowAnnotation=false;

    String s;
    StringBuilder word;
    boolean error=false;
    char[] operator1={'+','-','*','%','/','^'};
    char[] operator2={'=','!','<','>'};
    char[] operator3={'&','|'};
    char[] separator={'{','}','[',']','(',')',';',','};
    String[] keyword={"int","const","void","return","if","while","break","continue","main","printf"};

    Map<String, Set<String>> resultSet=new HashMap<>();
    List<String> outputList=new ArrayList<>();
    private void output(String type,String word){
        System.out.println("<"+type+","+word+">");
        //添加输出结果集合
        if (word.equals("_"))
            outputList.add(type);
        else
            outputList.add(word);
        //添加分类集合
        if (resultSet.containsKey(type)){
            if (!word.equals("_"))
                resultSet.get(type).add(word);
            else
                resultSet.get(type).add(type);
        }else{
            Set<String> set=new HashSet<>();
            if (!word.equals("_"))
                set.add(word);
            else
                set.add(type);
            resultSet.put(type,set);
        }
    }
    public void start(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入源文件名:");
        String fileName = scanner.next();
        FileReader reader=null;
        BufferedReader bufferedReader=null;
        try {
            reader=new FileReader("./src/"+fileName);
            bufferedReader=new BufferedReader(reader);
            String strLine;
            while((strLine=bufferedReader.readLine())!=null){
                this.line++;
                this.aRowAnnotation=false;
                String[] strArray= strLine.trim().split(" ");
                for(String s :strArray)
                    if (!s.trim().equals("")&&!this.aRowAnnotation) {
                        this.s=s;
                        this.Start(0);
                    }
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在！");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (bufferedReader!=null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void identOutPut(String word){
        for (int i = 0; i < keyword.length; i++) {
            if (word.equals(keyword[i])) {
                output("KEY",word);
                return;
            }
        }
        output("IDT",word);

    }
    private String hexadecimalToDecimal(String num16){
            int num10=0;
            int weight=0;
        for (int i = num16.length()-1; i >1; i--) {
            char c = num16.charAt(i);
            int num;
            if (c>='A'&&c<='Z'){
                num=((c-'A')+10)*(int)Math.pow(16,weight);
            }else if(c>='a'&&c<='z'){
                num=((c-'a')+10)*(int)Math.pow(16,weight);
            }else{
                num=(c-'0')*(int)Math.pow(16,weight);
            }
            num10+=num;
            weight++;
        }
        return String.valueOf(num10);
    }
    private String octalToDecimal(String num8){
        int num10=0;
        int weight=0;
        for (int i = num8.length()-1; i >0 ; i--) {
            num10+=(num8.charAt(i)-'0')*(int)Math.pow(8,weight);
            weight++;
        }
        return String.valueOf(num10);
    }
    private void error(String type,String message){
        System.out.println("Error: "+type+" at line "+line+"： "+message+"");
        this.error=true;
    }
    private boolean contains(char[] chars,char c){
        for (int i = 0; i < chars.length; i++) {
            if (c==chars[i])
                return true;
        }
        return false;
    }
    private char kind(char c){
        if (c=='0')
            return '0';
        else if (c>='1'&&c<='9')
            return '9';
        else if (c=='"')
            return '"';
        else if ((c>='a'&&c<='z')||(c>='A'&&c<='Z'))
            return 'n';
        else if (contains(operator1,c))
            return '+';
        else if (contains(operator2,c))
            return '=';
        else if (contains(operator3,c))
            return '&';
        else if (contains(separator,c))
            return '#';

        return ' ';
    }
    private void Start(int i){
        if (someRowAnnotation){
            State9(i);
            return;
        }
        word=new StringBuilder();
        word.append(s.charAt(i));
        switch (kind(s.charAt(i))){
            case '#':
                State1(i+1);
                break;
            case '+':
                State11(i+1);
                break;
            case 'n':
                State2(i+1);
                break;
            case '0':
                State3(i+1);
                break;
            case '=':
                State16(i+1);
                break;
            case '&':
                State12(i+1);
                break;
            case '9':
                State18(i+1);
                break;
            case '"':
                State5(i+1);
                break;
            default:
                error("字符错误","未定义的字符"+s.charAt(i));
        }
    }
    private void State1(int i){
        output("SEP",word.toString());
        if (i<s.length())
            Start(i);
    }
    private void State2(int i){
        if (i==s.length())
            identOutPut(word.toString());
        else{
            char cType=kind(s.charAt(i));
            if (cType=='n'||cType=='0'||cType=='9'){
                word.append(s.charAt(i));
                State2(i+1);
            }else{
                identOutPut(word.toString());
                Start(i);
            }
        }
    }
    private void State3(int i) {
        if (i == s.length())
            output("NUM", octalToDecimal(word.toString()));
        else if (s.charAt(i) <= '7' && s.charAt(i) >= '0') {
            word.append(s.charAt(i));
            State3(i + 1);
        } else if (s.charAt(i) == 'x'&&word.length()==1){
            word.append(s.charAt(i));
            State4(i+1);
        }
        else{
                output("NUM", octalToDecimal(word.toString()));
                if (kind(s.charAt(i))=='n')
                    error("整数定义错误","缺少;");
                Start(i);
        }
    }
    private void State4(int i){
        if (i == s.length())
            output("NUM", hexadecimalToDecimal(word.toString()));
        else if (s.charAt(i) <= '9' && s.charAt(i) >= '0'||s.charAt(i)>'a'&&s.charAt(i)<='f'||s.charAt(i)>='A'&&s.charAt(i)<='F') {
            word.append(s.charAt(i));
            State4(i + 1);
        }
        else{
            output("NUM", hexadecimalToDecimal(word.toString()));
            if (kind(s.charAt(i))=='n')
                error("整数定义错误","缺少;");
                Start(i);
        }
    }
    private void State5(int i){
        if (i==s.length())
            error("String","字符串未闭合");
        else if (s.charAt(i)!='"'){
            word.append(s.charAt(i));
            State5(i+1);
        }else{
            word.append(s.charAt(i));
            output("String",word.toString());
            if (i+1<s.length())
                Start(i+1);
        }
    }
    private void State6(int i){
        aRowAnnotation=true;
    }
    private void State7(int i){
        someRowAnnotation=true;
    }
    private void State9(int i){
        if (i==s.length())
            return;
        if (s.charAt(i)=='*')
            State10(i+1);
        else
            State9(i+1);
    }
    private void State10(int i){
        if (s.charAt(i)=='/') {
            someRowAnnotation=false;
            if (i+1<s.length())
            Start(i + 1);
        }
        else
            State9(i+1);
    }
    private void State11(int i){
        if (i==s.length())
            output(word.toString(),"_");
        else if (s.charAt(i-1)!='/'){
            output(word.toString(),"_");
            if (i<s.length())
                Start(i);
        }
        else if (s.charAt(i)=='/'){
            word.append(s.charAt(i));
            State6(i+1);
        }
        else if (s.charAt(i)=='*'){
            word.append(s.charAt(i));
            State7(i+1);
        }
        else{
            output(word.toString(),"_");
            if (i<s.length())
                Start(i);
        }
    }
    private void State12(int i){
        word.append(s.charAt(i));
        if (s.charAt(i)==s.charAt(i-1)){
            output(word.toString(),"_");
            if (i+1<s.length())
                Start(i+1);
        }
        else
            error("运算符错误","错误的运算符"+word.toString());
    }
    private void State16(int i){
        if (i<s.length()&&s.charAt(i)=='=') {
            word.append('=');
            State17(i + 1);
        }
        else {
            output(word.toString(), "_");
            if (i<s.length())
                Start(i);
        }
    }
    private void State17(int i){
        output(word.toString(),"_");
        if (i<s.length())
            Start(i);
    }
    private void State18(int i){
        if (i == s.length())
            output("NUM", word.toString());
        else if (s.charAt(i) <= '9' && s.charAt(i) >= '0') {
            word.append(s.charAt(i));
            State18(i + 1);
        }
        else{
            output("NUM", word.toString());
            if (kind(s.charAt(i))=='n')
                error("定义错误","缺少;");
            Start(i);
        }
    }
    public static void main(String[] args) {
        Lexical_analyser analyser=new Lexical_analyser();
        analyser.start();
    }
}
