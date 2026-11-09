package com.skillpath.ui;

import com.skillpath.model.Student;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("SkillPath • Student Career Roadmap System")
public class HomeView extends VerticalLayout {

    public HomeView(StudentService studentService) {
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        Student student = studentService.getOrCreateDefaultStudent();

        // Hero Banner Glass Panel
        Div heroPanel = new Div();
        heroPanel.addClassName("glass-panel");
        heroPanel.getStyle().set("padding", "3rem 2.5rem");
        heroPanel.getStyle().set("position", "relative");
        heroPanel.getStyle().set("overflow", "hidden");

        Span heroTag = new Span("ACADEMIC TO INDUSTRY BRIDGING PLATFORM");
        heroTag.getStyle().set("color", "var(--skillpath-accent-cyan)");
        heroTag.getStyle().set("font-size", "0.85rem");
        heroTag.getStyle().set("font-weight", "700");
        heroTag.getStyle().set("letter-spacing", "0.08em");

        H1 heroTitle = new H1("Close Your Technical Skill Gaps & Navigate Your Career Roadmap");
        heroTitle.getStyle().set("font-size", "2.4rem");
        heroTitle.getStyle().set("font-weight", "800");
        heroTitle.getStyle().set("line-height", "1.2");
        heroTitle.getStyle().set("margin", "0.75rem 0");
        heroTitle.getStyle().set("color", "#FFFFFF");

        Paragraph heroDesc = new Paragraph(
                "SkillPath empowers college students to evaluate their technical proficiencies against " +
                "industry-standard career requirements. Identify missing competencies, pinpoint areas needing improvement, " +
                "calculate your deterministic readiness score, and follow a structured step-by-step roadmap to achieve your target role."
        );
        heroDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
        heroDesc.getStyle().set("font-size", "1.1rem");
        heroDesc.getStyle().set("max-width", "800px");
        heroDesc.getStyle().set("line-height", "1.6");
        heroDesc.getStyle().set("margin-bottom", "1.75rem");

        // Action Buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button startBtn = new Button("Launch Dashboard", VaadinIcon.DASHBOARD.create());
        startBtn.addClassName("btn-primary-glass");
        startBtn.addClickListener(e -> UI.getCurrent().navigate(DashboardView.class));

        Button gapBtn = new Button("Run Skill-Gap Analysis", VaadinIcon.CHART_LINE.create());
        gapBtn.addClassName("btn-secondary-glass");
        gapBtn.addClickListener(e -> UI.getCurrent().navigate(SkillGapView.class));

        Button rolesBtn = new Button("Explore Career Roles", VaadinIcon.BRIEFCASE.create());
        rolesBtn.addClassName("btn-secondary-glass");
        rolesBtn.addClickListener(e -> UI.getCurrent().navigate(CareerRolesView.class));

        actions.add(startBtn, gapBtn, rolesBtn);
        heroPanel.add(heroTag, heroTitle, heroDesc, actions);

        // Current Student Quick Status Card
        GlassCard currentStatus = new GlassCard("Active Student Profile", "Currently operating profile stored in persistent H2 database");
        HorizontalLayout statusLayout = new HorizontalLayout();
        statusLayout.setWidthFull();
        statusLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        statusLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        VerticalLayout studentDetails = new VerticalLayout();
        studentDetails.setPadding(false);
        studentDetails.setSpacing(false);

        H3 studentName = new H3(student.getName());
        studentName.getStyle().set("margin", "0");
        Span college = new Span(student.getCollegeName() + " • " + student.getDepartment() + " (Sem " + student.getSemester() + ")");
        college.getStyle().set("color", "var(--skillpath-text-muted)");
        studentDetails.add(studentName, college);

        HorizontalLayout roleBox = new HorizontalLayout();
        roleBox.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        Span roleLabel = new Span("Target Career: ");
        roleLabel.getStyle().set("color", "var(--skillpath-text-secondary)");
        Span roleName = new Span(student.getTargetCareer() != null ? student.getTargetCareer().getName() : "None Selected");
        roleName.getStyle().set("font-weight", "700");
        roleName.getStyle().set("color", "var(--skillpath-accent-cyan)");
        roleBox.add(roleLabel, roleName);

        Button editProfileBtn = new Button("Edit Profile", VaadinIcon.EDIT.create());
        editProfileBtn.addClassName("btn-secondary-glass");
        editProfileBtn.addClickListener(e -> UI.getCurrent().navigate(ProfileView.class));

        statusLayout.add(studentDetails, roleBox, editProfileBtn);
        currentStatus.addContent(statusLayout);

        // Core Workflow Cards (4 Pillars)
        H2 pillarsTitle = new H2("Core Application Workflow");
        pillarsTitle.getStyle().set("margin-top", "1.5rem");
        pillarsTitle.getStyle().set("font-size", "1.5rem");

        HorizontalLayout pillarsLayout = new HorizontalLayout();
        pillarsLayout.setWidthFull();
        pillarsLayout.setSpacing(true);

        pillarsLayout.add(createWorkflowCard("1. Skill Portfolio", "Record and manage your technical skills with accurate 1-5 proficiency rankings (Beginner to Expert).", VaadinIcon.SPECIALIST, SkillsView.class));
        pillarsLayout.add(createWorkflowCard("2. Target Career", "Choose from 6 predefined industry roles with weighted skill requirements in H2.", VaadinIcon.BRIEFCASE, CareerRolesView.class));
        pillarsLayout.add(createWorkflowCard("3. Gap & Readiness", "Deterministic mathematical readiness scoring, pinpointing missing and weak skills.", VaadinIcon.CHART_LINE, SkillGapView.class));
        pillarsLayout.add(createWorkflowCard("4. Learning Roadmap", "Prioritized, step-by-step learning milestones with practical technical objectives.", VaadinIcon.MAP_MARKER, RoadmapView.class));

        add(heroPanel, currentStatus, pillarsTitle, pillarsLayout);
    }

    private Div createWorkflowCard(String title, String desc, VaadinIcon iconType, Class<? extends com.vaadin.flow.component.Component> targetView) {
        Div card = new Div();
        card.addClassName("glass-card");
        card.getStyle().set("flex", "1");
        card.getStyle().set("padding", "1.5rem");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flex-direction", "column");
        card.getStyle().set("justify-content", "space-between");
        card.getStyle().set("min-height", "210px");

        Icon icon = iconType.create();
        icon.setSize("26px");
        icon.setColor("#648CFF");
        icon.getStyle().set("margin-bottom", "0.75rem");

        H3 heading = new H3(title);
        heading.getStyle().set("font-size", "1.1rem");
        heading.getStyle().set("margin", "0 0 0.5rem 0");

        Paragraph text = new Paragraph(desc);
        text.getStyle().set("color", "var(--skillpath-text-secondary)");
        text.getStyle().set("font-size", "0.85rem");
        text.getStyle().set("line-height", "1.5");
        text.getStyle().set("margin", "0 0 1rem 0");
        text.getStyle().set("flex-grow", "1");

        Button action = new Button("Open", VaadinIcon.ARROW_RIGHT.create());
        action.addClassName("btn-secondary-glass");
        action.getStyle().set("align-self", "flex-start");
        action.addClickListener(e -> UI.getCurrent().navigate(targetView));

        card.add(icon, heading, text, action);
        return card;
    }
}
