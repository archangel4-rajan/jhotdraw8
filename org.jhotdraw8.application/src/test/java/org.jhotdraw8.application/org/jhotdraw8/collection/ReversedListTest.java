/*
 * @(#)ReversedListTest.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author werni
 */
public class ReversedListTest {

    public static void testAdd(@NonNull String initialList, int index, Character value, @NonNull String expectedList, String expectedChanges) throws Exception {
        ReversedObservableList<Character> list = new ReversedObservableList<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.add(index, value);

        assertEquals(list, asList(expectedList));
        assertEquals(list.getSource(), asReversedList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsAdd() {
        return Arrays.asList(
                dynamicTest("1", () -> testAdd("", 0, 'a', "a", "add(0,[a])")),
                dynamicTest("2", () -> testAdd("a", 0, 'b', "ba", "add(0,[b])")),
                dynamicTest("3", () -> testAdd("a", 1, 'b', "ab", "add(1,[b])")),
                dynamicTest("4", () -> testAdd("ab", 0, 'c', "cba", "add(0,[c])")),
                dynamicTest("5", () -> testAdd("ab", 1, 'c', "bca", "add(1,[c])")),
                dynamicTest("6", () -> testAdd("ab", 2, 'c', "bac", "add(2,[c])"))
        );
    }

    public static void testSet(@NonNull String initialList, int index, Character value, @NonNull String expectedList, String expectedChanges) throws Exception {
        ReversedObservableList<Character> list = new ReversedObservableList<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        list.set(index, value);

        assertEquals(list, asList(expectedList));
        assertEquals(list.getSource(), asReversedList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsSet() {
        return Arrays.asList(
                dynamicTest("1", () -> testSet("a", 0, 'b', "b", "rep([a]->[b])")),
                dynamicTest("2", () -> testSet("ab", 0, 'c', "ca", "rep([b]->[c])")),
                dynamicTest("3", () -> testSet("ab", 1, 'c', "bc", "rep([a]->[c])"))
        );
    }

    public static void testRemove(@NonNull String initialList, int index, Character value, @NonNull String expectedList, String expectedChanges) throws Exception {
        ReversedObservableList<Character> list = new ReversedObservableList<Character>(asList(initialList));

        ChangeRecorder recorder = new ChangeRecorder();
        list.addListener(recorder);
        if (index < 0) {
            list.remove(value);
        } else {
            list.remove(index);
        }
        assertEquals(list, asList(expectedList));
        assertEquals(list.getSource(), asReversedList(expectedList));
        assertEquals(recorder.getChanges(), expectedChanges);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsRemove() {
        return Arrays.asList(
                dynamicTest("1", () -> testRemove("a", 0, 'x', "", "rem(0,[a])")),
                dynamicTest("1", () -> testRemove("a", -1, 'a', "", "rem(0,[a])")),
                dynamicTest("1", () -> testRemove("ab", 0, 'x', "a", "rem(0,[b])")),
                dynamicTest("1", () -> testRemove("ab", -1, 'a', "b", "rem(1,[a])")),
                dynamicTest("1", () -> testRemove("abc", 0, 'x', "ba", "rem(0,[c])")),
                dynamicTest("1", () -> testRemove("abc", 1, 'x', "ca", "rem(1,[b])")),
                dynamicTest("1", () -> testRemove("abc", 2, 'x', "cb", "rem(2,[a])")),
                dynamicTest("1", () -> testRemove("abc", -1, 'a', "cb", "rem(2,[a])")),
                dynamicTest("1", () -> testRemove("abc", -1, 'b', "ca", "rem(1,[b])")),
                dynamicTest("1", () -> testRemove("abc", -1, 'c', "ba", "rem(0,[c])"))
        );
    }


    private static class ChangeRecorder implements ListChangeListener<Character> {

        private @NonNull StringBuilder buf = new StringBuilder();

        @Override
        public void onChanged(@NonNull ListChangeListener.Change<? extends Character> c) {
            while (c.next()) {
                if (c.wasPermutated()) {
                    buf.append("perm(");
                    boolean first = true;
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        if (i != c.getPermutation(i)) {
                            if (first) {
                                first = false;
                            } else {
                                buf.append(',');
                            }
                            buf.append(i).append("->").append(c.getPermutation(i));
                        }
                    }
                    buf.append(")");
                } else if (c.wasReplaced()) {
                    buf.append("rep(").append(c.getRemoved()).append("->").append(c.getAddedSubList()).append(")");
                } else {
                    if (c.wasAdded()) {
                        buf.append("add(").append(c.getFrom()).append(',').append(c.getAddedSubList()).append(')');
                    }
                    if (c.wasRemoved()) {
                        buf.append("rem(").append(c.getFrom()).append(',').append(c.getRemoved()).append(')');
                    }
                }
            }
        }

        private @NonNull String getChanges() {
            return buf.toString();
        }
    }

    private static @NonNull ObservableList<Character> asList(@NonNull String str) {
        ArrayList<Character> l = new ArrayList<>();
        for (char ch : str.toCharArray()) {
            l.add(ch);
        }
        return FXCollections.observableList(l);
    }

    private static @NonNull ObservableList<Character> asReversedList(@NonNull String str) {
        LinkedList<Character> l = new LinkedList<>();
        for (char ch : str.toCharArray()) {
            l.addFirst(ch);
        }
        return FXCollections.observableList(l);
    }
}
