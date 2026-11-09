package com.skillpath.ui;

import com.skillpath.model.CareerRole;
import com.skillpath.model.RoleRequirement;
import com.skillpath.model.Student;
import com.skillpath.service.CareerRoleService;
import com.skillpath.service.RoadmapService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "roles", layout = MainLayout.class)
@PageTitle("Career Roles • SkillPath")
public class CareerRolesView extends VerticalLayout {

    private final CareerRoleService careerRoleService;
    private final StudentService studentService;
    private final RoadmapService roadmapService;

    public CareerRolesView(CareerRoleService careerRoleService,
                           StudentService studentService,
                           RoadmapService roadmapService) {
        this.careerRoleService = careerRoleService;
        this.studentService = studentService;
        this.roadmapService = roadmapService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        renderRoles();
    }

    private void renderRoles() {
        removeAll();

        Student currentStudent = studentService.getOrCreateDefaultStudent();
        CareerRole activeRole = currentStudent.getTargetCareer();

        H2 title = new H2("Career Track Profiles & Benchmarks");
        title.getStyle().set("margin", "0 0 0.25rem 0");

        Paragraph desc = new Paragraph("Select your target career path to compare your skill portfolio, compute readiness percentages, and generate customized roadmaps.");
        desc.getStyle().set("color", "var(--skillpath-text-secondary)");
        desc.getStyle().set("margin", "0 0 1.5rem 0");

        add(title, desc);

        List<CareerRole> roles = careerRoleService.getAllRoles();

        VerticalLayout rolesContainer = new VerticalLayout();
        rolesContainer.setPadding(false);
        rolesContainer.setSpacing(true);
        rolesContainer.setWidthFull();

        for (CareerRole role : roles) {
            boolean isCurrentTarget = activeRole != null && activeRole.getId().equals(role.getId());
            rolesContainer.add(createCareerRoleCard(role, isCurrentTarget, currentStudent));
        }

        add(rolesContainer);
    }

