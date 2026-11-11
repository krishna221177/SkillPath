package com.skillpath.ui;

import com.skillpath.dto.DashboardSummaryDto;
import com.skillpath.model.CareerRole;
import com.skillpath.model.Student;
import com.skillpath.service.DashboardService;
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

@Route(value = "", layout = MainLayout.class)
@PageTitle("SkillPath • Student Career Roadmap System")
public class HomeView extends VerticalLayout {

    public HomeView(StudentService studentService, DashboardService dashboardService) {
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "1.75rem 1.5rem");

        Student student = studentService.getOrCreateDefaultStudent();
        DashboardSummaryDto summary = dashboardService.getSummary(student.getId());

        // Hero Banner
        Div heroPanel = new Div();
        heroPanel.addClassName("glass-panel");
        heroPanel.getStyle().set("padding", "2rem 2.25rem 1.75rem 2.25rem");

        Span heroTag = new Span("ACADEMIC TO INDUSTRY BRIDGING SYSTEM");
        heroTag.getStyle().set("color", "var(--skillpath-accent-cyan)");
        heroTag.getStyle().set("font-size", "0.75rem");
        heroTag.getStyle().set("font-weight", "700");
        heroTag.getStyle().set("letter-spacing", "0.1em");

        H1 heroTitle = new H1("SkillPath: Technical Skill-Gap & Career Roadmap");
        heroTitle.getStyle().set("font-size", "1.9rem");
        heroTitle.getStyle().set("font-weight", "800");
        heroTitle.getStyle().set("line-height", "1.25");
        heroTitle.getStyle().set("margin", "0.4rem 0 0.6rem 0");
        heroTitle.getStyle().set("color", "#FFFFFF");

        Paragraph heroDesc = new Paragraph(
                "Evaluate your technical proficiencies against industry career benchmarks. " +
                "SkillPath identifies your weak and missing skills, calculates your deterministic readiness score, " +
                "and generates a structured, step-by-step learning roadmap."
        );
        heroDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
        heroDesc.getStyle().set("font-size", "0.95rem");
        heroDesc.getStyle().set("max-width", "750px");
        heroDesc.getStyle().set("line-height", "1.5");
        heroDesc.getStyle().set("margin", "0 0 1.25rem 0");

        HorizontalLayout actionRow = new HorizontalLayout();
        actionRow.setSpacing(true);

        Button gapBtn = new Button("Run Skill-Gap Analysis", VaadinIcon.CHART_LINE.create());
        gapBtn.addClassName("btn-primary-glass");
        gapBtn.addClickListener(e -> UI.getCurrent().navigate(SkillGapView.class));

        Button roadmapBtn = new Button("View Learning Roadmap", VaadinIcon.MAP_MARKER.create());
        roadmapBtn.addClassName("btn-secondary-glass");
        roadmapBtn.addClickListener(e -> UI.getCurrent().navigate(RoadmapView.class));

        Button skillsBtn = new Button("Manage Skills Portfolio", VaadinIcon.SPECIALIST.create());
        skillsBtn.addClassName("btn-secondary-glass");
        skillsBtn.addClickListener(e -> UI.getCurrent().navigate(SkillsView.class));

        Button rolesBtn = new Button("Explore Career Roles", VaadinIcon.BRIEFCASE.create());
        rolesBtn.addClassName("btn-secondary-glass");
        rolesBtn.addClickListener(e -> UI.getCurrent().navigate(CareerRolesView.class));

        actionRow.add(gapBtn, roadmapBtn, skillsBtn, rolesBtn);
        heroPanel.add(heroTag, heroTitle, heroDesc, actionRow);
        add(heroPanel);

        // Live Student Career Snapshot Card
        CareerRole targetRole = summary.getTargetCareer();
        String roleName = targetRole != null ? targetRole.getName() : "None Selected";

        GlassCard snapshotCard = new GlassCard("Active Student Profile & Career Snapshot",
                student.getName() + " • " + student.getCollegeName() + " • " + student.getDepartment() + " (Semester " + student.getSemester() + ")");
        snapshotCard.setWidthFull();

        VerticalLayout snapshotContent = snapshotCard.getContentLayout();

        // Target Career Header inside Card
        HorizontalLayout targetRoleRow = new HorizontalLayout();
        targetRoleRow.setWidthFull();
        targetRoleRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        targetRoleRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout roleInfo = new HorizontalLayout();
        roleInfo.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        roleInfo.setSpacing(true);

        Span targetLabel = new Span("Target Career Role:");
        targetLabel.getStyle().set("color", "var(--skillpath-text-secondary)");
        targetLabel.getStyle().set("font-size", "0.9rem");

        Span targetBadge = new Span(roleName);
        targetBadge.getStyle().set("background", "rgba(100, 140, 255, 0.2)");
        targetBadge.getStyle().set("color", "var(--skillpath-accent-blue)");
        targetBadge.getStyle().set("border", "1px solid rgba(100, 140, 255, 0.35)");
        targetBadge.getStyle().set("padding", "0.25rem 0.65rem");
        targetBadge.getStyle().set("border-radius", "8px");
        targetBadge.getStyle().set("font-weight", "700");
        targetBadge.getStyle().set("font-size", "0.85rem");

        roleInfo.add(targetLabel, targetBadge);

        Button changeRoleBtn = new Button("Change Career Goal", VaadinIcon.EXCHANGE.create());
        changeRoleBtn.addClassName("btn-secondary-glass");
        changeRoleBtn.getStyle().set("font-size", "0.8rem");
        changeRoleBtn.addClickListener(e -> UI.getCurrent().navigate(CareerRolesView.class));

        targetRoleRow.add(roleInfo, changeRoleBtn);
        snapshotContent.add(targetRoleRow);

