package analyzer;

import exception.WrongCharsetException;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import page.Page;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//网页内容分析器实现
public class AnalyzerImpl implements Analyzer {

    private StopRecognition stopRecognition = new StopRecognition();     //停用词过滤器

    public AnalyzerImpl(){
        initStopWords();
    }

    //初始化默认停用词
    private void initStopWords(){
        stopRecognition.insertStopNatures("w");        //过滤标点符号
        stopRecognition.insertStopNatures("m");        //过滤数词
        stopRecognition.insertStopNatures("mq");       //过滤数词+量词
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
            } catch (IOException e){
                //停止词文件读取失败
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

    public void clearStopWords(){
        stopRecognition.clear();
        initStopWords();
    }

    public Page analyze(String url, InputStream stream, String charset) throws WrongCharsetException {
        try {
            //利用jsoup获取网页文本,检查字符集
            Document document = Jsoup.parse(stream, charset, url);
            checkCharset(document);
            //提取关键词文本和网页正文
            String keyText = processKeyText(document);
            String text = processText(document);
            //获取分词结果,生成网页对象并返回
            Result result = ToAnalysis.parse(text).recognition(stopRecognition);
            List<Term> textTerms = result.getTerms();
            result = ToAnalysis.parse(keyText).recognition(stopRecognition);
            List<Term> titleTerms = result.getTerms();
            return new Page(url, getWordFromTerm(titleTerms), getWordFromTerm(textTerms));
        } catch (IOException e){
            //文件解析失败
            return null;
        }
    }

    //检查读取网页的字符集是否正确,不正确则抛出异常
    private void checkCharset(Document document) throws WrongCharsetException{
        //从meta标签中获取字符集
        Elements elements = document.select("meta[http-equiv=Content-Type]");
        for(Element element : elements){
            String content = element.attr("content");
            //检查字符集与网页的解析字符集是否相同
            if(content != null && content.contains("charset")){
                Charset charset = Charset.forName(content.substring(content.indexOf("charset") + 8));
                if(!charset.equals(document.charset())){
                    throw new WrongCharsetException("Parse the page with wrong charset", charset);
                }
            }
        }
    }

    //处理网页的关键词文本
    private String processKeyText(Document document){
        //将网页的标题视为关键词文本
        StringBuilder stringBuilder = new StringBuilder(document.title());
        //从meta标签中获取关键词文本
        Elements elements = document.select("meta[name=keywords]");
        for(Element element : elements){
            stringBuilder.append(element.attr("content"));
        }
        return stringBuilder.toString();
    }

    //处理网页内容文本
    private String processText(Document document){
        StringBuilder stringBuilder = new StringBuilder();
        //提取body标签中的网页内容
        Elements elements = document.select("body");
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                //去除内容中的链接,css句段和js句段
                element.select("a").remove();
                element.select("script").remove();
                element.select("style").remove();
                stringBuilder.append(extractText(element.html()));
            }
        }
        String text = stringBuilder.toString().trim();
        if(!"".equals(text)){
            return text;
        } else {
            return null;
        }
    }

    //从分词结果集中获取分词
    private List<String> getWordFromTerm(List<Term> termList){
        List<String> words = new ArrayList<>();
        for(Term term : termList){
            String name = term.getName().trim();
            if(!"".equals(name)) {
                words.add(name);
            }
        }
        return words;
    }

    //从html文本中提取正文,基于文本密度提取
    //计算从当前行开始固定行数的文本字数,若字数超过某个阈值视为正文首,字数少于某个阈值则视为正文尾,
    //提取正文首行到尾行间的每一行的文本
    private String extractText(String html){
        StringBuilder stringBuilder = new StringBuilder();
        //去除文本中的标签和html特殊字符
        String text = html.replaceAll("<.*?>", "").
                replaceAll("&\\w{1,8};", "");
        //按行分割文本,并去除每行首尾的空格,避免影响算法准确度
        String[] textByLines = text.split("\n");
        for(int i = 0; i < textByLines.length; ++i){
            textByLines[i] = textByLines[i].trim();
        }
        int startPos = -1;       //正文的开始行数
        int preLen = 0;          //上一次计算得到的文本字数
        final int depth = 5;     //文本字数计算的行数
        for(int i = 0; i < textByLines.length - depth; ++i){
            int len = 0;
            //计算当前行开始5行的文本字数
            if(i == 0) {
                for (int j = 0; j < depth; ++j) {
                    len += textByLines[i + j].length();
                }
            } else {
                len += preLen - textByLines[i - 1].length() + textByLines[i + depth - 1].length();
            }
            if(startPos == -1){
                //若仍未找到正文开头,则判断当前行是否符合开头条件
                if(len > 180){
                    startPos = i;
                    stringBuilder.append(textByLines[i]);
                }
            } else {
                //若已找到正文开头,则判断当前行是否符合结尾条件
                if(len < 90){
                    startPos = -1;
                }
                stringBuilder.append(textByLines[i]);
            }
            preLen = len;
        }
        return stringBuilder.toString();
    }

}
