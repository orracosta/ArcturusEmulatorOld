package com.habboproject.server.utilities.collections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E> {
    private final Map<E, Boolean> backingMap;
    private final Set<E> backingMapSet;

    public ConcurrentHashSet() {
        backingMap = new ConcurrentHashMap<E, Boolean>();
        backingMapSet = backingMap.keySet();
    }

    public int size() {
        return backingMap.size();
    }

    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    public boolean contains(Object o) {
        return backingMap.containsKey(o);
    }

    public Iterator<E> iterator() {
        return backingMapSet.iterator();
    }

    public void clear() {
        backingMap.clear();
    }

    public Object[] toArray() {
        return backingMapSet.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return backingMapSet.toArray(a);
    }

    public boolean add(E e) {
        return backingMap.put(e, Boolean.TRUE) == null;
    }

    public boolean remove(Object o) {
        return backingMap.remove(o) != null;
    }

    public boolean containsAll(Collection<?> c) {
        return backingMapSet.containsAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return backingMapSet.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return backingMapSet.retainAll(c);
    }

    public String toString() {
        return backingMapSet.toString();
    }

    public int hashCode() {
        return backingMapSet.hashCode();
    }

    public boolean equals(Object o) {
        return o == this || backingMapSet.equals(o);
    }
}