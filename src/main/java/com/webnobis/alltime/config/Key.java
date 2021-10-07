package com.webnobis.alltime.config;

public enum Key {

	MAX_COUNT_OF_DAYS("max.count.of.days"), MAX_COUNT_OF_RANGE_BOOKING_DAYS("max.count.of.range.booking.days"),
	MAX_COUNT_OF_DESCRIPTIONS("max.count.of.descriptions"), TIME_RASTER_MINUTES("time.raster.minutes"),
	TIME_START_OFFSET_MINUTES("time.start.offset.minutes"), TIME_END_OFFSET_MINUTES("time.end.offset.minutes"),
	ITEM_DURATION_RASTER_MINUTES("item.duration.raster.minutes"), FILE_STORE_ROOT_PATH("file.store.root.path"),
	FILE_EXPORT_ROOT_PATH("file.export.root.path");

	private final String key;

	private Key(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}