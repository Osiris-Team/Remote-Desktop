package com.osiris.remotedesktop.views;

import com.osiris.remotedesktop.User;
import com.osiris.events.Action;
import com.osiris.events.Event;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.shared.communication.PushMode;
import org.apache.commons.codec.binary.Base64;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@PageTitle("Remote-Desktop")
@Route(value = "")
public class MainView2 extends VerticalLayout {
    public static final Event<List<org.bytedeco.javacv.Frame>> onScreenshot = new Event<>();
    public static final Event<ClickEvent<?>> onClick = new Event<>();
    public static final Event<Integer> onFPSChange = new Event<>();
    public static int msSleepBetweenFrames = 10;
    public static ExecutorService exec = Executors.newCachedThreadPool();

    static class Display {
        public final GraphicsDevice screen;
        public final Rectangle screenBounds;
        public final FrameGrabber frameGrabber;

        public Display(GraphicsDevice screen, Rectangle screenBounds, FrameGrabber frameGrabber) {
            this.screen = screen;
            this.screenBounds = screenBounds;
            this.frameGrabber = frameGrabber;
        }
    }

    static{
        /*
        Screen recorder
         */
        System.setProperty("java.awt.headless", "false");
        new Thread(() -> {
            Thread.currentThread().setName("Screen-Recorder-Thread");

            try {
                // Get all screens
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] screens = ge.getScreenDevices();
                List<Display> displays = new ArrayList<>();
                int i = 1;
                for (GraphicsDevice screen : screens) {
                    System.out.println("Initialising recorder for screen "+i+" \""+screen.getIDstring()+"\"...");
                    Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
                    FrameGrabber grabber = FFmpegFrameGrabber.createDefault("desktop");
                    grabber.setFormat("gdigrab");
                    grabber.setFrameRate(30);
                    grabber.start();
                    displays.add(new Display(screen, screenBounds, grabber));
                    i++;
                    System.out.println("Done!");
                    break;
                }

                while (true) {
                    List<org.bytedeco.javacv.Frame> screenshots = new ArrayList<>();

                    for (Display t : displays) {
                        org.bytedeco.javacv.Frame screenshot = t.frameGrabber.grab();
                        screenshots.add(screenshot);
                    }

                    try{
                        exec.execute(() -> {
                            onScreenshot.execute(screenshots);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(msSleepBetweenFrames);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        /*
        FPS counter
         */
        Thread.startVirtualThread(() -> {
           try{
               AtomicInteger counter = new AtomicInteger();
               onScreenshot.addAction(img -> {
                   counter.incrementAndGet();
               });
               while(true){
                   Thread.sleep(1000);
                   onFPSChange.execute(counter.getAndSet(0));
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
        });
    }

    UI ui = UI.getCurrent();

    ExpandingOverlay expandingOverlay = new ExpandingOverlay();

    /*
    Compression
     */
    Consumer<Double> setCompression = percent -> {
        // 0 = high compression, 1 = high quality
        float f = (float)(percent / 100.0);
        float quality = Math.abs(1 - f);

        ImageIO.getImageWritersByFormatName("jpg").forEachRemaining(writer -> {
            // Set compression parameters
            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(quality);
            System.out.println("Changed image compression to "+percent+"% meaning quality to "+quality+".");
        });
    };
    public NumberField compression = new NumberField("Compression", 0.0, e -> {
        setCompression.accept(e.getValue());
    });
    {
        setCompression.accept(compression.getValue());
        compression.setMin(0);
        compression.setMax(100);
        compression.setStep(10.0);
        Tooltip tooltip = Tooltip.forComponent(compression);
        tooltip.setText("Image compression in %. Compression is disabled by default, due to its high impact on the CPU." +
                " Note that the drawback of this approach is high network usage.");
    }

    /*
    Display
     */
    Consumer<Integer> setDisplay = id -> {
        expandingOverlay.diplay = id;
        System.out.println("Showing display "+id+".");
    };
    public NumberField display = new NumberField("Display", 0.0, e -> {
        setDisplay.accept(e.getValue().intValue());
    });
    {
        setDisplay.accept(display.getValue().intValue());
        display.setMin(0);
        display.setStep(1.0);
        Tooltip tooltip = Tooltip.forComponent(display);
        tooltip.setText("Select the display to show.");
    }

    /*
    FPS
    */
    Consumer<Integer> setFPS = fps -> {
        // Example 10frames/1second = 10fps
        // How many ms do we sleep between each frame, to achieve the above fps?
        msSleepBetweenFrames = 1000 / fps;
        System.out.println("Set FPS to "+ fps+" and msSleepBetweenFrames to "+msSleepBetweenFrames+"ms.");
    };
    public NumberField fps = new NumberField("FPS (-)", 30.0, e -> {
        setFPS.accept(e.getValue().intValue());

    });
    {
        setFPS.accept(fps.getValue().intValue());
        fps.setMin(1);
        fps.setStep(1.0);
        Tooltip tooltip = Tooltip.forComponent(fps);
        tooltip.setText("The maximum allowed FPS (Frames Per Second). Note that FPS are dictated by your hardware and network speed.");
        onFPSChange.addAction((action, fps_) -> {
            if(ui.isClosing()){
                action.remove();
                return;
            }
            ui.access(() -> {
                fps.setLabel("FPS ("+fps_+")");
            });
        }, Exception::printStackTrace);
    }



    public MainView2() {
        setSizeFull();
        setMargin(false);
        setPadding(false);


        ui.getPushConfiguration().setPushMode(PushMode.MANUAL);

        /*
        Auth
         */
        if(getCurrentUser().isDenied()){
            LoginView loginView = new LoginView();
            loginView.addLoginListener(e -> {
                if(getCurrentUser().login(e.getUsername(), e.getPassword())){
                    loginView.setError(false);
                    ui.getPage().reload();
                }
                else
                    loginView.setError(true);
            });
            loginView.setOpened(true);
            return;
        }

        /*
        Layout
         */
        add(expandingOverlay);
        HorizontalLayout menu = expandingOverlay.content;
        menu.add(compression, display, fps);

        VerticalLayout imageContainer = new VerticalLayout();
        add(imageContainer);
        imageContainer.setMargin(false);
        imageContainer.setPadding(false);
        imageContainer.setSpacing(false);
        imageContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        imageContainer.setSizeFull();
        imageContainer.getStyle().set("overflow", "hidden");
        imageContainer.setClassName("image-container");
        imageContainer.addClickListener(onClick::execute);


        /*
        Server-side
         */
        Java2DFrameConverter converter = new Java2DFrameConverter();
        Action<List<org.bytedeco.javacv.Frame>> actionOnScreenshot = onScreenshot.addAction((action, screenshots) -> {
            if (ui.isClosing()) {
                action.remove();
                return;
            }

            try {
                org.bytedeco.javacv.Frame screenshot = screenshots.get(expandingOverlay.diplay);
                try {

                    if (screenshot.image != null) {

                        long msLast = System.currentTimeMillis();
                        BufferedImage buf = converter.convert(screenshot);
                        System.out.println(System.currentTimeMillis() - msLast+"ms for conversion to BufferedImage");

                        if (buf != null) {

                            msLast = System.currentTimeMillis();
                            byte[] compressedScreenshot = compressImage(buf);
                            System.out.println(System.currentTimeMillis() - msLast+"ms for compression to byte[]");

                            ui.access(() -> {
                                long msLast1 = System.currentTimeMillis();
                                ui.getPage().executeJs("let imageContainer = document.querySelector('.image-container')\n" +
                                        "let img = document.createElement(\"img\")\n" +
                                        "img.style.height = \"100%\"\n" +
                                        "img.src = `data:image/jpg;base64," + Base64.encodeBase64String(compressedScreenshot) + "`\n" +
                                        "imageContainer.appendChild(img)\n");
                                ui.push();
                                System.out.println(System.currentTimeMillis() - msLast1+"ms for sending to UI");
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to compress image! " + e.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                imageContainer.add("Failed to retrieve image! " + e.getMessage());
                return;
            }
        }, Exception::printStackTrace);

        onVisibilityChange.addAction(isVisible -> {
            if (isVisible) {
                synchronized (onScreenshot){
                    if(!onScreenshot.actions.contains(actionOnScreenshot)){
                        onScreenshot.addAction(actionOnScreenshot);
                        System.out.println("Tab is back in focus. Continued streaming.");
                    }
                }
            } else {
                synchronized (onScreenshot){
                    onScreenshot.actions.remove(actionOnScreenshot);
                    System.out.println("Tab is out of focus. Paused streaming.");
                }

            }
        });

        /*
        Client-side
        This complicated loop is necessary to display a smooth, non-flickering video.
         */
        ui.getPage().executeJs("""
                    if (typeof document.hidden == null) {
                      alert("Page Visibility API is not supported by this browser. This will result in your memory filling up when leaving this tab.")
                      return
                    }
                        document.addEventListener("visibilitychange", function () {
                            $1.$server.triggerPageVisibilityChange(!document.hidden)
                        });
                    
                    var imageContainer = document.querySelector('.image-container');
                    var maxChildren = 10; // Maximum number of children to display
                    var childrenToRemove = 5;
                    var currentIndex = 0;
                                            
                    function scrollImages() {
                        let images = imageContainer.children;
                        let img = images[currentIndex];
                        if (img == null){
                            //console.log('Client is too fast');
                            setTimeout(scrollImages, $0); // Retry after x ms
                            return
                        }
                        
                        img.scrollIntoView({ behavior: 'instant', block: 'nearest', inline: 'nearest' });
                        currentIndex++;
                            
                        if (images.length > maxChildren) {
                            // Remove images that exceed the maximum limit and adjust currentIndex
                            for (var i = 0; i < childrenToRemove; i++) {
                                images[0].remove();
                                currentIndex--;
                            }
                        }
                        setTimeout(scrollImages, $0); // Loop
                    }
                                            
                    setInterval(scrollImages, $0); // Scroll images every x ms
                """, msSleepBetweenFrames, this);

    }

    Event<Boolean> onVisibilityChange = new Event<>();
    @ClientCallable
    public void triggerPageVisibilityChange(boolean isVisible){
        onVisibilityChange.execute(isVisible);
    }

    public static User getCurrentUser(){
        User user = UI.getCurrent().getSession().getAttribute(User.class);
        if(user == null) {
            user = new User("", "");
            UI.getCurrent().getSession().setAttribute(User.class, user);
        }
        return user;
    }

    // Function to compress an image using a specified compression quality
    public static byte[] compressImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos); // Using JPEG format for compression
        baos.flush();

        byte[] byteArray = baos.toByteArray();
        baos.close();

        return byteArray;
    }

}
