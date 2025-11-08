package com.agilemorph.service;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.dto.RuleEvaluationRequest;
import com.agilemorph.dto.RuleEvaluationResponse;
import com.agilemorph.model.Provider;
import com.agilemorph.model.RuleEvaluation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RuleEngineService {
    
    private static final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);
    
    @Inject
    ProviderService providerService;
    
    private KieContainer kieContainer;
    
    public void initializeRuleEngine() {
        try {
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            
            // Load DRL files from classpath
            loadDrlFiles(kieServices, kieFileSystem);
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                logger.error("Error building rules: {}", kieBuilder.getResults().getMessages());
                throw new RuntimeException("Failed to build rules");
            }
            
            kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
            logger.info("Rule engine initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize rule engine", e);
            throw new RuntimeException("Failed to initialize rule engine", e);
        }
    }
    
    private void loadDrlFiles(KieServices kieServices, KieFileSystem kieFileSystem) {
        try {
            // Load license expiry rule
            InputStream licenseExpiryStream = getClass().getClassLoader()
                .getResourceAsStream("rules/license-expiry-rule.drl");
            if (licenseExpiryStream != null) {
                kieFileSystem.write("src/main/resources/rules/license-expiry-rule.drl", 
                    kieServices.getResources().newInputStreamResource(licenseExpiryStream));
            }
            
            // Load duplicate detection rule
            InputStream duplicateStream = getClass().getClassLoader()
                .getResourceAsStream("rules/duplicate-detection-rule.drl");
            if (duplicateStream != null) {
                kieFileSystem.write("src/main/resources/rules/duplicate-detection-rule.drl", 
                    kieServices.getResources().newInputStreamResource(duplicateStream));
            }
            
        } catch (Exception e) {
            logger.error("Error loading DRL files", e);
            throw new RuntimeException("Failed to load DRL files", e);
        }
    }
    
    @Transactional
    public RuleEvaluationResponse evaluateRules(RuleEvaluationRequest request) {
        if (kieContainer == null) {
            initializeRuleEngine();
        }
        
        RuleEvaluationResponse response = new RuleEvaluationResponse();
        response.evaluatedAt = LocalDateTime.now();
        response.results = new ArrayList<>();
        response.metadata = new HashMap<>();
        
        try {
            KieSession kieSession = kieContainer.newKieSession();
            
            // Add provider to session
            kieSession.insert(request.provider);
            
            // Add context if provided
            if (request.context != null) {
                kieSession.insert(request.context);
            }
            
            // Fire rules
            int rulesFired = kieSession.fireAllRules();
            
            // Collect results and facts from session
            List<Object> facts = new ArrayList<>();
            for (Object fact : kieSession.getObjects()) {
                if (fact instanceof RuleEvaluationResponse.RuleResult result) {
                    response.results.add(result);
                }
                facts.add(fact);
            }
            
            response.success = true;
            response.message = "Rules evaluated successfully. " + rulesFired + " rules fired.";
            response.metadata.put("rulesFired", rulesFired);
            response.metadata.put("facts", facts);
            
            // Create rule evaluation records
            createRuleEvaluationRecords(request.provider, response);
            
            kieSession.dispose();
            
        } catch (Exception e) {
            logger.error("Error evaluating rules", e);
            response.success = false;
            response.message = "Error evaluating rules: " + e.getMessage();
        }
        
        return response;
    }
    
    @Transactional
    public RuleEvaluationResponse evaluateRulesForProvider(Long providerId) {
        ProviderDto provider = providerService.getProvider(providerId);
        
        RuleEvaluationRequest request = new RuleEvaluationRequest();
        request.provider = provider;
        request.context = new HashMap<>();
        request.includeFacts = true;
        
        return evaluateRules(request);
    }
    
    private void createRuleEvaluationRecords(ProviderDto provider, RuleEvaluationResponse response) {
        // Skip persistence if provider is not yet saved (id is null)
        if (provider.id == null) {
            return;
        }
        // Create rule evaluation records for each result
        for (RuleEvaluationResponse.RuleResult result : response.results) {
            RuleEvaluation evaluation = new RuleEvaluation();
            evaluation.provider = Provider.findById(provider.id);
            evaluation.ruleName = result.ruleName;
            evaluation.triggered = result.triggered;
            evaluation.severity = result.severity;
            evaluation.message = result.message;
            evaluation.metadata = result.metadata;
            evaluation.facts = result.facts;
            evaluation.persist();
        }
    }
    
    public List<String> getLoadedRules() {
        List<String> rules = new ArrayList<>();
        rules.add("license-expiry-rule");
        rules.add("duplicate-detection-rule");
        return rules;
    }
    
    public boolean isRuleEngineInitialized() {
        return kieContainer != null;
    }
}
