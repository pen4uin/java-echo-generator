package jeg.common.util;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javassist.*;
import jeg.common.config.Constants;
import jeg.common.config.Config;

public class GadgetUtil {
    private Config config;
    private ClassPool pool;
    private CtClass modifiedClass;

    public GadgetUtil(Config config, ClassPool pool, CtClass modifiedClass) {
        this.config = config;
        this.pool = pool;
        this.modifiedClass = modifiedClass;
    }

    public byte[] modify() throws Exception {
        byte[] classBytes = null;
        if (pool != null && modifiedClass != null) {
            if (config.getGadgetType().contains(Constants.GADGET_JDK_TRANSLET)) {
                applyJDKAbstractTranslet();
            }

            if (config.getGadgetType().contains(Constants.GADGET_XALAN_TRANSLET)) {
                applyXALANAbstractTranslet();
            }
            modifiedClass.getClassFile().setVersionToJava5();
            classBytes = modifiedClass.toBytecode();
            modifiedClass.defrost();
        } else {
            throw new Exception("pool or modifiedClass is null");
        }
        return classBytes;
    }


    public void applyJDKAbstractTranslet() throws NotFoundException, CannotCompileException {
        pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
        modifiedClass.setSuperclass(pool.get(AbstractTranslet.class.getName()));
    }


    public void applyXALANAbstractTranslet() throws NotFoundException, CannotCompileException, ClassNotFoundException {
        try {
            pool.get("org.apache.xalan.xsltc.runtime.AbstractTranslet");
        } catch (NotFoundException e) {
            pool.makeClass("org.apache.xalan.xsltc.runtime.AbstractTranslet");
        }
        modifiedClass.setSuperclass(pool.get("org.apache.xalan.xsltc.runtime.AbstractTranslet"));
    }

}
