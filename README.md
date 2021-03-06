## 信息检索系统
网络信息检索课程实验，实现一个基于向量模型的网络信息检索系统，能够根据输入的查询语句，输出指定数目的按照相关度排序的url。
### Webpage Preprocessing
网页内容预处理实现，主要流程是从给定文件中读取url，获取url对应网页，从网页中提取正文，分词后存储到文件中。
* `AnalyzerImpl`：实现对网页正文和网页关键词（标题与meta标签中的keywords）的提取，以及分词，解析结果返回Page对象。
其中网页解析基于 jsoup 1.11.3 实现，分词基于 ansj 5.1.6 实现。
支持停用词的设置，网页正文提取参考了网络上基于文本密度的方法（见引用）。
* `PageWriterImpl`：实现将网页词袋模型（Page对象）写入文件，文件保存在指定的目录下。
文件格式：文件名为url索引号，文件中第一行为url，第二行为关键词分词结果，第三行为网页正文分词结果。
* `CrawlerImpl`：实现从文件中读取url，抓取网页，调用Analyzer对象获得网页分词结果，再调用PageWriter对象将结果写入文件。
### Index
检索系统索引实现，读取上一部分程序生成的文件，生成正向索引和倒排索引，并将索引写入指定文件中。
* `PageReaderImpl`：从指定的目录中，读取网页分词文件，返回Page对象
* `IndexWriterImpl`：将索引结果写入指定目录下的index.txt文件中。
文件格式：第一行为url数目n，接下来n行为（url，向量长度）的键值对，之后一行为词项数目m，接下来m行格式为（词项 url索引号：词频 ……）。
* `IndexBuilderImpl`：调用PageReader对象读取文件，建立索引，调用IndexWriter对象将索引写入文件。
### Searcher
检索系统检索程序实现，读取上一部分程序生成的索引文件，接收查询语句输入，输出查询结果。
支持设置输出结果的数目，以及查询语句分词使用的停用词。
* `IndexReaderImpl`：从指定目录中读取索引文件index.txt，重新生成索引。
* `QueryParserImpl`：对输入的查询语句进行分词处理，返回查询对象Query。
分词处理同样基于 ansj 5.1.6 实现。
* `IndexSearcherImpl`：计算查询对象Query与所有包含查询词项的网页的相似度，返回结果。
### 测试程序
* 运行Webpage Preprocessing目录下，startCrawler中的main方法
* 输入停止词文件位置（每行一个词），url文件位置，以及网页文件保存位置
* 等待程序运行完成，输出抓取时间，网页数目和抓取速度（程序刚开始运行会输出ansj的控制台信息，请忽略）
* 运行Index目录下，startIndexBuilder中的main方法
* 输入网页文件目录，以及索引结果保存目录
* 等待程序运行完成，输出文件数目，建索时间，词汇表长度，以及索引文件大小
* 运行Searcher目录下，startSearcher中的main方法
* 输入停止词文件位置以及索引目录位置
* 输入查询语句，获取查询结果（第一次查询会输出ansj的控制台信息，请忽略）
### 引用
* [ansj分词项目github主页](https://github.com/NLPchina/ansj_seg)
* [jsoup项目主页](https://jsoup.org/)
* [网页正文提取算法参考](https://www.cnblogs.com/jasondan/p/3497757.html)
