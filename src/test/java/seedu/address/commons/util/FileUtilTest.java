package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.TestUtil;

public class FileUtilTest {

    @Test
    public void fileOperations_validPath_readAndWriteContent() throws IOException {
        Path file = TestUtil.getFilePathInSandboxFolder("file-util.txt");
        FileUtil.createIfMissing(file);
        FileUtil.createIfMissing(file);
        FileUtil.writeToFile(file, "content");

        assertTrue(Files.exists(file));
        assertEquals("content", FileUtil.readFromFile(file));
    }

    @Test
    public void fileOperations_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.createIfMissing(null));
        assertThrows(NullPointerException.class, () -> FileUtil.readFromFile(null));
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(null, "content"));
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(Path.of("content.txt"), null));
    }
}
