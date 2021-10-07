package com.webnobis.alltime.export;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.webnobis.alltime.model.Entry;

public interface EntryExport {

	List<Entry> exportRange(LocalDate fromDay, LocalDate untilDay);

	default List<Entry> exportMonth(YearMonth month) {
		return Optional.ofNullable(month).map(m -> exportRange(m.atDay(1), m.atEndOfMonth()))
				.orElseGet(Collections::emptyList);
	}

}
