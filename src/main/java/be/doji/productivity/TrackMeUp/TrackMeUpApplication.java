package be.doji.productivity.TrackMeUp;

import be.doji.productivity.TrackMeUp.presentation.webfront.UiApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication public class TrackMeUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}
