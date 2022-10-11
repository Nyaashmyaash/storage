package com.cdek.storage.application.service.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HolidaysLists {

    public static List<LocalDate> getWeekendsOfFirstMay(LocalDate estimatedDate) {
        return Stream.iterate(LocalDate.of(estimatedDate.getYear(), 5, 1), date -> date.plusDays(1))
                        .limit(3)
                        .collect(Collectors.toList());
    }

    public static List<LocalDate> getWeekendsOfNinthOfMay(LocalDate estimatedDate) {
        return Stream.iterate(LocalDate.of(estimatedDate.getYear(), 5, 9), date -> date.plusDays(1))
                .limit(2)
                .collect(Collectors.toList());
    }

    public static List<LocalDate> getWeekendsOfNewYear(LocalDate estimatedDate) {
        return Stream.iterate(LocalDate.of(estimatedDate.getYear(), 1, 1), date -> date.plusDays(1))
                .limit(11)
                .collect(Collectors.toList());
    }
}
