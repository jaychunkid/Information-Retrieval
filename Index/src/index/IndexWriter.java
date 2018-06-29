package index;

import java.util.List;
import java.util.Map;

//将索引写入文件接口
public interface IndexWriter {

    //将索引写入文件
    boolean write(List<Url> urls, Map<String, Term> terms);

    //返回索引文件大小
    long indexLength();

}
