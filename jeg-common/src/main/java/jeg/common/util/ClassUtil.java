package jeg.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassUtil {
    static String[] classNames = {
            "NetworkUtils",
            "KeyUtils",
            "EncryptionUtils",
            "SessionDataUtil",
            "SOAPUtils",
            "ReflectUtil",
            "HttpClientUtil",
            "EncryptionUtil",
            "XMLUtil",
            "JSONUtil",
            "FileUtils",
            "DateUtil",
            "StringUtil",
            "MathUtil",
            "HttpUtil",
            "CSVUtil"
    };
    private static final String[] packageNames = {
            "org.springframework",
            "org.apache.logging",
            "org.apache",
            "com.fasterxml.jackson",
            "org.junit",
            "org.apache.commons.lang",
            "com.google.gso",
            "ch.qos.logback"
    };

    public static String generatePackageName(String[] packageNames) {
        Random random = new Random();
        String packageName = packageNames[random.nextInt(packageNames.length)];
        return packageName;
    }


    public static String getRandomPackageName(String[] packageNames) {
        return generatePackageName(packageNames);
    }

    public static String getRandomName(String[]... arrays) {
        List<String> classNames = new ArrayList<>();
        for (String[] array : arrays) {
            for (String className : array) {
                classNames.add(className);
            }
        }
        Random random = new Random();
        int index = random.nextInt(classNames.size());
        return classNames.get(index);
    }


    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(5) + 2; // 生成2-6之间的随机数
        for (int i = 0; i < length; i++) {
            char c = (char) (random.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getRandomClassName() {

        return getRandomPackageName(packageNames) + "." + generateRandomString() + "." + getRandomName(classNames);
    }

    public static String getRandomLoaderClassName() {

        return getRandomPackageName(packageNames) + "." + generateRandomString() + "." + getRandomName(classNames);
    }

    public static String getSimpleName(String className) {
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < className.length() - 1) {
            return className.substring(lastDotIndex + 1);
        }
        return className;
    }

}
