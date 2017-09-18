package com.webnobis.alltime.persistence;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.FindService;

public interface EntryStore extends FindService {

	Entry storeEntry(Entry entry);

}