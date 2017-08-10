package jclp.vdm.file;

import jclp.util.CollectionUtils;
import lombok.NonNull;
import jclp.vdm.VdmFactory;
import jclp.vdm.VdmReader;
import jclp.vdm.VdmWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class FileVdmFactory implements VdmFactory {
    @Override
    public String getName() {
        return "FileVDM";
    }

    @Override
    public Set<String> getNames() {
        return CollectionUtils.setOf("dir");
    }

    private File getDirectory(Object input, boolean reading) throws IOException {
        File dir;
        if (input instanceof String) {
            dir = new File((String) input);
        } else if (input instanceof File) {
            dir = (File) input;
        } else {
            throw new IllegalArgumentException(input.toString());
        }
        if (!dir.exists()) {
            if (reading) {
                throw new FileNotFoundException(dir.getAbsolutePath());
            } else if (!dir.mkdirs()) {
                throw new IOException("Cannot create directory: " + dir);
            }
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }
        return dir;
    }

    @Override
    public VdmReader getReader(@NonNull Object input) throws IOException {
        return new FileVdmReader(getDirectory(input, true));
    }

    @Override
    public VdmWriter getWriter(@NonNull Object output) throws IOException {
        return new FileVdmWriter(getDirectory(output, false));
    }
}
