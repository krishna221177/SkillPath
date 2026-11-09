package com.skillpath.ui;

import com.skillpath.model.*;
import com.skillpath.service.CareerRoleService;
import com.skillpath.service.RoadmapService;
import com.skillpath.service.SkillService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "progress", layout = MainLayout.class)
@PageTitle("Progress Tracking • SkillPath")
public class ProgressView extends VerticalLayout {

    private final RoadmapService roadmapService;
    private final StudentService studentService;
    private final CareerRoleService careerRoleService;
    private final SkillService skillService;

    private final ComboBox<CareerRole> roleSelector = new ComboBox<>("Track Progress for Career Role");
    private final ProgressBar overallProgressBar = new ProgressBar();
    private final H2 progressPercentHeading = new H2("0%");
    private final Span totalItemsSpan = new Span();
    private final Span completedItemsSpan = new Span();
    private final Span inProgressSpan = new Span();
    private final Span remainingSpan = new Span();

    private final Tabs filterTabs = new Tabs();
    private final Tab allTab = new Tab("All Items");
    private final Tab notStartedTab = new Tab("Not Started");
    private final Tab inProgressTab = new Tab("In Progress");
    private final Tab completedTab = new Tab("Completed");

    private final VerticalLayout itemsContainer = new VerticalLayout();

    private Student currentStudent;
    private CareerRole selectedRole;

