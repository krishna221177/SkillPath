package com.skillpath.service;

import com.skillpath.dto.SkillGapAnalysisResultDto;
import com.skillpath.dto.SkillGapItemDto;
import com.skillpath.exception.SkillPathException;
import com.skillpath.model.*;
import com.skillpath.repository.RoadmapItemRepository;
import com.skillpath.repository.RoadmapRepository;
import com.skillpath.repository.StudentRepository;
import com.skillpath.util.LearningObjectivesProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapItemRepository roadmapItemRepository;
    private final StudentRepository studentRepository;
    private final SkillGapAnalysisService skillGapAnalysisService;
    private final LearningObjectivesProvider learningObjectivesProvider;

    public RoadmapService(RoadmapRepository roadmapRepository,
                          RoadmapItemRepository roadmapItemRepository,
                          StudentRepository studentRepository,
                          SkillGapAnalysisService skillGapAnalysisService,
                          LearningObjectivesProvider learningObjectivesProvider) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapItemRepository = roadmapItemRepository;
        this.studentRepository = studentRepository;
        this.skillGapAnalysisService = skillGapAnalysisService;
        this.learningObjectivesProvider = learningObjectivesProvider;
    }

    public Roadmap generateOrRefreshRoadmap(Long studentId, Long careerRoleId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new SkillPathException("Student not found with ID: " + studentId));

        SkillGapAnalysisResultDto analysis = skillGapAnalysisService.analyze(studentId, careerRoleId);
        CareerRole careerRole = analysis.getCareerRole();
        if (careerRole == null) {
            throw new SkillPathException("Career role could not be analyzed.");
        }

        // Find existing roadmap if present to preserve user progress
        Optional<Roadmap> existingOpt = roadmapRepository.findByStudentIdAndCareerRoleId(studentId, careerRoleId);
        Map<Long, RoadmapItemStatus> preservedStatuses = new HashMap<>();

        Roadmap roadmap;
        if (existingOpt.isPresent()) {
            roadmap = existingOpt.get();
            for (RoadmapItem item : roadmap.getItems()) {
                if (item.getSkill() != null && item.getSkill().getId() != null) {
                    preservedStatuses.put(item.getSkill().getId(), item.getStatus());
                }
            }
            roadmap.getItems().clear();
            roadmap.setGeneratedAt(LocalDateTime.now());
        } else {
            roadmap = new Roadmap(student, careerRole);
        }

        // Partition items by priority order:
        // 1. Missing mandatory skills (HIGH)
        // 2. Needs Improvement mandatory skills (MEDIUM)
        // 3. Optional skills (LOW)
        // 4. Satisfied mandatory skills (COMPLETED / achieved)
        List<SkillGapItemDto> missingMandatory = new ArrayList<>();
        List<SkillGapItemDto> needsImprovementMandatory = new ArrayList<>();
        List<SkillGapItemDto> optionalItems = new ArrayList<>();
        List<SkillGapItemDto> satisfiedMandatory = new ArrayList<>();

        for (SkillGapItemDto item : analysis.getItems()) {
            if (item.isOptional()) {
                optionalItems.add(item);
            } else if (item.getStatus() == SkillGapStatus.MISSING) {
                missingMandatory.add(item);
            } else if (item.getStatus() == SkillGapStatus.NEEDS_IMPROVEMENT) {
                needsImprovementMandatory.add(item);
            } else if (item.getStatus() == SkillGapStatus.SATISFIED) {
                satisfiedMandatory.add(item);
            }
        }

        int sequence = 1;

        // 1. Missing Mandatory Skills -> Priority HIGH
        for (SkillGapItemDto item : missingMandatory) {
            RoadmapItemStatus status = preservedStatuses.getOrDefault(item.getSkill().getId(), RoadmapItemStatus.NOT_STARTED);
            String objective = learningObjectivesProvider.getObjective(item.getSkillName(), item.getRequiredProficiency());
            RoadmapItem rItem = new RoadmapItem(roadmap, item.getSkill(), item.getCurrentProficiency(),
                    item.getRequiredProficiency(), RoadmapPriority.HIGH, objective, sequence++, status);
            roadmap.addItem(rItem);
        }

        // 2. Needs Improvement Mandatory Skills -> Priority MEDIUM
        for (SkillGapItemDto item : needsImprovementMandatory) {
            RoadmapItemStatus status = preservedStatuses.getOrDefault(item.getSkill().getId(), RoadmapItemStatus.IN_PROGRESS);
            String objective = learningObjectivesProvider.getObjective(item.getSkillName(), item.getRequiredProficiency());
            RoadmapItem rItem = new RoadmapItem(roadmap, item.getSkill(), item.getCurrentProficiency(),
                    item.getRequiredProficiency(), RoadmapPriority.MEDIUM, objective, sequence++, status);
            roadmap.addItem(rItem);
        }

        // 3. Optional Skills -> Priority LOW
        for (SkillGapItemDto item : optionalItems) {
            RoadmapItemStatus defaultStatus = (item.getStatus() == SkillGapStatus.SATISFIED)
                    ? RoadmapItemStatus.COMPLETED
                    : RoadmapItemStatus.NOT_STARTED;
            RoadmapItemStatus status = preservedStatuses.getOrDefault(item.getSkill().getId(), defaultStatus);
            String objective = learningObjectivesProvider.getObjective(item.getSkillName(), item.getRequiredProficiency());
            RoadmapItem rItem = new RoadmapItem(roadmap, item.getSkill(), item.getCurrentProficiency(),
                    item.getRequiredProficiency(), RoadmapPriority.LOW, objective, sequence++, status);
            roadmap.addItem(rItem);
        }

        // 4. Satisfied Mandatory Skills -> Marked COMPLETED
        for (SkillGapItemDto item : satisfiedMandatory) {
            String objective = learningObjectivesProvider.getObjective(item.getSkillName(), item.getRequiredProficiency());
            RoadmapItem rItem = new RoadmapItem(roadmap, item.getSkill(), item.getCurrentProficiency(),
                    item.getRequiredProficiency(), RoadmapPriority.LOW, objective, sequence++, RoadmapItemStatus.COMPLETED);
            roadmap.addItem(rItem);
        }

        return roadmapRepository.save(roadmap);
    }

    @Transactional(readOnly = true)
    public Optional<Roadmap> getLatestRoadmap(Long studentId) {
        return roadmapRepository.findTopByStudentIdOrderByGeneratedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public Optional<Roadmap> getRoadmapForRole(Long studentId, Long careerRoleId) {
        return roadmapRepository.findByStudentIdAndCareerRoleId(studentId, careerRoleId);
    }

    public RoadmapItem updateItemStatus(Long itemId, RoadmapItemStatus newStatus) {
        RoadmapItem item = roadmapItemRepository.findById(itemId)
                .orElseThrow(() -> new SkillPathException("Roadmap item not found with ID: " + itemId));
        item.setStatus(newStatus);
        return roadmapItemRepository.save(item);
    }
}
