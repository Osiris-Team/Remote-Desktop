package com.osiris.remotedesktop.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.shared.Registration;

public class ExpandingOverlay extends VerticalLayout {

    public int diplay = 0;
    public HorizontalLayout content = new HorizontalLayout();
    public Button toggleButton = new Button(new Icon(VaadinIcon.ARROW_DOWN));

    {
        Style overlayStyle = getElement().getStyle();
        overlayStyle.set("position", "fixed");
        overlayStyle.set("top", "0");
        overlayStyle.set("left", "0");
        overlayStyle.set("width", "100%");
        overlayStyle.set("display", "flex");
        overlayStyle.set("flex-direction", "column");
        overlayStyle.set("z-index", "1000");

        Style contentStyle = content.getElement().getStyle();
        contentStyle.set("background-color", "white");
        contentStyle.set("padding", "20px");
        contentStyle.set("border-radius", "5px");
        contentStyle.set("box-shadow", "0 0 10px rgba(0, 0, 0, 0.3)");

        Style buttonStyle = toggleButton.getElement().getStyle();
        buttonStyle.set("margin-top", "10px");
    }

    public ExpandingOverlay() {
        setPadding(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        content.setVisible(false); // Content is initially hidden

        toggleButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toggleButton.addClickListener(event -> toggleContent());

        addClassName("vaadin-expanding-overlay"); // Apply the CSS class

        add(toggleButton, content);
    }

    public void setContent(Component component) {
        content.removeAll();
        content.add(component);
    }

    public Registration addToggleListener(ComponentEventListener<ClickEvent<Button>> listener) {
        return toggleButton.addClickListener(listener);
    }

    private void toggleContent() {
        content.setVisible(!content.isVisible());
    }
}