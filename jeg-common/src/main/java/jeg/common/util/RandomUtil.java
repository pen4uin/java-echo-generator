package jeg.common.util;

import java.util.Random;

public class RandomUtil {

    // 生成随机长度的字符串
    public static String genRandomLengthString(int minLength) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(16) + minLength; // 生成2-6之间的随机数
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            if (i == 0) {
                c = Character.toUpperCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
