package com.agilemorph.resource;

import com.agilemorph.dto.ProviderDto;
import com.agilemorph.dto.RuleEvaluationRequest;
import com.agilemorph.dto.RuleEvaluationResponse;
import com.agilemorph.model.Provider;
import com.agilemorph.service.ProviderService;
import com.agilemorph.service.RuleEngineService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Path("/api/providers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Provider Management", description = "Operations for managing healthcare providers")
public class ProviderResource {
    
    @Inject
    ProviderService providerService;
    
    @Inject
    RuleEngineService ruleEngineService;
    
    @POST
    @Operation(summary = "Create a new provider", description = "Creates a new healthcare provider record")
    @Transactional
    public Response createProvider(@Valid ProviderDto providerDto) {
        try {
            // Normalize provider data
            ProviderDto normalizedProvider = providerService.normalizeProvider(providerDto);
            
            // Check for potential duplicates
            List<ProviderDto> duplicates = providerService.findPotentialDuplicates(normalizedProvider);
            if (!duplicates.isEmpty()) {
                return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("message", "Potential duplicate providers found", "duplicates", duplicates))
                    .build();
            }
            
            ProviderDto createdProvider = providerService.createProvider(normalizedProvider);
            return Response.status(Response.Status.CREATED).entity(createdProvider).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/bulk")
    @Operation(summary = "Create multiple providers", description = "Creates multiple healthcare provider records in batch")
    @Transactional
    public Response createProvidersBulk(@Valid List<ProviderDto> providerDtos) {
        try {
            List<ProviderDto> normalizedProviders = providerDtos.stream()
                .map(providerService::normalizeProvider)
                .collect(java.util.stream.Collectors.toList());
            
            List<ProviderDto> createdProviders = providerService.createProvidersBulk(normalizedProviders);
            return Response.status(Response.Status.CREATED).entity(createdProviders).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all providers", description = "Retrieves all healthcare providers")
    public Response getAllProviders() {
        try {
            List<ProviderDto> providers = providerService.getAllProviders();
            return Response.ok(providers).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get provider by ID", description = "Retrieves a specific healthcare provider by ID")
    public Response getProvider(@PathParam("id") Long id) {
        try {
            ProviderDto provider = providerService.getProvider(id);
            return Response.ok(provider).build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/npi/{npi}")
    @Operation(summary = "Get provider by NPI", description = "Retrieves a specific healthcare provider by NPI")
    public Response getProviderByNpi(@PathParam("npi") String npi) {
        try {
            ProviderDto provider = providerService.getProviderByNpi(npi);
            return Response.ok(provider).build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Get providers by verification status", description = "Retrieves providers filtered by verification status")
    public Response getProvidersByStatus(@PathParam("status") Provider.VerificationStatus status) {
        try {
            List<ProviderDto> providers = providerService.getProvidersByStatus(status);
            return Response.ok(providers).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update provider", description = "Updates an existing healthcare provider")
    @Transactional
    public Response updateProvider(@PathParam("id") Long id, @Valid ProviderDto providerDto) {
        try {
            ProviderDto updatedProvider = providerService.updateProvider(id, providerDto);
            return Response.ok(updatedProvider).build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete provider", description = "Deletes a healthcare provider")
    @Transactional
    public Response deleteProvider(@PathParam("id") Long id) {
        try {
            providerService.deleteProvider(id);
            return Response.noContent().build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/{id}/normalize")
    @Operation(summary = "Normalize provider data", description = "Normalizes provider data for consistency")
    public Response normalizeProvider(@PathParam("id") Long id) {
        try {
            ProviderDto provider = providerService.getProvider(id);
            ProviderDto normalizedProvider = providerService.normalizeProvider(provider);
            ProviderDto updatedProvider = providerService.updateProvider(id, normalizedProvider);
            return Response.ok(updatedProvider).build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/{id}/duplicates")
    @Operation(summary = "Find potential duplicates", description = "Finds potential duplicate providers")
    public Response findPotentialDuplicates(@PathParam("id") Long id) {
        try {
            ProviderDto provider = providerService.getProvider(id);
            List<ProviderDto> duplicates = providerService.findPotentialDuplicates(provider);
            return Response.ok(Map.of("duplicates", duplicates, "count", duplicates.size())).build();
            
        } catch (jakarta.ws.rs.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
}
