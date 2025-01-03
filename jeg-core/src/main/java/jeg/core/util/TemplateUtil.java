package jeg.core.util;

import jeg.core.config.jEGConstants;
import jeg.core.template.all.DFSCmdExecTpl;
import jeg.core.template.all.DFSCodeExecTpl;
import jeg.core.template.bes.BESCmdExecTpl;
import jeg.core.template.bes.BESCodeExecTpl;
import jeg.core.template.inforsuite.InforSuiteCmdExecTpl;
import jeg.core.template.inforsuite.InforSuiteCodeExecTpl;
import jeg.core.template.jetty.JettyCmdExecTpl;
import jeg.core.template.jetty.JettyCodeExecTpl;
import jeg.core.template.resin.ResinCmdExecTpl;
import jeg.core.template.resin.ResinCodeExecTpl;
import jeg.core.template.springmvc.SpringMVCCmdExecTpl;
import jeg.core.template.springmvc.SpringMVCCodeExecTpl;
import jeg.core.template.struts2.Struts2CmdExecTpl;
import jeg.core.template.struts2.Struts2CodeExecTpl;
import jeg.core.template.tomcat.TomcatCmdExecTpl;
import jeg.core.template.tomcat.TomcatCodeExecTpl;
import jeg.core.template.tongweb.TongWebCmdExecTpl;
import jeg.core.template.tongweb.TongWebCodeExecTpl;
import jeg.core.template.undertow.UndertowCmdExecTpl;
import jeg.core.template.undertow.UndertowCodeExecTpl;
import jeg.core.template.weblogic.WebLogicCmdExecTpl;
import jeg.core.template.weblogic.WebLogicCodeExecTpl;
import jeg.core.template.websphere.WebSphereCmdExecTpl;
import jeg.core.template.websphere.WebSphereCodeExecTpl;

import java.util.HashMap;
import java.util.Map;

public class TemplateUtil {
    private static final Map<String, Map<String, String>> classMap = new HashMap();

    public static String getEchoTplClassName(String serverType, String modleType) {
        Map<String, String> tplMap = (Map) classMap.get(serverType);
        return tplMap == null ? "" : tplMap.getOrDefault(modleType, "");
    }

    static {

        // 1、jetty
        Map<String, String> jettyMap = new HashMap();
        jettyMap.put(jEGConstants.MODEL_CMD, JettyCmdExecTpl.class.getName());
        jettyMap.put(jEGConstants.MODEL_CODE, JettyCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_JETTY, jettyMap);

        // 2、resin
        Map<String, String> resinMap = new HashMap();
        resinMap.put(jEGConstants.MODEL_CMD, ResinCmdExecTpl.class.getName());
        resinMap.put(jEGConstants.MODEL_CODE, ResinCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_RESIN, resinMap);


        // 3、spring
        Map<String, String> springmvcMap = new HashMap();
        springmvcMap.put(jEGConstants.MODEL_CMD, SpringMVCCmdExecTpl.class.getName());
        springmvcMap.put(jEGConstants.MODEL_CODE, SpringMVCCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_SPRING_MVC, springmvcMap);

        // 4、struts2
        Map<String, String> struts2Map = new HashMap();
        struts2Map.put(jEGConstants.MODEL_CMD, Struts2CmdExecTpl.class.getName());
        struts2Map.put(jEGConstants.MODEL_CODE, Struts2CodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_STRUTS2, struts2Map);

        // 5、tomcat
        Map<String, String> tomcatMap = new HashMap();
        tomcatMap.put(jEGConstants.MODEL_CMD, TomcatCmdExecTpl.class.getName());
        tomcatMap.put(jEGConstants.MODEL_CODE, TomcatCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_TOMCAT, tomcatMap);


        // 6、weblogic
        Map<String, String> weblogicMap = new HashMap();
        weblogicMap.put(jEGConstants.MODEL_CMD, WebLogicCmdExecTpl.class.getName());
        weblogicMap.put(jEGConstants.MODEL_CODE, WebLogicCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_WEBLOGIC, weblogicMap);

        // 7、websphere
        Map<String, String> websphereMap = new HashMap();
        websphereMap.put(jEGConstants.MODEL_CMD, WebSphereCmdExecTpl.class.getName());
        websphereMap.put(jEGConstants.MODEL_CODE, WebSphereCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_WEBSPHERE, websphereMap);

        // 8、undertow
        Map<String, String> undertowMap = new HashMap();
        undertowMap.put(jEGConstants.MODEL_CMD, UndertowCmdExecTpl.class.getName());
        undertowMap.put(jEGConstants.MODEL_CODE, UndertowCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_UNDERTOW, undertowMap);

        // 9、Unknown
        Map<String, String> unknownMap = new HashMap();
        unknownMap.put(jEGConstants.MODEL_CMD, DFSCmdExecTpl.class.getName());
        unknownMap.put(jEGConstants.MODEL_CODE, DFSCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_UNKNOWN, unknownMap);

        // 10、BES
        Map<String, String> besMap = new HashMap();
        besMap.put(jEGConstants.MODEL_CMD, BESCmdExecTpl.class.getName());
        besMap.put(jEGConstants.MODEL_CODE, BESCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_BES, besMap);

        // 11、InforSuite
        Map<String, String> inforsuiteMap = new HashMap();
        inforsuiteMap.put(jEGConstants.MODEL_CMD, InforSuiteCmdExecTpl.class.getName());
        inforsuiteMap.put(jEGConstants.MODEL_CODE, InforSuiteCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_INFORSUITE, inforsuiteMap);

        // 12、BES
        Map<String, String> tongwebMap = new HashMap();
        tongwebMap.put(jEGConstants.MODEL_CMD, TongWebCmdExecTpl.class.getName());
        tongwebMap.put(jEGConstants.MODEL_CODE, TongWebCodeExecTpl.class.getName());
        classMap.put(jEGConstants.SERVER_TONGWEB, tongwebMap);
    }
}
