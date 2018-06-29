package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//索引读取接口实现
public class IndexReaderImpl implements IndexReader {

    private File indexFile = null;     //索引文件位置

    public IndexReaderImpl(String filePath){
        indexFile = new File(filePath);
    }

    public boolean read(List<Url> urlList, Map<String, Term> termsMap){
        if(indexFile.exists() & indexFile.isFile()){
            urlList.clear();
            termsMap.clear();
            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new FileReader(indexFile));
                //读取文档数目
                long urlLen = Long.parseLong(reader.readLine());
                //读取文档url和文档向量长度
                for(int i = 0; i < urlLen; ++i){
                    String[] strings = reader.readLine().split(" ");
                    urlList.add(new Url(strings[0], Double.parseDouble(strings[1])));
                }
                //读取词汇表长度
                long termLen = Long.parseLong(reader.readLine());
                //读取词频
                for(int i = 0; i < termLen; ++i){
                    Term term = parseTermFromString(reader.readLine());
                    termsMap.put(term.getName(), term);
                }
            } catch (IOException e){
                //读取文档失败
                return false;
            }
            try{
                reader.close();
            } catch (IOException e){
                //读取文件流关闭失败
            }
            return true;
        }
        return false;
    }

    //将词频字符串转化为词项索引
    private Term parseTermFromString(String str){
        Term term = null;
        String[] args = str.split(" ");
        boolean isFirst = true;
        for(String arg : args) {
            if (isFirst) {
                term = new Term(arg);
                isFirst = false;
            } else {
                String[] tf = arg.split(":");
                term.addPage(Long.parseLong(tf[0]), Double.parseDouble(tf[1]));
            }
        }
        return term;
    }

}
