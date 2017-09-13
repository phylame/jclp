package jclp;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Map;

@RequiredArgsConstructor
public class ObservableMap<K, V> implements Map<K, V> {
    @Delegate
    private final Map<K, V> map;


}
