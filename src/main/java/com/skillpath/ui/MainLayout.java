package com.skillpath.ui;

import com.skillpath.model.Student;
import com.skillpath.service.StudentService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    private final StudentService studentService;
    private final Span studentBadge = new Span();
    private final Span careerBadge = new Span();

    public MainLayout(StudentService studentService) {
        this.studentService = studentService;
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
        refreshStudentInfo();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", "var(--skillpath-text-primary)");
        toggle.getStyle().set("cursor", "pointer");

        H2 pageBrand = new H2("SkillPath");
        pageBrand.getStyle().set("font-size", "1.25rem");
        pageBrand.getStyle().set("font-weight", "700");
        pageBrand.getStyle().set("margin", "0");
        pageBrand.getStyle().set("background", "linear-gradient(90deg, #648CFF, #55D6E8)");
        pageBrand.getStyle().set("-webkit-background-clip", "text");
        pageBrand.getStyle().set("-webkit-text-fill-color", "transparent");

        Span subtitle = new Span("Student Skill-Gap & Career Roadmap");
        subtitle.getStyle().set("color", "var(--skillpath-text-muted)");
        subtitle.getStyle().set("font-size", "0.8rem");
        subtitle.getStyle().set("margin-left", "0.5rem");

        HorizontalLayout brandLayout = new HorizontalLayout(pageBrand, subtitle);
        brandLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        studentBadge.getStyle().set("color", "var(--skillpath-text-primary)");
        studentBadge.getStyle().set("font-weight", "600");
        studentBadge.getStyle().set("font-size", "0.88rem");

        careerBadge.getStyle().set("background", "rgba(100, 140, 255, 0.15)");
        careerBadge.getStyle().set("color", "var(--skillpath-accent-blue)");
        careerBadge.getStyle().set("border", "1px solid rgba(100, 140, 255, 0.3)");
        careerBadge.getStyle().set("padding", "0.2rem 0.6rem");
        careerBadge.getStyle().set("border-radius", "8px");
        careerBadge.getStyle().set("font-size", "0.8rem");
        careerBadge.getStyle().set("font-weight", "600");

        HorizontalLayout userLayout = new HorizontalLayout(studentBadge, careerBadge);
        userLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        userLayout.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(toggle, brandLayout, userLayout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassName("glass-header");
        header.getStyle().set("padding", "0.75rem 1.5rem");
        header.getStyle().set("box-sizing", "border-box");
        header.expand(brandLayout);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);
        drawerContent.setSizeFull();
        drawerContent.addClassName("glass-sidebar");

        // Brand logo banner
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setPadding(true);
        logoSection.setSpacing(true);
        logoSection.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        logoSection.getStyle().set("padding", "1.5rem 1.25rem 1.25rem 1.25rem");

        Icon logoIcon = VaadinIcon.ACADEMY_CAP.create();
        logoIcon.setSize("28px");
        logoIcon.setColor("#648CFF");

        VerticalLayout titleBox = new VerticalLayout();
        titleBox.setPadding(false);
        titleBox.setSpacing(false);

        H1 appTitle = new H1("SkillPath");
        appTitle.getStyle().set("font-size", "1.3rem");
        appTitle.getStyle().set("font-weight", "800");
        appTitle.getStyle().set("margin", "0");
        appTitle.getStyle().set("color", "#FFFFFF");

        Span appSub = new Span("CAREER NAVIGATOR");
        appSub.getStyle().set("font-size", "0.65rem");
        appSub.getStyle().set("font-weight", "700");
        appSub.getStyle().set("letter-spacing", "0.1em");
        appSub.getStyle().set("color", "#55D6E8");

        titleBox.add(appTitle, appSub);
        logoSection.add(logoIcon, titleBox);

        // Navigation Links
        VerticalLayout navList = new VerticalLayout();
        navList.setPadding(false);
        navList.setSpacing(false);
        navList.setWidthFull();

        navList.add(createNavLink("Landing Page", VaadinIcon.HOME, HomeView.class));
        navList.add(createNavLink("Dashboard", VaadinIcon.DASHBOARD, DashboardView.class));
        navList.add(createNavLink("My Profile", VaadinIcon.USER, ProfileView.class));
        navList.add(createNavLink("My Skills", VaadinIcon.SPECIALIST, SkillsView.class));
        navList.add(createNavLink("Career Roles", VaadinIcon.BRIEFCASE, CareerRolesView.class));
        navList.add(createNavLink("Skill-Gap Analysis", VaadinIcon.CHART_LINE, SkillGapView.class));
        navList.add(createNavLink("Learning Roadmap", VaadinIcon.MAP_MARKER, RoadmapView.class));
        navList.add(createNavLink("Progress Tracking", VaadinIcon.CHECK_SQUARE_O, ProgressView.class));

        Scroller scroller = new Scroller(navList);
        scroller.setSizeFull();

        // Footer in drawer
        Div footer = new Div();
        footer.getStyle().set("padding", "1rem 1.25rem");
        footer.getStyle().set("border-top", "1px solid rgba(255, 255, 255, 0.08)");
        footer.getStyle().set("font-size", "0.75rem");
        footer.getStyle().set("color", "var(--skillpath-text-muted)");
        footer.setText("SkillPath v1.0 • H2 File DB");

        drawerContent.add(logoSection, scroller, footer);
        addToDrawer(drawerContent);
    }

    private RouterLink createNavLink(String title, VaadinIcon iconType, Class<? extends com.vaadin.flow.component.Component> target) {
        RouterLink link = new RouterLink();
        link.setRoute(target);
        link.addClassName("nav-link-glass");

        Icon icon = iconType.create();
        icon.setSize("18px");

        Span text = new Span(title);
        link.add(icon, text);
        return link;
    }

    public void refreshStudentInfo() {
        try {
            Student student = studentService.getOrCreateDefaultStudent();
            studentBadge.setText(student.getName());
            if (student.getTargetCareer() != null) {
                careerBadge.setText("🎯 " + student.getTargetCareer().getName());
                careerBadge.setVisible(true);
            } else {
                careerBadge.setText("🎯 No Role Selected");
                careerBadge.setVisible(true);
            }
        } catch (Exception ignored) {
        }
    }
}
