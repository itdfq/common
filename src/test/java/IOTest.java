import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author GodChin
 * @date 2022/2/18 17:12
 * @email 909256107@qq.com
 */
public class IOTest {


    @Test
    public void readByte() throws IOException {
        File file = new File("E:\\java-IDEA2\\common\\src\\main\\resources\\test.xml");
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        String s = new String(bytes);
        inputStream.close();
        System.out.println(s);
    }

    @Test
    public void readByteNotLength() throws IOException {
        File file = new File("E:\\java-IDEA2\\common\\src\\main\\resources\\test.xml");
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[64];
        int temp = 0, len = 0;
        //-1文件读取完毕
        StringBuilder builder = new StringBuilder();
        while ((temp = inputStream.read()) != -1) {
            bytes[len] = (byte) temp;
            len++;
            if (len == 64) {
                builder.append(new String(bytes));
                len = 0;
            }
        }
        inputStream.close();
        builder.append(new String(bytes, 0, len));
        System.out.println(builder.toString());

    }

    @Test
    public void readTest() throws IOException {
        File file = new File("E:\\java-IDEA2\\common\\src\\main\\resources\\test.xml");
        Reader fileReader = new FileReader(file);
        char[] chars = new char[(int) file.length()];
        fileReader.read(chars);
        fileReader.close();
        ;
        System.out.println(new String(chars));
    }


    @Test
    public void readTestNot() throws IOException {
        File file = new File("E:\\java-IDEA2\\common\\src\\main\\resources\\test.xml");
        Reader fileReader = new FileReader(file);
        char[] chars = new char[64];
        int len = 0;
        int temp ;
        StringBuilder builder = new StringBuilder();
        while ((temp = fileReader.read() )!= -1) {
            chars[len] = (char) temp;
            len++;
            if (len==64){
                builder.append(new String(chars));
                len=0;
            }
        }
        fileReader.close();
        builder.append(new String(chars,0,len));
        System.out.println(builder);
    }


}
