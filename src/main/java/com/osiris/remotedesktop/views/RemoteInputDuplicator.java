package com.osiris.remotedesktop.views;

import com.github.javaparser.utils.Pair;
import com.vaadin.flow.component.UI;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class RemoteInputDuplicator {
    public static class DisplayRobot{
        public MainView2.Display display;
        public Robot robot;

        public DisplayRobot(MainView2.Display display, Robot robot) {
            this.display = display;
            this.robot = robot;
        }
    }

    private ArrayList<DisplayRobot> robots = new ArrayList<>();

    public RemoteInputDuplicator(List<MainView2.Display> displays) {
        try {
            for (MainView2.Display display : displays) {
                robots.add(new DisplayRobot(display, new Robot(display.screen)));
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void register(UI ui, MoveScreenButton btn) {

        /*
        MOUSE
         */

        ui.getElement().addEventListener("mousedown", e -> {
            System.out.println(e.getEventData().toJson());
            double x = e.getEventData().get("event.clientX").asNumber();
            double y = e.getEventData().get("event.clientY").asNumber();
            double button = e.getEventData().get("event.button").asNumber();
            CorrectedXY c = getCorrectedXYRelativeToSingleScreen((int) x, (int) y, btn.left, btn.top, btn.scale, (int) btn.width, (int) btn.height);
            System.out.println("x="+c.x+" y="+c.y);
            //robot.mouseMove((int)x, (int)y);
            //robot.mousePress(1 << (10 + (int)button));
        }).addEventData("event.clientX").addEventData("event.clientY").addEventData("event.button");

        ui.getElement().addEventListener("mouseup", e -> {
            System.out.println(e.getEventData().toJson());
            double x = e.getEventData().get("event.clientX").asNumber();
            double y = e.getEventData().get("event.clientY").asNumber();
            double button = e.getEventData().get("event.button").asNumber();
            CorrectedXY c = getCorrectedXYRelativeToSingleScreen((int) x, (int) y, btn.left, btn.top, btn.scale, (int) btn.width, (int) btn.height);
            System.out.println("x="+c.x+" y="+c.y);
            //robot.mouseMove((int)x, (int)y);
            //robot.mouseRelease(1 << (10 + (int)button));
        }).addEventData("event.clientX").addEventData("event.clientY").addEventData("event.button");

        ui.getElement().addEventListener("mousemove", e -> {
            System.out.println(e.getEventData().toJson());
            double x = e.getEventData().get("event.clientX").asNumber();
            double y = e.getEventData().get("event.clientY").asNumber();
            CorrectedXY c = getCorrectedXYRelativeToSingleScreen((int) x, (int) y, btn.left, btn.top, btn.scale, (int) btn.width, (int) btn.height);
            System.out.println("x="+c.x+" y="+c.y);
            //robot.mouseMove((int)x, (int)y);
        }).addEventData("event.clientX").addEventData("event.clientY").debounce(250); // Update mouse pos 4 times in a second when it moves

        /*
        KEYBOARD
         */

        ui.getElement().addEventListener("keydown", e -> {
            System.out.println(e.getEventData().toJson());
            String key = e.getEventData().get("event.key").asString();
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(key.charAt(0));
            //robots.getFirst().keyPress(keyCode);
        }).addEventData("event.key");

        ui.getElement().addEventListener("keyup", e -> {
            System.out.println(e.getEventData().toJson());
            String key = e.getEventData().get("event.key").asString();
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(key.charAt(0));
            //robots.getFirst().keyRelease(keyCode);
        }).addEventData("event.key");
    }

    public static class CorrectedXY{
        public int x, y;
        public DisplayRobot displayRobot;

        public CorrectedXY(int x, int y, DisplayRobot displayRobot) {
            this.x = x;
            this.y = y;
            this.displayRobot = displayRobot;
        }
    }

    public CorrectedXY getCorrectedXYRelativeToSingleScreen(
            int x, int y, int movedLeft, int movedTop, double zoomScale,
            int imageWidth, int imageHeight) {

        // Calculate the offset due to the movement and scale
        int scaledX = (int) ((x - movedLeft) / zoomScale);
        int scaledY = (int) ((y - movedTop) / zoomScale);

        for (DisplayRobot robot : robots) {
            Rectangle screenBounds = robot.display.screenBounds;
            if (screenBounds.contains(scaledX, scaledY)) {
                // Calculate the corrected position relative to the single screen
                int correctedX = (int) ((scaledX - screenBounds.x) * (imageWidth / (double) screenBounds.width));
                int correctedY = (int) ((scaledY - screenBounds.y) * (imageHeight / (double) screenBounds.height));
                return new CorrectedXY(correctedX, correctedY, robot);
            }
        }

        throw new RuntimeException("Point scaledX="+scaledX+" scaledY="+scaledY+" is not in any screen.");
    }
}
