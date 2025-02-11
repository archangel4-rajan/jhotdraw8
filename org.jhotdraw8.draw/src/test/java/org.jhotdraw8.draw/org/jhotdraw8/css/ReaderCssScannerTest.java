/*
 * @(#)ReaderCssScannerTest.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import java.io.StringReader;

public class ReaderCssScannerTest extends AbstractCssScannerTest {
    @Override
    protected CssScanner createScanner(String inputData) {
        return new ReaderCssScanner(new StringReader(inputData));
    }
}
