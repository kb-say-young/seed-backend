package com.sayyoung.seed.domain.policy.entity;

import com.sayyoung.seed.domain.policy.entity.id.PolicySchoolId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "policy_schools")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicySchool {

    @EmbeddedId
    private PolicySchoolId id;

    @MapsId("policyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
}