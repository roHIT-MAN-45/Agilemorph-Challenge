package com.agilemorph.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rule_evaluations")
public class RuleEvaluation extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_evaluations_seq")
    @SequenceGenerator(name = "rule_evaluations_seq", sequenceName = "rule_evaluations_seq", allocationSize = 1)
    public Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    public Provider provider;
    
    @NotBlank
    @Column(name = "rule_name", nullable = false)
    public String ruleName;
    
    @Column(name = "triggered", nullable = false)
    public Boolean triggered = false;
    
    @Column(name = "severity")
    public String severity;
    
    @Column(name = "message", columnDefinition = "TEXT")
    public String message;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata;
    
    @NotNull
    @Column(name = "evaluated_at", nullable = false)
    public LocalDateTime evaluatedAt = LocalDateTime.now();
    
    @ElementCollection
    @CollectionTable(name = "rule_evaluation_facts", joinColumns = @JoinColumn(name = "rule_evaluation_id"))
    @Column(name = "fact")
    public List<String> facts;
    
    public RuleEvaluation() {
        this.evaluatedAt = LocalDateTime.now();
    }
}
