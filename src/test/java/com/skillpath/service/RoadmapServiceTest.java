package com.skillpath.service;

import com.skillpath.dto.SkillGapAnalysisResultDto;
import com.skillpath.dto.SkillGapItemDto;
import com.skillpath.model.*;
import com.skillpath.repository.RoadmapItemRepository;
import com.skillpath.repository.RoadmapRepository;
import com.skillpath.repository.StudentRepository;
import com.skillpath.util.LearningObjectivesProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoadmapServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapItemRepository roadmapItemRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SkillGapAnalysisService skillGapAnalysisService;

    private LearningObjectivesProvider learningObjectivesProvider;
    private RoadmapService roadmapService;

    @BeforeEach
    void setUp() {
        learningObjectivesProvider = new LearningObjectivesProvider();
        roadmapService = new RoadmapService(
                roadmapRepository,
                roadmapItemRepository,
                studentRepository,
                skillGapAnalysisService,
                learningObjectivesProvider
        );
    }

    @Test
    @DisplayName("Verify roadmap prioritization: 1. Missing (HIGH), 2. Needs Improvement (MEDIUM), 3. Optional (LOW)")
    void testRoadmapPriorityOrdering() {
        Student student = new Student("Alex", "alex@test.com", "Apex", "CSE", 5);
        student.setId(1L);

        CareerRole role = new CareerRole("Full Stack Dev", "Desc");
        role.setId(2L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(roadmapRepository.findByStudentIdAndCareerRoleId(1L, 2L)).thenReturn(Optional.empty());

        // Create gap analysis items
        Skill sMissing = new Skill("Docker", "DevOps", "", true);
        sMissing.setId(10L);

        Skill sNeedsImp = new Skill("Java", "Programming", "", true);
        sNeedsImp.setId(20L);

        Skill sOptional = new Skill("Linux", "OS", "", true);
        sOptional.setId(30L);

        List<SkillGapItemDto> gapItems = List.of(
                new SkillGapItemDto(sNeedsImp, ProficiencyLevel.ADVANCED, ProficiencyLevel.BASIC, false, ""),
                new SkillGapItemDto(sMissing, ProficiencyLevel.INTERMEDIATE, null, false, ""),
                new SkillGapItemDto(sOptional, ProficiencyLevel.BASIC, null, true, "")
        );

        SkillGapAnalysisResultDto analysisResult = new SkillGapAnalysisResultDto(role, gapItems);
        when(skillGapAnalysisService.analyze(1L, 2L)).thenReturn(analysisResult);

        when(roadmapRepository.save(any(Roadmap.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Roadmap generated = roadmapService.generateOrRefreshRoadmap(1L, 2L);

        assertNotNull(generated);
        List<RoadmapItem> items = generated.getItems();
        assertEquals(3, items.size());

        // First item must be the MISSING skill (Priority HIGH)
        assertEquals(RoadmapPriority.HIGH, items.get(0).getPriority());
        assertEquals("Docker", items.get(0).getSkill().getName());
        assertEquals(1, items.get(0).getSuggestedSequence());

        // Second item must be the NEEDS_IMPROVEMENT skill (Priority MEDIUM)
        assertEquals(RoadmapPriority.MEDIUM, items.get(1).getPriority());
        assertEquals("Java", items.get(1).getSkill().getName());
        assertEquals(2, items.get(1).getSuggestedSequence());

        // Third item must be the OPTIONAL skill (Priority LOW)
        assertEquals(RoadmapPriority.LOW, items.get(2).getPriority());
        assertEquals("Linux", items.get(2).getSkill().getName());
        assertEquals(3, items.get(2).getSuggestedSequence());
    }

    @Test
    @DisplayName("Verify updating roadmap item status")
    void testUpdateItemStatus() {
        RoadmapItem item = new RoadmapItem();
        item.setId(55L);
        item.setStatus(RoadmapItemStatus.NOT_STARTED);

        when(roadmapItemRepository.findById(55L)).thenReturn(Optional.of(item));
        when(roadmapItemRepository.save(any(RoadmapItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoadmapItem updated = roadmapService.updateItemStatus(55L, RoadmapItemStatus.IN_PROGRESS);

        assertEquals(RoadmapItemStatus.IN_PROGRESS, updated.getStatus());
        verify(roadmapItemRepository).save(item);
    }
}
