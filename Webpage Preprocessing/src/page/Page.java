package page;

import java.util.List;

//封装抓取的网页信息
public class Page {

    private String url;                 //网页url
    private List<String> keyWords;      //网页关键词
    private List<String> textWords;     //网页内容分词

    public Page(String url, List<String> keyWords, List<String> textWords){
        this.url = url;
        this.keyWords = keyWords;
        this.textWords = textWords;
    }

    //获取网页url
    public String getUrl(){
        return url;
    }

    //获取网页关键词
    public List<String> getKeyWords(){
        return keyWords;
    }

    //获取网页内容分词
    public List<String> getTextWords(){
        return textWords;
    }

    //获取关键词字符串(空格隔开)
    public String getKeyWordStr(){
        return getWordStr(keyWords);
    }

    //获取内容分词字符串(空格隔开)
    public String getTextWordStr(){
        return getWordStr(textWords);
    }

    //获取网页索引词数目
    public int size(){
        return textWords.size() + keyWords.size();
    }

    //将传入的词列表转换为空格隔开的字符串
    private String getWordStr(List<String> words){
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String word : words){
            if(isFirst){
                stringBuilder.append(word);
                isFirst = false;
            } else {
                stringBuilder.append(" ");
                stringBuilder.append(word);
            }
        }
        return stringBuilder.toString();
    }

}
