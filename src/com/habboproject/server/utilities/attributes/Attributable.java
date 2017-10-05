package com.habboproject.server.utilities.attributes;

public interface Attributable {
    void setAttribute(String attributeKey, Object attributeValue);

    Object getAttribute(String attributeKey);

    boolean hasAttribute(String attributeKey);

    void removeAttribute(String attributeKey);
}
