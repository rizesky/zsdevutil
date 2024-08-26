package com.zs.zsdevutil.viewmodel;

import com.zs.zsdevutil.etc.ErrorHandler;
import com.zs.zsdevutil.etc.StatePersistence;
import com.zs.zsdevutil.model.TimeConversionMode;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.util.Duration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UnixTimeConverterViewModel implements StatePersistingViewModel {
    private static final Logger log = LoggerFactory.getLogger(UnixTimeConverterViewModel.class);
    private final StringProperty input = new SimpleStringProperty();
    private final ObjectProperty<TimeConversionMode> conversionMode = new SimpleObjectProperty<>(TimeConversionMode.UNIX_TIME);

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty hasError = new SimpleBooleanProperty(false);

    private final StringProperty localDate = new SimpleStringProperty();
    private final StringProperty dayOfYear = new SimpleStringProperty();
    private final StringProperty utc = new SimpleStringProperty();
    private final StringProperty weekOfYear = new SimpleStringProperty();
    private final StringProperty relative = new SimpleStringProperty();
    private final StringProperty isLeapYear = new SimpleStringProperty();
    private final StringProperty unixTime = new SimpleStringProperty();
    private final StringProperty otherFormat1 = new SimpleStringProperty();
    private final StringProperty otherFormat2 = new SimpleStringProperty();
    private final StringProperty otherFormat3 = new SimpleStringProperty();
    private final StringProperty otherFormat4 = new SimpleStringProperty();
    private final StringProperty otherFormat5 = new SimpleStringProperty();
    private final StringProperty otherFormat6 = new SimpleStringProperty();
    private final StringProperty otherFormat7 = new SimpleStringProperty();
    private final StringProperty otherFormat8 = new SimpleStringProperty();

    private final List<DateTimeFormatter> formatters;
    private final PauseTransition debouncer;


    public UnixTimeConverterViewModel() {
        formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        formatters.add(DateTimeFormatter.ISO_LOCAL_DATE);
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        formatters.add(DateTimeFormatter.ofPattern("h:mm a"));
        formatters.add(DateTimeFormatter.ofPattern("MMMM yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        formatters.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        formatters.add(DateTimeFormatter.ISO_DATE);
        formatters.add(DateTimeFormatter.ISO_DATE_TIME);

        debouncer = new PauseTransition(Duration.millis(200));
        debouncer.setOnFinished(event -> convert());

        input.addListener((observable, oldValue, newValue) -> {
            debouncer.stop();
            debouncer.playFromStart();
        });

        conversionMode.addListener((observable, oldValue, newValue) -> convert());

        loadPersistedState();
    }

    public void convert() {
        if (input.get() == null || input.get().trim().isEmpty()) {
            setError("Input cannot be empty");
            clearFields();
            return;
        }

        if (conversionMode.get() == TimeConversionMode.UNIX_TIME) {
            convertUnixToHuman();
        } else {
            convertHumanToUnix();
        }

        saveState();

    }

    private void convertUnixToHuman() {
        try {
            long timestamp = Long.parseLong(input.get());
            updateFields(Instant.ofEpochSecond(timestamp));
            clearError();
        } catch (NumberFormatException | DateTimeException e) {
            setError("Invalid Unix timestamp");
            clearFields();
        }catch (Exception e){
            ErrorHandler.logAndNotify(e);
        }
    }

    private void convertHumanToUnix() {
        String inputText = input.get().trim();
        for (DateTimeFormatter formatter : formatters) {
            try {

                LocalDateTime dateTime = null;
                String formatterString = formatter.toString();

                boolean hasDate = formatterString.contains("Year") || formatterString.contains("Month") || formatterString.contains("Day");
                boolean hasTime = formatterString.contains("Hour") || formatterString.contains("Minute") || formatterString.contains("Second");

                if (hasDate && !hasTime) {
                    // Date only
                    LocalDate date = LocalDate.parse(inputText, formatter);
                    dateTime = date.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
                } else if (!hasDate && hasTime) {
                    // Time only
                    LocalTime time = LocalTime.parse(inputText, formatter);
                    dateTime =  LocalDateTime.of(LocalDate.now(), time);
                } else if (hasDate) {
                    // Date and Time
                    dateTime = LocalDateTime.parse(inputText, formatter);
                }
                assert dateTime != null;
                updateFields(dateTime.atZone(ZoneId.systemDefault()).toInstant());
                clearError();
                log.info("Formatter {} successfully format input {}", formatter,inputText);
                return;
            } catch (DateTimeParseException e) {
                log.warn("Formatter {} failed to format input {}", formatter, inputText);
            }catch (Exception e){
                ErrorHandler.logAndNotify(e);
            }
        }
        // If all parsers fail, set error and clear fields
        setError("Unsupported date format");
        clearFields();
    }

    private void updateFields(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));

        localDate.set(zonedDateTime.format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy")));
        dayOfYear.set(String.valueOf(zonedDateTime.getDayOfYear()));
        utc.set(utcDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        weekOfYear.set(String.valueOf(zonedDateTime.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())));
        relative.set(formatRelative(zonedDateTime));
        isLeapYear.set(String.valueOf(zonedDateTime.toLocalDate().isLeapYear()));
        unixTime.set(String.valueOf(instant.getEpochSecond()));

        otherFormat1.set(zonedDateTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")));
        otherFormat2.set(zonedDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        otherFormat3.set(zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        otherFormat4.set(zonedDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        otherFormat5.set(zonedDateTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        otherFormat6.set(zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        otherFormat7.set(zonedDateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        otherFormat8.set(zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
    }

    private String formatRelative(ZonedDateTime dateTime) {
        ZonedDateTime now = ZonedDateTime.now();
        boolean isInFuture = dateTime.isAfter(now);

        ZonedDateTime earlier = isInFuture ? now : dateTime;
        ZonedDateTime later = isInFuture ? dateTime : now;

        long[] times = {
                ChronoUnit.DECADES.between(earlier, later),
                ChronoUnit.YEARS.between(earlier, later) % 10,
                ChronoUnit.MONTHS.between(earlier, later) % 12,
                ChronoUnit.DAYS.between(earlier, later) % 30,
                ChronoUnit.HOURS.between(earlier, later) % 24,
                ChronoUnit.MINUTES.between(earlier, later) % 60,
                ChronoUnit.SECONDS.between(earlier, later) % 60
        };
        String[] timeUnits = {"decade", "year", "month", "day", "hour", "minute", "second"};

        StringBuilder result = new StringBuilder();
        boolean firstNonZeroAdded = false;

        for (int i = 0; i < times.length; i++) {
            if (times[i] > 0) {
                if (firstNonZeroAdded) {
                    result.append(", ");
                }
                result.append(times[i]).append(" ").append(timeUnits[i]);
                if (times[i] > 1) {
                    result.append("s");
                }
                firstNonZeroAdded = true;

                // Only show up to two non-zero units
                if (result.toString().split(",").length == 2) {
                    break;
                }
            }
        }

        if (result.isEmpty()) {
            return "now";
        }

        return result + (isInFuture ? " from now" : " ago");
    }

    private void clearFields() {
        localDate.set("");
        dayOfYear.set("");
        utc.set("");
        weekOfYear.set("");
        relative.set("");
        isLeapYear.set("");
        unixTime.set("");
        otherFormat1.set("");
        otherFormat2.set("");
        otherFormat3.set("");
        otherFormat4.set("");
        otherFormat5.set("");
        otherFormat6.set("");
        otherFormat7.set("");
        otherFormat8.set("");
    }

    public void setNow() {
        Instant now = Instant.now();
        if (conversionMode.get() == TimeConversionMode.UNIX_TIME) {
            input.set(String.valueOf(now.getEpochSecond()));
        } else {
            input.set(now.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        convert();
    }

    private void setError(String message) {
        errorMessage.set(message);
        hasError.set(true);
    }

    private void clearError() {
        errorMessage.set("");
        hasError.set(false);
    }


    // Getters for all properties
    public StringProperty inputProperty() { return input; }
    public ObjectProperty<TimeConversionMode> conversionModeProperty() { return conversionMode; }
    public StringProperty localDateProperty() { return localDate; }
    public StringProperty dayOfYearProperty() { return dayOfYear; }
    public StringProperty utcProperty() { return utc; }
    public StringProperty weekOfYearProperty() { return weekOfYear; }
    public StringProperty relativeProperty() { return relative; }
    public StringProperty isLeapYearProperty() { return isLeapYear; }
    public StringProperty unixTimeProperty() { return unixTime; }
    public StringProperty otherFormat1Property() { return otherFormat1; }
    public StringProperty otherFormat2Property() { return otherFormat2; }
    public StringProperty otherFormat3Property() { return otherFormat3; }
    public StringProperty otherFormat4Property() { return otherFormat4; }
    public StringProperty otherFormat5Property() { return otherFormat5; }
    public StringProperty otherFormat6Property() { return otherFormat6; }
    public StringProperty otherFormat7Property() { return otherFormat7; }
    public StringProperty otherFormat8Property() { return otherFormat8; }
    //  getters for error properties
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty hasErrorProperty() { return hasError; }

    @Override
    public void loadPersistedState() {
        try {
            JSONObject state = StatePersistence.loadState("UnixTimeConverter");
            input.set(state.optString("input", ""));
            conversionMode.set(TimeConversionMode.valueOf(state.optString("conversionMode", TimeConversionMode.UNIX_TIME.name())));
            if (input.get().isEmpty()) {
                setNow();
            } else {
                convert();
            }
        } catch (IOException e) {
            ErrorHandler.logAndNotify(e);
        }

    }

    @Override
    public void saveState() {

        try {
            JSONObject state = new JSONObject();
            state.put("input", input.get());
            state.put("conversionMode", conversionMode.get().name());
            StatePersistence.saveState("UnixTimeConverter", state);
        } catch (IOException e) {
            ErrorHandler.logAndNotify(e);
        }
    }
}