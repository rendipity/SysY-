# SysY编译器
## 词法分析 Lexical_analyser
在根目录下创建一个*.sy文件,并把待分析的程序写入即可运行词法分析器指定文件开始分析
## 语法分析 
### 自顶向下分析 SyntacticAnalyser_LL1
**需要在上述词法分析的基础上进行分析**
需要一个production.sy文件储存文法,该LL1分析可根据不同文法独立构建预测分析表并进行分析
指定*.sy文件，先进行词法分析后进行语法分析
自动构建预测分析表的程序还缺少提取公共左因子(removeCommonLeftFactor)和消除左递归(removeLeftRecursion)的两个函数为完成，可自行补充
### 自底向上分析 SyntacticAnalyser_SLR1
指定*.sy文件，先进行词法分析后进行语法分析
