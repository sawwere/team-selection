package ru.sfedu.teamselection.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.sfedu.teamselection.dto.track.TrackCreationDto;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, TrackCreationDto> {

    @Override
    public boolean isValid(TrackCreationDto dto, ConstraintValidatorContext context) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            return true;
        }
        return dto.getStartDate().isBefore(dto.getEndDate());
    }
}
