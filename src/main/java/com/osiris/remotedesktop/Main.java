package com.osiris.remotedesktop;

import com.github.mvysny.vaadinboot.VaadinBoot;
import com.osiris.remotedesktop.views.MainView2;
import org.jetbrains.annotations.NotNull;

/**
 * Run {@link #main(String[])} to launch your app in Embedded Jetty.
 * @author mavi
 */
public final class Main {
    public static void main(@NotNull String[] args) throws Exception {
        try{
            // Init statics before starting otherwise VaadinBoot somehow never launches completely
            Thread t = new Thread(() -> {
                MainView2.frameToJPG(null);
            });
            t.start();
            while (!t.isAlive()) {
                System.out.println("Loading...");
                Thread.sleep(1000);
            }
        } catch (Exception e) {}
        new VaadinBoot().run();
    }
}
