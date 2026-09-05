package com.sayyoung.seed.domain.policy.entity;

import com.sayyoung.seed.domain.policy.entity.id.PolicyJobId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "policy_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicyJob {

    @EmbeddedId
    private PolicyJobId id;

    @MapsId("policyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
}
