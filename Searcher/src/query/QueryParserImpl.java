package query;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//实现查询解析接口
public class QueryParserImpl implements QueryParser {

    private StopRecognition stopRecognition = new StopRecognition();     //停用词过滤

    public QueryParserImpl() {
        initStopWords();
    }

    //初始化默认停用词
    private void initStopWords() {
        stopRecognition.insertStopNatures("w");      //过滤标点符号
        stopRecognition.insertStopNatures("m");      //过滤数词
        stopRecognition.insertStopNatures("mq");     //过滤数词+量词
        stopRecognition.insertStopNatures("null");     //过滤无法识别词性的词
    }

    public boolean addStopWords(String stopWordsFilePath) {
        File file = new File(stopWordsFilePath);
        if (file.exists() && file.isFile()) {
            BufferedReader reader = null;
            try {
                //从文件中读取停用词表
                reader = new BufferedReader(new FileReader(file));
                Set<String> stopWords = new HashSet<>();
                String word = null;
                while ((word = reader.readLine()) != null) {
                    stopWords.add(word);
                }
                //将停用词表添加到过滤器中
                stopRecognition.insertStopWords(stopWords);
            } catch (IOException e) {
                //文件读取失败
                return false;
            }
            try{
                reader.close();
            } catch (IOException e){
                //文件读取流关闭失败,不做处理
            }
            return true;
        } else {
            return false;
        }
    }

    public void clearStopWords() {
        stopRecognition.clear();
        initStopWords();
    }

    public Query parse(String queryStr) {
        //对查询语句进行分词
        Result result = ToAnalysis.parse(queryStr).recognition(stopRecognition);
        List<Term> termList = result.getTerms();
        Map<String, Integer> termCount = new HashMap<>();
        double maxCount = 0;
        //统计分词词频
        for (Term term : termList) {
            String name = term.getName().trim();
            if (!"".equals(name)) {
                if (termCount.containsKey(term.getName())) {
                    Integer count = termCount.get(term.getName()) + 1;
                    termCount.put(term.getName(), count);
                    if (maxCount < count) {
                        maxCount = count;
                    }
                } else {
                    termCount.put(term.getName(), 1);
                    if (maxCount == 0) {
                        ++maxCount;
                    }
                }
            }
        }
        //计算标准化词频
        Map<String, Double> tfs = new HashMap<>();
        for (Map.Entry<String, Integer> term : termCount.entrySet()) {
            tfs.put(term.getKey(), term.getValue() / maxCount);
        }
        return new Query(tfs);
    }

}
