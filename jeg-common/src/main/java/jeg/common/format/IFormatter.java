package jeg.common.format;

import jeg.common.config.Config;

public interface IFormatter {
    public byte[] transform(byte[] bytes, Config config) throws Exception;
}
