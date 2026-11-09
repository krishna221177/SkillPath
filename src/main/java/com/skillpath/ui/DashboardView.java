package com.skillpath.ui;

import com.skillpath.dto.DashboardSummaryDto;
import com.skillpath.model.CareerRole;
import com.skillpath.model.Roadmap;
import com.skillpath.model.RoadmapItem;
import com.skillpath.model.Student;
import com.skillpath.service.DashboardService;
import com.skillpath.service.RoadmapService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Optional;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard • SkillPath")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;
    private final StudentService studentService;
    private final RoadmapService roadmapService;

    public DashboardView(DashboardService dashboardService,
                         StudentService studentService,
                         RoadmapService roadmapService) {
        this.dashboardService = dashboardService;
        this.studentService = studentService;
        this.roadmapService = roadmapService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "1.75rem 1.5rem");

        refreshDashboard();
    }

    private void refreshDashboard() {
        removeAll();

        Student student = studentService.getOrCreateDefaultStudent();
        DashboardSummaryDto summary = dashboardService.getSummary(student.getId());

        // Header Section
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        VerticalLayout headerText = new VerticalLayout();
        headerText.setPadding(false);
        headerText.setSpacing(false);

        H2 title = new H2("Welcome back, " + student.getName());
        title.getStyle().set("margin", "0 0 0.25rem 0");
        title.getStyle().set("font-size", "1.75rem");

        Span subtitle = new Span(student.getCollegeName() + " • " + student.getDepartment() + " • Semester " + student.getSemester());
        subtitle.getStyle().set("color", "var(--skillpath-text-secondary)");
        subtitle.getStyle().set("font-size", "0.95rem");
        headerText.add(title, subtitle);

        HorizontalLayout quickActions = new HorizontalLayout();
        Button analyzeBtn = new Button("Run Analysis", VaadinIcon.CHART_LINE.create());
        analyzeBtn.addClassName("btn-primary-glass");
        analyzeBtn.addClickListener(e -> UI.getCurrent().navigate(SkillGapView.class));

        Button addSkillBtn = new Button("My Skills", VaadinIcon.PLUS.create());
        addSkillBtn.addClassName("btn-secondary-glass");
        addSkillBtn.addClickListener(e -> UI.getCurrent().navigate(SkillsView.class));

        quickActions.add(addSkillBtn, analyzeBtn);
        header.add(headerText, quickActions);
        add(header);

        // Stats Cards Row
        HorizontalLayout statsRow = new HorizontalLayout();
        statsRow.setWidthFull();
        statsRow.setSpacing(true);

        CareerRole targetRole = summary.getTargetCareer();
        String roleTitle = targetRole != null ? targetRole.getName() : "None Selected";

        statsRow.add(createStatCard("Career Readiness", String.format("%.1f%%", summary.getReadinessPercentage()),
                "Target: " + roleTitle, VaadinIcon.TROPHY, "#55D6E8", summary.getReadinessPercentage()));

        statsRow.add(createStatCard("Total Skills Added", String.valueOf(summary.getTotalSkillsAdded()),
                summary.getSatisfiedSkillsCount() + " requirements satisfied", VaadinIcon.SPECIALIST, "#648CFF", -1));

        statsRow.add(createStatCard("Skill Gaps Identified", String.valueOf(summary.getMissingSkillsCount() + summary.getSkillsNeedingImprovementCount()),
                summary.getMissingSkillsCount() + " missing • " + summary.getSkillsNeedingImprovementCount() + " weak", VaadinIcon.WARNING, "#F6AD55", -1));

        statsRow.add(createStatCard("Roadmap Progress", String.format("%.0f%%", summary.getRoadmapProgressPercentage()),
                summary.getCompletedRoadmapItems() + " of " + summary.getTotalRoadmapItems() + " completed", VaadinIcon.MAP_MARKER, "#9274FF", summary.getRoadmapProgressPercentage()));

        add(statsRow);

        // Main 2-Column Content Layout
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true);

        // Left Panel: Career Gap Summary
        GlassCard gapCard = new GlassCard("Target Career Role: " + roleTitle, "Live gap analysis metrics against predefined industry benchmarks");
        gapCard.getStyle().set("flex", "1");

        VerticalLayout gapCardContent = gapCard.getContentLayout();

        if (targetRole != null) {
            Paragraph roleDesc = new Paragraph(targetRole.getDescription());
            roleDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
            roleDesc.getStyle().set("font-size", "0.9rem");
            roleDesc.getStyle().set("margin-bottom", "1rem");
            gapCardContent.add(roleDesc);

            // Metrics row
            HorizontalLayout metricsLayout = new HorizontalLayout();
            metricsLayout.setWidthFull();
            metricsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
            metricsLayout.getStyle().set("background", "rgba(255, 255, 255, 0.04)");
            metricsLayout.getStyle().set("border-radius", "12px");
            metricsLayout.getStyle().set("padding", "1rem");

            metricsLayout.add(createMetricPill("Satisfied Skills", String.valueOf(summary.getSatisfiedSkillsCount()), "#68D391"));
            metricsLayout.add(createMetricPill("Needs Improvement", String.valueOf(summary.getSkillsNeedingImprovementCount()), "#F6AD55"));
            metricsLayout.add(createMetricPill("Missing Skills", String.valueOf(summary.getMissingSkillsCount()), "#FC8181"));

            gapCardContent.add(metricsLayout);

            ProgressBar readinessBar = new ProgressBar();
            readinessBar.setValue(summary.getReadinessPercentage() / 100.0);
            readinessBar.setWidthFull();

            HorizontalLayout barInfo = new HorizontalLayout();
            barInfo.setWidthFull();
            barInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            Span readinessLabel = new Span("Overall Match Score");
            readinessLabel.getStyle().set("color", "var(--skillpath-text-secondary)");
            Span readinessScore = new Span(String.format("%.2f%%", summary.getReadinessPercentage()));
            readinessScore.getStyle().set("font-weight", "700");
            readinessScore.getStyle().set("color", "#55D6E8");
            barInfo.add(readinessLabel, readinessScore);

            gapCardContent.add(new Div(), barInfo, readinessBar);

            Button viewAnalysisBtn = new Button("Open Detailed Skill-Gap Analysis", VaadinIcon.ARROW_RIGHT.create());
            viewAnalysisBtn.addClassName("btn-primary-glass");
            viewAnalysisBtn.setWidthFull();
            viewAnalysisBtn.addClickListener(e -> UI.getCurrent().navigate(SkillGapView.class));
            gapCardContent.add(viewAnalysisBtn);
        } else {
            Paragraph noRole = new Paragraph("No target career role selected yet. Choose a career role to begin analysis.");
            Button chooseRoleBtn = new Button("Select Career Role", VaadinIcon.BRIEFCASE.create());
            chooseRoleBtn.addClassName("btn-primary-glass");
            chooseRoleBtn.addClickListener(e -> UI.getCurrent().navigate(CareerRolesView.class));
            gapCardContent.add(noRole, chooseRoleBtn);
        }

        // Right Panel: Learning Roadmap & Milestones
        GlassCard roadmapCard = new GlassCard("Active Learning Roadmap", "Immediate milestones generated for your target role");
        roadmapCard.getStyle().set("flex", "1");
        VerticalLayout roadmapCardContent = roadmapCard.getContentLayout();

        Optional<Roadmap> roadmapOpt = targetRole != null
                ? roadmapService.getRoadmapForRole(student.getId(), targetRole.getId())
                : Optional.empty();

        if (roadmapOpt.isPresent() && !roadmapOpt.get().getItems().isEmpty()) {
            List<RoadmapItem> items = roadmapOpt.get().getItems();
            VerticalLayout itemList = new VerticalLayout();
            itemList.setPadding(false);
            itemList.setSpacing(true);
            itemList.setWidthFull();

            int displayCount = Math.min(4, items.size());
            for (int i = 0; i < displayCount; i++) {
                RoadmapItem item = items.get(i);
                itemList.add(createRoadmapMiniItem(item));
            }
            roadmapCardContent.add(itemList);

            HorizontalLayout roadmapActions = new HorizontalLayout();
            roadmapActions.setWidthFull();
            roadmapActions.setSpacing(true);

            Button openRoadmapBtn = new Button("View Full Roadmap", VaadinIcon.MAP_MARKER.create());
            openRoadmapBtn.addClassName("btn-primary-glass");
            openRoadmapBtn.getStyle().set("flex", "1");
            openRoadmapBtn.addClickListener(e -> UI.getCurrent().navigate(RoadmapView.class));

            Button trackProgressBtn = new Button("Track Progress", VaadinIcon.CHECK_SQUARE_O.create());
            trackProgressBtn.addClassName("btn-secondary-glass");
            trackProgressBtn.getStyle().set("flex", "1");
            trackProgressBtn.addClickListener(e -> UI.getCurrent().navigate(ProgressView.class));

            roadmapActions.add(openRoadmapBtn, trackProgressBtn);
            roadmapCardContent.add(roadmapActions);
        } else {
            Paragraph noRoadmap = new Paragraph("No roadmap generated yet for this role.");
            Button genBtn = new Button("Generate Learning Roadmap", VaadinIcon.LIGHTBULB.create());
            genBtn.addClassName("btn-primary-glass");
            genBtn.addClickListener(e -> {
                if (targetRole != null) {
                    roadmapService.generateOrRefreshRoadmap(student.getId(), targetRole.getId());
                    refreshDashboard();
                }
            });
            roadmapCardContent.add(noRoadmap, genBtn);
        }

        mainContent.add(gapCard, roadmapCard);
        add(mainContent);
    }

    private Div createStatCard(String label, String value, String subtext, VaadinIcon iconType, String accentColor, double progressPercent) {
        Div card = new Div();
        card.addClassName("glass-stat-card");
        card.getStyle().set("flex", "1");
        card.getStyle().set("min-width", "220px");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "var(--skillpath-text-secondary)");
        labelSpan.getStyle().set("font-size", "0.85rem");
        labelSpan.getStyle().set("font-weight", "500");

        Icon icon = iconType.create();
        icon.setSize("20px");
        icon.setColor(accentColor);

        top.add(labelSpan, icon);

        H2 valHeading = new H2(value);
        valHeading.getStyle().set("font-size", "1.9rem");
        valHeading.getStyle().set("font-weight", "800");
        valHeading.getStyle().set("margin", "0.4rem 0 0.2rem 0");
        valHeading.getStyle().set("color", "var(--skillpath-text-primary)");

        Span sub = new Span(subtext);
        sub.getStyle().set("color", "var(--skillpath-text-muted)");
        sub.getStyle().set("font-size", "0.78rem");

        card.add(top, valHeading, sub);

        if (progressPercent >= 0) {
            ProgressBar bar = new ProgressBar();
            bar.setValue(progressPercent / 100.0);
            bar.getStyle().set("margin-top", "0.6rem");
            bar.getStyle().set("height", "6px");
            card.add(bar);
        }

        return card;
    }

    private VerticalLayout createMetricPill(String title, String count, String color) {
        VerticalLayout pill = new VerticalLayout();
        pill.setPadding(false);
        pill.setSpacing(false);
        pill.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 num = new H3(count);
        num.getStyle().set("margin", "0");
        num.getStyle().set("color", color);
        num.getStyle().set("font-weight", "800");

        Span name = new Span(title);
        name.getStyle().set("color", "var(--skillpath-text-muted)");
        name.getStyle().set("font-size", "0.78rem");

        pill.add(num, name);
        return pill;
    }

    private Div createRoadmapMiniItem(RoadmapItem item) {
        Div row = new Div();
        row.getStyle().set("background", "rgba(255, 255, 255, 0.03)");
        row.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.08)");
        row.getStyle().set("border-radius", "10px");
        row.getStyle().set("padding", "0.75rem 1rem");
        row.getStyle().set("display", "flex");
        row.getStyle().set("align-items", "center");
        row.getStyle().set("justify-content", "space-between");

        HorizontalLayout left = new HorizontalLayout();
        left.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        Span seq = new Span("#" + item.getSuggestedSequence());
        seq.getStyle().set("color", "var(--skillpath-accent-cyan)");
        seq.getStyle().set("font-weight", "700");
        seq.getStyle().set("font-size", "0.85rem");

        VerticalLayout itemDetails = new VerticalLayout();
        itemDetails.setPadding(false);
        itemDetails.setSpacing(false);

        Span skillName = new Span(item.getSkill().getName());
        skillName.getStyle().set("font-weight", "600");
        skillName.getStyle().set("color", "var(--skillpath-text-primary)");

        Span target = new Span("Target: " + item.getTargetProficiency().getLabel());
        target.getStyle().set("font-size", "0.78rem");
        target.getStyle().set("color", "var(--skillpath-text-muted)");

        itemDetails.add(skillName, target);
        left.add(seq, itemDetails);

        Span statusBadge = new Span(item.getStatus().getLabel());
        statusBadge.getStyle().set("font-size", "0.75rem");
        statusBadge.getStyle().set("padding", "0.2rem 0.55rem");
        statusBadge.getStyle().set("border-radius", "6px");
        statusBadge.getStyle().set("font-weight", "600");

        switch (item.getStatus()) {
            case COMPLETED:
                statusBadge.getStyle().set("background", "rgba(104, 211, 145, 0.2)");
                statusBadge.getStyle().set("color", "#68D391");
                break;
            case IN_PROGRESS:
                statusBadge.getStyle().set("background", "rgba(100, 140, 255, 0.2)");
                statusBadge.getStyle().set("color", "#648CFF");
                break;
            default:
                statusBadge.getStyle().set("background", "rgba(255, 255, 255, 0.08)");
                statusBadge.getStyle().set("color", "var(--skillpath-text-secondary)");
                break;
        }

        row.add(left, statusBadge);
        return row;
    }
}
