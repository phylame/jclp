package jclp.vdm;

import jclp.util.ServiceManager;
import lombok.val;

import java.io.IOException;

public final class VdmManager extends ServiceManager<VdmFactory> {
    private static final VdmManager instance = new VdmManager();

    private VdmManager() {
        super(VdmFactory.class);
    }

    public static VdmReader getReader(String name, Object input) throws IOException {
        val factory = instance.getService(name);
        return factory != null ? factory.getReader(input) : null;
    }

    public static VdmWriter getWriter(String name, Object output) throws IOException {
        val factory = instance.getService(name);
        return factory != null ? factory.getWriter(output) : null;
    }
}
