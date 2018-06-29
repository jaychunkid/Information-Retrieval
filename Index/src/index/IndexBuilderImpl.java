package index;

import page.Page;
import page.PageReader;

import java.io.File;
import java.util.*;

//建立索引接口实现
public class IndexBuilderImpl implements IndexBuilder {

    private PageReader reader;                             //从文件中读取网页内容
    private IndexWriter writer;                            //索引写入文件
    private Map<String, Term> terms = new HashMap<>();     //词汇索引
    private List<Url> urls = new ArrayList<>();            //文档索引
    private int sizeOfFile = 0;                            //读取的文件数目

    public IndexBuilderImpl(PageReader reader, IndexWriter writer){
        this.reader = reader;
        this.writer = writer;
    }

    public int sizeOfTerms(){
        return terms.size();
    }

    public int sizeOfUrls() {
        return urls.size();
    }

    public int sizeOfFile(){
        return sizeOfFile;
    }

    public long lengthOfIndex(){
        return writer.indexLength();
    }

    public boolean buildIndex(String pagePath) {
        //清除上一次的索引结果
        terms.clear();
        urls.clear();
        //判断传入的目录是否存在
        File directory = new File(pagePath);
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }
        //获取目录中的所有文件
        File[] files = directory.listFiles();
        if(files != null) {
            sizeOfFile = files.length;
            List<Page> pageList = new ArrayList<>();
            //从文件中提取文档内容
            for (File file : files) {
                Page page = reader.read(file);
                if (page != null) {
                    pageList.add(page);
                }
            }
            //将文档按照文档号排序
            pageList.sort(Comparator.comparingLong(Page::getId));
            //建立索引
            buildIndexFromPage(pageList);
            //向文件中写入索引
            return writer.write(urls, terms);
        } else {
            return false;
        }
    }

    //利用传入的文档列表创建索引
    private void buildIndexFromPage(List<Page> pageList){
        //创建词汇索引
        for(Page page : pageList){
            //获取当前文档的所有词汇和计数
            Map<String, Integer> wordsMap = page.getWordsMap();
            double maxCount = page.getMaxCount();
            //遍历所有词汇
            for(Map.Entry<String, Integer> entry : wordsMap.entrySet()){
                if(terms.containsKey(entry.getKey())){
                    //若当前词汇在词汇表中已存在,则将该文档的编号和词频添加到词汇索引中
                    terms.get(entry.getKey()).addPage(page.getId(), entry.getValue()/maxCount);
                } else {
                    //若当前词汇为新词汇,则需要先添加新的索引项
                    Term term = new Term(entry.getKey());
                    term.addPage(page.getId(), entry.getValue()/maxCount);
                    terms.put(entry.getKey(), term);
                }
            }
        }
        //创建文档索引
        for(Page page : pageList){
            //对每个文档计算其向量长度
            double length = 0.0;
            for(String word : page.getWordsMap().keySet()){
                Term term = terms.get(word);
                double weight = term.getTf(page.getId()) * getIdf(term.getDf(), pageList.size());
                length += weight * weight;
            }
            urls.add(new Url(page.getUrl(), Math.sqrt(length)));
        }
    }

    //通过传入的文档频率和文档数计算逆文档频率
    private double getIdf(long df, long sum){
        return Math.log10(((double) sum) / df);
    }

}
