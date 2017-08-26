package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;

public interface TimeAssetsService {

	Duration getTimeAssetsSumBefore(LocalDate day);

}
