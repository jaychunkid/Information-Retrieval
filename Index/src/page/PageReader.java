package page;

import java.io.File;

//从文件中读取文档信息接口
public interface PageReader {

    //从传入的文件中读取文档信息
    Page read(File file);

}
