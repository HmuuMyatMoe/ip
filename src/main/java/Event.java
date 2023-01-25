import java.time.LocalDate;

/**
 * Encapsulation of an Event task,
 * a task that starts and ends at a specific date/time.
 */
public class Event extends Task {
    //Start time
    private LocalDate start;

    //End time
    private LocalDate end;

    /**
     * Constructor for Event.
     * @param name The name of the task.
     */
    public Event(String name, LocalDate start, LocalDate end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    /**
     * Get the string with a [E] icon representing this task.
     * @return A string representation of this Event task.
     */
    @Override
    public String toString() {
        String s = Duke.formatDate(this.start);
        String e = Duke.formatDate(this.end);
        String toPrint = String.format("[E]%s (from: %s to: %s)",
                super.toString(), s, e);
        return toPrint;
    }
}