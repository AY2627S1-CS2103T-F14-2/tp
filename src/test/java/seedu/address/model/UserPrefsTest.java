package seedu.address.model;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void copyAndObjectMethods_validPrefs_behaveAsValueObject() {
        UserPrefs original = new UserPrefs();
        GuiSettings settings = new GuiSettings(1, 2, 3, 4);
        original.setGuiSettings(settings);
        UserPrefs copy = new UserPrefs(original);

        org.junit.jupiter.api.Assertions.assertEquals(original, copy);
        org.junit.jupiter.api.Assertions.assertEquals(original.hashCode(), copy.hashCode());
        org.junit.jupiter.api.Assertions.assertEquals(original, original);
        org.junit.jupiter.api.Assertions.assertNotEquals(original, null);
        org.junit.jupiter.api.Assertions.assertNotEquals(original, settings);
        org.junit.jupiter.api.Assertions.assertTrue(original.toString().contains("Gui Settings"));
        assertThrows(NullPointerException.class, () -> new UserPrefs(null));
    }

}
