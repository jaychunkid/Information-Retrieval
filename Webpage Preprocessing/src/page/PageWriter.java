package page;

//将抓取网页内容写入文件的接口
public interface PageWriter {

    //将网页写入对应索引名的文件中
    boolean write(Page page, long index);

}
