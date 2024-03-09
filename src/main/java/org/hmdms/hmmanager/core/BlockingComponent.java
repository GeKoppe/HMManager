package org.hmdms.hmmanager.core;

import java.lang.annotation.Documented;

@Documented
@Component
public @interface BlockingComponent {
    boolean tryToReserve() default false;
}
