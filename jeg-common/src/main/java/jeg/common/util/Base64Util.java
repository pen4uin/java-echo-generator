package jeg.common.util;

public class Base64Util {

    public static String encodeToBase64(byte[] input) throws Exception {
        String value = null;
        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", (Class[]) null).invoke(base64, (Object[]) null);
            value = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, input);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, input);
            } catch (Exception var5) {
            }
        }
        return value;
    }

    public static String decodeFromBase64(String input) throws Exception {
        byte[] var2 = null;

        Class var1;
        try {
            var1 = Class.forName("java.util.Base64");
            Object var3 = var1.getMethod("getDecoder").invoke((Object) null, (Object[]) null);
            var2 = (byte[]) ((byte[]) var3.getClass().getMethod("decode", String.class).invoke(var3, input));
        } catch (Exception var6) {
            try {
                var1 = Class.forName("sun.misc.BASE64Decoder");
                Object var4 = var1.newInstance();
                var2 = (byte[]) ((byte[]) var4.getClass().getMethod("decodeBuffer", String.class).invoke(var4, input));
            } catch (Exception var5) {
            }
        }

        return new String(var2);
    }
}
