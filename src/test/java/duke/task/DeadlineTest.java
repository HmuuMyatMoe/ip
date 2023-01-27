package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

/**
 * Test the behavior of Deadline class.
 */
public class DeadlineTest {
    @Test
    public void testStringConversion() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate ld = LocalDate.parse("2024/11/11", dtf);
        assertEquals("[D][ ] get food (by: 11 NOVEMBER 2024)",
                new Deadline("get food", ld).toString());
    }

    @Test
    public void testFormatStore() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate ld = LocalDate.parse("2024/11/11", dtf);
        assertEquals("D | 0 | get food | 2024/11/11",
                new Deadline("get food", ld).formatForStorage());
    }
}
