package be.doji.productivity.trambuapp;

import be.doji.productivity.trambuapp.styles.DefaultTrambuStyle;
import be.doji.productivity.trambuapp.views.ActivityOverview;
import kotlin.jvm.JvmClassMappingKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.App;

import java.io.IOException;

/**
 * @author Doji
 */
public class TrambuApplication extends App {

    private static final Logger LOG = LoggerFactory.getLogger(TrambuApplication.class);

    public TrambuApplication() throws IOException {
        super(JvmClassMappingKt.getKotlinClass(ActivityOverview.class),
                JvmClassMappingKt.getKotlinClass(DefaultTrambuStyle.class));
    }

    public static void main(String[] args) {
        LOG.info("Starting application");
        App.launch(TrambuApplication.class);
    }
}
