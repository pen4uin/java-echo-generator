package jeg.core.config;

import jeg.common.config.Config;
import jeg.common.config.Constants;
import jeg.common.util.ClassUtil;
import jeg.common.util.HeaderUtil;
import jeg.common.util.RandomUtil;

import java.util.Objects;

public class jEGConfig extends Config {

    public jEGConfig() {
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setReqHeaderName(String reqHeaderName) {
        this.reqHeaderName = reqHeaderName;
    }

    public void setReqParamName(String reqParamName) {
        this.reqParamName = reqParamName;
    }

    public void setRespHeaderName(String respHeaderName) {
        this.respHeaderName = respHeaderName;
    }

    public void setBase64ClassString(String base64ClassString) {
        this.base64ClassString = base64ClassString;
    }

    public void setClassBytes(byte[] classBytes) {
        this.classBytes = classBytes;
    }

    public void setClassBytesLength(int classBytesLength) {
        this.classBytesLength = classBytesLength;
    }

    public void setLoaderClassName(String loaderClassName) {
        this.loaderClassName = loaderClassName;
    }

    public void setGadgetType(String gadgetType) {
        this.gadgetType = gadgetType;
    }

    private String serverType;
    private String modelType;
    private String className;
    private String formatType;
    private String outputDir;
    private String desKey;
    private String reqHeaderName;
    private String reqParamName;
    private String respHeaderName;
    private String base64ClassString;
    private byte[] classBytes;
    private int classBytesLength;
    private boolean implementsASTTransformationType = false;
    private boolean implementsScriptEngineFactory = false;
    private String loaderClassName;
    private String gadgetType;

    public String getModelType() {
        return modelType;
    }

    public String getServerType() {
        return serverType;
    }

    public String getClassName() {
        return className;
    }

    public String getFormatType() {
        return formatType;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getDesKey() {
        return desKey;
    }

    public String getReqHeaderName() {
        return reqHeaderName;
    }

    public String getReqParamName() {
        return reqParamName;
    }

    public String getRespHeaderName() {
        return respHeaderName;
    }

    public String getBase64ClassString() {
        return base64ClassString;
    }

    public byte[] getClassBytes() {
        return classBytes;
    }

    public int getClassBytesLength() {
        return classBytesLength;
    }

    public String getLoaderClassName() {
        return loaderClassName;
    }

    public String getGadgetType() {
        return gadgetType;
    }

    public void build() {
        // 检查 serverType、modelType、formatType  是否已设置
        if (this.modelType == null || this.serverType == null || this.formatType == null) {
            throw new IllegalStateException("serverType、modelType and formatType must be set.");
        }
        // 可选参数，默认随机
        if (this.getOutputDir() == null || Objects.equals(this.getOutputDir(), "")) setOutputDir(System.getProperty("user.dir"));
        if (this.getClassName() == null || Objects.equals(this.getClassName(), "")) setClassName(ClassUtil.getRandomClassName());
        if (this.getReqHeaderName() == null || Objects.equals(this.getReqHeaderName(), "")) setReqHeaderName(HeaderUtil.genHeaderName(jEGConstants.headerKeys));
        if (this.getReqParamName() == null || Objects.equals(this.getReqParamName(), "")) setReqParamName(RandomUtil.genRandomLengthString(4));
        if (this.getGadgetType() == null) setGadgetType(Constants.GADGET_NONE);
    }
}