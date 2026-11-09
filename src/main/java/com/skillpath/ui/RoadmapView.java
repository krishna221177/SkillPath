package com.skillpath.ui;

import com.skillpath.model.*;
import com.skillpath.service.CareerRoleService;
import com.skillpath.service.RoadmapService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Optional;

@Route(value = "roadmap", layout = MainLayout.class)
@PageTitle("Learning Roadmap • SkillPath")
public class RoadmapView extends VerticalLayout {

    private final RoadmapService roadmapService;
    private final StudentService studentService;
    private final CareerRoleService careerRoleService;

    private final ComboBox<CareerRole> roleSelector = new ComboBox<>("Target Career Roadmap");
    private final Select<String> statusFilter = new Select<>();
    private final VerticalLayout timelineContainer = new VerticalLayout();

    private Student currentStudent;
    private CareerRole selectedRole;

    public RoadmapView(RoadmapService roadmapService,
                       StudentService studentService,
                       CareerRoleService careerRoleService) {
        this.roadmapService = roadmapService;
        this.studentService = studentService;
        this.careerRoleService = careerRoleService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1100px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        this.currentStudent = studentService.getOrCreateDefaultStudent();
        this.selectedRole = currentStudent.getTargetCareer();

        createHeader();
        loadRoadmap();
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

        VerticalLayout titles = new VerticalLayout();
        titles.setPadding(false);
        titles.setSpacing(false);

        H2 pageTitle = new H2("Curated Learning Roadmap");
        pageTitle.getStyle().set("margin", "0 0 0.25rem 0");

        Paragraph pageDesc = new Paragraph("Prioritized sequential learning milestones generated from your skill gaps: 1. Missing skills, 2. Weak skills, 3. Optional competencies.");
        pageDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
        pageDesc.getStyle().set("margin", "0");
        titles.add(pageTitle, pageDesc);

        List<CareerRole> roles = careerRoleService.getAllRoles();
        roleSelector.setItems(roles);
        roleSelector.setItemLabelGenerator(CareerRole::getName);
        if (selectedRole != null) {
            roleSelector.setValue(selectedRole);
        } else if (!roles.isEmpty()) {
            roleSelector.setValue(roles.get(0));
            selectedRole = roles.get(0);
        }
        roleSelector.addValueChangeListener(e -> {
            selectedRole = e.getValue();
            loadRoadmap();
        });
        roleSelector.getStyle().set("min-width", "260px");

        statusFilter.setLabel("Filter Status");
        statusFilter.setItems("All Items", "Not Started", "In Progress", "Completed");
        statusFilter.setValue("All Items");
        statusFilter.addValueChangeListener(e -> loadRoadmap());

        Button refreshBtn = new Button("Regenerate Roadmap", VaadinIcon.REFRESH.create());
        refreshBtn.addClassName("btn-primary-glass");
        refreshBtn.addClickListener(e -> {
            if (selectedRole != null) {
                roadmapService.generateOrRefreshRoadmap(currentStudent.getId(), selectedRole.getId());
                Notification n = Notification.show("Roadmap refreshed based on current skill gaps!", 3000, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadRoadmap();
            }
        });

        Button progressBtn = new Button("Progress Tracking", VaadinIcon.CHECK_SQUARE_O.create());
        progressBtn.addClassName("btn-secondary-glass");
        progressBtn.addClickListener(e -> UI.getCurrent().navigate(ProgressView.class));

        HorizontalLayout controls = new HorizontalLayout(roleSelector, statusFilter, refreshBtn, progressBtn);
        controls.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        controls.setSpacing(true);

        header.add(titles, controls);
        add(header);

        timelineContainer.setPadding(false);
        timelineContainer.setSpacing(true);
        timelineContainer.setWidthFull();
        add(timelineContainer);
    }

    private void loadRoadmap() {
        timelineContainer.removeAll();

        if (selectedRole == null) {
            return;
        }

        Optional<Roadmap> roadmapOpt = roadmapService.getRoadmapForRole(currentStudent.getId(), selectedRole.getId());
        Roadmap roadmap;
        if (roadmapOpt.isEmpty() || roadmapOpt.get().getItems().isEmpty()) {
            roadmap = roadmapService.generateOrRefreshRoadmap(currentStudent.getId(), selectedRole.getId());
        } else {
            roadmap = roadmapOpt.get();
        }

        List<RoadmapItem> items = roadmap.getItems();
        String filter = statusFilter.getValue();

        int renderedCount = 0;
        for (RoadmapItem item : items) {
            if (filter != null && !filter.equals("All Items")) {
                if (!item.getStatus().getLabel().equalsIgnoreCase(filter)) {
                    continue;
                }
            }
            timelineContainer.add(createRoadmapCard(item));
            renderedCount++;
        }

        if (renderedCount == 0) {
            GlassCard emptyCard = new GlassCard();
            emptyCard.addContent(new Paragraph("No roadmap milestones match the selected status filter."));
            timelineContainer.add(emptyCard);
        }
    }

    private GlassCard createRoadmapCard(RoadmapItem item) {
        GlassCard card = new GlassCard();
        card.setWidthFull();

        VerticalLayout content = card.getContentLayout();

        // Top Row: Sequence #, Skill Name, Priority Badge, Status Selector
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout left = new HorizontalLayout();
        left.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        Span seqBadge = new Span("STEP " + item.getSuggestedSequence());
        seqBadge.getStyle().set("background", "linear-gradient(135deg, #648CFF, #55D6E8)");
        seqBadge.getStyle().set("color", "#FFFFFF");
        seqBadge.getStyle().set("padding", "0.25rem 0.65rem");
        seqBadge.getStyle().set("border-radius", "6px");
        seqBadge.getStyle().set("font-weight", "800");
        seqBadge.getStyle().set("font-size", "0.78rem");

        H3 skillName = new H3(item.getSkill().getName());
        skillName.getStyle().set("margin", "0");
        skillName.getStyle().set("font-size", "1.2rem");

        Span priorityBadge = new Span();
        priorityBadge.setText(item.getPriority().getLabel());
        switch (item.getPriority()) {
            case HIGH:
                priorityBadge.addClassName("badge-priority-high");
                break;
            case MEDIUM:
                priorityBadge.addClassName("badge-priority-med");
                break;
            case LOW:
                priorityBadge.addClassName("badge-priority-low");
                break;
        }

        left.add(seqBadge, skillName, priorityBadge);

        // Status Select Dropdown
        Select<RoadmapItemStatus> statusSelect = new Select<>();
        statusSelect.setItems(RoadmapItemStatus.values());
        statusSelect.setValue(item.getStatus());
        statusSelect.setItemLabelGenerator(RoadmapItemStatus::getLabel);
        statusSelect.getStyle().set("min-width", "160px");
        statusSelect.addValueChangeListener(e -> {
            roadmapService.updateItemStatus(item.getId(), e.getValue());
            Notification.show("Milestone status updated to " + e.getValue().getLabel(), 2500, Notification.Position.TOP_CENTER);
        });

        topRow.add(left, statusSelect);

        // Proficiency Transition Row
        HorizontalLayout transRow = new HorizontalLayout();
        transRow.setSpacing(true);
        transRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        transRow.getStyle().set("margin", "0.3rem 0");

        Span currentSpan = new Span("Current: " + (item.getCurrentProficiency() != null ? item.getCurrentProficiency().getLabel() + " (" + item.getCurrentProficiency().getValue() + "/5)" : "None (0/5)"));
        currentSpan.getStyle().set("color", "var(--skillpath-text-secondary)");
        currentSpan.getStyle().set("font-size", "0.85rem");

        Icon arrow = VaadinIcon.ARROW_RIGHT.create();
        arrow.setSize("14px");
        arrow.setColor("#648CFF");

        Span targetSpan = new Span("Target: " + item.getTargetProficiency().getLabel() + " (" + item.getTargetProficiency().getValue() + "/5)");
        targetSpan.getStyle().set("color", "var(--skillpath-accent-cyan)");
        targetSpan.getStyle().set("font-weight", "600");
        targetSpan.getStyle().set("font-size", "0.85rem");

        transRow.add(currentSpan, arrow, targetSpan);

        // Objective Description Box
        Div objBox = new Div();
        objBox.getStyle().set("background", "rgba(255, 255, 255, 0.03)");
        objBox.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.08)");
        objBox.getStyle().set("border-radius", "8px");
        objBox.getStyle().set("padding", "0.85rem 1rem");
        objBox.getStyle().set("margin-top", "0.5rem");

        Span objTitle = new Span("Practical Learning Objective:");
        objTitle.getStyle().set("font-size", "0.8rem");
        objTitle.getStyle().set("font-weight", "700");
        objTitle.getStyle().set("color", "var(--skillpath-text-primary)");
        objTitle.getStyle().set("display", "block");
        objTitle.getStyle().set("margin-bottom", "0.25rem");

        Paragraph objText = new Paragraph(item.getLearningObjective());
        objText.getStyle().set("margin", "0");
        objText.getStyle().set("font-size", "0.88rem");
        objText.getStyle().set("color", "var(--skillpath-text-secondary)");
        objText.getStyle().set("line-height", "1.5");

        objBox.add(objTitle, objText);
        content.add(topRow, transRow, objBox);

        return card;
    }
}
