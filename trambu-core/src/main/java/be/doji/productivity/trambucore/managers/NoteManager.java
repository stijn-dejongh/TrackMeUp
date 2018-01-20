package be.doji.productivity.trambucore.managers;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tasks.Note;
import be.doji.productivity.trambucore.parser.ActivityParser;
import be.doji.productivity.trambucore.utils.TrackerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NoteManager {

    private static final Logger LOG = LoggerFactory.getLogger(NoteManager.class);
    private Path fileDirectory;
    private List<Note> notes = new ArrayList<>();

    public NoteManager(Path fileDirectory) throws IOException {
        LOG.info("Creating new NoteManager on location: " + fileDirectory.toString());
        this.fileDirectory = fileDirectory;
        this.readNoteDate();
    }

    private void readNoteDate() throws IOException {
        if (Files.isDirectory(fileDirectory)) {
            Files.walkFileTree(fileDirectory, new SimpleFileVisitor<Path>() {

                @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        Path noteFileName = path.getFileName();
                        Optional<String> activityIdFromFileName = findActivityIdFromFileName(noteFileName.toString());
                        if (activityIdFromFileName.isPresent()) {
                            notes.add(new Note(activityIdFromFileName.get(), path));
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private Optional<String> findActivityIdFromFileName(String fileName) {
        List<String> uuidMatches = TrackerUtils.findAllMatches(ActivityParser.REGEX_UUID, fileName);
        for (String uuidMatch : uuidMatches) {
            String uuidString = uuidMatch.replace(TrackMeConstants.INDICATOR_UUID, "").trim();
            return Optional.of(uuidString);
        }
        return Optional.empty();
    }

    public Optional<Note> findNoteForActivity(UUID activityId) {
        for (Note note : notes) {
            if (note.getActivityId().equals(activityId)) {
                return Optional.of(note);
            }
        }
        return Optional.empty();
    }

    public Note createNoteForActivity(UUID activityId) throws IOException {
        Path noteFile = Files
                .createFile(fileDirectory.resolve(activityId.toString() + TrackMeConstants.NOTES_FILE_EXTENSION));
        Note note = new Note(activityId, noteFile);
        this.notes.add(note);
        return note;
    }

    public List<Note> getAllNotes() {
        return this.notes;
    }
}
