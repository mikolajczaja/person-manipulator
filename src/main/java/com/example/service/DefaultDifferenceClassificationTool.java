package com.example.service;

import com.example.model.Classification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Name could be better, but "Comparator" or "Classifier" would be misleading.
 */
@Slf4j
@Service
public class DefaultDifferenceClassificationTool implements DifferenceClassificationTool {

    private final long threadSleepInterval;

    public DefaultDifferenceClassificationTool(@Value("${config.thread-sleep-interval:3000}") long threadSleepInterval) {
        this.threadSleepInterval = threadSleepInterval;
    }

    /**
     * Compares both values, and determines the level of similarity between them, in form of {@link Classification}
     * <br><br>
     * Classification is determined based on similarity, where similarity = 1 minus
     * dissimilarity.
     * <br>Classifications are the following: ADDED - previous value was not present, LOW -
     * similarity < 0.4, MEDIUM - similarity in the range [0.4, 0.9], HIGH - similarity > 0.9,
     * DELETED.
     * <br>Dissimilarity is the number of differences in characters divided by a longer
     * sequence length. The difference is when a character has been removed or
     * changed in the respective position. A lower score is selected in case of more
     * variants.
     * <br><br>
     * e.g.:
     * <br>Previous: ABCDEFG, new: CFG -> difference: 4, dissimilarity: 4/7, classification: MEDIUM
     * <br>Previous: ABCABC, new: ABC -> difference: 3, dissimilarity: 0.5, classification: MEDIUM
     * <br>Previous: ABCDEFGH, new: TDD -> difference: 7, dissimilarity: 0.875, classification: LOW
     */
    @Cacheable("classifications")
    @SneakyThrows
    public Classification classifyDifference(String oldValue, String newValue) {
        if(oldValue.isBlank()){
            return Classification.ADDED;
        }
        if(newValue.isBlank()){
            return Classification.DELETED;
        }
        log.info("comparing <{}> vs <{}>", oldValue, newValue);
        int maxLength = Math.max(oldValue.length(), newValue.length());

        BigDecimal thisMuchAreUnique = countUniqueChars(oldValue, newValue);
        BigDecimal similarity = BigDecimal.ONE.subtract(thisMuchAreUnique.divide(BigDecimal.valueOf(maxLength), 2, RoundingMode.HALF_UP));

        Classification result = Classification.fromBigDecimal(similarity);
        log.info("similarity: {}, result: {} (will return after: {} ms)", similarity, result, threadSleepInterval);

        Thread.sleep(threadSleepInterval);
        return result;
    }

    BigDecimal countUniqueChars(String oldValue, String newValue) {
        return BigDecimal.valueOf(CollectionUtils.subtract(
                oldValue.chars().mapToObj(c -> (char) c).toList(),
                newValue.chars().mapToObj(c -> (char) c).toList()).size());
    }

}