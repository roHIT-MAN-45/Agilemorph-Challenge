package com.agilemorph.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class RuleEvaluationRequest {
    
    @NotNull
    public ProviderDto provider;
    
    public Map<String, Object> context;
    
    public String ruleSet;
    
    public boolean includeFacts = true;
}
