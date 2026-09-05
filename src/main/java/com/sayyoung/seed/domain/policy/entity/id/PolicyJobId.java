package com.sayyoung.seed.domain.policy.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class PolicyJobId implements Serializable {
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "job_code")
    private String jobCode;
}
