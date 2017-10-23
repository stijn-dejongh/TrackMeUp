package be.doji.productivity.TrackMeUp;

import be.doji.productivity.TrackMeUp.presentation.cli.TrackMeCLI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication public class TrackMeUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackMeUpApplication.class, args);
    }

}
