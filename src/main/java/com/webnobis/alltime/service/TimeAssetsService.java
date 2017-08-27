package com.webnobis.alltime.service;

import java.time.LocalDate;

import com.webnobis.alltime.model.TimeAssetsSum;

public interface TimeAssetsService {

	TimeAssetsSum getTimeAssetsSumBefore(LocalDate day);

}
