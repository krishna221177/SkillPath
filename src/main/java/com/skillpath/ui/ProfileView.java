package com.skillpath.ui;

import com.skillpath.exception.SkillPathException;
import com.skillpath.model.CareerRole;
import com.skillpath.model.Student;
import com.skillpath.service.CareerRoleService;
import com.skillpath.service.StudentService;
import com.skillpath.ui.components.GlassCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Student Profile • SkillPath")
public class ProfileView extends VerticalLayout {

    private final StudentService studentService;
    private final CareerRoleService careerRoleService;

    private final TextField nameField = new TextField("Full Name");
    private final TextField emailField = new TextField("Email Address");
    private final TextField collegeField = new TextField("College / University");
    private final TextField departmentField = new TextField("Department / Major");
    private final IntegerField semesterField = new IntegerField("Current Semester");
    private final ComboBox<CareerRole> targetCareerCombo = new ComboBox<>("Target Career Role");

    private Student currentStudent;

    public ProfileView(StudentService studentService, CareerRoleService careerRoleService) {
        this.studentService = studentService;
        this.careerRoleService = careerRoleService;

        setPadding(true);
        setSpacing(true);
        setWidthFull();
        getStyle().set("max-width", "900px");
        getStyle().set("margin", "0 auto");
        getStyle().set("padding", "2rem 1.5rem");

        createUI();
        loadStudentData();
    }

    private void createUI() {
        H2 pageTitle = new H2("Student Profile Management");
        pageTitle.getStyle().set("margin", "0 0 0.25rem 0");

        Paragraph pageDesc = new Paragraph("View and update your academic details and career aspiration. Data is stored persistently in the local H2 database.");
        pageDesc.getStyle().set("color", "var(--skillpath-text-secondary)");
        pageDesc.getStyle().set("margin", "0 0 1.5rem 0");

        add(pageTitle, pageDesc);

        GlassCard card = new GlassCard("Personal & Academic Details", "All fields are validated and saved directly to the database.");

        nameField.setRequired(true);
        nameField.setPlaceholder("e.g. Alex Morgan");
        nameField.setWidthFull();

        emailField.setRequired(true);
        emailField.setPlaceholder("e.g. alex.morgan@university.edu");
        emailField.setWidthFull();

        collegeField.setRequired(true);
        collegeField.setPlaceholder("e.g. Apex Institute of Technology");
        collegeField.setWidthFull();

        departmentField.setRequired(true);
        departmentField.setPlaceholder("e.g. Computer Science & Engineering");
        departmentField.setWidthFull();

        semesterField.setRequired(true);
        semesterField.setMin(1);
        semesterField.setMax(12);
        semesterField.setStepButtonsVisible(true);
        semesterField.setWidthFull();

        List<CareerRole> roles = careerRoleService.getAllRoles();
        targetCareerCombo.setItems(roles);
        targetCareerCombo.setItemLabelGenerator(CareerRole::getName);
        targetCareerCombo.setPlaceholder("Select desired career track...");
        targetCareerCombo.setWidthFull();

        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, emailField, collegeField, departmentField, semesterField, targetCareerCombo);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        Button saveBtn = new Button("Save Profile Changes", VaadinIcon.CHECK.create());
        saveBtn.addClassName("btn-primary-glass");
        saveBtn.addClickListener(e -> saveProfile());

        Button resetBtn = new Button("Revert", VaadinIcon.ROTATE_LEFT.create());
        resetBtn.addClassName("btn-secondary-glass");
        resetBtn.addClickListener(e -> loadStudentData());

        HorizontalLayout btnRow = new HorizontalLayout(saveBtn, resetBtn);
        btnRow.setSpacing(true);
        btnRow.getStyle().set("margin-top", "1rem");

        card.addContent(formLayout, btnRow);
        add(card);
    }

    private void loadStudentData() {
        currentStudent = studentService.getOrCreateDefaultStudent();
        nameField.setValue(currentStudent.getName() != null ? currentStudent.getName() : "");
        emailField.setValue(currentStudent.getEmail() != null ? currentStudent.getEmail() : "");
        collegeField.setValue(currentStudent.getCollegeName() != null ? currentStudent.getCollegeName() : "");
        departmentField.setValue(currentStudent.getDepartment() != null ? currentStudent.getDepartment() : "");
        semesterField.setValue(currentStudent.getSemester() != null ? currentStudent.getSemester() : 1);
        targetCareerCombo.setValue(currentStudent.getTargetCareer());
    }

    private void saveProfile() {
        try {
            currentStudent.setName(nameField.getValue());
            currentStudent.setEmail(emailField.getValue());
            currentStudent.setCollegeName(collegeField.getValue());
            currentStudent.setDepartment(departmentField.getValue());
            currentStudent.setSemester(semesterField.getValue() != null ? semesterField.getValue() : 1);
            currentStudent.setTargetCareer(targetCareerCombo.getValue());

            studentService.updateStudentProfile(currentStudent);

            Notification notification = Notification.show("Student profile successfully saved!", 3500, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Refresh header navbar
            if (getUI().isPresent()) {
                getUI().get().getPage().reload();
            }
        } catch (SkillPathException spe) {
            Notification notification = Notification.show(spe.getMessage(), 4000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Notification notification = Notification.show("Error saving profile: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
