package be.doji.productivity.trambucore;

import be.doji.productivity.trambucore.testutil.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TrambuTest {

    private static final String DATA_TEST_ONE_TASK_TXT = "data/testOneTask.txt";

    protected Path createTempFile() throws IOException {
        Path directoryPath = Paths.get(FileUtils.getTestPath(DATA_TEST_ONE_TASK_TXT, this.getClass().getClassLoader()))
                .getParent();
        return Files.createTempFile(directoryPath, "temp", "txt");
    }
}
