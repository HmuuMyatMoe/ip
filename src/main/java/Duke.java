import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
public class Duke {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    public Duke(String dirPath, String fileName) {
        storage = new Storage(dirPath, fileName);
        ui = new Ui();
        try {
            tasks = new TaskList(storage.read());
        } catch (DukeException e) {
            ui.print(e.getMessage());
            tasks = new TaskList();
        }
    }

    public void run() {
        ui.printWelcome();
        boolean isExit = false;
        while(!isExit) {
            try {
                String input = ui.readCommand();
                Command cmd = Parser.parse(input);
                cmd.execute(tasks, storage, ui);
                isExit = cmd.isExit();
            } catch (DukeException e) {
                ui.print(e.getMessage());
            }
        }
        storage.write(tasks.getTaskList());
        return;
    }

    public static void main(String[] args) {
        new Duke("./data", "DukeList.txt").run();
    }
}