package jeg.common.config;

public class Config {
    private String serverType;
    private String formatType;
    private String gadgetType;
    private String loaderClassName;
    private String classNameInFormatter;
    private byte[] classBytesInFormatter;
    private String classBase64InFormatter;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    private String outputDir;
    public String getLoaderClassName() {
        return loaderClassName;
    }

    public void setLoaderClassName(String loaderClassName) {
        this.loaderClassName = loaderClassName;
    }

    public String getClassNameInFormatter() {
        return classNameInFormatter;
    }

    public void setClassNameInFormatter(String classNameInFormatter) {
        this.classNameInFormatter = classNameInFormatter;
    }

    public byte[] getClassBytesInFormatter() {
        return classBytesInFormatter;
    }

    public void setClassBytesInFormatter(byte[] classBytesInFormatter) {
        this.classBytesInFormatter = classBytesInFormatter;
    }

    public String getClassBase64InFormatter() {
        return classBase64InFormatter;
    }

    public void setClassBase64InFormatter(String classBase64InFormatter) {
        this.classBase64InFormatter = classBase64InFormatter;
    }



    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getGadgetType() {
        return gadgetType;
    }

    public void setGadgetType(String gadgetType) {
        this.gadgetType = gadgetType;
    }

    public boolean isImplementsASTTransformationType() {
        return implementsASTTransformationType;
    }

    public void setImplementsASTTransformationType(boolean implementsASTTransformationType) {
        this.implementsASTTransformationType = implementsASTTransformationType;
    }

    public boolean isImplementsScriptEngineFactory() {
        return implementsScriptEngineFactory;
    }

    public void setImplementsScriptEngineFactory(boolean implementsScriptEngineFactory) {
        this.implementsScriptEngineFactory = implementsScriptEngineFactory;
    }

    private boolean implementsASTTransformationType = false;
    private boolean implementsScriptEngineFactory = false;

}
