package com.osiris.remotedesktop.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@CssImport("./move-button.css")
public class MoveScreenButton extends Button {
    public AtomicBoolean isMoveModeActive = new AtomicBoolean(false);
    public static AtomicLong idCounter = new AtomicLong();
    public final String id = "move-screen-button"+idCounter.getAndIncrement();
    public volatile int left = 0;
    public volatile int top = 0;
    public int neededImgLeft = 0; // Needed to make sure the main screen is at the top left of browser window
    public int neededImgTop = 0; // Needed to make sure the main screen is at the top left of browser window
    public volatile double scale = 1.0;
    public volatile double width = 0;
    public volatile double height = 0;


    public MoveScreenButton(Component imageContainer) {
        super(VaadinIcon.ARROWS.create());
        setId(id);
        imageContainer.setId("image-container");
        MainView2.onScreenshot.addOneTimeAction(screenshot -> {
            // Initial sizes from first screenshot
            width = screenshot.get(0).imageWidth;
            height = screenshot.get(0).imageHeight;
        });
        for (MainView2.Display d : MainView2.displays) {
            if(d.screenBounds.x < 0) neededImgLeft += d.screenBounds.x;
            if(d.screenBounds.y < 0) neededImgTop += d.screenBounds.y;
        }
        imageContainer.getStyle().set("left", neededImgLeft+"px");
        imageContainer.getStyle().set("top", neededImgTop+"px");

        addClickListener(e -> {
            isMoveModeActive.set(!isMoveModeActive.get());
            updateLabel();
        });
        updateLabel();

        AtomicInteger i = new AtomicInteger(1);

        imageContainer.addAttachListener(e -> {
            if(!e.isInitialAttach()) return;
            synchronized (i){
                i.incrementAndGet();
                if(i.get() == 2) init(imageContainer);
            }
        });

        addAttachListener(e -> {
            if(!e.isInitialAttach()) return;
            synchronized (i){
                i.incrementAndGet();
                if(i.get() == 2) init(imageContainer);
            }
        });

        UI.getCurrent().getElement().addEventListener("move-mode-closed", e -> {
            System.out.println(e.getEventData().toJson());
            left = (int) e.getEventData().get("event.left").asNumber();
            top = (int) e.getEventData().get("event.top").asNumber();
            scale =  e.getEventData().get("event.zoomLevel").asNumber();
        }).addEventData("event.left").addEventData("event.top").addEventData("event.zoomLevel");
    }

    public void init(Component imageContainer){
        UI.getCurrent().getPage().executeJs("" +
                "let isMoveMode = false;\n" +
                "        let isDragging = false;\n" +
                "        let offsetX, offsetY;\n" +
                "        let zoomLevel = 1;\n" +
                "\n" +
                "        const enterMoveModeButton = $0;\n" +
                "        const imageContainer = $1;\n" +
                "\n" +
                "        enterMoveModeButton.addEventListener('click', () => {\n" +
                "            isMoveMode = !isMoveMode;\n" +
                "            if (isMoveMode) {\n" +
                "                //enterMoveModeButton.innerText = 'Exit Move-Mode';\n" +
                "                enableMoveMode();\n" +
                "            } else {\n" +
                "                //enterMoveModeButton.innerText = 'Enter Move-Mode';\n" +
                "                disableMoveMode();\n" +
                "                dispatchMoveModeClosedEvent();\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        function dispatchMoveModeClosedEvent() {\n" +
                "            const moveModeClosedEvent = new CustomEvent('move-mode-closed', {\n" +
                "                detail: {\n" +
                "                    left: imageContainer.style.left,\n" +
                "                    top: imageContainer.style.top,\n" +
                "                    zoomLevel: zoomLevel,\n" +
                "                    width: imageContainer.clientWidth,\n" +
                "                    height: imageContainer.clientHeight\n" +
                "                }\n" +
                "            });\n" +
                "            document.dispatchEvent(moveModeClosedEvent);\n" +
                "        }\n" +
                "\n" +
                "        function enableMoveMode() {\n" +
                "            imageContainer.addEventListener('mousedown', startDrag);\n" +
                "            imageContainer.addEventListener('mouseup', endDrag);\n" +
                "            document.addEventListener('mousemove', throttledMove);\n" +
                "            imageContainer.addEventListener('wheel', handleZoom);\n" +
                "        }\n" +
                "\n" +
                "        function disableMoveMode() {\n" +
                "            imageContainer.removeEventListener('mousedown', startDrag);\n" +
                "            imageContainer.removeEventListener('mouseup', endDrag);\n" +
                "            document.removeEventListener('mousemove', throttledMove);\n" +
                "            imageContainer.removeEventListener('wheel', handleZoom);\n" +
                "        }\n" +
                "\n" +
                "        function startDrag(e) {\n" +
                "            isDragging = true;\n" +
                "            offsetX = e.clientX - imageContainer.getBoundingClientRect().left;\n" +
                "            offsetY = e.clientY - imageContainer.getBoundingClientRect().top;\n" +
                "        }\n" +
                "\n" +
                "        function endDrag() {\n" +
                "            isDragging = false;\n" +
                "        }\n" +
                "\n" +
                "        let throttleTimer;\n" +
                "        const throttleDelay = 16; // 60 fps\n" +
                "\n" +
                "        function throttledMove(e) {\n" +
                "            if (!isDragging) return;\n" +
                "\n" +
                "            if (!throttleTimer) {\n" +
                "                throttleTimer = setTimeout(() => {\n" +
                "                    throttleTimer = null;\n" +
                "                    moveImage(e);\n" +
                "                }, throttleDelay);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function moveImage(e) {\n" +
                "            const x = e.clientX - offsetX;\n" +
                "            const y = e.clientY - offsetY;\n" +
                "\n" +
                "            imageContainer.style.left = `${x}px`;\n" +
                "            imageContainer.style.top = `${y}px`;\n" +
                "        }\n" +
                "\n" +
                "        function handleZoom(e) {\n" +
                "            e.preventDefault();\n" +
                "\n" +
                "            const delta = e.deltaY || e.detail || e.wheelDelta;\n" +
                "\n" +
                "            if (delta > 0) {\n" +
                "                zoomOut();\n" +
                "            } else {\n" +
                "                zoomIn();\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function zoomIn() {\n" +
                "            zoomLevel += 0.1;\n" +
                "            applyZoom();\n" +
                "        }\n" +
                "\n" +
                "        function zoomOut() {\n" +
                "            zoomLevel -= 0.1;\n" +
                "            applyZoom();\n" +
                "        }\n" +
                "\n" +
                "        function applyZoom() {\n" +
                "            imageContainer.style.transform = `scale(${zoomLevel})`;\n" +
                "        }" +
                "", this, imageContainer);
    }

    public void updateLabel(){
        if(!isMoveModeActive.get()) {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            removeThemeVariants(ButtonVariant.LUMO_ERROR);
        } else{
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        }
    }
}
