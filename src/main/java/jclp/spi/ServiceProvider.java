package jclp.spi;

import java.util.Set;

public interface ServiceProvider {
    String getName();

    Set<String> getKeys();
}
