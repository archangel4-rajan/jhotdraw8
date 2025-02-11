/*
 * @(#)Nullable.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * The Nullable annotation indicates that the {@code null} value is
 * allowed for the annotated element.
 */
@Documented
@Retention(CLASS)
@Target({TYPE_USE, TYPE_PARAMETER, FIELD, METHOD, PARAMETER, LOCAL_VARIABLE})
public @interface Nullable {
}
