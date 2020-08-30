import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the task list, handles all of the list manipulations + checks.
 */
public class ListOfItems {

    protected List<Task> list;
    protected int index;
    protected String divider = "____________________________________________________________";
    protected String tabSpacing = "   ";

    /**
     * Initialises a new ArrayList, and index starts from 0.
     */
    public ListOfItems() {
        this.list = new ArrayList<>();
        this.index = 0;
    }

    /**
     * Handles all of the information from "stored.txt" and adds it to the list.
     * It also updates the index with every addition.
     *
     * @param input a line from "stored.txt".
     */
    protected void addStored(String input) {
        char type = input.charAt(1);
        boolean isDone = input.charAt(4) == '✓';

        if (type == 'T') {
            //To-do
            String description = input.substring(7);
            Todo todo = new Todo(description, index + 1);
            if (isDone) {
                todo.markedDone();
            }
            list.add(index, todo);
            index++;
        } else if (type == 'D') {
            //Deadline
            String[] info = input.split("[(]");
            String description = info[0].substring(7);
            String dueDateTime = info[1].substring(0, info[1].length() - 1);
            Deadline deadline = new Deadline(description, index + 1, dueDateTime, true);
            if (isDone) {
                deadline.markedDone();
            }
            list.add(index, deadline);
            index++;
        } else {
            //Event
            String[] info = input.split("[(]");
            String description = info[0].substring(7);
            String duration = info[1].substring(0, info[1].length() - 1);
            Event event = new Event(description, index + 1, duration, true);
            if (isDone) {
                event.markedDone();
            }
            list.add(index, event);
            index++;
        }
    }

    /**
     * Retrieves the list and prints out every task.
     *
     * @throws DukeException if list is empty.
     */
    protected void getList() throws DukeException {
        System.out.println(divider);
        if (list.size() == 0) {
            throw new DukeException("List is empty, you have free time (for now)! YAY :D" + "\n" + divider);
        } else {
            System.out.println("Here are the task(s) in your list: ");
            for (int i = 0; i < index; i++) {
                System.out.println(list.get(i).id + "." + list.get(i));
            }
        }
        System.out.println(divider);
    }

