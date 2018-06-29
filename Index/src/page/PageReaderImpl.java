package page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//从文件中读取文档信息实现
public class PageReaderImpl implements PageReader {

    private int keyWeight;      //文档关键词权重
    private int textWeight;     //文档内容分词权重

    public PageReaderImpl(int keyWeight, int textWeight){
        this.keyWeight = keyWeight;
        this.textWeight = textWeight;
    }

    public Page read(File file){
        //获取文件名(包含文档号)
        String fileName = file.getName();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            //从文件名中提取文档号
            long index = Long.parseLong(fileName.substring(0, fileName.indexOf(".")));
            //读取文件第一行的url
            String url = reader.readLine();
            //读取文件第二行的关键词字符串
            String keyText = reader.readLine().trim();
            //读取文件第三行的内容分词字符串
            String text = reader.readLine().trim();
            String[] keys = keyText.split(" ");
            String[] words = text.split(" ");
            Map<String, Integer> wordsMap = new HashMap<>();
            //更新词频map
            int maxCount = addWordToMap(wordsMap, keyWeight, keys, 0);
            maxCount = addWordToMap(wordsMap, textWeight, words, maxCount);
            //返回读取到的文档
            return new Page(index, url, wordsMap, maxCount);
        } catch (IOException e){
            //读取文件发生错误,返回空
            return null;
        }
    }

    //向词频map中添加一个词语,并更新文档的最高词频
    private int addWordToMap(Map<String, Integer> wordsMap, int weight, String[] words, int curMaxCount){
        for(String word : words){
            if(!"".equals(word)) {
                if (wordsMap.containsKey(word)) {
                    Integer count = wordsMap.get(word) + weight;
                    wordsMap.put(word, count);
                    if (count > curMaxCount) {
                        curMaxCount = count;
                    }
                } else {
                    wordsMap.put(word, weight);
                    if (curMaxCount == 0) {
                        curMaxCount = weight;
                    }
                }
            }
        }
        return curMaxCount;
    }

}
