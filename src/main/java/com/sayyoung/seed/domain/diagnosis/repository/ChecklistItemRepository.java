package com.sayyoung.seed.domain.diagnosis.repository;

import com.sayyoung.seed.domain.diagnosis.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 체크리스트 항목 데이터 접근을 담당합니다.
 */
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

}
