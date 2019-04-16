import org.junit.Test;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: cls
 **/
public class testMap {

    @Test
    public void test(){
        Map<String, String[]> ma=new HashMap<>();
        String[] abc={"RSA2","jhg"};
        String[] qwe={"afg"};
        System.out.println(Arrays.toString(abc));
        ma.put("sign_type",abc);
        ma.put("sx",qwe);
        System.out.println(Arrays.toString(ma.remove("sign_type")));
        System.out.println(ma);
        System.out.println("okokok1");

    }




}
