import java.util.*;

public class SyntacticAnalyser_SLR1 {
    Set<Character> nonTerminus=new HashSet<>();
    Set<Character> terminus=new HashSet<>();
    List<Productions> productions=new ArrayList<>();
    String[][] table;
    Lexical_analyser analyser;
    Set<String> IDT;
    Set<String> NUM;
    Stack<String> analyseStack;
    public void Start(){
        createAnalyseTable();
        analyseStart();
    }
    private void createAnalyseTable(){
        inputProductions();
        produceSymbleTable();
        createTable();
    }
    private void analyseStart(){
        analyser=new Lexical_analyser();
        analyser.start();
        List<String> outputList=analyser.outputList;
        outputList.add("$");
        NUM=analyser.resultSet.get("NUM");
        IDT=analyser.resultSet.get("IDT");
        analyseStack=new Stack<>();
        analyseStack.push("0");
        for (int i = 0; i <outputList.size() ; i++) {
            String E=signReplace(outputList.get(i));
            int top=Integer.parseInt(analyseStack.peek());
            int column=getColumn(E.charAt(0));
            if (column==-1) {
                error(2, outputList.get(i));
                return;
            }
            String action=table[top][column];
            if (action==null){
                error(1,E);
                return;
            }
            switch(action.charAt(0)){
                case 'r':
                    Productions p=productions.get(Integer.parseInt(action.substring(1)));
                    stackPop(p.getBody().length());
                    top=Integer.parseInt(analyseStack.peek());
                    analyseStack.push(p.getHead());
                    analyseStack.push(table[top][getColumn(p.getHead().charAt(0))]);
                    output(1,p);
                    i--;
                    break;
                case 's':
                    analyseStack.push(E);
                    analyseStack.push(action.substring(1));
                    break;
                case 'A':
                    output(2,null);
                    return;
            }
        }
    }
    private void error(int type,String str){
        switch(type){
            case 1:
                System.out.println("error,赋值语句错误, "+str);
                break;
            case 2:
                System.out.println("error,文法未定义字符:"+str);
                break;
        }
    }
    private void output(int type,Productions p){
        switch (type){
            case 1:
                System.out.println(p.getHead()+" → "+p.getBody());
                break;
            case 2:
                System.out.println("success,分析完成！");
        }

    }
    private void stackPop(int l){
        for (int i = 0; i < 2*l; i++) {
            analyseStack.pop();
        }
    }
    private String signReplace(String c){
        if (NUM!=null&&NUM.contains(c)||IDT!=null&&IDT.contains(c))
            return "i";
        else
            return c;
    }
    private void createTable(){
        table=new String[19][nonTerminus.size()+terminus.size()];
        int column1=getColumn('i');
        int column2=getColumn('=');
        int column3=getColumn('+');
        int column4=getColumn('-');
        int column5=getColumn('*');
        int column6=getColumn('/');
        int column7=getColumn('(');
        int column8=getColumn(')');
        int column9=getColumn('$');
        int column10=getColumn('S');
        int column11=getColumn('E');
        int column12=getColumn('T');
        int column13=getColumn('F');
        table[0][column1]="s2";
        table[0][column10]="1";
        table[1][column9]="Acc";
        table[2][column2]="s3";
        table[3][column1]="s11";
        table[3][column7]="s12";
        table[3][column11]="4";
        table[3][column12]="5";
        table[3][column13]="6";
        table[4][column3]="s7";
        table[4][column4]="s8";
        table[4][column9]="r1";
        table[5][column3]="r4";
        table[5][column4]="r4";
        table[5][column5]="s9";
        table[5][column6]="s10";
        table[5][column8]="r4";
        table[6][column3]="r7";
        table[6][column4]="r7";
        table[6][column5]="r7";
        table[6][column6]="r7";
        table[6][column8]="r7";
        table[6][column9]="r7";
        table[7][column1]="s11";
        table[7][column7]="s12";
        table[7][column12]="15";
        table[7][column13]="6";
        table[8][column1]="s11";
        table[8][column7]="s12";
        table[8][column12]="16";
        table[8][column13]="6";
        table[9][column1]="s11";
        table[9][column7]="s12";
        table[9][column13]="18";
        table[10][column1]="s11";
        table[10][column7]="s12";
        table[10][column13]="17";
        table[11][column3]="r9";
        table[11][column4]="r9";
        table[11][column5]="r9";
        table[11][column6]="r9";
        table[11][column8]="r9";
        table[11][column9]="r9";
        table[12][column1]="s11";
        table[12][column7]="s12";
        table[12][column11]="13";
        table[12][column12]="5";
        table[12][column13]="6";
        table[13][column3]="s7";
        table[13][column4]="s8";
        table[13][column8]="s14";
        table[14][column3]="r8";
        table[14][column4]="r8";
        table[14][column5]="r8";
        table[14][column6]="r8";
        table[14][column8]="r8";
        table[14][column9]="r8";

        table[15][column3]="r2";
        table[15][column4]="r2";
        table[15][column5]="s9";
        table[15][column6]="s10";
        table[15][column8]="r2";
        table[15][column9]="r2";

        table[16][column3]="r3";
        table[16][column4]="r3";
        table[16][column5]="s9";
        table[16][column6]="s10";
        table[16][column8]="r3";
        table[16][column9]="r3";

        table[17][column3]="r6";
        table[17][column4]="r6";
        table[17][column5]="r6";
        table[17][column6]="r6";
        table[17][column8]="r6";
        table[17][column9]="r6";
        table[18][column3]="r5";
        table[18][column4]="r5";
        table[18][column5]="r5";
        table[18][column6]="r5";
        table[18][column8]="r5";
        table[18][column9]="r5";


    }
    private int getColumn(char c){
        int result=-1;
        switch(c){
            case 'i':result=0;
                     break;
            case '=':result=1;
                break;
            case '+':result=2;
                break;
            case '-':result=3;
                break;
            case '*':result=4;
                break;
            case '/':result=5;
                break;
            case '(':result=6;
                break;
            case ')':result=7;
                break;
            case '$':result=8;
                break;
            case 'S':result=9;
                break;
            case 'E':result=10;
                break;
            case 'T':result=11;
                break;
            case 'F':result=12;
                break;
        }
        return result;
    }
    private void produceSymbleTable(){
        nonTerminus.add('S');
        nonTerminus.add('F');
        nonTerminus.add('T');
        nonTerminus.add('E');
        terminus.add('i');
        terminus.add('=');
        terminus.add('+');
        terminus.add('-');
        terminus.add('*');
        terminus.add('/');
        terminus.add('(');
        terminus.add(')');
        terminus.add('$');
    }
    private void inputProductions(){
        productions.add(new Productions("S’","S"));
        productions.add(new Productions("S","i=E"));
        productions.add(new Productions("E","E+T"));
        productions.add(new Productions("E","E-T"));
        productions.add(new Productions("E’","T"));
        productions.add(new Productions("T","T*F"));
        productions.add(new Productions("T’","T/F"));
        productions.add(new Productions("T","F"));
        productions.add(new Productions("F","(E)"));
        productions.add(new Productions("F","i"));
    }
    public static void main(String[] args) {
        SyntacticAnalyser_SLR1 slr0Analyser=new SyntacticAnalyser_SLR1();
        slr0Analyser.Start();
    }
}
class Productions{
    private String head;
    private String body;

    public Productions() {
    }
    public Productions(String head, String body) {
        this.head = head;
        this.body = body;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}