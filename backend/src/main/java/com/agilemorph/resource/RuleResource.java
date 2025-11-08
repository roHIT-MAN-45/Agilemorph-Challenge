package com.agilemorph.resource;

import com.agilemorph.dto.RuleEvaluationRequest;
import com.agilemorph.dto.RuleEvaluationResponse;
import com.agilemorph.service.RuleEngineService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Path("/api/rules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Rule Engine", description = "Operations for evaluating business rules")
public class RuleResource {
    
    @Inject
    RuleEngineService ruleEngineService;
    
    @POST
    @Path("/evaluate")
    @Operation(summary = "Evaluate rules against provider", description = "Evaluates configured business rules against a provider payload")
    public Response evaluateRules(@Valid RuleEvaluationRequest request) {
        try {
            RuleEvaluationResponse response = ruleEngineService.evaluateRules(request);
            
            if (response.success) {
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
            }
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/evaluate/{providerId}")
    @Operation(summary = "Evaluate rules for provider by ID", description = "Evaluates business rules for a specific provider")
    public Response evaluateRulesForProvider(@PathParam("providerId") Long providerId) {
        try {
            RuleEvaluationResponse response = ruleEngineService.evaluateRulesForProvider(providerId);
            
            if (response.success) {
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
            }
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Operation(summary = "Get loaded rules", description = "Retrieves list of loaded business rules")
    public Response getLoadedRules() {
        try {
            List<String> rules = ruleEngineService.getLoadedRules();
            return Response.ok(Map.of("rules", rules, "count", rules.size())).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/status")
    @Operation(summary = "Get rule engine status", description = "Checks if the rule engine is initialized and ready")
    public Response getRuleEngineStatus() {
        try {
            boolean initialized = ruleEngineService.isRuleEngineInitialized();
            return Response.ok(Map.of("initialized", initialized)).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
}
