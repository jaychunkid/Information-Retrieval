package analyzer;

import exception.WrongCharsetException;
import page.Page;

import java.io.*;

//网页内容分析器接口
public interface Analyzer {

    //从传入文件中读取停止词并添加,要求文件格式为每行一个停止词
    boolean addStopWords(String stopWordsFilePath);

    //清除已经添加的停止词
    void clearStopWords();

    //从传入的输入流中抓取网页内容,
    //当传入的字符集和网页文件中声明的字符集不同时,抛出字符集错误异常
    Page analyze(String url, InputStream stream, String charset) throws WrongCharsetException;

}
