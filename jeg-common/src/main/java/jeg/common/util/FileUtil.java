package jeg.common.util;

import java.io.*;

public class FileUtil {
    public static void writeFile(String filePath, byte[] bytes) throws IOException {
        OutputStream out = new FileOutputStream(filePath);
        InputStream is = new ByteArrayInputStream(bytes);
        byte[] buff = new byte[1024];
        int len;
        while((len = is.read(buff)) != -1) {
            out.write(buff, 0, len);
        }

        is.close();
        out.close();
    }

}
