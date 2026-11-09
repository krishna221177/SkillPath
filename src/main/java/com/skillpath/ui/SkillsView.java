package com.skillpath.ui;

import com.skillpath.exception.SkillPathException;
import com.skillpath.model.ProficiencyLevel;
import com.skillpath.model.Skill;
import com.skillpath.model.Student;
import com.skillpath.model.StudentSkill;
import com.skillpath.service.SkillService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "skills", layout = MainLayout.class)
@PageTitle("My Skills • SkillPath")
public class SkillsView extends VerticalLayout {

    private final SkillService skillService;
    private final StudentService studentService;

    private final Grid<StudentSkill> skillsGrid = new Grid<>(StudentSkill.class, false);
    private final TextField searchField = new TextField();
    private final Select<String> categoryFilter = new Select<>();
    private final Span totalSkillsBadge = new Span();

    private Student currentStudent;
    private List<StudentSkill> allStudentSkills = new ArrayList<>();

    public SkillsView(SkillService skillService, StudentService studentService) {
        this.skillService = skillService;
        this.studentService = studentService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "1200px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        this.currentStudent = studentService.getOrCreateDefaultStudent();

        createHeader();
        createControlsAndGrid();
        loadSkills();
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        VerticalLayout titles = new VerticalLayout();
        titles.setPadding(false);
        titles.setSpacing(false);

        HorizontalLayout titleRow = new HorizontalLayout();
        titleRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        H2 pageTitle = new H2("Technical Skill Management");
        pageTitle.getStyle().set("margin", "0");

        totalSkillsBadge.getStyle().set("background", "rgba(100, 140, 255, 0.2)");
        totalSkillsBadge.getStyle().set("color", "var(--skillpath-accent-blue)");
        totalSkillsBadge.getStyle().set("border", "1px solid rgba(100, 140, 255, 0.35)");
        totalSkillsBadge.getStyle().set("padding", "0.2rem 0.6rem");
        totalSkillsBadge.getStyle().set("border-radius", "8px");
        totalSkillsBadge.getStyle().set("font-size", "0.85rem");
        totalSkillsBadge.getStyle().set("font-weight", "700");

        titleRow.add(pageTitle, totalSkillsBadge);

        Paragraph pageDesc = new Paragraph("Record, adjust, or expand your technical proficiency portfolio. Proficiency ranks range from 1 (Beginner) to 5 (Expert).");
        pageDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
        pageDesc.getStyle().set("margin", "0.25rem 0 0 0");

        titles.add(titleRow, pageDesc);

        HorizontalLayout actionButtons = new HorizontalLayout();
        actionButtons.setSpacing(true);

        Button addSkillBtn = new Button("Add Skill to Profile", VaadinIcon.PLUS_CIRCLE.create());
        addSkillBtn.addClassName("btn-primary-glass");
        addSkillBtn.addClickListener(e -> openAddSkillDialog());

        Button createCustomBtn = new Button("Create Custom Skill", VaadinIcon.LIGHTBULB.create());
        createCustomBtn.addClassName("btn-secondary-glass");
        createCustomBtn.addClickListener(e -> openCreateCustomSkillDialog());

        actionButtons.add(createCustomBtn, addSkillBtn);
        header.add(titles, actionButtons);
        add(header);
    }

