package com.skillpath.ui;

import com.skillpath.dto.SkillGapAnalysisResultDto;
import com.skillpath.dto.SkillGapItemDto;
import com.skillpath.model.CareerRole;
import com.skillpath.model.ProficiencyLevel;
import com.skillpath.model.SkillGapStatus;
import com.skillpath.model.Student;
import com.skillpath.service.CareerRoleService;
import com.skillpath.service.SkillGapAnalysisService;
import com.skillpath.service.SkillService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "gap-analysis", layout = MainLayout.class)
@PageTitle("Skill-Gap Analysis • SkillPath")
public class SkillGapView extends VerticalLayout {

    private final SkillGapAnalysisService skillGapAnalysisService;
    private final StudentService studentService;
    private final CareerRoleService careerRoleService;
    private final SkillService skillService;

    private final ComboBox<CareerRole> roleSelector = new ComboBox<>("Target Career Track");
    private final Grid<SkillGapItemDto> gapGrid = new Grid<>(SkillGapItemDto.class, false);

    private final Div readinessBox = new Div();
    private final ProgressBar progressBar = new ProgressBar();
    private final Span satisfiedBadge = new Span();
    private final Span improvementBadge = new Span();
    private final Span missingBadge = new Span();
    private final Span formulaBreakdown = new Span();

    private Student currentStudent;
    private CareerRole selectedRole;

    public SkillGapView(SkillGapAnalysisService skillGapAnalysisService,
                        StudentService studentService,
                        CareerRoleService careerRoleService,
                        SkillService skillService) {
        this.skillGapAnalysisService = skillGapAnalysisService;
        this.studentService = studentService;
        this.careerRoleService = careerRoleService;
        this.skillService = skillService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        this.currentStudent = studentService.getOrCreateDefaultStudent();
        this.selectedRole = currentStudent.getTargetCareer();

        createHeader();
        createReadinessCard();
        createGapGrid();
        runAnalysis();
    }

    private void createHeader() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

        VerticalLayout titles = new VerticalLayout();
        titles.setPadding(false);
        titles.setSpacing(false);

        H2 pageTitle = new H2("Skill-Gap Analysis & Career Readiness");
        pageTitle.getStyle().set("margin", "0 0 0.25rem 0");

