package index;

import java.util.List;
import java.util.Map;

//从文件中读取索引接口
public interface IndexReader {

    //从文件中读取索引,保存到传入的参数中
    boolean read(List<Url> urlList, Map<String, Term> termsMap);

}
