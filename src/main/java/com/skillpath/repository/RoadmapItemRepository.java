package com.skillpath.repository;

import com.skillpath.model.RoadmapItem;
import com.skillpath.model.RoadmapItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapItemRepository extends JpaRepository<RoadmapItem, Long> {
    List<RoadmapItem> findByRoadmapIdOrderBySuggestedSequenceAsc(Long roadmapId);
    long countByRoadmapIdAndStatus(Long roadmapId, RoadmapItemStatus status);
}
