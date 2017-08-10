package jclp.vdm.zip;

import jclp.util.CollectionUtils;
import lombok.NonNull;
import jclp.vdm.VdmFactory;
import jclp.vdm.VdmReader;
import jclp.vdm.VdmWriter;

import java.io.*;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipVdmFactory implements VdmFactory {
    @Override
    public String getName() {
        return "ZipVDM";
    }

    @Override
    public Set<String> getNames() {
        return CollectionUtils.setOf("zip");
    }

    @Override
    public VdmReader getReader(@NonNull Object input) throws IOException {
        ZipFile zip;
        if (input instanceof String) {
            zip = new ZipFile((String) input);
        } else if (input instanceof File) {
            zip = new ZipFile((File) input);
        } else if (input instanceof ZipFile) {
            zip = (ZipFile) input;
        } else {
            throw new IllegalArgumentException(input.toString());
        }
        return new ZipVdmReader(zip);
    }

    @Override
    public VdmWriter getWriter(@NonNull Object output) throws IOException {
        ZipOutputStream zip;
        if (output instanceof String) {
            zip = new ZipOutputStream(new FileOutputStream((String) output));
        } else if (output instanceof File) {
            zip = new ZipOutputStream(new FileOutputStream((File) output));
        } else if (output instanceof OutputStream) {
            zip = new ZipOutputStream((OutputStream) output);
        } else if (output instanceof ZipOutputStream) {
            zip = (ZipOutputStream) output;
        } else {
            throw new IllegalArgumentException(output.toString());
        }
        return new ZipVdmWriter(zip);
    }
}
