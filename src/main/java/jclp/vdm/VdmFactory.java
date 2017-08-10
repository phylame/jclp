package jclp.vdm;

import jclp.util.NamedService;

import java.io.IOException;

public interface VdmFactory extends NamedService {
    VdmReader getReader(Object input) throws IOException;

    VdmWriter getWriter(Object output) throws IOException;
}
