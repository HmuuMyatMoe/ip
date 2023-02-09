package duke.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import duke.Formatter;
import duke.command.AddCommand;
import duke.command.ByeCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.FindCommand;
import duke.command.ListCommand;
import duke.command.MarkCommand;
import duke.command.UnmarkCommand;
import duke.exception.IncompleteDescException;
import duke.exception.InvalidInputException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

/**
 * Encapsulates the related fields and behavior of a parser that parses the inputs given.
 */
public class Parser {
    protected enum CommandEnum {
        BYE, LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE, FIND
    }

    /**
     * Checks if name of tasks is given in the given input
     * by checking the length of String array of the split input
     * and checking that the portion of the given input behind the command name is not empty.
     * @param splitInputs The given user input that was split into 2 sections using " ".
     * @param cmdType The type of the command given by user.
     * @throws IncompleteDescException If there is no name of task given.
     */
    private void checkName(String splitInputs[], String cmdType) throws IncompleteDescException {
        if (splitInputs.length <= 1 || splitInputs[1].isBlank()) {
            throw new IncompleteDescException(
                    "The description of a " + cmdType + " cannot be empty!");
        }
    }

    /**
     * Instantiates a new ToDo task and returns it.
     * @param splitInputs An array containing the input by the user.
     * @return The new ToDo task.
     * @throws IncompleteDescException If name of the task is not given.
     */
    private ToDo getTodo(String[] splitInputs) throws IncompleteDescException {
        checkName(splitInputs, "todo");
        return new ToDo(splitInputs[1]);
    }

    /**
     * Parses the given string into a LocalDate object.
     *
     * @param date The given string representation of the date to be parsed.
     * @return The parsed LocalDate.
     */
    public static LocalDate parseDate(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return LocalDate.parse(date, dtf);
    }

    /**
     * Finds the index of the given keyword
     * in a section of the input given by user.
     * @param input The section of the user input with the command name removed.
     * @param keyword The keyword to be found.
     * @param keywordType The type of keyword given (what the keyword represents).
     * @return The index of the given keyword in the given string.
     * @throws IncompleteDescException If keyword is not found.
     */
    private int getKeywordIndex(String input, String keyword, String keywordType) throws IncompleteDescException {
        int index = input.indexOf(keyword);
        if (index < 0) {
            throw new IncompleteDescException(
                    "Please add the " + keywordType);
        }
        return index;
    }

    /**
     * Extracts the name of the task from the given input
     * using the indexes given.
     * @param input The string to extract the name from.
     * @param startIndex The starting index of the name in input string.
     * @param endIndex The last index of the name in input string.
     * @param nameType The task type of the given name.
     * @return The extracted name.
     * @throws IncompleteDescException If no name can be found using the given indexes.
     */
    private String getName(String input, int startIndex, int endIndex, String nameType) throws IncompleteDescException {
        String name = input.substring(startIndex, endIndex).strip();
        if (name.isBlank()) {
            throw new IncompleteDescException(
                    "The description of a " + nameType + " cannot be empty!");
        }
        return name;
    }

    /**
     * Extracts date from the input string given
     * and parses it into a LocalDate object.
     * @param input The input string to extract the date from.
     * @param startIndex The starting index of the date in input string.
     * @param endIndex The last index of the date in input string.
     * @param dateType The type of date extracted (what the date represents).
     * @return A LocalDate representation of the extracted date.
     * @throws IncompleteDescException If no date can be found using the given indexes.
     * @throws InvalidInputException If given date is not in the yyyy/mm/dd format
     *                               or if the given date is not a valid date in the calendar.
     */
    private LocalDate getLocalDate(String input, int startIndex, int endIndex, String dateType) throws IncompleteDescException, InvalidInputException {
        String date = input.substring(startIndex, endIndex).strip();
        if (date.isBlank()) {
            throw new IncompleteDescException(
                    "Please add the " + dateType);
        }
        try {
            LocalDate localDate = Parser.parseDate(date);
            return localDate;
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(
                    "Please enter a valid date/time in \"yyyy/mm/dd\" format.");
        }
    }

    /**
     * Checks whether the given dateInput is before the dateToCheckWith.
     * @param dateInput The date we want to check.
     * @param dateToCheckWith The date we are cross-checking against.
     * @param dateType The type of dateInput given (what the dateInput represents).
     * @throws InvalidInputException If dateInput is not before the dateToCheckWith.
     */
    private void checkDateIsBefore(LocalDate dateInput, LocalDate dateToCheckWith, String dateType) throws InvalidInputException {
        if (dateInput.isBefore(dateToCheckWith)) {
            String dateFormatted = Formatter.formatDateForPrint(dateInput);
            throw new InvalidInputException(
                    "The given " + dateType + " (yyyy/mm/dd) " + dateFormatted + " has passed.");
        }
    }

