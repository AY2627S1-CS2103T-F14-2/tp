package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/** A modal overlay that presents every stored detail for one student. */
public class StudentDetailModal extends UiPart<Region> {

    private static final String FXML = "StudentDetailModal.fxml";
    private static final String NOT_PROVIDED = "Not provided";

    private final Runnable closeAction;

    @FXML
    private Label initials;

    @FXML
    private Label name;

    @FXML
    private Label phone;

    @FXML
    private Label email;

    @FXML
    private Label subjects;

    @FXML
    private Label level;

    @FXML
    private Label notes;

    @FXML
    private Label address;

    @FXML
    private Label tags;

    /**
     * Creates a modal containing the complete record of the given student.
     *
     * @param person student whose details should be shown.
     * @param closeAction action to run when the modal is closed.
     */
    public StudentDetailModal(Person person, Runnable closeAction) {
        super(FXML);
        this.closeAction = closeAction;
        setStudentDetails(person);
    }

    /** Handles the close button in the modal footer and header. */
    @FXML
    private void handleClose() {
        closeAction.run();
    }

    /** Populates every detail field, including optional legacy fields when present. */
    private void setStudentDetails(Person person) {
        String fullName = person.getName().fullName;
        initials.setText(PersonCard.getInitialsForDisplay(fullName));
        name.setText(fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail() == null ? NOT_PROVIDED : person.getEmail().value);
        subjects.setText(person.getSubjects().isEmpty() ? NOT_PROVIDED : String.join(", ", person.getSubjects()));
        level.setText(person.getLevel() == null ? NOT_PROVIDED : person.getLevel());
        notes.setText(person.getNotes().isEmpty() ? NOT_PROVIDED : person.getNotes());
        address.setText(person.getAddress() == null ? NOT_PROVIDED : person.getAddress().value);
        tags.setText(person.getTags().isEmpty()
                ? NOT_PROVIDED
                : person.getTags().stream().sorted(Comparator.comparing(Object::toString))
                        .map(Object::toString).collect(Collectors.joining(", ")));
    }
}
