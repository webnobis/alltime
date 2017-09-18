package com.webnobis.alltime.service;

import java.time.LocalDate;
import java.util.List;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.TimeAssetsSum;

public interface FindService {
	
	List<LocalDate> getLastDays();
	
	Entry getEntry(LocalDate day);
	
	List<String> getLastDescriptions();

	TimeAssetsSum getTimeAssetsSumBefore(LocalDate day);

}
