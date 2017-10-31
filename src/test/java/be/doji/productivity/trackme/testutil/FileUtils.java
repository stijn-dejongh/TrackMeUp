package be.doji.productivity.trackme.testutil;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Doji on 30/10/2017.
 */
public class FileUtils {

    /**
     * Utility classes should not have a public or default constructor
     */
    private FileUtils() {
    }

    public static String getTestPath(String path) throws FileNotFoundException {
        File testFile = ResourceUtils.getFile(FileUtils.class.getClassLoader().getResource(path));
        Assert.assertTrue(testFile.exists());
        String testPath = testFile.getAbsolutePath();
        Assert.assertFalse(StringUtils.isBlank(testPath));
        return testPath;
    }
}
