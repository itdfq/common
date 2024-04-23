import com.itdfq.common.utils.OkHttpUtils;

/**
 * @author dfq 2024/4/23 17:42
 * @implNote
 */
public class OkHttpTest {
    public static void main(String[] args) {
        try {
            String s = OkHttpUtils.syncHeadersAndJson("https://baidu.com", null, null, null, "GET", String.class);
            System.out.println(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
