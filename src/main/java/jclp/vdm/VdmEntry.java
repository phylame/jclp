package jclp.vdm;

public interface VdmEntry {
    String getName();

    String getComment();

    long lastModified();

    boolean isDirectory();
}
