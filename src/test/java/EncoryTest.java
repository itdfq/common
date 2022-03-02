import com.itdfq.common.pojo.Result;
import com.itdfq.common.utils.encry.AESUtils;
import org.junit.jupiter.api.Test;

/**
 * @author GodChin
 * @date 2022/3/2 15:51
 * @email 909256107@qq.com
 */
public class EncoryTest {

    private static final String KEY = "ASDQWEFDSFFWEFDFSDFWEEWEE";
    @Test
    public void encory(){
        Result encrypt = AESUtils.encrypt("dfq1234556", KEY);
        System.out.println(encrypt);
    }

    @Test
    public  void decrypt(){
        Result result = AESUtils.decrypt("Den/3fPo3jkJy9Su1YgyzA==", KEY);
        System.out.println(result);
    }
}
