package com.webnobis.alltime.persistence;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.FindService;
import com.webnobis.alltime.service.TimeAssetsService;

public interface EntryStore extends FindService, TimeAssetsService {

	Entry storeEntry(Entry entry);

}