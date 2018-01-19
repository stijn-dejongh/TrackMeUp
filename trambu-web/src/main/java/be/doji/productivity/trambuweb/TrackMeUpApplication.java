package be.doji.productivity.trambuweb;

import be.doji.productivity.trambuweb.presentation.webfront.UiApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @deprecated Package is no longer maintained
 */
@Deprecated @SpringBootApplication public class TrackMeUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}
