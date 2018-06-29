package search;

//封装文档检索结果
public class Page {

    private String url;       //文档url
    private double score;     //文档得分

    Page(String url, double score){
        this.url = url;
        this.score = score;
    }

    //获取文档url
    public String getUrl() {
        return url;
    }

    //获取文档得分
    public double getScore() {
        return score;
    }

}
