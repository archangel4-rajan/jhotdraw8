/*
 * @(#)PathBuilderException.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph.path;

/**
 * PathBuilderException.
 *
 * @author Werner Randelshofer
 */
public class PathBuilderException extends Exception {

    private static final long serialVersionUID = 0L;

    public PathBuilderException(String message) {
        super(message);
    }

    public PathBuilderException(Exception cause) {
        super(cause);
    }

    public PathBuilderException(String message, Exception cause) {
        super(message, cause);
    }

}
