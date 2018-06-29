import index.IndexReaderImpl;
import query.QueryParserImpl;
import search.IndexSearcher;
import search.IndexSearcherImpl;
import search.Page;
import query.Query;
import query.QueryParser;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class startSearcher {

    public static void main(String[] args) {
        QueryParser queryParser = new QueryParserImpl();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入停止词文件位置: ");
        String stopWordsFilePath = scanner.nextLine();
        if (queryParser.addStopWords(stopWordsFilePath)) {
            System.out.println("加载停止词成功");
        } else {
            System.out.println("加载停止词失败");
        }
        System.out.println("请输入索引目录: ");
        String indexDirectory = scanner.nextLine();
        String indexFilePath = indexDirectory + File.separator + "index.txt";
        IndexSearcher searcher = new IndexSearcherImpl(new IndexReaderImpl(indexFilePath));
        while (true) {
            System.out.println("请输入查询语句: ");
            String queryStr = scanner.nextLine();
            Query query = queryParser.parse(queryStr);
            List<Page> pageList = searcher.search(query, 10);
            if(pageList != null) {
                if (pageList.size() > 0) {
                    for (Page page : pageList) {
                        System.out.println(page.getUrl() + "     " + page.getScore());
                    }
                } else {
                    System.out.println("查询结果为空");
                }
            } else {
                System.out.println("索引加载失败，无法进行检索");
                break;
            }
        }
    }

}
