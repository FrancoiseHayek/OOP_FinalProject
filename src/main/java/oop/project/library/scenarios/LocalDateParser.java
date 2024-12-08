package oop.project.library.scenarios;

import oop.project.library.parser.Parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateParser implements Parser<LocalDate> {

    public LocalDate parse(String input) throws LocalDateException {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new LocalDateException(e.getMessage());
        }

    }
}
