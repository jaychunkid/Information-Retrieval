package search;

import index.IndexReader;
import index.Term;
import index.Url;
import query.Query;

import java.util.*;

//检索接口实现
public class IndexSearcherImpl implements IndexSearcher {

    private List<Url> urlList = new ArrayList<>();            //文档索引
    private Map<String, Term> termsMap = new HashMap<>();     //词项索引

    public IndexSearcherImpl(IndexReader reader){
        //读取索引
        if(reader.read(urlList, termsMap)){
            System.out.println("读取索引成功");
        } else {
            System.out.println("读取索引失败");
            urlList.clear();
            termsMap.clear();
        }
    }

    public List<Page> search(Query query, int num){
        //检查索引读取是否成功
        if(urlList == null || termsMap == null){
            return null;
        }
        Set<Long> pageSet = new HashSet<>();
        //获取查询词
        String[] queryTerms = query.getTerms();
        //查询向量
        List<Double> queryVector = new ArrayList<>();
        double[] idf = new double[queryTerms.length];
        //遍历查询词
        for(int i = 0; i < queryTerms.length; ++i){
            String queryTerm = queryTerms[i];
            if(termsMap.containsKey(queryTerm)){
                //若查询词在词汇表中存在,则记录包含查询词的文档号,并计算查询词的逆文档频率和词项权重
                pageSet.addAll(termsMap.get(queryTerm).getPageSet());
                idf[i] = getIdf(termsMap.get(queryTerm).getDf(), urlList.size());
                queryVector.add(idf[i] * query.getTfs(queryTerm));
            } else {
                //若查询词在词汇表中不存在,则权重和逆文档频率皆为0
                queryVector.add(0.0);
                idf[i] = 0.0;
            }
        }
        //计算所有包含查询词的文档的词项权重
        Long[] pageArray = pageSet.toArray(new Long[pageSet.size()]);
        List<List<Double>> pageVectors = new ArrayList<>();
        for(Long page : pageArray){
            List<Double> pageVector = new ArrayList<>();
            for(int i = 0; i < queryTerms.length; ++i){
                pageVector.add(idf[i] * termsMap.get(queryTerms[i]).getTf(page));
            }
            pageVectors.add(pageVector);
        }
        //计算文档得分
        List<Page> pages = new ArrayList<>();
        for(int i = 0; i < pageArray.length; ++i) {
            double score = calculateScore(queryVector, pageVectors.get(i),
                    urlList.get(pageArray[i].intValue()).getLength());
            if(score > 1.0){
                score = 1.0;
            }
            pages.add(new Page(urlList.get(pageArray[i].intValue()).getUrl(), score));
        }
        //根据得分对文档进行排序
        pages.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
        //返回得分最高的前num个文档,如果包含查询词的文档少于num,则返回所有文档
        if(num >= pages.size()){
            return pages;
        } else {
            return pages.subList(0, num);
        }
    }

    //计算文档向量与查询向量的相似度(余弦法)
    private double calculateScore(List<Double> queryVector, List<Double> pageVector, double pageLength){
        return multiplyVector(queryVector, pageVector) / (pageLength * getVectorLen(queryVector));
    }

    //计算词项的逆文档频率
    private double getIdf(long df, long sum){
        return Math.log10(((double) sum) / df);
    }

    //计算向量长度
    private double getVectorLen(List<Double> vector){
        double sum = 0;
        for(Double component : vector){
            sum += component * component;
        }
        return Math.sqrt(sum);
    }

    //向量点乘
    private double multiplyVector(List<Double> vector1, List<Double> vector2){
        int size = vector1.size() > vector2.size() ? vector2.size() : vector1.size();
        double result = 0;
        for(int i = 0; i < size; ++i){
            result += vector1.get(i) * vector2.get(i);
        }
        return result;
    }

}
