package exception;

import java.nio.charset.Charset;

//封装字符集异常
public class WrongCharsetException extends Exception {

    private Charset correctCharset;     //正确的字符集

    public WrongCharsetException(String message, Charset correctCharset){
        super(message);
        this.correctCharset = correctCharset;
    }

    public Charset getCorrectCharset(){
        return correctCharset;
    }

}
