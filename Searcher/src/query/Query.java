package query;

import java.util.Map;

//封装查询信息
public class Query {

    private Map<String, Double> tfs;     //查询词频

    Query(Map<String, Double> tfs){
        this.tfs = tfs;
    }

    //获取查询词
    public String[] getTerms() {
        return tfs.keySet().toArray(new String[tfs.size()]);
    }

    //获取对应查询词的词频
    public double getTfs(String term){
        return tfs.getOrDefault(term, 0.0);
    }

}
