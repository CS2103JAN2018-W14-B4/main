package seedu.address.logic.parser.calendar;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CELEBRITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.calendar.EditAppointmentCommand;
import seedu.address.logic.commands.calendar.EditAppointmentCommand.EditAppointmentDescriptor;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author muruges95
/**
 * Parses input arguments and creates a new EditAppointmentCommand object
 */
public class EditAppointmentCommandParser implements Parser<EditAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditAppointmentCommand
     * and returns an EditAppointmentCommand object for execution
     * @throws ParseException if the user input does not comform to the expected format
     */
    @Override
    public EditAppointmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultiMap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_START_TIME,
                PREFIX_START_DATE,  PREFIX_LOCATION, PREFIX_END_TIME, PREFIX_END_DATE, PREFIX_CELEBRITY);

        Index appointmentIndex;

        try {
            appointmentIndex = ParserUtil.parseIndex(argMultiMap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditAppointmentCommand.MESSAGE_USAGE));
        }

        EditAppointmentDescriptor editApptDescriptor = new EditAppointmentDescriptor();

        try {
            ParserUtil.parseGeneralName(argMultiMap.getValue(PREFIX_NAME))
                    .ifPresent(editApptDescriptor::setAppointmentName);
            ParserUtil.parseTime(argMultiMap.getValue(PREFIX_START_TIME)).ifPresent(editApptDescriptor::setStartTime);
            ParserUtil.parseDate(argMultiMap.getValue(PREFIX_START_DATE)).ifPresent(editApptDescriptor::setStartDate);
            ParserUtil.parseTime(argMultiMap.getValue(PREFIX_END_TIME)).ifPresent(editApptDescriptor::setEndTime);
            ParserUtil.parseDate(argMultiMap.getValue(PREFIX_END_DATE)).ifPresent(editApptDescriptor::setEndDate);
            ParserUtil.parseMapAddress(argMultiMap.getValue(PREFIX_LOCATION))
                    .ifPresent(editApptDescriptor::setLocation);
            parseCelebrityIndicesForEditAppointment(argMultiMap.getAllValues(PREFIX_CELEBRITY))
                    .ifPresent(editApptDescriptor::setCelebrityIndices);

        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editApptDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditAppointmentCommand.MESSAGE_NOT_EDITED);
        }

        return new EditAppointmentCommand(appointmentIndex, editApptDescriptor);
    }

    /**
     * Parses {@code Collection<String> celebrityIndices} into a {@code Set<Index>}
     * if {@code celebrityIndices} is non-empty. If {@code celebrityIndices} contain only one element
     * which is an empty string, it will be parsed into a {@code Set<Celebrity>} containing zero celebrities.
     */
    private Optional<Set<Index>> parseCelebrityIndicesForEditAppointment(Collection<String> celebrityIndices)
        throws IllegalValueException {
        assert celebrityIndices != null;

        if (celebrityIndices.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> celebrityIndexSet = (celebrityIndices.size() == 1 && celebrityIndices.contains(""))
                ? Collections.emptySet() : celebrityIndices;
        return Optional.of(ParserUtil.parseIndices(celebrityIndexSet));
    }
}