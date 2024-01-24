package jeg.common.util;

import javassist.*;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.SourceFileAttribute;

import java.util.List;

/**
 * javassist 工具类
 */
public class JavassistUtil {

    private static ClassPool pool = ClassPool.getDefault();

    public static void addMethod(CtClass ctClass, String methodName, String methodBody) throws Exception {
        ctClass.defrost();
        try {
            // 已存在，修改
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
            ctMethod.setBody(methodBody);
        } catch (NotFoundException ignored) {
            // 不存在，直接添加
            CtMethod method = CtNewMethod.make(methodBody, ctClass);
            ctClass.addMethod(method);
        }
    }


    public static void addField(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        ctClass.defrost();
        try {
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.PUBLIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (NotFoundException ignored) {
            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.STATIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addStaticField(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        ctClass.defrost();
        try {
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.PUBLIC);
                defField.setModifiers(Modifier.STATIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (NotFoundException ignored) {
            try {
                CtField defField = new CtField(pool.getCtClass("java.lang.String"), fieldName, ctClass);
                defField.setModifiers(Modifier.STATIC);
                ctClass.addField(defField, "\"" + fieldValue + "\"");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    // 删除 SourceFileAttribute (源文件名) 信息
    public static void removeSourceFileAttribute(CtClass ctClass) {
        ctClass.defrost();
        ClassFile classFile = ctClass.getClassFile2();

        try {
            // javassist.bytecode.ClassFile.removeAttribute  Since: 3.21
            ReflectionUtil.invokeMethod(classFile, "removeAttribute", new Class[]{String.class}, new Object[]{SourceFileAttribute.tag});
        } catch (Exception e) {
            try {
                // 兼容 javassist v3.20 及以下
                List<AttributeInfo> attributes = (List<AttributeInfo>) ReflectionUtil.getFV(classFile, "attributes");
                removeAttribute(attributes, SourceFileAttribute.tag);
            } catch (Exception ignored) {
            }
        }
    }


    public static synchronized AttributeInfo removeAttribute(List<AttributeInfo> attributes, String name) {
        if (attributes == null) return null;

        for (AttributeInfo ai : attributes)
            if (ai.getName().equals(name)) if (attributes.remove(ai)) return ai;

        return null;
    }


    public static void addFieldIfNotNull(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        if (fieldValue != null) {
            JavassistUtil.addField(ctClass, fieldName, fieldValue);
        }
    }

    public static void addStaticFieldIfNotNull(CtClass ctClass, String fieldName, String fieldValue) throws Exception {
        if (fieldValue != null) {
            JavassistUtil.addStaticField(ctClass, fieldName, fieldValue);
        }
    }

    public static void setNameIfNotNull(CtClass ctClass, String className) throws Exception {
        if (className != null) {
            ctClass.setName(className);
        }
    }

}
