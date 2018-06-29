package index;

//封装文档索引
public class Url {

    private String url;        //文档对应url
    private double length;     //文档向量长度

    public Url(String url, double length){
        this.url = url;
        this.length = length;
    }

    //获取url
    public String getUrl() {
        return url;
    }

    //获取向量长度
    public double getLength() {
        return length;
    }

}
