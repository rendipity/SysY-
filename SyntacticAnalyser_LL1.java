import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SyntacticAnalyser_LL1 {
    String productionFilename="productions.sy";
    String beginSign=null;
    Map<String,List<String>> productions=new HashMap<>();
    Map<String,Set<Character>> first=new HashMap<>();
    Map<String,Set<Character>> follow=new HashMap<>();
    Set<String> non_terminals,terminals;
    String[][] table;
    Lexical_analyser lexical_analyser;
    Set<String> num;
    Set<String> ident;
    Stack<String> analyseStack;
    int errorNum=0;
    public void Start() {
        if (!createAnalyserTable()){
            System.out.println("该文法不能转化为LL(1)文法");
            return;
        }
        analyse();
    }
    private boolean createAnalyserTable(){
        getProduction();
        produceSymbleTable();
        removeLeftRecursion();
        removeCommonLeftFactor();
        getFirsts();
        getFollows();
        return CreateTable();
    }
    private void analyse(){
        if (!input())
            return;
        analyseStack=new Stack<>();
        analyseStack.push("$");
        analyseStack.push(beginSign);
        int i=0;
        for(;!analyseStack.peek().equals("$");i++){
            String E=analyseStack.peek();
            String nonTerminus=i<lexical_analyser.outputList.size()?lexical_analyser.outputList.get(i):"$";
            while(!E.equals("$")&&!terminals.contains(E)){
                int row=rowIndex(E);
               nonTerminus=i<lexical_analyser.outputList.size()?lexical_analyser.outputList.get(i):"$";
                int column=columnIndex(signReplace(nonTerminus));
                if (row==-1) {
                    error(3, E);
                    return;
                }
                else if(column==-1) {
                    error(3, lexical_analyser.outputList.get(i));
                    return;
                }
                String body=table[row][column];
                if (body==null) {
                    error(1,lexical_analyser.outputList.get(i));
                    i++;
                }else if (body.equals("synch")){
                    error(2,E);
                    analyseStack.pop();
                    E=analyseStack.peek();
                }
                else{
                    String head=analyseStack.pop();
                    pushBody(body,0);
                    E=analyseStack.peek();
                    System.out.println(head+"→"+body);
                }
            }
            String id=analyseStack.pop();
            if (id.charAt(0)!=signReplace(nonTerminus)) {
                error(2, id);
                i--;
            }else{
                System.out.println("匹配"+nonTerminus);
            }
            if (id.equals("$"))
                break;
        }
            System.out.println("语法分析完成,存在"+errorNum+"处语法错误!");
    }
    private void pushBody(String body,int start){
        if (start>=body.length()||body.equals("ε"))
            return;
        String elem=getOneSign(body,start);
        pushBody(body,start+elem.length());
        analyseStack.push(elem);
    }
    private void error(int type,String terminal){
        errorNum++;
       switch (type){
           case 1:
               System.out.println("error,忽略"+terminal+" 继续分析");
               break;
           case 2:
               System.out.println("error,弹出"+terminal+" 继续分析");
               break;
           case 3:
               System.out.println("error,文法未定义字符"+terminal);
               break;
           case 4:
               System.out.println("error,表达式错误");
       }
    }
    private char signReplace(String terminal){
        if (ident!=null&&ident.contains(terminal)||num!=null&&num.contains(terminal))
            return 'i';
        else
            return terminal.charAt(0);
    }
    private void getProduction(){
        if (this.productionFilename==null){
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入文法文件名:");
            this.productionFilename = scanner.next();
        }
        FileReader reader;
        BufferedReader bufferedReader;
        try {
            reader=new FileReader("./src/"+this.productionFilename);
            bufferedReader=new BufferedReader(reader);
            String production;
            while( (production=bufferedReader.readLine())!=null){
                int divide=production.indexOf('→');
                String head=production.substring(0,divide).trim();
                String body=production.substring(divide+1).trim();
                List<String> bodys=new ArrayList<>();
                int end;
                while(true){
                    end=body.indexOf('|');
                    if (end==-1)
                        break;
                    bodys.add(body.substring(0,end).trim());
                    body=body.substring(end+1);
                }
                if (beginSign==null)
                    beginSign=head;
                bodys.add(body.trim());
                productions.put(head,bodys);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void produceSymbleTable(){
        non_terminals=productions.keySet();
        terminals=new HashSet<>();
        for(Map.Entry<String,List<String>> entry: productions.entrySet() ){
            List<String> bodys=entry.getValue();
            for (int i = 0; i < bodys.size(); i++) {
                String body=bodys.get(i);
                int begin=0;
                while(begin<body.length()){
                    String one = getOneSign(body,begin);
                    if (!non_terminals.contains(one)&&!one.equals("ε"))
                        terminals.add(one);
                    begin+=one.length();
                }
            }
        }
    }

    private String getOneSign(String body,int begin){
        int i = begin+1;
        while(i<body.length()&&body.charAt(i)==(char)8217){
            i++;
        }
        return body.substring(begin,i);
    }

    private void removeLeftRecursion(){

    }

    private void removeCommonLeftFactor(){

    }

    private Set<Character> first(String body){
        Set<Character> chars=new HashSet<>();
        if (body==null||body.equals(""))
            return chars;
        int start = 0;
        String firstOne = getOneSign(body,start);
        if (terminals.contains(firstOne))
            chars.add(firstOne.charAt(0));
        else if (!firstOne.equals("ε")){
            List<String> bodys=productions.get(firstOne);
            for (int i = 0; i < bodys.size(); i++) {
                chars.addAll(first(bodys.get(i)));
            }
        }
        return chars;
    }

    private void getFirsts(){
        for(Map.Entry<String,List<String>> entry:productions.entrySet()){
            List<String> bodys=entry.getValue();
            Set<Character> chars=new HashSet<>();
            for (String body : bodys) {
                if (body == null)
                    break;
                chars.addAll(first(body));
            }
            first.put(entry.getKey(),chars);
        }
        for(String s:terminals){
            Set<Character> set=new HashSet<>();
            set.add(s.charAt(0));
            first.put(s,set);
        }
    }

    private boolean empty(String one){
        List<String> bodys=productions.get(one);
        for(String body:bodys){
            if (body.equals("ε"))
                return true;
            int i=0;
            while(i<body.length()){
                String newSign=getOneSign(body,i);
                if (terminals.contains(newSign)||!empty(newSign))
                    break;
                i+=newSign.length();
            }
            if (i==body.length())
                return true;
        }
        return false;
    }

    private boolean mayEmpty(String subStr){
        if (subStr.equals("ε"))
            return true;
        int i=0;
        while(i<subStr.length()){
            String one=getOneSign(subStr,i);
            if (terminals.contains(one)||!empty(one))
                return false;
            i+=one.length();
        }
        return true;
    }

    private int signContains(String body,String key){
        int loc=body.indexOf(key);
        if (loc+key.length()<body.length()&&body.charAt(loc+key.length())==(char)8217) {
            body=body.substring(loc+key.length()+1);
            loc=body.indexOf(key);
        }
        return loc;
    }

    private Set<Character> follow(String key){
        Set<Character> chars=new HashSet<>();
        if (key.equals(beginSign))
            chars.add('$');
        for(Map.Entry<String,List<String>> entry: productions.entrySet()){
            List<String>bodys=entry.getValue();
            for (int i = 0; i < bodys.size(); i++) {
                String body=bodys.get(i);
                int loc=signContains(body,key);
                if (loc!=-1){
                    String b=body.substring(loc+key.length());
                    if((b.equals("")||mayEmpty(b))){
                        if (!entry.getKey().equals(key)) {
                            chars.addAll(first(b));
                            chars.addAll(follow(entry.getKey()));
                        }
                    }else {
                        String sign=getOneSign(b,0);
                        chars.addAll(first.get(sign));
                    }
                }
            }
        }
        return chars;
    }

    private void getFollows(){
        for(Map.Entry<String,List<String>> entry: productions.entrySet()){
            follow.put(entry.getKey(),follow(entry.getKey()));
        }
    }

    private Set<Character> Select(String head,String body){
        if (body.equals("ε"))
            return follow.get(head);
        String one =getOneSign(body,0);
        Set<Character> first=this.first.get(one);
        if (mayEmpty(body)){
            first.addAll(follow.get(head));
        }
        return first;
    }

    private int rowIndex(String head){
        int i=0;
        for(String nt:non_terminals){
            if (nt.equals(head))
                return i;
            i++;
        }
        return -1;
    }

    private int columnIndex(char terminals){
        int i=0;
        for(String nt:this.terminals){
            if (nt.charAt(0)==terminals)
                return i;
            i++;
        }
        //最后一列是$
        if (terminals=='$')
            return i;
        return -1;
    }

    private boolean CreateTable(){
        table=new String[non_terminals.size()][terminals.size()+1];
        Set<Character> signSet;
        for(Map.Entry<String,List<String>> entry : productions.entrySet()){
            List<String> bodys=entry.getValue();
            String head= entry.getKey();
            for (String body : bodys) {
                signSet = Select(head, body);
                int row = rowIndex(entry.getKey());
                for (Character c : signSet) {
                    int column = columnIndex(c);
                    if (table[row][column] != null)
                        return false;
                    table[row][column]=body;
                }
            }
        }
        for(Map.Entry<String,Set<Character>> entry:follow.entrySet()){
            for(Character i:entry.getValue()){
                int row=rowIndex(entry.getKey());
                int column=columnIndex(i);
                if (table[row][column]==null)
                    table[row][column]="synch";
            }
        }
        return true;
    }
    private boolean input(){
        lexical_analyser = new Lexical_analyser();
        lexical_analyser.start();
        if (lexical_analyser.error)
            return false;
        this.ident=lexical_analyser.resultSet.get("IDT");
        this.num=lexical_analyser.resultSet.get("NUM");
        return true;
    }
    public static void main(String[] args) {
        SyntacticAnalyser_LL1 ll1Analyser = new SyntacticAnalyser_LL1();
        ll1Analyser.Start();
    }
}
