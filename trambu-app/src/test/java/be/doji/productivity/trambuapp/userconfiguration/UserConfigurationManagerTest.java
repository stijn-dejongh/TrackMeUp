package be.doji.productivity.trambuapp.userconfiguration;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserConfigurationManagerTest {

    private final String TEST_NAME_FILE = "testing.conf";

    @Test public void testCreation() throws IOException {
        UserConfigurationManager ucm = new UserConfigurationManager(TEST_NAME_FILE);
        Assert.assertTrue(Files.exists(ucm.getConfigFilePath()));
        Files.delete(ucm.getConfigFilePath());
        Assert.assertTrue(!Files.exists(ucm.getConfigFilePath()));
    }

    @Test public void testWriteFile() throws IOException {
        UserConfigurationManager ucm = new UserConfigurationManager(TEST_NAME_FILE);
        Assert.assertTrue(Files.readAllLines(ucm.getConfigFilePath()).isEmpty());
        ucm.addProperty("testKey", "testValue");
        ucm.writeToFile();
        Assert.assertFalse(Files.readAllLines(ucm.getConfigFilePath()).isEmpty());
        Assert.assertEquals(1, Files.readAllLines(ucm.getConfigFilePath()).size());
        Files.delete(ucm.getConfigFilePath());
        Assert.assertTrue(!Files.exists(ucm.getConfigFilePath()));
    }

    @Test public void testExistingReadFile() throws IOException {
        List<String> linesToWrite = new ArrayList<>();
        linesToWrite.add("testKey=testValue");
        Path testFilePath = UserConfigurationManager.PATH_CONFIGURATION_DIRECTORY.resolve(TEST_NAME_FILE);
        Files.createFile(testFilePath);
        Files.write(testFilePath, linesToWrite);

        UserConfigurationManager ucm = new UserConfigurationManager(TEST_NAME_FILE);
        Map<String, String> savedProperties = ucm.getProperties();
        Assert.assertEquals(1, savedProperties.size());
        Assert.assertTrue(ucm.containsProperty("testKey"));
        Optional<String> savedValue = ucm.getProperty("testKey");
        Assert.assertTrue(savedValue.isPresent());
        Assert.assertEquals("testValue", savedValue.get());

        Files.delete(ucm.getConfigFilePath());
        Assert.assertTrue(!Files.exists(ucm.getConfigFilePath()));
    }

}
