package index;

//建立索引接口
public interface IndexBuilder {

    //从传入的目录内读取词袋形式文件,并建立索引
    boolean buildIndex(String pagePath);

    //返回词汇表长度
    int sizeOfTerms();

    //返回url数目
    int sizeOfUrls();

    //返回读取的文件数目
    int sizeOfFile();

    //返回索引文件的大小
    long lengthOfIndex();

}
