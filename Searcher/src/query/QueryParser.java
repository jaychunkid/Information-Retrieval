package query;

//查询解析接口
public interface QueryParser {

    //添加停止词
    boolean addStopWords(String stopWordsFilePath);

    //清空已添加的停止词
    void clearStopWords();

    //解析查询,返回查询信息
    Query parse(String queryStr);

}