    public ProgressView(RoadmapService roadmapService,
                        StudentService studentService,
                        CareerRoleService careerRoleService,
                        SkillService skillService) {
        this.roadmapService = roadmapService;
        this.studentService = studentService;
        this.careerRoleService = careerRoleService;
        this.skillService = skillService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1100px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        this.currentStudent = studentService.getOrCreateDefaultStudent();
        this.selectedRole = currentStudent.getTargetCareer();

        createHeader();
        createStatsSection();
        createTabsAndList();
        loadProgress();
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

        VerticalLayout titles = new VerticalLayout();
        titles.setPadding(false);
        titles.setSpacing(false);

        H2 pageTitle = new H2("Learning Progress & Milestone Tracker");
        pageTitle.getStyle().set("margin", "0 0 0.25rem 0");

        Paragraph pageDesc = new Paragraph("Track your journey through technical milestones. Status updates are stored persistently in the H2 database.");
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
            loadProgress();
        });
        roleSelector.getStyle().set("min-width", "280px");

        header.add(titles, roleSelector);
        add(header);
    }

    private void createStatsSection() {
        GlassCard card = new GlassCard();
        card.setWidthFull();

        VerticalLayout content = card.getContentLayout();

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout progressBox = new HorizontalLayout();
        progressBox.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        progressBox.setSpacing(true);

        progressPercentHeading.getStyle().set("font-size", "2.6rem");
        progressPercentHeading.getStyle().set("font-weight", "900");
        progressPercentHeading.getStyle().set("margin", "0");
        progressPercentHeading.getStyle().set("color", "#55D6E8");

        VerticalLayout labelBox = new VerticalLayout();
        labelBox.setPadding(false);
        labelBox.setSpacing(false);

        Span label = new Span("ROADMAP COMPLETION");
        label.getStyle().set("font-size", "0.8rem");
        label.getStyle().set("letter-spacing", "0.08em");
        label.getStyle().set("font-weight", "700");
        label.getStyle().set("color", "var(--skillpath-accent-cyan)");

        totalItemsSpan.getStyle().set("color", "var(--skillpath-text-secondary)");
        totalItemsSpan.getStyle().set("font-size", "0.85rem");
        labelBox.add(label, totalItemsSpan);

        progressBox.add(progressPercentHeading, labelBox);

        HorizontalLayout statsBadges = new HorizontalLayout();
        statsBadges.setSpacing(true);

        completedItemsSpan.getStyle().set("background", "rgba(104, 211, 145, 0.18)");
        completedItemsSpan.getStyle().set("color", "#68D391");
        completedItemsSpan.getStyle().set("border", "1px solid rgba(104, 211, 145, 0.35)");
        completedItemsSpan.getStyle().set("padding", "0.3rem 0.75rem");
        completedItemsSpan.getStyle().set("border-radius", "8px");
        completedItemsSpan.getStyle().set("font-weight", "600");
        completedItemsSpan.getStyle().set("font-size", "0.82rem");

        inProgressSpan.getStyle().set("background", "rgba(100, 140, 255, 0.18)");
        inProgressSpan.getStyle().set("color", "#648CFF");
        inProgressSpan.getStyle().set("border", "1px solid rgba(100, 140, 255, 0.35)");
        inProgressSpan.getStyle().set("padding", "0.3rem 0.75rem");
        inProgressSpan.getStyle().set("border-radius", "8px");
        inProgressSpan.getStyle().set("font-weight", "600");
        inProgressSpan.getStyle().set("font-size", "0.82rem");

        remainingSpan.getStyle().set("background", "rgba(255, 255, 255, 0.08)");
        remainingSpan.getStyle().set("color", "var(--skillpath-text-secondary)");
        remainingSpan.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.15)");
        remainingSpan.getStyle().set("padding", "0.3rem 0.75rem");
        remainingSpan.getStyle().set("border-radius", "8px");
        remainingSpan.getStyle().set("font-weight", "600");
        remainingSpan.getStyle().set("font-size", "0.82rem");

        statsBadges.add(completedItemsSpan, inProgressSpan, remainingSpan);
        top.add(progressBox, statsBadges);

        overallProgressBar.setWidthFull();
        overallProgressBar.getStyle().set("height", "10px");
        overallProgressBar.getStyle().set("margin-top", "0.5rem");

        content.add(top, overallProgressBar);
        add(card);
    }

    private void createTabsAndList() {
        filterTabs.add(allTab, notStartedTab, inProgressTab, completedTab);
        filterTabs.setSelectedTab(allTab);
        filterTabs.addSelectedChangeListener(e -> renderFilteredItems());

        itemsContainer.setPadding(false);
        itemsContainer.setSpacing(true);
        itemsContainer.setWidthFull();

        add(filterTabs, itemsContainer);
    }

    private List<RoadmapItem> currentRoadmapItems;

    private void loadProgress() {
        if (selectedRole == null) return;

        Optional<Roadmap> roadmapOpt = roadmapService.getRoadmapForRole(currentStudent.getId(), selectedRole.getId());
        Roadmap roadmap;
        if (roadmapOpt.isEmpty() || roadmapOpt.get().getItems().isEmpty()) {
            roadmap = roadmapService.generateOrRefreshRoadmap(currentStudent.getId(), selectedRole.getId());
        } else {
            roadmap = roadmapOpt.get();
        }

        currentRoadmapItems = roadmap.getItems();

        long total = currentRoadmapItems.size();
        long completed = currentRoadmapItems.stream().filter(i -> i.getStatus() == RoadmapItemStatus.COMPLETED).count();
        long inProg = currentRoadmapItems.stream().filter(i -> i.getStatus() == RoadmapItemStatus.IN_PROGRESS).count();
        long remaining = currentRoadmapItems.stream().filter(i -> i.getStatus() == RoadmapItemStatus.NOT_STARTED).count();

        double pct = total > 0 ? ((double) completed / total) * 100.0 : 0.0;
        progressPercentHeading.setText(String.format("%.1f%%", pct));
        overallProgressBar.setValue(pct / 100.0);

        totalItemsSpan.setText(completed + " of " + total + " milestones finished");
        completedItemsSpan.setText("✓ " + completed + " Completed");
        inProgressSpan.setText("↻ " + inProg + " In Progress");
        remainingSpan.setText("○ " + remaining + " Remaining");

        renderFilteredItems();
    }

    private void renderFilteredItems() {
        itemsContainer.removeAll();
        if (currentRoadmapItems == null || currentRoadmapItems.isEmpty()) {
            GlassCard empty = new GlassCard();
            empty.addContent(new Paragraph("No roadmap milestones found for this career track."));
            itemsContainer.add(empty);
            return;
        }

        Tab selectedTab = filterTabs.getSelectedTab();
        RoadmapItemStatus filterStatus = null;
        if (selectedTab == notStartedTab) {
            filterStatus = RoadmapItemStatus.NOT_STARTED;
        } else if (selectedTab == inProgressTab) {
            filterStatus = RoadmapItemStatus.IN_PROGRESS;
        } else if (selectedTab == completedTab) {
            filterStatus = RoadmapItemStatus.COMPLETED;
        }

        final RoadmapItemStatus finalFilter = filterStatus;
        List<RoadmapItem> list = currentRoadmapItems.stream()
                .filter(i -> finalFilter == null || i.getStatus() == finalFilter)
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            GlassCard empty = new GlassCard();
            empty.addContent(new Paragraph("No milestones currently in this status category."));
            itemsContainer.add(empty);
            return;
        }

        for (RoadmapItem item : list) {
            itemsContainer.add(createProgressItemCard(item));
        }
    }

    private GlassCard createProgressItemCard(RoadmapItem item) {
        GlassCard card = new GlassCard();
        card.setWidthFull();

        VerticalLayout content = card.getContentLayout();

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        HorizontalLayout left = new HorizontalLayout();
        left.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        Span seq = new Span("#" + item.getSuggestedSequence());
        seq.getStyle().set("color", "var(--skillpath-accent-cyan)");
        seq.getStyle().set("font-weight", "800");

        H3 title = new H3(item.getSkill().getName());
        title.getStyle().set("margin", "0");
        title.getStyle().set("font-size", "1.15rem");

        Span target = new Span("Target: " + item.getTargetProficiency().getLabel() + " (" + item.getTargetProficiency().getValue() + "/5)");
        target.getStyle().set("color", "var(--skillpath-text-secondary)");
        target.getStyle().set("font-size", "0.85rem");

        left.add(seq, title, target);

        // Status Toggle Buttons Group
        HorizontalLayout statusButtons = new HorizontalLayout();
        statusButtons.setSpacing(true);

        Button notStartedBtn = new Button("Not Started");
        Button inProgBtn = new Button("In Progress");
        Button completeBtn = new Button("Mark Completed", VaadinIcon.CHECK.create());

        styleStatusButtons(item.getStatus(), notStartedBtn, inProgBtn, completeBtn);

        notStartedBtn.addClickListener(e -> updateStatus(item, RoadmapItemStatus.NOT_STARTED));
        inProgBtn.addClickListener(e -> updateStatus(item, RoadmapItemStatus.IN_PROGRESS));
        completeBtn.addClickListener(e -> {
            updateStatus(item, RoadmapItemStatus.COMPLETED);
            // Also synchronize skill portfolio level if student achieved the target
            skillService.addOrUpdateStudentSkill(currentStudent.getId(), item.getSkill().getId(), item.getTargetProficiency());
            Notification.show("Milestone marked completed & skill portfolio updated to " + item.getTargetProficiency().getLabel() + "!", 3500, Notification.Position.TOP_CENTER);
        });

        statusButtons.add(notStartedBtn, inProgBtn, completeBtn);
        top.add(left, statusButtons);

        // Objective snippet
        Paragraph obj = new Paragraph(item.getLearningObjective());
        obj.getStyle().set("margin", "0.5rem 0 0 0");
        obj.getStyle().set("font-size", "0.85rem");
        obj.getStyle().set("color", "var(--skillpath-text-secondary)");
        obj.getStyle().set("line-height", "1.4");

        content.add(top, obj);
        return card;
    }

    private void styleStatusButtons(RoadmapItemStatus status, Button notStartedBtn, Button inProgBtn, Button completeBtn) {
        notStartedBtn.removeClassNames("btn-primary-glass", "btn-secondary-glass");
        inProgBtn.removeClassNames("btn-primary-glass", "btn-secondary-glass");
        completeBtn.removeClassNames("btn-primary-glass", "btn-secondary-glass");

        if (status == RoadmapItemStatus.NOT_STARTED) {
            notStartedBtn.addClassName("btn-primary-glass");
            inProgBtn.addClassName("btn-secondary-glass");
            completeBtn.addClassName("btn-secondary-glass");
        } else if (status == RoadmapItemStatus.IN_PROGRESS) {
            notStartedBtn.addClassName("btn-secondary-glass");
            inProgBtn.addClassName("btn-primary-glass");
            completeBtn.addClassName("btn-secondary-glass");
        } else if (status == RoadmapItemStatus.COMPLETED) {
            notStartedBtn.addClassName("btn-secondary-glass");
            inProgBtn.addClassName("btn-secondary-glass");
            completeBtn.getStyle().set("background", "linear-gradient(135deg, #48BB78 0%, #38A169 100%)");
            completeBtn.getStyle().set("color", "#FFFFFF");
            completeBtn.getStyle().set("font-weight", "600");
            completeBtn.getStyle().set("border", "none");
            completeBtn.getStyle().set("border-radius", "8px");
        }
    }

    private void updateStatus(RoadmapItem item, RoadmapItemStatus newStatus) {
        roadmapService.updateItemStatus(item.getId(), newStatus);
        loadProgress();
    }
}