    private void createControlsAndGrid() {
        GlassCard card = new GlassCard();
        card.setWidthFull();

        // Search & Filter controls
        HorizontalLayout controls = new HorizontalLayout();
        controls.setWidthFull();
        controls.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        controls.setSpacing(true);

        searchField.setPlaceholder("Search skills by name or keyword...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> filterGrid());
        searchField.getStyle().set("flex", "2");

        categoryFilter.setItems("All Categories", "Programming", "Database", "Web", "Framework", "Core CS", "DevOps", "Operating System", "Architecture");
        categoryFilter.setValue("All Categories");
        categoryFilter.addValueChangeListener(e -> filterGrid());
        categoryFilter.getStyle().set("flex", "1");

        controls.add(searchField, categoryFilter);

        // Grid Configuration
        skillsGrid.addColumn(studentSkill -> studentSkill.getSkill().getName())
                .setHeader("Skill Name")
                .setSortable(true)
                .setFlexGrow(2);

        skillsGrid.addComponentColumn(studentSkill -> {
            Span cat = new Span(studentSkill.getSkill().getCategory());
            cat.getStyle().set("background", "rgba(255, 255, 255, 0.08)");
            cat.getStyle().set("padding", "0.2rem 0.5rem");
            cat.getStyle().set("border-radius", "6px");
            cat.getStyle().set("font-size", "0.8rem");
            cat.getStyle().set("color", "var(--skillpath-text-secondary)");
            return cat;
        }).setHeader("Category").setFlexGrow(1);

        skillsGrid.addComponentColumn(studentSkill -> {
            ProficiencyLevel level = studentSkill.getProficiency();
            Span badge = new Span(level.getLabel() + " (" + level.getValue() + "/5)");
            badge.getStyle().set("padding", "0.25rem 0.65rem");
            badge.getStyle().set("border-radius", "6px");
            badge.getStyle().set("font-weight", "600");
            badge.getStyle().set("font-size", "0.82rem");

            switch (level) {
                case EXPERT:
                    badge.getStyle().set("background", "rgba(85, 214, 232, 0.2)");
                    badge.getStyle().set("color", "#55D6E8");
                    badge.getStyle().set("border", "1px solid rgba(85, 214, 232, 0.4)");
                    break;
                case ADVANCED:
                    badge.getStyle().set("background", "rgba(104, 211, 145, 0.2)");
                    badge.getStyle().set("color", "#68D391");
                    badge.getStyle().set("border", "1px solid rgba(104, 211, 145, 0.4)");
                    break;
                case INTERMEDIATE:
                    badge.getStyle().set("background", "rgba(100, 140, 255, 0.2)");
                    badge.getStyle().set("color", "#648CFF");
                    badge.getStyle().set("border", "1px solid rgba(100, 140, 255, 0.4)");
                    break;
                case BASIC:
                    badge.getStyle().set("background", "rgba(246, 173, 85, 0.2)");
                    badge.getStyle().set("color", "#F6AD55");
                    badge.getStyle().set("border", "1px solid rgba(246, 173, 85, 0.4)");
                    break;
                default:
                    badge.getStyle().set("background", "rgba(255, 255, 255, 0.1)");
                    badge.getStyle().set("color", "var(--skillpath-text-secondary)");
                    badge.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.2)");
                    break;
            }
            return badge;
        }).setHeader("Proficiency Level").setSortable(true).setFlexGrow(2);

        skillsGrid.addColumn(studentSkill -> studentSkill.getLastUpdated().toString())
                .setHeader("Last Updated")
                .setSortable(true)
                .setFlexGrow(1);

        skillsGrid.addComponentColumn(studentSkill -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addClassName("btn-secondary-glass");
            editBtn.getStyle().set("padding", "0.2rem 0.5rem");
            editBtn.addClickListener(e -> openEditProficiencyDialog(studentSkill));

            Button delBtn = new Button(VaadinIcon.TRASH.create());
            delBtn.getStyle().set("color", "#FC8181");
            delBtn.addClassName("btn-secondary-glass");
            delBtn.getStyle().set("padding", "0.2rem 0.5rem");
            delBtn.addClickListener(e -> openDeleteConfirmationDialog(studentSkill));

            actions.add(editBtn, delBtn);
            return actions;
        }).setHeader("Actions").setFlexGrow(1);

        skillsGrid.setAllRowsVisible(true);

