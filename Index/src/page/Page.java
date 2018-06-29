package page;

import java.util.Map;

//封装文档信息
public class Page {
    private long id;                           //文档号
    private String url;                        //文档url
    private Map<String, Integer> wordsMap;     //文档词频
    private int maxCount;                      //文档的最高词频

    Page(long id, String url, Map<String, Integer> wordsMap, int maxCount){
        this.id = id;
        this.url = url;
        this.wordsMap = wordsMap;
        this.maxCount = maxCount;
    }

    //获取文档号
    public long getId(){
        return id;
    }

    //获取文档url
    public String getUrl(){
        return url;
    }

    //获取文档词频
    public Map<String, Integer> getWordsMap(){
        return wordsMap;
    }

    //获取文档的最高词频
    public int getMaxCount() {
        return maxCount;
    }

}