    private GlassCard createCareerRoleCard(CareerRole role, boolean isCurrentTarget, Student student) {
        GlassCard card = new GlassCard();
        card.setWidthFull();
        if (isCurrentTarget) {
            card.getStyle().set("border-color", "rgba(100, 140, 255, 0.5)");
            card.getStyle().set("box-shadow", "0 10px 30px rgba(100, 140, 255, 0.2)");
        }

        VerticalLayout content = card.getContentLayout();

        // Top Row: Role Title & Active Target Badge
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout titleBox = new HorizontalLayout();
        titleBox.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        titleBox.setSpacing(true);

        Icon roleIcon = VaadinIcon.BRIEFCASE.create();
        roleIcon.setColor("#648CFF");
        roleIcon.setSize("24px");

        H3 roleHeading = new H3(role.getName());
        roleHeading.getStyle().set("margin", "0");
        roleHeading.getStyle().set("font-size", "1.25rem");

        titleBox.add(roleIcon, roleHeading);

        if (isCurrentTarget) {
            Span targetBadge = new Span("★ ACTIVE TARGET ROLE");
            targetBadge.getStyle().set("background", "linear-gradient(90deg, #648CFF, #55D6E8)");
            targetBadge.getStyle().set("color", "#FFFFFF");
            targetBadge.getStyle().set("padding", "0.3rem 0.75rem");
            targetBadge.getStyle().set("border-radius", "8px");
            targetBadge.getStyle().set("font-weight", "700");
            targetBadge.getStyle().set("font-size", "0.75rem");
            targetBadge.getStyle().set("letter-spacing", "0.05em");
            topRow.add(titleBox, targetBadge);
        } else {
            topRow.add(titleBox);
        }

        // Description
        Paragraph roleDesc = new Paragraph(role.getDescription());
        roleDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
        roleDesc.getStyle().set("font-size", "0.92rem");
        roleDesc.getStyle().set("margin", "0.5rem 0 1rem 0");

        // Requirements chips
        List<RoleRequirement> requirements = role.getRequirements();

        Div mandatorySection = new Div();
        Span mandatoryLabel = new Span("Mandatory Core Skills (" + countMandatory(requirements) + "): ");
        mandatoryLabel.getStyle().set("font-weight", "600");
        mandatoryLabel.getStyle().set("font-size", "0.85rem");
        mandatoryLabel.getStyle().set("color", "var(--skillpath-text-primary)");
        mandatorySection.add(mandatoryLabel);

        HorizontalLayout mandatoryChips = new HorizontalLayout();
        mandatoryChips.getStyle().set("flex-wrap", "wrap");
        mandatoryChips.getStyle().set("gap", "0.4rem");
        mandatoryChips.getStyle().set("margin", "0.4rem 0 0.8rem 0");

        HorizontalLayout optionalChips = new HorizontalLayout();
        optionalChips.getStyle().set("flex-wrap", "wrap");
        optionalChips.getStyle().set("gap", "0.4rem");
        optionalChips.getStyle().set("margin", "0.4rem 0 1rem 0");

        Div optionalSection = new Div();
        Span optionalLabel = new Span("Optional / Nice-to-Have Skills: ");
        optionalLabel.getStyle().set("font-weight", "600");
        optionalLabel.getStyle().set("font-size", "0.85rem");
        optionalLabel.getStyle().set("color", "var(--skillpath-text-secondary)");
        optionalSection.add(optionalLabel);

        for (RoleRequirement req : requirements) {
            Span chip = new Span(req.getSkill().getName() + " (" + req.getRequiredProficiency().getLabel() + ")");
            chip.getStyle().set("padding", "0.25rem 0.6rem");
            chip.getStyle().set("border-radius", "6px");
            chip.getStyle().set("font-size", "0.78rem");
            chip.getStyle().set("font-weight", "500");

            if (req.isOptional()) {
                chip.getStyle().set("background", "rgba(255, 255, 255, 0.06)");
                chip.getStyle().set("border", "1px dashed rgba(255, 255, 255, 0.2)");
                chip.getStyle().set("color", "var(--skillpath-text-secondary)");
                optionalChips.add(chip);
            } else {
                chip.getStyle().set("background", "rgba(100, 140, 255, 0.15)");
                chip.getStyle().set("border", "1px solid rgba(100, 140, 255, 0.3)");
                chip.getStyle().set("color", "var(--skillpath-accent-blue)");
                mandatoryChips.add(chip);
            }
        }

        // Action Buttons Row
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "0.75rem");

        if (!isCurrentTarget) {
            Button selectBtn = new Button("Set as Target Career", VaadinIcon.CHECK_CIRCLE.create());
            selectBtn.addClassName("btn-primary-glass");
            selectBtn.addClickListener(e -> {
                studentService.setTargetCareer(student.getId(), role.getId());
                roadmapService.generateOrRefreshRoadmap(student.getId(), role.getId());
                Notification n = Notification.show("Target career set to: " + role.getName(), 3000, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                renderRoles();
            });
            actions.add(selectBtn);
        }

        Button gapAnalysisBtn = new Button("Run Skill-Gap Analysis", VaadinIcon.CHART_LINE.create());
        gapAnalysisBtn.addClassName("btn-secondary-glass");
        gapAnalysisBtn.addClickListener(e -> {
            if (!isCurrentTarget) {
                studentService.setTargetCareer(student.getId(), role.getId());
            }
            UI.getCurrent().navigate(SkillGapView.class);
        });
        actions.add(gapAnalysisBtn);

        content.add(topRow, roleDesc, mandatorySection, mandatoryChips);
        if (optionalChips.getComponentCount() > 0) {
            content.add(optionalSection, optionalChips);
        }
        content.add(actions);

        return card;
    }

    private int countMandatory(List<RoleRequirement> reqs) {
        int count = 0;
        for (RoleRequirement r : reqs) {
            if (!r.isOptional()) count++;
        }
        return count;
    }
}