        card.addContent(controls, skillsGrid);
        add(card);
    }

    private void loadSkills() {
        allStudentSkills = skillService.getStudentSkills(currentStudent.getId());
        totalSkillsBadge.setText(allStudentSkills.size() + " Skills Active");
        filterGrid();
    }

    private void filterGrid() {
        String query = searchField.getValue() != null ? searchField.getValue().trim().toLowerCase() : "";
        String category = categoryFilter.getValue();

        List<StudentSkill> filtered = allStudentSkills.stream()
                .filter(ss -> {
                    boolean matchesQuery = query.isEmpty() ||
                            ss.getSkill().getName().toLowerCase().contains(query) ||
                            (ss.getSkill().getCategory() != null && ss.getSkill().getCategory().toLowerCase().contains(query));

                    boolean matchesCategory = category == null || category.equals("All Categories") ||
                            category.equalsIgnoreCase(ss.getSkill().getCategory());

                    return matchesQuery && matchesCategory;
                })
                .collect(Collectors.toList());

        skillsGrid.setItems(filtered);
    }

    private void openAddSkillDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add Skill to Your Profile");

        ComboBox<Skill> skillCombo = new ComboBox<>("Select Skill");
        List<Skill> availableSkills = skillService.getAllSkills();
        skillCombo.setItems(availableSkills);
        skillCombo.setItemLabelGenerator(s -> s.getName() + " (" + s.getCategory() + ")");
        skillCombo.setWidthFull();

        Select<ProficiencyLevel> levelSelect = new Select<>();
        levelSelect.setLabel("Your Proficiency Level");
        levelSelect.setItems(ProficiencyLevel.values());
        levelSelect.setValue(ProficiencyLevel.INTERMEDIATE);
        levelSelect.setItemLabelGenerator(p -> p.getLabel() + " (Score " + p.getValue() + "/5)");
        levelSelect.setWidthFull();

        VerticalLayout form = new VerticalLayout(skillCombo, levelSelect);
        form.setPadding(false);
        form.setSpacing(true);
        dialog.add(form);

        Button saveBtn = new Button("Add to Portfolio", e -> {
            Skill selected = skillCombo.getValue();
            if (selected == null) {
                Notification.show("Please select a skill from the list.", 3000, Notification.Position.TOP_CENTER);
                return;
            }
            try {
                skillService.addOrUpdateStudentSkill(currentStudent.getId(), selected.getId(), levelSelect.getValue());
                loadSkills();
                dialog.close();
                Notification n = Notification.show("Skill added successfully!", 3000, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error adding skill: " + ex.getMessage(), 3500, Notification.Position.TOP_CENTER);
            }
        });
        saveBtn.addClassName("btn-primary-glass");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("btn-secondary-glass");

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private void openEditProficiencyDialog(StudentSkill studentSkill) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Update Proficiency: " + studentSkill.getSkill().getName());

        Select<ProficiencyLevel> levelSelect = new Select<>();
        levelSelect.setLabel("Updated Proficiency Level");
        levelSelect.setItems(ProficiencyLevel.values());
        levelSelect.setValue(studentSkill.getProficiency());
        levelSelect.setItemLabelGenerator(p -> p.getLabel() + " (Score " + p.getValue() + "/5)");
        levelSelect.setWidthFull();

        dialog.add(new VerticalLayout(levelSelect));

        Button updateBtn = new Button("Update Level", e -> {
            try {
                skillService.addOrUpdateStudentSkill(currentStudent.getId(), studentSkill.getSkill().getId(), levelSelect.getValue());
                loadSkills();
                dialog.close();
                Notification n = Notification.show("Proficiency level updated!", 3000, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error updating: " + ex.getMessage(), 3500, Notification.Position.TOP_CENTER);
            }
        });
        updateBtn.addClassName("btn-primary-glass");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("btn-secondary-glass");

        dialog.getFooter().add(cancelBtn, updateBtn);
        dialog.open();
    }

    private void openDeleteConfirmationDialog(StudentSkill studentSkill) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Remove Skill from Portfolio");

        Paragraph confirm = new Paragraph("Are you sure you want to remove '" + studentSkill.getSkill().getName() + "' from your skill portfolio? This will adjust your career readiness score.");
        dialog.add(confirm);

        Button deleteBtn = new Button("Delete", e -> {
            skillService.removeStudentSkill(currentStudent.getId(), studentSkill.getSkill().getId());
            loadSkills();
            dialog.close();
            Notification n = Notification.show("Skill removed from profile.", 3000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("btn-secondary-glass");

        dialog.getFooter().add(cancelBtn, deleteBtn);
        dialog.open();
    }

    private void openCreateCustomSkillDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create New Custom Technical Skill");

        TextField nameField = new TextField("Skill Name");
        nameField.setRequired(true);
        nameField.setPlaceholder("e.g. GraphQL, Kubernetes, Rust");
        nameField.setWidthFull();

        TextField categoryField = new TextField("Category");
        categoryField.setPlaceholder("e.g. Framework, Cloud, Language");
        categoryField.setWidthFull();

        TextArea descArea = new TextArea("Description");
        descArea.setPlaceholder("Describe key concepts, tools, or domain relevance...");
        descArea.setWidthFull();

        VerticalLayout form = new VerticalLayout(nameField, categoryField, descArea);
        form.setPadding(false);
        form.setSpacing(true);
        dialog.add(form);

        Button createBtn = new Button("Create Skill", e -> {
            try {
                Skill created = skillService.createCustomSkill(
                        nameField.getValue(),
                        categoryField.getValue(),
                        descArea.getValue()
                );
                dialog.close();
                Notification n = Notification.show("Custom skill '" + created.getName() + "' created successfully!", 3500, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (SkillPathException spe) {
                Notification n = Notification.show(spe.getMessage(), 3500, Notification.Position.TOP_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3500, Notification.Position.TOP_CENTER);
            }
        });
        createBtn.addClassName("btn-primary-glass");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("btn-secondary-glass");

        dialog.getFooter().add(cancelBtn, createBtn);
        dialog.open();
    }
}
