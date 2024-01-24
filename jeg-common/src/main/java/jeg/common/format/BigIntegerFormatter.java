package jeg.common.format;

import jeg.common.config.Config;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerFormatter implements IFormatter {
    @Override
    public byte[] transform(byte[] bytes, Config config) throws IOException {
        return new BigInteger(bytes).toString(36).getBytes();
    }
}