        Paragraph pageDesc = new Paragraph("Deterministic mathematical comparison between your current proficiencies and industry career requirements.");
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
            runAnalysis();
        });
        roleSelector.getStyle().set("min-width", "280px");

        Button genRoadmapBtn = new Button("View Learning Roadmap", VaadinIcon.MAP_MARKER.create());
        genRoadmapBtn.addClassName("btn-primary-glass");
        genRoadmapBtn.addClickListener(e -> UI.getCurrent().navigate(RoadmapView.class));

        HorizontalLayout selectorLayout = new HorizontalLayout(roleSelector, genRoadmapBtn);
        selectorLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        selectorLayout.setSpacing(true);

        topBar.add(titles, selectorLayout);
        add(topBar);
    }

    private void createReadinessCard() {
        GlassCard card = new GlassCard();
        card.setWidthFull();

        VerticalLayout content = card.getContentLayout();

        HorizontalLayout mainRow = new HorizontalLayout();
        mainRow.setWidthFull();
        mainRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Score display
        HorizontalLayout scoreBox = new HorizontalLayout();
        scoreBox.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        scoreBox.setSpacing(true);

        readinessBox.getStyle().set("font-size", "2.8rem");
        readinessBox.getStyle().set("font-weight", "900");
        readinessBox.getStyle().set("background", "linear-gradient(135deg, #55D6E8 0%, #648CFF 100%)");
        readinessBox.getStyle().set("-webkit-background-clip", "text");
        readinessBox.getStyle().set("-webkit-text-fill-color", "transparent");

        VerticalLayout labelBox = new VerticalLayout();
        labelBox.setPadding(false);
        labelBox.setSpacing(false);

        Span scoreLabel = new Span("CAREER READINESS SCORE");
        scoreLabel.getStyle().set("font-size", "0.8rem");
        scoreLabel.getStyle().set("letter-spacing", "0.08em");
        scoreLabel.getStyle().set("font-weight", "700");
        scoreLabel.getStyle().set("color", "var(--skillpath-accent-cyan)");

        formulaBreakdown.getStyle().set("color", "var(--skillpath-text-secondary)");
        formulaBreakdown.getStyle().set("font-size", "0.85rem");
        labelBox.add(scoreLabel, formulaBreakdown);

        scoreBox.add(readinessBox, labelBox);

        // Status counts badges
        HorizontalLayout badgesRow = new HorizontalLayout();
        badgesRow.setSpacing(true);

        satisfiedBadge.addClassName("badge-satisfied");
        improvementBadge.addClassName("badge-improvement");
        missingBadge.addClassName("badge-missing");

        badgesRow.add(satisfiedBadge, improvementBadge, missingBadge);
        mainRow.add(scoreBox, badgesRow);

        progressBar.setWidthFull();
        progressBar.getStyle().set("height", "10px");
        progressBar.getStyle().set("margin-top", "0.5rem");

        // Formula Explainability Footer
        Div formulaExpl = new Div();
        formulaExpl.getStyle().set("background", "rgba(255, 255, 255, 0.03)");
        formulaExpl.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.08)");
        formulaExpl.getStyle().set("border-radius", "8px");
        formulaExpl.getStyle().set("padding", "0.6rem 0.9rem");
        formulaExpl.getStyle().set("font-size", "0.8rem");
        formulaExpl.getStyle().set("color", "var(--skillpath-text-muted)");
        formulaExpl.getStyle().set("line-height", "1.4");
        formulaExpl.setText("Algorithm: Readiness % = (Total Achieved Points ÷ Total Required Points) × 100. " +
                "Achieved Points = Min(Student Proficiency, Required Proficiency). Missing skills = 0. " +
                "Exceeding levels are capped at requirement. Optional skills are excluded from mandatory denominator.");

        content.add(mainRow, progressBar, formulaExpl);
        add(card);
    }

    private void createGapGrid() {
        GlassCard card = new GlassCard("Role Competency Matrix", "Breakdown of individual technical requirements and student status");
        card.setWidthFull();

        gapGrid.addColumn(SkillGapItemDto::getSkillName)
                .setHeader("Required Competency")
                .setSortable(true)
                .setFlexGrow(2);

        gapGrid.addComponentColumn(item -> {
            Span req = new Span(item.getRequiredProficiency().getLabel() + " (" + item.getRequiredPoints() + "/5)");
            req.getStyle().set("font-weight", "600");
            req.getStyle().set("color", "var(--skillpath-accent-blue)");
            return req;
        }).setHeader("Required Level").setSortable(true).setFlexGrow(1);

        gapGrid.addComponentColumn(item -> {
            if (item.getCurrentProficiency() != null) {
                Span cur = new Span(item.getCurrentProficiency().getLabel() + " (" + item.getCurrentPoints() + "/5)");
                cur.getStyle().set("font-weight", "600");
                cur.getStyle().set("color", "var(--skillpath-text-primary)");
                return cur;
            } else {
                Span notAdded = new Span("Not Added (0/5)");
                notAdded.getStyle().set("color", "var(--skillpath-text-muted)");
                notAdded.getStyle().set("font-style", "italic");
                return notAdded;
            }
        }).setHeader("Your Level").setSortable(true).setFlexGrow(1);

        gapGrid.addComponentColumn(item -> {
            Span badge = new Span();
            switch (item.getStatus()) {
                case SATISFIED:
                    badge.setText("Satisfied");
                    badge.addClassName("badge-satisfied");
                    break;
                case NEEDS_IMPROVEMENT:
                    badge.setText("Needs Improvement");
                    badge.addClassName("badge-improvement");
                    break;
                case MISSING:
                    badge.setText("Missing Skill");
                    badge.addClassName("badge-missing");
                    break;
            }
            return badge;
        }).setHeader("Classification").setSortable(true).setFlexGrow(1);

        gapGrid.addComponentColumn(item -> {
            Span text = new Span(item.getImprovementNeeded());
            text.getStyle().set("font-size", "0.85rem");
            if (item.getStatus() == SkillGapStatus.SATISFIED) {
                text.getStyle().set("color", "#68D391");
            } else if (item.getStatus() == SkillGapStatus.NEEDS_IMPROVEMENT) {
                text.getStyle().set("color", "#F6AD55");
            } else {
                text.getStyle().set("color", "#FC8181");
            }
            return text;
        }).setHeader("Gap / Improvement Needed").setFlexGrow(2);

        gapGrid.addComponentColumn(item -> {
            Span chip = new Span(item.isOptional() ? "Optional" : "Mandatory");
            chip.getStyle().set("padding", "0.15rem 0.5rem");
            chip.getStyle().set("border-radius", "4px");
            chip.getStyle().set("font-size", "0.75rem");
            chip.getStyle().set("font-weight", "600");
            if (item.isOptional()) {
                chip.getStyle().set("background", "rgba(255, 255, 255, 0.06)");
                chip.getStyle().set("color", "var(--skillpath-text-muted)");
            } else {
                chip.getStyle().set("background", "rgba(100, 140, 255, 0.15)");
                chip.getStyle().set("color", "var(--skillpath-accent-blue)");
            }
            return chip;
        }).setHeader("Type").setFlexGrow(1);

        gapGrid.addComponentColumn(item -> {
            Button updateBtn = new Button("Set Level", VaadinIcon.EDIT.create());
            updateBtn.addClassName("btn-secondary-glass");
            updateBtn.getStyle().set("padding", "0.2rem 0.6rem");
            updateBtn.getStyle().set("font-size", "0.8rem");
            updateBtn.addClickListener(e -> openProficiencyDialog(item));
            return updateBtn;
        }).setHeader("Action").setFlexGrow(1);

        gapGrid.setAllRowsVisible(true);
        card.addContent(gapGrid);
        add(card);
    }

    private void runAnalysis() {
        if (selectedRole == null) {
            return;
        }

        SkillGapAnalysisResultDto result = skillGapAnalysisService.analyze(currentStudent.getId(), selectedRole.getId());

        readinessBox.setText(String.format("%.2f%%", result.getReadinessPercentage()));
        progressBar.setValue(result.getReadinessPercentage() / 100.0);

        formulaBreakdown.setText("Achieved: " + result.getTotalAchievedPoints() + " pts / Required: " + result.getTotalRequiredPoints() + " pts");
        satisfiedBadge.setText("✓ " + result.getSatisfiedCount() + " Satisfied");
        improvementBadge.setText("▲ " + result.getNeedsImprovementCount() + " Needs Improvement");
        missingBadge.setText("✕ " + result.getMissingCount() + " Missing");

        gapGrid.setItems(result.getItems());
    }

    private void openProficiencyDialog(SkillGapItemDto item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Update Skill: " + item.getSkillName());

        Select<ProficiencyLevel> levelSelect = new Select<>();
        levelSelect.setLabel("Select Your Current Proficiency");
        levelSelect.setItems(ProficiencyLevel.values());
        if (item.getCurrentProficiency() != null) {
            levelSelect.setValue(item.getCurrentProficiency());
        } else {
            levelSelect.setValue(ProficiencyLevel.INTERMEDIATE);
        }
        levelSelect.setItemLabelGenerator(p -> p.getLabel() + " (Score " + p.getValue() + "/5)");
        levelSelect.setWidthFull();

        Paragraph hint = new Paragraph("Target requirement for this career: " + item.getRequiredProficiency().getLabel() + " (" + item.getRequiredPoints() + "/5)");
        hint.getStyle().set("color", "var(--skillpath-text-secondary)");
        hint.getStyle().set("font-size", "0.85rem");

        dialog.add(new VerticalLayout(hint, levelSelect));

        Button saveBtn = new Button("Update & Recalculate", e -> {
            try {
                skillService.addOrUpdateStudentSkill(currentStudent.getId(), item.getSkill().getId(), levelSelect.getValue());
                runAnalysis();
                dialog.close();
                Notification n = Notification.show("Proficiency updated! Readiness score recalculated.", 3500, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3500, Notification.Position.TOP_CENTER);
            }
        });
        saveBtn.addClassName("btn-primary-glass");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("btn-secondary-glass");

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }
}
