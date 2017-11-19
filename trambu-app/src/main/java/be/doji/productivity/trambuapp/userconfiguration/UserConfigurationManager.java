package be.doji.productivity.trambuapp.userconfiguration;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UserConfigurationManager {

    static final Path PATH_CONFIGURATION_DIRECTORY = Paths.get(System.getProperty("user.home"), ".trambu");

    private static final String SEPARATOR_PROPERTY = "=";

    private Path configFilePath;
    private Map<String, String> properties = new HashMap<>();

    public UserConfigurationManager(String configurationName) throws IOException {
        createFilesIfNeeded(configurationName);
        properties = readProperties();
    }

    private void createFilesIfNeeded(String configurationName) throws IOException {
        this.configFilePath = PATH_CONFIGURATION_DIRECTORY.resolve(configurationName);
        if (!PATH_CONFIGURATION_DIRECTORY.toFile().exists()) {
            Files.createDirectories(PATH_CONFIGURATION_DIRECTORY);
        }

        if (!configFilePath.toFile().exists()) {
            Files.createFile(configFilePath);
        }
    }

    private Map<String, String> readProperties() throws IOException {
        List<String> fileContents = Files.readAllLines(configFilePath);
        Map<String, String> readProperties = new HashMap<>();
        for (String line : fileContents) {
            if (StringUtils.isNotBlank(line)) {
                String[] splitLine = line.split(SEPARATOR_PROPERTY, 2);
                String propertyKey = splitLine[0].trim();
                String propertyValue = splitLine[1].trim();
                readProperties.put(propertyKey, propertyValue);
            }
        }
        return readProperties;
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

    public Optional<String> getProperty(String key) {
        if (containsProperty(key)) {
            return Optional.of(this.getProperties().get(key));
        } else {
            return Optional.empty();
        }

    }

    public boolean containsProperty(String key) {
        return this.getProperties().containsKey(key);
    }

    public void addProperty(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.properties.put(key, value);
        }
    }

    public Path getConfigFilePath() {
        return configFilePath;
    }
}
