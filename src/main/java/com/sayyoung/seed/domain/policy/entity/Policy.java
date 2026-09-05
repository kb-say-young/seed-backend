package com.sayyoung.seed.domain.policy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_no", nullable = false, unique = true, length = 20)
    private String policyNo;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "description")
    private String description;

    @Column(name = "support_content")
    private String supportContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "age_limit", nullable = false)
    private boolean ageLimit;

    @Column(name = "income_code")
    private String incomeCode;

    @Column(name = "income_min")
    private Long incomeMin;

    @Column(name = "income_max")
    private Long incomeMax;

    @Column(name = "income_etc")
    private String incomeEtc;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "restriction")
    private String restriction;

    @Column(name = "apply_start_date")
    private LocalDate applyStartDate;

    @Column(name = "apply_end_date")
    private LocalDate applyEndDate;

    @Column(name = "independent_youth", nullable = false)
    private boolean independentYouth;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
