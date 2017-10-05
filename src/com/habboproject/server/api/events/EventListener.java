package com.habboproject.server.api.events;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
    Class<? extends Event> event();
}
