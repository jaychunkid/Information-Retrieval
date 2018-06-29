package index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//封装词项索引
public class Term {

    private String name;                                 //词项名
    private Map<Long, Double> tfs = new HashMap<>();     //包含该词的文档号和对应词频

    public Term(String name){
        this.name = name;
    }

    //添加包含该词项的文档
    public void addPage(long id, double tf){
        tfs.put(id, tf);
    }

    //获取词项名
    public String getName() {
        return name;
    }

    //获取词项的文档频率
    public long getDf() {
        return tfs.size();
    }

    //获取所有包含该词的文档和对应词频
    public Map<Long, Double> getTfs() {
        return tfs;
    }

    //获取传入文档号对应文档的词频,如果该文档不包含词项则返回0
    public double getTf(long id){
        return tfs.getOrDefault(id, 0.0);
    }

    //获取所有包含该词的文档
    public Set<Long> getPageSet(){
        return tfs.keySet();
    }

}
