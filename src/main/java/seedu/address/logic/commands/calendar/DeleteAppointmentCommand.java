package seedu.address.logic.commands.calendar;

import static seedu.address.model.ModelManager.DAY_VIEW_PAGE;

import java.util.List;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.ChangeCalendarViewPageRequestEvent;
import seedu.address.commons.events.ui.ShowAppointmentListEvent;
import seedu.address.commons.events.ui.ShowCalendarEvent;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Celebrity;

//@@author WJY-norainu
/**
 * Deletes an appointment identified using its last displayed index from the calendar.
 */
public class DeleteAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "deleteAppointment";
    public static final String COMMAND_ALIAS = "da";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes an appointment. "
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_MUST_SHOW_LIST_OF_APPOINTMENTS = "List of appointments must be shown "
            + "before deleting an appointment";
    public static final String MESSAGE_SUCCESS = "Deleted Appointment: %1$s";
    public static final String MESSAGE_APPOINTMENT_LIST_BECOMES_EMPTY = "\nAppointment list becomes empty, "
            + "Switching back to calendar view by day\n"
            + "Currently showing %1$s calendar";

    private final Index targetIndex;

    private Appointment apptToDelete;

    public DeleteAppointmentCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult execute() throws CommandException {
        // throw exception if the user is not currently viewing an appointment list
        if (!model.getIsListingAppointments()) {
            throw new CommandException(MESSAGE_MUST_SHOW_LIST_OF_APPOINTMENTS);
        }
        model.deleteAppointmentFromStorageCalendar(targetIndex.getZeroBased());
        apptToDelete = model.removeAppointmentFromInternalList(targetIndex.getZeroBased());

        // remove the appt from last displayed appointment list
        List<Appointment> newAppointmentList = model.getAppointmentList();
        // if the list becomes empty, switch back to combined calendar day view
        if (model.getAppointmentList().size() < 1) {
            model.setIsListingAppointments(false);
            model.setCelebCalendarViewPage(DAY_VIEW_PAGE);
            EventsCenter.getInstance().post(new ChangeCalendarViewPageRequestEvent(DAY_VIEW_PAGE));
            EventsCenter.getInstance().post(new ShowCalendarEvent());

            Celebrity currentCalendarOwner = model.getCurrentCelebCalendarOwner();
            if (currentCalendarOwner == null) {
                return new CommandResult(
                        String.format(MESSAGE_SUCCESS, apptToDelete.getTitle())
                                + String.format(MESSAGE_APPOINTMENT_LIST_BECOMES_EMPTY,
                                "combined"));
            } else {
                return new CommandResult(
                        String.format(MESSAGE_SUCCESS, apptToDelete.getTitle())
                                + String.format(MESSAGE_APPOINTMENT_LIST_BECOMES_EMPTY,
                                currentCalendarOwner.getName().toString() + "'s"));
            }
        }
        // if the list is not empty yet, update appointment list view
        EventsCenter.getInstance().post(new ShowAppointmentListEvent(newAppointmentList));

        return new CommandResult(String.format(MESSAGE_SUCCESS, apptToDelete.getTitle()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof DeleteAppointmentCommand
                && apptToDelete.equals(((DeleteAppointmentCommand) other).apptToDelete));
    }
}
