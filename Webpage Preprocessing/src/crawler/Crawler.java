package crawler;

//Crawler接口
public interface Crawler {

    //开始抓取网页
    boolean start(String urlFilePath);

    //获取处理成功的网页数目
    int sizeProcessed();

}
