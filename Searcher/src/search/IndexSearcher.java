package search;

import query.Query;

import java.util.*;

//检索接口
public interface IndexSearcher {

    //根据传入的查询信息,返回得分最高的前num个文档
    List<Page> search(Query query, int num);

}
