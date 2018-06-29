package index;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

//将索引写入文件实现
public class IndexWriterImpl implements IndexWriter {

    private File indexFile = null;     //索引文件位置

    public IndexWriterImpl(String indexFilePath) {
        indexFile = new File(indexFilePath);
    }

    public boolean write(List<Url> urls, Map<String, Term> terms) {
        //判断索引所在路径是否存在,不存在则新建目录
        if (!indexFile.getParentFile().exists()) {
            indexFile.getParentFile().mkdirs();
        }
        try {
            //判断索引文件是否存在,不存在则新建
            if (!indexFile.exists() || !indexFile.isFile()) {
                indexFile.createNewFile();
            }
            PrintWriter writer = new PrintWriter(indexFile);
            //写入文档数目
            writer.println(urls.size());
            //写入文档索引(url 向量长度)
            for (Url url : urls) {
                writer.print(url.getUrl());
                writer.print(" ");
                writer.println(url.getLength());
            }
            //写入词汇表长度
            writer.println(terms.size());
            //写入词项索引(词 文档号:词频 ...)
            for (Map.Entry<String, Term> entry : terms.entrySet()) {
                writer.println(termToString(entry.getValue()));
            }
            writer.close();
            return true;
        } catch (IOException e) {
            //写入文件失败
            return false;
        }
    }

    public long indexLength(){
        return indexFile.length();
    }

    //将词项索引对象转换为写入文件的字符串
    private String termToString(Term term){
        StringBuilder stringBuilder = new StringBuilder(term.getName());
        for(Map.Entry<Long, Double> entry : term.getTfs().entrySet()){
            stringBuilder.append(" ");
            stringBuilder.append(entry.getKey());
            stringBuilder.append(":");
            stringBuilder.append(entry.getValue());
        }
        return stringBuilder.toString();
    }

}
