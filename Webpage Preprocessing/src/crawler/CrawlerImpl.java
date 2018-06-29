package crawler;

import analyzer.Analyzer;
import exception.WrongCharsetException;
import page.Page;
import page.PageWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

//Crawler接口实现
public class CrawlerImpl implements Crawler {

    private List<String> urlWaiting = new LinkedList<>();     //等待处理的url
    private Set<String> urlProcessed = new HashSet<>();       //处理成功的url
    private Analyzer analyzer;                                //文档分词
    private PageWriter writer;                                //分词结果保存
    private int indexCount = 0;                               //记录处理的文档数目

    public CrawlerImpl(Analyzer analyzer, PageWriter writer){
        this.analyzer = analyzer;
        this.writer = writer;
    }

    public boolean start(String urlFilePath){
        if(getUrlFromFile(urlFilePath)){
            crawl();
            return true;
        } else {
            return false;
        }
    }

    public int sizeProcessed(){
        return urlProcessed.size();
    }

    //从文件中读取所有的url
    private boolean getUrlFromFile(String urlFilePath){
        BufferedReader reader = null;
        try {
            File file = new File(urlFilePath);
            if(file.exists() && file.isFile()){
                reader = new BufferedReader(new FileReader(file));
                String url = null;
                while((url = reader.readLine()) != null){
                    if(url.contains("scut.edu.cn")){
                        urlWaiting.add(url);
                    }
                }
            } else {
                throw new FileNotFoundException("url file not found");
            }
        } catch (IOException e){
            //url文件不存在或读取失败
            return false;
        }
        try{
            reader.close();
        } catch (IOException e){
            //文件关闭失败，不做处理
        }
        return true;
    }

    //抓取文档控制方法
    private void crawl(){
        while (urlWaiting.size() > 0) {
            //从等待队列中获取url
            String url = urlWaiting.remove(0);
            if (processUrl(url)) {
                //处理成功则将url加入完成队列中
                urlProcessed.add(url);
                System.out.println(new Date().toString() + " " + url + " success");
            } else {
                System.out.println(new Date().toString() + " " + url + " error");
            }
        }
    }

    //抓取对应url的信息
    private boolean processUrl(String url) {
        InputStream stream = null;
        String charset = null;
        try {
            //连接服务器
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Content-type", "text/html");
            connection.setInstanceFollowRedirects(true);
            connection.connect();
            //获取响应码
            int responseCode = connection.getResponseCode();
            //若请求成功,则判断文件类型,检查文件字符集
            if (responseCode == 200 || responseCode == 304) {
                String contentType = connection.getContentType();
                if (contentType.contains("text/html")) {
                    stream = connection.getInputStream();
                    if (contentType.contains("charset")) {
                        charset = contentType.substring(contentType.indexOf("=") + 1);
                    } else {
                        //若响应头中不存在字符集信息,则默认为utf-8编码
                        charset = "utf-8";
                    }
                } else {
                    return false;
                }
            } else if (responseCode == 301 || responseCode == 302 || responseCode == 307) {
                //处理重定向响应码
                String redirect = connection.getHeaderField("Location");
                if (redirect != null && !"".equals(redirect)) {
                    if (!urlProcessed.contains(redirect) && !urlWaiting.contains(redirect)) {
                        urlWaiting.add(redirect);
                    }
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            //连接失败
            return false;
        }
        try {
            //文档分词
            Page page = analyzer.analyze(url, stream, charset);
            if (page != null && page.size() > 0) {
                //分词结果不为空,则将文档保存到文件中
                return savePageToFile(page);
            } else {
                return false;
            }
        } catch (WrongCharsetException e){
            //字符集错误异常,用正确的字符集重抓取文档
            return reprocessWithCharset(url, e.getCorrectCharset().name());
        }
    }

    //在已知字符集的情况下抓取文档信息
    private boolean reprocessWithCharset(String url, String charset){
        InputStream stream = null;
        try {
            //连接服务器
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Content-type", "text/html");
            connection.setInstanceFollowRedirects(true);
            connection.connect();
            //获取响应码
            int responseCode = connection.getResponseCode();
            //重定向响应在出现字符集错误前会被解决,所以这里只需要处理响应成功的情况
            if (responseCode == 200 || responseCode == 304) {
                stream = connection.getInputStream();
            } else {
                return false;
            }
        } catch (Exception e) {
            //连接失败
            return false;
        }
        try {
            //文档分词
            Page page = analyzer.analyze(url, stream, charset);
            if (page != null && page.size() > 0) {
                //分词结果不为空,则将文档保存到文件中
                return savePageToFile(page);
            } else {
                return false;
            }
        } catch (WrongCharsetException e){
            //理论上不应该出现,返回处理错误
            return false;
        }
    }

    //将分词结果存储到文件中
    private boolean savePageToFile(Page page) {
        if(writer.write(page, indexCount)){
            ++indexCount;
            return true;
        } else {
            return false;
        }
    }

}
