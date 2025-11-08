package com.agilemorph.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RuleEvaluationResponse {
    
    public boolean success;
    public String message;
    public LocalDateTime evaluatedAt;
    public List<RuleResult> results;
    public Map<String, Object> metadata;
    
    public static class RuleResult {
        public String ruleName;
        public boolean triggered;
        public String severity;
        public String message;
        public String metadata;
        public List<String> facts;
        public Map<String, Object> context;
    }
}
