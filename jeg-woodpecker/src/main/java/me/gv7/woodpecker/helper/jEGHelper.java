package me.gv7.woodpecker.helper;

import jeg.core.jEGenerator;
import jeg.core.config.jEGConstants;
import jeg.core.config.jEGConfig;
import me.gv7.woodpecker.plugin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class jEGHelper implements IHelperPlugin {
    public static IHelperPluginCallbacks callbacks;
    public static IPluginHelper pluginHelper;

    @Override
    public void HelperPluginMain(IHelperPluginCallbacks helperPluginCallbacks) {

        callbacks = helperPluginCallbacks;
        pluginHelper = callbacks.getPluginHelper();
        callbacks.setHelperPluginName("jEcho Generator Utils");
        callbacks.setHelperPluginVersion(jEGConstants.JEG_VERSION);
        callbacks.setHelperPluginAutor(jEGConstants.JEG_AUTHOR);
        callbacks.setHelperPluginDescription(jEGConstants.JEG_DESCRIPTION);
        helperPluginCallbacks.registerHelper(new ArrayList<IHelper>() {{
            add(new jEchoHelper(jEGConstants.SERVER_TOMCAT));
            add(new jEchoHelper(jEGConstants.SERVER_SPRING_MVC));
            add(new jEchoHelper(jEGConstants.SERVER_JETTY));
            add(new jEchoHelper(jEGConstants.SERVER_RESIN));
            add(new jEchoHelper(jEGConstants.SERVER_WEBSPHERE));
            add(new jEchoHelper(jEGConstants.SERVER_WEBLOGIC));
            add(new jEchoHelper(jEGConstants.SERVER_UNDERTOW));
            add(new jEchoHelper(jEGConstants.SERVER_STRUTS2));
            add(new jEchoHelper(jEGConstants.SERVER_BES));
            add(new jEchoHelper(jEGConstants.SERVER_INFORSUITE));
            add(new jEchoHelper(jEGConstants.SERVER_TONGWEB));
            add(new jEchoHelper(jEGConstants.SERVER_UNKNOWN));
        }});
    }

    public class jEchoHelper implements IHelper {

        private String helperName;

        public jEchoHelper(String helperName) {
            this.helperName = helperName;
        }

        @Override
        public String getHelperTabCaption() {
            return this.helperName;
        }


        @Override
        public IArgsUsageBinder getHelperCutomArgs() {

            IArgsUsageBinder binder = pluginHelper.createArgsUsageBinder();
            List<IArg> list = new ArrayList();
            IArg modelType = pluginHelper.createArg();
            modelType.setName("model_type");
            modelType.setType(7);
            List<String> enumModelType = new ArrayList();
            enumModelType.add(jEGConstants.MODEL_CMD);
            enumModelType.add(jEGConstants.MODEL_CODE);
            modelType.setEnumValue(enumModelType);
            modelType.setDefaultValue(jEGConstants.MODEL_CMD);
            modelType.setRequired(true);
            modelType.setDescription("自定义执行模式(命令/代码)");
            list.add(modelType);

            IArg gadgetType = pluginHelper.createArg();
            gadgetType.setName("gadget_type");
            gadgetType.setType(7);
            List<String> enumGadgetType = new ArrayList();
            enumGadgetType.add(jEGConstants.GADGET_NONE);
            enumGadgetType.add(jEGConstants.GADGET_JDK_TRANSLET);
            enumGadgetType.add(jEGConstants.GADGET_XALAN_TRANSLET);
            gadgetType.setEnumValue(enumGadgetType);
            gadgetType.setDefaultValue(jEGConstants.GADGET_NONE);
            gadgetType.setRequired(true);
            gadgetType.setDescription("自定义利用链");
            list.add(gadgetType);

            IArg formatType = pluginHelper.createArg();
            formatType.setName("format_type");
            formatType.setType(7);
            List<String> enumFormattType = new ArrayList();
            enumFormattType.add(jEGConstants.FORMAT_BASE64);
            enumFormattType.add(jEGConstants.FORMAT_BCEL);
            enumFormattType.add(jEGConstants.FORMAT_BIGINTEGER);
            enumFormattType.add(jEGConstants.FORMAT_CLASS);
            enumFormattType.add(jEGConstants.FORMAT_JAR);
            enumFormattType.add(jEGConstants.FORMAT_JS);
            formatType.setEnumValue(enumFormattType);
            formatType.setDefaultValue(jEGConstants.FORMAT_BASE64);
            formatType.setRequired(true);
            formatType.setDescription("自定义输出格式");
            list.add(formatType);

            IArg request_header_name = pluginHelper.createArg();
            request_header_name.setName("request_header_name");
            request_header_name.setType(0);
            request_header_name.setDefaultValue("随机生成");
            request_header_name.setRequired(false);
            request_header_name.setDescription("自定义HTTP请求头: Header Name");
            list.add(request_header_name);

            IArg request_param_name = pluginHelper.createArg();
            request_param_name.setName("request_param_name");
            request_param_name.setType(0);
            request_param_name.setDefaultValue("随机生成");
            request_param_name.setRequired(false);
            request_param_name.setDescription("自定义请求参数: Param Name");
            list.add(request_param_name);

            IArg shell_class_name = pluginHelper.createArg();
            shell_class_name.setName("class_name");
            shell_class_name.setType(0);
            shell_class_name.setRequired(false);
            shell_class_name.setDescription("自定义类名");
            list.add(shell_class_name);

            IArg output_path = pluginHelper.createArg();
            output_path.setName("output_path");
            output_path.setType(0);
            output_path.setDefaultValue("workdir");
            output_path.setRequired(false);
            output_path.setDescription("自定义输出路径");
            list.add(output_path);
            binder.setArgsList(list);
            return binder;
        }

        @Override
        public void doHelp(Map<String, Object> customArgs, IResultOutput resultOutput) {
            try {
                jEGConfig config = new jEGConfig() {{
                    setServerType(helperName);
                    setModelType((String) customArgs.get("model_type"));
                    setGadgetType((String) customArgs.get("gadget_type"));
                    setFormatType((String) customArgs.get("format_type"));
                    setReqParamName((String) customArgs.get("request_param_name"));
                    setRespHeaderName((String) customArgs.get("request_header_name"));
                    setClassName((String) customArgs.get("class_name"));
                    setOutputDir((String) customArgs.get("output_path"));
                    build();
                }};
                // 生成 payload
                jEGenerator generator = new jEGenerator(config);
                String result = generator.getPayload();
                resultOutput(resultOutput, config, result);
            } catch (Throwable e) {
                resultOutput.errorPrintln(jEGHelper.pluginHelper.getThrowableInfo(e));
            }
        }
    }

    public static void resultOutput(IResultOutput resultOutput, jEGConfig config, String result) {
        resultOutput.successPrintln("基础信息:");
        resultOutput.rawPrintln("");
        if (config.getModelType().equals(jEGConstants.MODEL_CMD)) {
            resultOutput.rawPrintln("请求头: " + config.getReqHeaderName());
        } else {
            resultOutput.rawPrintln("请求参数: " + config.getReqParamName());
        }
        resultOutput.rawPrintln("类名: " + config.getClassName());
        resultOutput.rawPrintln("载荷长度: " + config.getClassBytesLength());
        resultOutput.rawPrintln("");
        try {
            resultOutput.successPrintln("结果输出:");
            resultOutput.rawPrintln("");
            resultOutput.rawPrintln(result);
            resultOutput.rawPrintln("");
        } catch (Throwable e) {
            resultOutput.errorPrintln(jEGHelper.pluginHelper.getThrowableInfo(e));
        }
    }
}
