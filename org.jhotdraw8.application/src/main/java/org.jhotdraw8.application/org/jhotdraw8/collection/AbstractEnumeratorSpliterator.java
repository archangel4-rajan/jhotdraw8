/*
 * @(#)AbstractEnumeratorSpliterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Base classes for {@link Enumerator}s that also want to fulfill the
 * {@link Spliterator} interface.
 * <p>
 * Subclasses should only implement the {@link Enumerator#moveNext()}
 * method and the {@link Spliterator#trySplit()} method:
 * <pre>
 *     public boolean moveNext() {
 *         if (...end not reached...) {
 *             current = ...;
 *             return true;
 *         }
 *         return false;
 *     }
 * </pre>
 *
 * @param <E>
 */
public abstract class AbstractEnumeratorSpliterator<E> extends Spliterators.AbstractSpliterator<E>
        implements Enumerator<E> {
    protected E current;

    /**
     * Creates a spliterator reporting the given estimated size and
     * additionalCharacteristics.
     *
     * @param est                       the estimated size of this spliterator if known, otherwise
     *                                  {@code Long.MAX_VALUE}.
     * @param additionalCharacteristics properties of this spliterator's
     *                                  source or elements.  If {@code SIZED} is reported then this
     *                                  spliterator will additionally report {@code SUBSIZED}.
     */
    protected AbstractEnumeratorSpliterator(long est, int additionalCharacteristics) {
        super(est, additionalCharacteristics);
    }

    @Override
    public final boolean tryAdvance(@NonNull Consumer<? super E> action) {
        if (moveNext()) {
            action.accept(current);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final E current() {
        return current;
    }
}
