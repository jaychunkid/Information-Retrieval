package page;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//PageWriter接口实现
public class PageWriterImpl implements PageWriter {

    private File pageDirectory;     //网页文件保存目录

    public PageWriterImpl(String pageDirectoryPath){
        this.pageDirectory = new File(pageDirectoryPath);
        //若传入的目录不存在,则新建目录
        if(!pageDirectory.exists() || !pageDirectory.isDirectory()){
            pageDirectory.mkdirs();
        }
    }

    public boolean write(Page page, long index){
        //文件命名格式为"网页索引号.txt"
        File file = new File(pageDirectory.getPath() + File.separator + index + ".txt");
        try {
            //若目标文件不存在,则先新建文件
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            //将网页的url,关键词,内容分词分别写入文件的第1,2,3行
            writer.println(page.getUrl());
            writer.println(page.getKeyWordStr());
            writer.println(page.getTextWordStr());
            writer.close();
            return true;
        } catch (IOException e) {
            //写入文件失败,将文件删除
            file.delete();
            return false;
        }
    }

}
