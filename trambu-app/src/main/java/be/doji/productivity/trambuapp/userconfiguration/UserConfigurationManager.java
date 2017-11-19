package be.doji.productivity.trambuapp.userconfiguration;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserConfigurationManager {

    private static final Path PATH_CONFIGURATION_DIRECTORY = Paths.get(System.getProperty("user.home"), ".trambu");

    private static final String SEPARATOR_PROPERTY = "=";

    private Path configFilePath;
    private Map<String, String> properties = new HashMap<>();

    public UserConfigurationManager(String configurationName) throws IOException {
        createFilesIfNeeded(configurationName);
        properties = readProperties();
    }

    private void createFilesIfNeeded(String configurationName) throws IOException {
        this.configFilePath = PATH_CONFIGURATION_DIRECTORY.resolve(configurationName);
        if (!Files.exists(PATH_CONFIGURATION_DIRECTORY)) {
            Files.createDirectories(PATH_CONFIGURATION_DIRECTORY);
        }

        if (!Files.exists(configFilePath)) {
            Files.createFile(configFilePath);
        }
    }

    private Map<String, String> readProperties() throws IOException {
        List<String> fileContents = Files.readAllLines(configFilePath);
        Map<String, String> properties = new HashMap<>();
        for (String line : fileContents) {
            if (StringUtils.isNotBlank(line)) {
                String[] splitLine = line.split(SEPARATOR_PROPERTY, 2);
                String propertyKey = splitLine[0].trim();
                String propertyValue = splitLine[1].trim();
                properties.put(propertyKey, propertyValue);
            }
        }
        return properties;
    }

    public void writeToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> property : this.getProperties().entrySet()) {
            lines.add(property.getKey() + SEPARATOR_PROPERTY + property.getValue());
        }
        Files.write(configFilePath, lines);
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
