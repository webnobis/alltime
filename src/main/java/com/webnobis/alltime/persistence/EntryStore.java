package com.webnobis.alltime.persistence;

import java.util.List;

import com.webnobis.alltime.model.Entry;

public interface EntryStore {

	List<Entry> getLastEntries(int maxCount);

	void storeEntry(Entry entry);

}