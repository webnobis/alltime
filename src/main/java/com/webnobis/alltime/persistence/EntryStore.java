package com.webnobis.alltime.persistence;

import java.time.LocalDate;
import java.util.List;

import com.webnobis.alltime.model.Entry;

public interface EntryStore {
	
	List<LocalDate> getLastDays(LocalDate untilDay);

	Entry getEntry(LocalDate day);

	void storeEntry(Entry entry);

}