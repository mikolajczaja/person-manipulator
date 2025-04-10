package com.example.service;

import com.example.model.Classification;

public interface DifferenceClassificationTool {
    Classification classifyDifference(String oldValue, String newValue);
}
