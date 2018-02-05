package be.doji.productivity.trambuapp;

import be.doji.productivity.trambuapp.components.view.ActivityPageView;
import be.doji.productivity.trambuapp.styles.DefaultTrambuStyle;
import java.io.IOException;
import kotlin.jvm.JvmClassMappingKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.App;

/**
 * @author Doji
 */
public class TrambuApplication extends App {

  private static final Logger LOG = LoggerFactory.getLogger(TrambuApplication.class);

  public TrambuApplication() throws IOException {
    super(JvmClassMappingKt.getKotlinClass(ActivityPageView.class),
        JvmClassMappingKt.getKotlinClass(DefaultTrambuStyle.class));
  }

  public static void main(String[] args) {
    LOG.info("Starting application");
    App.launch(TrambuApplication.class);
  }
}