    /**
     * Checks whether the given endDate is after or the same as the startDate.
     * @param startDate The starting date.
     * @param endDate The end date.
     * @throws InvalidInputException If endDate given is before startDate given.
     */
    private void checkEndIsAfterStart(LocalDate startDate, LocalDate endDate) throws InvalidInputException {
        if (endDate.isAfter(startDate)) {
            String dateFormatted = Formatter.formatDateForPrint(startDate);
            throw new InvalidInputException(
                    "The given start date " + dateFormatted + " (yyyy/mm/dd) should be before the end date (yyyy/mm/dd).");
        }
    }

    /**
     * Instantiates a new Deadline object and returns it.
     * @param splitInputs An array containing the input by the user.
     * @return A new Deadline object.
     * @throws IncompleteDescException If name or due date/time or both is/are not provided.
     * @throws InvalidInputException If due date/time is given in the wrong format.
     */
    private Deadline getDeadline(String[] splitInputs) throws IncompleteDescException, InvalidInputException {
        checkName(splitInputs, "deadline");
        int endIndex = getKeywordIndex(splitInputs[1], "/by", "due date/time");

        String name = getName(splitInputs[1], 0, endIndex, "deadline");
        LocalDate endLocalDate = getLocalDate(splitInputs[1], endIndex + 3,
                splitInputs[1].length(), "due date/time");
        checkDateIsBefore(endLocalDate, LocalDate.now(), "deadline");
        return new Deadline(name, endLocalDate);
    }

    /**
     * Instantiate a new Event object and returns it.
     * @param splitInputs An array containing the input by the user.
     * @return A new Event.
     * @throws IncompleteDescException If the name, start date/time or due date/time are not given.
     * @throws InvalidInputException If any of the date/time are given
     *                               in a format different from "yyyy/MM/dd".
     */
    private Event getEvent(String[] splitInputs) throws IncompleteDescException, InvalidInputException {
        checkName(splitInputs, "event");
        int startIndex = getKeywordIndex(splitInputs[1], "/from", "start date/time");
        int endIndex = getKeywordIndex(splitInputs[1], "/to", "end date/time");
        String name = getName(splitInputs[1], 0, startIndex, "event");

        LocalDate startLocalDate = getLocalDate(splitInputs[1], startIndex + 5,
                endIndex, "start date/time");
        LocalDate endLocalDate = getLocalDate(splitInputs[1], endIndex + 3,
                splitInputs[1].length(), "end date/time");

        checkEndIsAfterStart(startLocalDate, endLocalDate);
        checkDateIsBefore(endLocalDate, LocalDate.now(), "end date");
        return new Event(name, startLocalDate, endLocalDate);
    }

    /**
     * Parses the given string into the correct command.
     *
     * @param input The string input given by the user.
     * @return A Command representing the command given.
     * @throws IncompleteDescException If command given is incomplete.
     * @throws InvalidInputException If command is invalid.
     */
    public Command parseCommand(String input) throws IncompleteDescException, InvalidInputException {
        String[] splitInputs = input.split(" ", 2);
        CommandEnum commandType;
        try {
            commandType = CommandEnum.valueOf(splitInputs[0].toUpperCase());
            switch (commandType) {
            case BYE:
                return new ByeCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                if (splitInputs.length <= 1) {
                    throw new InvalidInputException(
                            "Please give the index of the item to be marked.");
                }
                return new MarkCommand(Integer.parseInt(splitInputs[1]) - 1);
            case UNMARK:
                if (splitInputs.length <= 1) {
                    throw new InvalidInputException(
                            "Please give the index of the item to be unmarked.");
                }
                return new UnmarkCommand(Integer.parseInt(splitInputs[1]) - 1);
            case DELETE:
                if (splitInputs.length <= 1) {
                    throw new InvalidInputException(
                            "Please give the index of the item to be deleted.");
                }
                return new DeleteCommand(Integer.parseInt(splitInputs[1]) - 1);
            case TODO:
                Task task = this.getTodo(splitInputs);
                return new AddCommand(task);
            case DEADLINE:
                Deadline deadline = this.getDeadline(splitInputs);
                return new AddCommand(deadline);
            case EVENT:
                Event event = this.getEvent(splitInputs);
                return new AddCommand(event);
            case FIND:
                return new FindCommand(splitInputs[1]);
            default:
                throw new InvalidInputException(
                        "I'm sorry, there is no such command.");
            }
        } catch (IllegalArgumentException i) {
            throw new InvalidInputException(
                    "I'm sorry, there is no such command.");
        }
    }
}