        // 4 KPI Summary Cards
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        statsLayout.getStyle().set("margin", "0.5rem 0");

        statsLayout.add(createMiniStat("Career Readiness", String.format("%.1f%%", summary.getReadinessPercentage()),
                summary.getSatisfiedSkillsCount() + " of " + (summary.getSatisfiedSkillsCount() + summary.getMissingSkillsCount() + summary.getSkillsNeedingImprovementCount()) + " requirements met",
                VaadinIcon.TROPHY, "#55D6E8"));

        statsLayout.add(createMiniStat("Skills in Portfolio", String.valueOf(summary.getTotalSkillsAdded()),
                "Recorded with proficiency ratings", VaadinIcon.SPECIALIST, "#648CFF"));

        statsLayout.add(createMiniStat("Skill Gaps to Address", String.valueOf(summary.getMissingSkillsCount() + summary.getSkillsNeedingImprovementCount()),
                summary.getMissingSkillsCount() + " missing • " + summary.getSkillsNeedingImprovementCount() + " needing improvement",
                VaadinIcon.WARNING, "#F6AD55"));

        statsLayout.add(createMiniStat("Roadmap Progress", String.format("%.0f%%", summary.getRoadmapProgressPercentage()),
                summary.getCompletedRoadmapItems() + " of " + summary.getTotalRoadmapItems() + " milestones finished",
                VaadinIcon.CHECK_SQUARE_O, "#68D391"));

        snapshotContent.add(statsLayout);

        // Progress Bar
        ProgressBar readinessBar = new ProgressBar();
        readinessBar.setValue(summary.getReadinessPercentage() / 100.0);
        readinessBar.setWidthFull();
        readinessBar.getStyle().set("height", "8px");
        snapshotContent.add(readinessBar);

        add(snapshotCard);

        // 4 Core Workflow Pillars
        H2 pillarsTitle = new H2("SkillPath Core Workflow");
        pillarsTitle.getStyle().set("font-size", "1.25rem");
        pillarsTitle.getStyle().set("margin", "1rem 0 0.5rem 0");

        HorizontalLayout pillarsLayout = new HorizontalLayout();
        pillarsLayout.setWidthFull();
        pillarsLayout.setSpacing(true);

        pillarsLayout.add(createWorkflowCard("1. Skill Portfolio", "Record and update your technical skills with 1–5 proficiency rankings (Beginner to Expert).", VaadinIcon.SPECIALIST, SkillsView.class));
        pillarsLayout.add(createWorkflowCard("2. Target Career", "Choose from 6 predefined industry roles with weighted mandatory & optional requirements.", VaadinIcon.BRIEFCASE, CareerRolesView.class));
        pillarsLayout.add(createWorkflowCard("3. Gap & Readiness", "Deterministic mathematical readiness scoring, pinpointing missing competencies.", VaadinIcon.CHART_LINE, SkillGapView.class));
        pillarsLayout.add(createWorkflowCard("4. Learning Roadmap", "Sequential learning milestones with practical technical objectives and progress tracking.", VaadinIcon.MAP_MARKER, RoadmapView.class));

        add(pillarsTitle, pillarsLayout);
    }

    private Div createMiniStat(String label, String value, String subtext, VaadinIcon iconType, String accentColor) {
        Div card = new Div();
        card.getStyle().set("background", "rgba(255, 255, 255, 0.04)");
        card.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.08)");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("padding", "0.85rem 1rem");
        card.getStyle().set("flex", "1");
        card.getStyle().set("min-width", "200px");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "var(--skillpath-text-secondary)");
        labelSpan.getStyle().set("font-size", "0.78rem");
        labelSpan.getStyle().set("font-weight", "600");

        Icon icon = iconType.create();
        icon.setSize("16px");
        icon.setColor(accentColor);
        top.add(labelSpan, icon);

        H3 valHeading = new H3(value);
        valHeading.getStyle().set("font-size", "1.5rem");
        valHeading.getStyle().set("font-weight", "800");
        valHeading.getStyle().set("margin", "0.3rem 0 0.15rem 0");
        valHeading.getStyle().set("color", accentColor);

        Span sub = new Span(subtext);
        sub.getStyle().set("color", "var(--skillpath-text-muted)");
        sub.getStyle().set("font-size", "0.75rem");

        card.add(top, valHeading, sub);
        return card;
    }

    private Div createWorkflowCard(String title, String desc, VaadinIcon iconType, Class<? extends com.vaadin.flow.component.Component> targetView) {
        Div card = new Div();
        card.addClassName("glass-card");
        card.getStyle().set("flex", "1");
        card.getStyle().set("padding", "1.25rem 1.15rem");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flex-direction", "column");
        card.getStyle().set("min-height", "180px");

        Icon icon = iconType.create();
        icon.setSize("20px");
        icon.setColor("#648CFF");
        icon.getStyle().set("margin-bottom", "0.5rem");

        H3 heading = new H3(title);
        heading.getStyle().set("font-size", "0.95rem");
        heading.getStyle().set("margin", "0 0 0.35rem 0");

        Paragraph text = new Paragraph(desc);
        text.getStyle().set("color", "var(--skillpath-text-secondary)");
        text.getStyle().set("font-size", "0.82rem");
        text.getStyle().set("line-height", "1.45");
        text.getStyle().set("margin", "0 0 0.85rem 0");
        text.getStyle().set("flex-grow", "1");

        Button action = new Button("Open", VaadinIcon.ARROW_RIGHT.create());
        action.addClassName("btn-secondary-glass");
        action.getStyle().set("align-self", "flex-start");
        action.getStyle().set("font-size", "0.8rem");
        action.addClickListener(e -> UI.getCurrent().navigate(targetView));

        card.add(icon, heading, text, action);
        return card;
    }
}