    /**
     * Marks a particular task is done.
     *
     * @param input user input.
     * @throws DukeException if number given is invalid.
     */
    protected void doneItem(String input) throws DukeException {
        try {
            // retrieve number after "done "
            int number = Integer.parseInt(input.substring(5));

            Task task = list.get(number - 1);
            if (task.isDone) {
                System.out.println("Task already done!");
            } else {
                task.markedDone();
                String message = "Good job! I've marked this task as done: ";
                System.out.println(divider + "\n" + message + "\n" + tabSpacing
                        + task + "\n" + divider);
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new DukeException("\n" + divider + "\n" + "Sorry, you did not enter a valid number. Please try again."
                    + "\n" + divider);
        }
    }

    /**
     * Deletes a particular task.
     * Modifies other task's index if necessary, so that list is still in chronological order.
     *
     * @param input user input.
     * @throws DukeException if number given is invalid.
     */
    protected void deleteItem(String input) throws DukeException {
        try {
            int number = Integer.parseInt(input.substring(7));
            Task task = list.get(number - 1);
            for (int i = number; i < list.size(); i++) {
                list.get(i).id = list.get(i).id - 1;
            }
            list.remove(task);
            index--;
            System.out.println(divider);
            System.out.println("Noted. I've removed this task: ");
            System.out.println(tabSpacing + task);
            System.out.println("Now you have " + index + " tasks in the list.");
            System.out.println(divider);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new DukeException("\n" + divider + "\n" + "Whoops, you did not enter a valid number."
                    + "\n" + divider);
        }
    }

    /**
     * Adds a new task to the list.
     * Checks what type of task is given and initialise a new sub-class of Task (To-do, Deadline, or Event).
     *
     * @param input user input.
     * @throws DukeException if incomplete commands are given.
     */
    protected void addItem(String input) throws DukeException {
        String addedMessage = "Got it. I've added this task: ";
        String totalMessage = "Now you have " + (index + 1) + " task(s) in the list.";

        if (input.contains("todo")) {
            try {
                String description = input.substring(5);
                Todo todo = new Todo(description, index + 1);

                System.out.println(divider + "\n" + addedMessage);
                list.add(index, todo);
                System.out.println(tabSpacing + list.get(index));
                System.out.println(totalMessage + "\n" + divider);
                index++;
            } catch (StringIndexOutOfBoundsException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Whoops, you did not fill in the details of the Todo properly :("
                        + "\n" + "Please try again."
                        + "\n" + divider);
            }
        } else if (input.contains("deadline")) {
            try {
                String[] info = input.split("/", 2);
                String description = info[0].substring(9);
                String dueDateTime = info[1];
                Deadline deadline = new Deadline(description, index + 1, dueDateTime, false);

                System.out.println(divider + "\n" + addedMessage);
                list.add(index, deadline);
                System.out.println(tabSpacing + list.get(index));
                System.out.println(totalMessage + "\n" + divider);
                index++;
            } catch (StringIndexOutOfBoundsException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Whoops, you did not fill in the details of the Deadline properly :("
                        + "\n" + "Please try again."
                        + "\n" + divider);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Whoops, you did not fill in the due date/time of the Deadline properly."
                        + "\n" + "Please try again."
                        + "\n" + divider);
            } catch (DateTimeParseException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Sorry, you did not fill in the due date(DD/MM/YYYY)"
                        + "\n" + "and/or time(HHmm) properly. Please try again."
                        + "\n" + divider);
            }
        } else if (input.contains("event")) {
            try {
                String[] info = input.split("/", 2);
                String description = info[0].substring(6);
                String duration = info[1];
                Event event = new Event(description, index + 1, duration, false);

                System.out.println(divider + "\n" + addedMessage);
                list.add(index, event);
                System.out.println(tabSpacing + list.get(index));
                System.out.println(totalMessage + "\n" + divider);
                index++;
            } catch (StringIndexOutOfBoundsException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Whoops, you did not fill in the details of the Event properly :("
                        + "\n" + "Please try again."
                        + "\n" + divider);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Whoops, you did not fill in the duration of the Event properly."
                        + "\n" + "Please try again."
                        + "\n" + divider);
            } catch (DateTimeParseException e) {
                throw new DukeException("\n" + divider + "\n"
                        + "Sorry, you did not fill in the due date(DD/MM/YYYY)"
                        + "\n" + "and/or time(HHmm) properly. Please try again."
                        + "\n" + divider);
            }
        } else {
            throw new DukeException("\n" + divider + "\n"
                    + "Sorry, you did not enter a valid command! Please try again."
                    + "\n" + divider);
        }
    }

    /**
     * Checks and outputs all of the tasks that are due by a specific date.
     *
     * @param input user input.
     * @throws DukeException if input does not follow this format: "items due by DD/MM/YYYY".
     */
    protected void checkBy(String input) throws DukeException {
        try {
            boolean hasResults = false;
            String info = input.substring(13);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate date = LocalDate.parse(info, dateFormat);
            System.out.println(divider);
            System.out.println("Task(s) due by " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy")) + " :");
            for (int i = 0; i < this.list.size(); i++) {
                if ((this.list.get(i) instanceof Deadline && ((Deadline) this.list.get(i)).date.equals(date))
                        || (this.list.get(i) instanceof Event && ((Event) this.list.get(i)).date.equals(date))) {
                    hasResults = true;
                    System.out.println(this.list.get(i));
                }
            }
            if (!hasResults) {
                System.out.println("- No tasks due on " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                        + " -");
            }
            System.out.println(divider);
        } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
            throw new DukeException("\n" + divider + "\n"
                    + "Sorry, you did not enter a valid date (DD/MM/YYYY)! "
                    + "\n" + "Please try again."
                    + "\n" + divider);
        }
    }

    /**
     * Checks and outputs all of the task that are due before a specific date and/or time.
     *
     * @param input user input.
     * @throws DukeException if input does not follow this format: "items due before DD/MM/YYYY"
     * or "items due before DD/MM/YYYY HHmm".
     */
    protected void checkBefore(String input) throws DukeException {
        try {
            boolean hasResults = false;
            String info = input.substring(17);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmm");

            if (info.length() <= 10) {
                // only consists of date
                LocalDate date = LocalDate.parse(info, dateFormat);
                System.out.println(divider);
                System.out.println("Task(s) due before " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                        + " :");
                for (int i = 0; i < this.list.size(); i++) {
                    if ((this.list.get(i) instanceof Deadline && !((Deadline) this.list.get(i)).date.isAfter(date))
                            || (this.list.get(i) instanceof Event && !((Event) this.list.get(i)).date.isAfter(date))) {
                        hasResults = true;
                        System.out.println(this.list.get(i));
                    }
                }
                if (!hasResults) {
                    System.out.println("- No tasks due before " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                            + " -");
                }
                System.out.println(divider);
            } else {
                // date + time
                LocalDate date = LocalDate.parse(info.split(" ")[0], dateFormat);
                LocalTime time = LocalTime.parse(info.split(" ")[1], timeFormat);
                System.out.println(divider);
                System.out.println("Task(s) due before " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                        + ", " + time.format(DateTimeFormatter.ofPattern("h:mma")) + " :");
                for (int i = 0; i < this.list.size(); i++) {
                    if ((this.list.get(i) instanceof Deadline && !((Deadline) this.list.get(i)).date.isAfter(date)
                            && ((Deadline) this.list.get(i)).time != null
                            && !((Deadline) this.list.get(i)).time.isAfter(time))
                            || (this.list.get(i) instanceof Event && !((Event) this.list.get(i)).date.isAfter(date)
                            && ((Event) this.list.get(i)).endTime != null
                            && !((Event) this.list.get(i)).endTime.isAfter(time))) {
                        hasResults = true;
                        System.out.println(this.list.get(i));
                    }
                }
                if (!hasResults) {
                    System.out.println("- No tasks due before " + date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                            + ", " + time.format(DateTimeFormatter.ofPattern("h:mma")) + " -");
                }
                System.out.println(divider);
            }
        } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
            throw new DukeException("\n" + divider + "\n"
                    + "Sorry, you did not enter a valid date (DD/MM/YYYY)"
                    + "\n" + "and/or time (HHmm)! Please try again."
                    + "\n" + divider);
        }

    }

    protected void find(String input) throws DukeException {
        try {
            boolean hasResults = false;
            String info = input.substring(5);

            System.out.println(divider);
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < this.list.size(); i++) {
                if (this.list.get(i).description.contains(info)) {
                    System.out.println(this.list.get(i).id + ". " + this.list.get(i));
                    hasResults = true;
                }
            }
            if (!hasResults) {
                System.out.println("- No results found -");
            }
            System.out.println(divider);
        } catch (StringIndexOutOfBoundsException e) {
            throw new DukeException("\n" + divider + "\n"
                    + "Sorry, you did not enter a search. Please try again."
                    + "\n" + divider);
        }
    }
}
