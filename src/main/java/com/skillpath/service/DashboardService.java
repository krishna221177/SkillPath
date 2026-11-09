package com.skillpath.service;

import com.skillpath.dto.DashboardSummaryDto;
import com.skillpath.dto.SkillGapAnalysisResultDto;
import com.skillpath.model.*;
import com.skillpath.repository.RoadmapItemRepository;
import com.skillpath.repository.RoadmapRepository;
import com.skillpath.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentService studentService;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillGapAnalysisService skillGapAnalysisService;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapItemRepository roadmapItemRepository;

    public DashboardService(StudentService studentService,
                            StudentSkillRepository studentSkillRepository,
                            SkillGapAnalysisService skillGapAnalysisService,
                            RoadmapRepository roadmapRepository,
                            RoadmapItemRepository roadmapItemRepository) {
        this.studentService = studentService;
        this.studentSkillRepository = studentSkillRepository;
        this.skillGapAnalysisService = skillGapAnalysisService;
        this.roadmapRepository = roadmapRepository;
        this.roadmapItemRepository = roadmapItemRepository;
    }

    public DashboardSummaryDto getSummary(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setStudent(student);

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentId(studentId);
        summary.setTotalSkillsAdded(studentSkills.size());

        CareerRole targetRole = student.getTargetCareer();
        summary.setTargetCareer(targetRole);

        if (targetRole != null) {
            SkillGapAnalysisResultDto gapAnalysis = skillGapAnalysisService.analyze(student, targetRole);
            summary.setReadinessPercentage(gapAnalysis.getReadinessPercentage());
            summary.setMissingSkillsCount(gapAnalysis.getMissingCount());
            summary.setSkillsNeedingImprovementCount(gapAnalysis.getNeedsImprovementCount());
            summary.setSatisfiedSkillsCount(gapAnalysis.getSatisfiedCount());

            // Check roadmap progress
            Optional<Roadmap> roadmapOpt = roadmapRepository.findByStudentIdAndCareerRoleId(studentId, targetRole.getId());
            if (roadmapOpt.isPresent()) {
                Roadmap roadmap = roadmapOpt.get();
                long total = roadmap.getItems().size();
                long completed = roadmapItemRepository.countByRoadmapIdAndStatus(roadmap.getId(), RoadmapItemStatus.COMPLETED);
                long inProgress = roadmapItemRepository.countByRoadmapIdAndStatus(roadmap.getId(), RoadmapItemStatus.IN_PROGRESS);

                summary.setTotalRoadmapItems(total);
                summary.setCompletedRoadmapItems(completed);
                summary.setInProgressRoadmapItems(inProgress);

                if (total > 0) {
                    double progress = ((double) completed / total) * 100.0;
                    summary.setRoadmapProgressPercentage(Math.round(progress * 100.0) / 100.0);
                }
            }
        }

        return summary;
    }
}
