package jeg.core;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import jeg.common.config.Constants;
import jeg.common.format.*;
import jeg.common.util.*;
import jeg.core.config.jEGConfig;
import jeg.core.config.jEGConstants;
import jeg.core.util.TemplateUtil;
import java.io.File;
import java.io.IOException;

public class jEGenerator {
    private final static ClassPool pool = ClassPool.getDefault();

    private final jEGConfig config;
    private byte[] clazzBytes;


    public jEGenerator(jEGConfig config) throws Throwable {
        this.config = config;
        this.genPayload();
        this.formatPayload();
    }

    private void genPayload() throws Exception {
        CtClass ctClass;
        pool.insertClassPath(new ClassClassPath(jEGenerator.class));

        String className = TemplateUtil.getEchoTplClassName(config.getServerType(), config.getModelType());
        ctClass = pool.getCtClass(className);

        ctClass.getClassFile().setVersionToJava5();

        try {
            if (config.getReqHeaderName() != null && config.getModelType().equals(jEGConstants.MODEL_CMD)) {
                JavassistUtil.addMethod(ctClass, "getReqHeaderName", String.format("{return \"%s\";}", config.getReqHeaderName()));
            }
            if (config.getReqParamName() != null && config.getModelType().equals(jEGConstants.MODEL_CODE)) {
                JavassistUtil.addMethod(ctClass, "getReqParamName", String.format("{return \"%s\";}", config.getReqParamName()));
            }
            ctClass.setName(config.getClassName());
        } catch (Exception e) {
            e.printStackTrace();

        }
        JavassistUtil.removeSourceFileAttribute(ctClass);
        clazzBytes = new GadgetUtil(config, pool, ctClass).modify();
        config.setClassBytesLength(clazzBytes.length);
        config.setClassBytesInFormatter(clazzBytes);
        config.setClassNameInFormatter(config.getClassName());
        ctClass.detach();
    }


    private void formatPayload() throws Throwable {
        if (config.getFormatType().contains(jEGConstants.FORMAT_BCEL)) {
            clazzBytes = new BCELFormatter().transform(clazzBytes, config);
        } else if (config.getFormatType().contains(jEGConstants.FORMAT_JAR)) {
            clazzBytes = new JARFormatter().transform(clazzBytes, config);
        } else if (config.getFormatType().contains(jEGConstants.FORMAT_BASE64)) {
            clazzBytes = new BASE64Formatter().transform(clazzBytes, config);
        } else if (config.getFormatType().contains(jEGConstants.FORMAT_BIGINTEGER)) {
            clazzBytes = new BigIntegerFormatter().transform(clazzBytes, config);
        } else if (config.getFormatType().contains(jEGConstants.FORMAT_JS)) {
            clazzBytes = new JavaScriptFormatter().transform(clazzBytes, config);
        }
    }

    public String getPayload() throws IOException {

        String outputDir = config.getOutputDir();
        String file_output_path = outputDir;
        if (!file_output_path.endsWith(File.separator)) file_output_path = file_output_path + File.separator;
        File dir = new File(outputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        // 判断输出格式
        switch (config.getFormatType()) {
            case Constants.FORMAT_CLASS:
                file_output_path = file_output_path + ClassUtil.getSimpleName(config.getClassName()) + ".class";
                break;
            case Constants.FORMAT_JAR:
                file_output_path = file_output_path + ClassUtil.getSimpleName(config.getClassName()) + ".jar";
                break;
            case Constants.FORMAT_BIGINTEGER:
            case Constants.FORMAT_JS:
            case Constants.FORMAT_BASE64:
            case Constants.FORMAT_BCEL:
                return new String(clazzBytes);
            default:
                break;
        }
        FileUtil.writeFile(file_output_path, clazzBytes);
        return file_output_path;
    }

}