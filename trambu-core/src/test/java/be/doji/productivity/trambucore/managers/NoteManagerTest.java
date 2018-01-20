package be.doji.productivity.trambucore.managers;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.TrambuTest;
import be.doji.productivity.trambucore.model.tasks.Note;
import be.doji.productivity.trambucore.testutil.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class NoteManagerTest extends TrambuTest {

    private static final String ACTIVITY_ID_READ = "283b6298-b513-4e89-b757-10e98c9078ea";
    private static final String ACTIVITY_ID_WRITE = "283b6298-b513-4e69-b767-10e98c9078ea";
    private static final String DATA_DIRECTORY = "noteData";
    private static final String FILE_LOCATION =
            DATA_DIRECTORY + "/" + ACTIVITY_ID_READ + TrackMeConstants.NOTES_FILE_EXTENSION;
    private Path dataDirectory;

    @Before public void init() throws FileNotFoundException {
        dataDirectory = Paths.get(FileUtils.getTestPath("noteData", this.getClass().getClassLoader()));
    }

    @Test public void testNoteManagerRead() throws IOException {
        NoteManager nm = new NoteManager(dataDirectory);
        List<Note> allNotes = nm.getAllNotes();
        Assert.assertNotNull(allNotes);
        Assert.assertFalse(allNotes.isEmpty());
        Assert.assertEquals(1, allNotes.size());
        Note note = allNotes.get(0);
        Assert.assertEquals(FileUtils.getTestPath(FILE_LOCATION, this.getClass().getClassLoader()),
                note.getLocation().toString());
        List<String> content = note.getContent();
        Assert.assertNotNull(content);
        Assert.assertFalse(content.isEmpty());
        Assert.assertEquals(7, content.size());
    }

    @Test public void testNoteManagerWrite() throws IOException {
        NoteManager nm = new NoteManager(dataDirectory);
        Note createdNote = nm.createNoteForActivity(UUID.fromString(ACTIVITY_ID_WRITE));
        Assert.assertNotNull(createdNote);
        Path createdFile = dataDirectory.resolve(ACTIVITY_ID_WRITE + TrackMeConstants.NOTES_FILE_EXTENSION);
        Assert.assertTrue(Files.exists(createdFile));
        Assert.assertEquals(createdFile, createdNote.getLocation());
        Files.delete(createdFile);
        Assert.assertFalse(Files.exists(createdFile));
    }

}
