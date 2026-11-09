package com.skillpath.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class GlassCard extends Div {

    private final VerticalLayout contentLayout;

    public GlassCard() {
        addClassName("glass-panel");
        getStyle().set("padding", "1.5rem");
        getStyle().set("box-sizing", "border-box");

        contentLayout = new VerticalLayout();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(true);
        contentLayout.setWidthFull();
        add(contentLayout);
    }

    public GlassCard(String title) {
        this();
        if (title != null && !title.isEmpty()) {
            H3 heading = new H3(title);
            heading.getStyle().set("margin", "0 0 0.5rem 0");
            heading.getStyle().set("color", "var(--skillpath-text-primary)");
            heading.getStyle().set("font-weight", "600");
            contentLayout.add(heading);
        }
    }

    public GlassCard(String title, String subtitle) {
        this(title);
        if (subtitle != null && !subtitle.isEmpty()) {
            Paragraph sub = new Paragraph(subtitle);
            sub.getStyle().set("margin", "0 0 1rem 0");
            sub.getStyle().set("color", "var(--skillpath-text-secondary)");
            sub.getStyle().set("font-size", "0.9rem");
            contentLayout.add(sub);
        }
    }

    public void addContent(Component... components) {
        contentLayout.add(components);
    }

    public VerticalLayout getContentLayout() {
        return contentLayout;
    }
}
