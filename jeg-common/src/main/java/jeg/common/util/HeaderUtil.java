package jeg.common.util;

import java.util.Random;

public class HeaderUtil {

    private static final Random RANDOM = new Random();


    public static String genHeaderName(String[] keys) {
        String key = genRandomKey(keys);
        return key + genRandomSuffix();
    }

    private static String genRandomKey(String[] keys) {
        return keys[RANDOM.nextInt(keys.length)];
    }

    private static String genRandomSuffix() {
        StringBuilder sb = new StringBuilder();
        int length = RANDOM.nextInt(9) + 4; // 生成4-10之间长度的随机字符串
        for (int i = 0; i < length; i++) {
            char c = (char) (RANDOM.nextInt(26) + 'a');
            if (i == 0) {
                c = Character.toUpperCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
