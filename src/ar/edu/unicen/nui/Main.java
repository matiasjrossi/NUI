package ar.edu.unicen.nui;

import ar.edu.unicen.nui.controller.Controller;
import ar.edu.unicen.nui.model.Model;
import ar.edu.unicen.nui.views.gl.GLViewFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    private static int FRAMES_PER_SECOND = 24;
    
    public static void main(String[] args) {

        setupLoggingFormat();
        
        Model model = new Model();
        Controller controller = new Controller(model);
        GLViewFactory.makeGLView(controller, FRAMES_PER_SECOND);
        controller.run();
        System.exit(0);
    }

    private static void setupLoggingFormat() {
        for (Handler h : Logger.getLogger(Main.class.getName()).getParent().getHandlers()) {
            h.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format("[%s] %s %s: %s\r\n", (new SimpleDateFormat("MM/dd/yyyy h:mm:ss a")).format(new Date()), record.getLoggerName(), record.getLevel(), record.getMessage());
                }
            });
        }
    }
}