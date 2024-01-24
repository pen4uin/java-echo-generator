package jeg.core.config;

import jeg.common.config.Constants;

public interface jEGConstants extends Constants {

    String JEG_NAME = "java-echo-generator";
    String JEG_VERSION = "v1.0.0";
    String JEG_AUTHOR = "pen4uin";
    String JEG_DESCRIPTION = "Java 回显载荷生成器";

    String[] headerKeys = {"Accept-","Content-","Cache-Control-","Transfer-","Last-Modified-","Etag-"};

    String MODEL_CODE = "Code";
    String MODEL_CMD = "Command";
}