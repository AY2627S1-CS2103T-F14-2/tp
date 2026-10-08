package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.TypicalPersons;

public class PersonCardTest {
    @BeforeAll
    public static void startToolkit() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void card_student_showsOrderedSubjectsAndHidesAbsentEmail() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            Person student = SampleDataUtil.getSamplePersons()[1];
            PersonCard card = new PersonCard(student, 2);
            assertEquals("2. ", ((Label) card.getRoot().lookup("#id")).getText());
            Label subjects = (Label) card.getRoot().lookup("#subjects");
            assertEquals("Subjects: Mathematics, Physics", subjects.getText());
            assertEquals("Level: JC 1", ((Label) card.getRoot().lookup("#level")).getText());
            assertFalse(card.getRoot().lookup("#email").isManaged());
            assertFalse(card.getRoot().lookup("#email").isVisible());
            assertFalse(card.getRoot().lookup("#address").isManaged());
            assertFalse(card.getRoot().lookup("#tags").isManaged());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void card_legacyContact_keepsOldDetailsAndOmitsStudentFields() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(TypicalPersons.ALICE, 1);
            assertTrue(card.getRoot().lookup("#address").isManaged());
            assertTrue(card.getRoot().lookup("#email").isManaged());
            assertFalse(card.getRoot().lookup("#subjects").isManaged());
            assertFalse(card.getRoot().lookup("#level").isManaged());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
}
