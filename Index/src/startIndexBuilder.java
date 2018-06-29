import index.IndexBuilder;
import index.IndexBuilderImpl;
import index.IndexWriter;
import index.IndexWriterImpl;
import page.PageReader;
import page.PageReaderImpl;

import java.io.File;
import java.util.Scanner;

public class startIndexBuilder {

    public static void main(String[] args) {
        //关键词权重为3,内容分词权重为1(即关键词每出现1次计为3次,而内容分词每出现1次计为1次)
        PageReader reader = new PageReaderImpl(3, 1);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入文档集目录: ");
        String documentDirectory = scanner.nextLine();
        System.out.println("请输入索引结果目录: ");
        String resultDirectory = scanner.nextLine();
        String resultFilePath = resultDirectory + File.separator + "index.txt";
        IndexWriter writer = new IndexWriterImpl(resultFilePath);
        long startTime = System.currentTimeMillis();
        IndexBuilder indexBuilder = new IndexBuilderImpl(reader, writer);
        if(indexBuilder.buildIndex(documentDirectory)){
            System.out.println("索引创建成功");
            System.out.println("文件数目: " + indexBuilder.sizeOfFile());
            System.out.println("建索时间: " + (System.currentTimeMillis() - startTime) + "ms");
            System.out.println("词汇表长度: " + indexBuilder.sizeOfTerms());
            System.out.println("索引文件大小: " + indexBuilder.lengthOfIndex() + "bytes");
        } else {
            System.out.println("索引创建失败");
        }
    }

}
