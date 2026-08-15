package de.igslandstuhl.database.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskLevelTest {

    @Test
    void defaultLevelsMatchPreviousBehavior() {
        assertEquals("Niveau 1", TaskLevel.get(1).getGermanTranslation());
        assertEquals("Niveau 2", TaskLevel.get(2).getGermanTranslation());
        assertEquals("Niveau 3", TaskLevel.get(3).getGermanTranslation());
        assertEquals("Nanstein-Aufgabe", TaskLevel.get(-1).getGermanTranslation());
    }

    @Test
    void defaultRatiosMatchPreviousBehavior() {
        assertEquals(0.45, TaskLevel.get(1).getRatio());
        assertEquals(0.30, TaskLevel.get(2).getRatio());
        assertEquals(0.25, TaskLevel.get(3).getRatio());
    }

    @Test
    void specialLevelHasNoRatio() {
        assertThrows(
            IllegalStateException.class,
            () -> TaskLevel.get(-1).getRatio()
        );
    }

    @Test
    void serializationMatchesPreviousBehavior() {
        assertEquals("1", TaskLevel.get(1).toString());
        assertEquals("1", TaskLevel.get(1).toJSON());

        assertEquals("Special", TaskLevel.get(-1).toString());
        assertEquals("\"Special\"", TaskLevel.get(-1).toJSON());
    }

    @Test
    void valuesAreOrderedByNumericLevelWithSpecialLast() {
        TaskLevel[] values = TaskLevel.get(1).values();

        assertEquals(4, values.length);
        assertEquals(1, values[0].getNumber());
        assertEquals(2, values[1].getNumber());
        assertEquals(3, values[2].getNumber());
        assertEquals(-1, values[3].getNumber());
    }

    @Test
    void unknownLevelThrowsException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> TaskLevel.get(999)
        );
    }
}
