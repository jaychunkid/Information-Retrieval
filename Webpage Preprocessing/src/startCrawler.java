import analyzer.Analyzer;
import analyzer.AnalyzerImpl;
import crawler.Crawler;
import crawler.CrawlerImpl;
import page.PageWriter;
import page.PageWriterImpl;

import java.util.Scanner;

public class startCrawler {

    public static void main(String[] args){
        Analyzer analyzer = new AnalyzerImpl();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入停止词文件位置: ");
        String stopWordsFilePath = scanner.nextLine();
        if(analyzer.addStopWords(stopWordsFilePath)) {
            System.out.println("加载停止词成功");
        } else {
            System.out.println("加载停止词失败");
        }
        System.out.println("请输入url文件位置: ");
        String urlFilePath = scanner.nextLine();
        System.out.println("请输入爬取内容保存目录: ");
        String pageFileDirectory = scanner.nextLine();
        PageWriter writer = new PageWriterImpl(pageFileDirectory);
        Crawler crawler = new CrawlerImpl(analyzer, writer);
        long startTime = System.currentTimeMillis();
        if(crawler.start(urlFilePath)){
            double time = System.currentTimeMillis() - startTime;
            System.out.println("爬取时间: " + time + "ms");
            System.out.println("爬取网页数目: " + crawler.sizeProcessed());
            System.out.println("爬取速度: " + crawler.sizeProcessed() * 1000 / time + "个网页每秒");
        } else {
            System.out.println("url文件读取失败");
        }
    }

}
