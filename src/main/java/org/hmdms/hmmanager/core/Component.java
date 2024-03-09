package org.hmdms.hmmanager.core;

import java.lang.annotation.Documented;

@Documented
public @interface Component {
    StateC state = StateC.INITIALIZED;
}
