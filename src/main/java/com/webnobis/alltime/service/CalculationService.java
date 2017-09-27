package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import com.webnobis.alltime.model.CalculationType;
import com.webnobis.alltime.model.EntryType;

@FunctionalInterface
public interface CalculationService {

    Map<CalculationType, Duration> calculate(LocalDate day, EntryType type, LocalTime start, LocalTime end, Duration idleTime);

}
