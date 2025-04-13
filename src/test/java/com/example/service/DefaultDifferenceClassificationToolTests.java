package com.example.service;

import com.example.model.Classification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultDifferenceClassificationToolTests {

    private static Stream<Arguments> testDefaultDifferenceClassificationToolData() {
        return Stream.of(
                Arguments.of("ABCD", "ABCD", 0, Classification.HIGH),
                Arguments.of("ABCD", "BCD", 1, Classification.MEDIUM),
                Arguments.of("ABCD", "BWD", 2, Classification.MEDIUM),
                Arguments.of("ABCDEFG", "CFG", 4, Classification.MEDIUM),
                Arguments.of("ABCABC", "ABC", 3, Classification.MEDIUM),
                Arguments.of("ABCDEFGH", "TDD", 7, Classification.LOW),
                Arguments.of("ABC", "ABCDEF", 0, Classification.HIGH)
        );
    }

    @ParameterizedTest
    @MethodSource("testDefaultDifferenceClassificationToolData")
    void testCountUniqueChars(String oldValue, String newValue, int expected) {
        DefaultDifferenceClassificationTool comparator = new DefaultDifferenceClassificationTool(0);
        assertThat(comparator.countUniqueChars(oldValue, newValue)).isEqualTo(new BigDecimal(expected));
    }
    @ParameterizedTest
    @MethodSource("testDefaultDifferenceClassificationToolData")
    void testClassifyDifference(String oldValue, String newValue, int count, Classification expected) {
        DefaultDifferenceClassificationTool comparator = new DefaultDifferenceClassificationTool(0);
        assertThat(comparator.classifyDifference(oldValue, newValue)).isEqualTo(expected);
    }

    @Test
    void testOneValueCases() {
        DefaultDifferenceClassificationTool comparator = new DefaultDifferenceClassificationTool(0);
        assertThat(comparator.classifyDifference("aaa", "")).isEqualTo(Classification.DELETED);
        assertThat(comparator.classifyDifference("", "Xyz103")).isEqualTo(Classification.ADDED);
    }

    @Test
    void testWeirdCases() {
        DefaultDifferenceClassificationTool comparator = new DefaultDifferenceClassificationTool(0);
        assertThat(comparator.classifyDifference("aa a", "aaa")).isEqualTo(Classification.MEDIUM);
        assertThat(comparator.classifyDifference("aa a", "aaa ")).isEqualTo(Classification.HIGH);
        assertThat(comparator.classifyDifference("a", "   ")).isEqualTo(Classification.DELETED);
        assertThat(comparator.classifyDifference("129512AEGASGA🤡😅😥", "12AEGA🥰😿")).isEqualTo(Classification.MEDIUM);
    }
}