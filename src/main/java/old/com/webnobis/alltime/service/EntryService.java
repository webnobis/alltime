package old.com.webnobis.alltime.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;
import com.webnobis.alltime.persistence.EntryStore;
import com.webnobis.alltime.service.BookingService;
import com.webnobis.alltime.service.IdleTimeHandler;

public class EntryService implements BookingService {

	private final Supplier<LocalTime> now;

	private final int maxCount;

	private final Map<DayOfWeek, Duration> expectedTimes;

	private final IdleTimeHandler idleTimeHandler;

	private final EntryStore store;

	public EntryService(Supplier<LocalTime> now, int maxCount, Map<DayOfWeek, Duration> expectedTimes, Map<Duration, Duration> idleTimes, EntryStore store) {
		this.now = Objects.requireNonNull(now, "now is null");
		this.maxCount = maxCount;
		this.expectedTimes = Objects.requireNonNull(expectedTimes, "expectedTimes is null");
		this.idleTimeHandler = new IdleTimeHandler(idleTimes);
		this.store = Objects.requireNonNull(store, "store is null");
	}

	private Duration getTimeAssetsBefore(LocalDate day) {
		return store.getLastEntries(2).stream()
				.filter(entry -> entry.getDay().isBefore(day))
				.findFirst()
				.map(entry -> entry.getTimeAssets())
				.orElse(Duration.ZERO);
	}

	/* (non-Javadoc)
	 * @see com.webnobis.alltime.service.BookingService#startAZ(java.time.LocalDate, java.time.LocalTime)
	 */
	@Override
	public Entry startAZ(LocalDate day, LocalTime start) {
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		LocalTime now = this.now.get();
		Entry entry = new AZEntry(day, start, now, expectedTimes.get(DayOfWeek.from(day)), idleTimeHandler.getIdleTime(day, start, now), timeAssetsBefore);
		store.storeEntry(entry);
		return entry;
	}

	/* (non-Javadoc)
	 * @see com.webnobis.alltime.service.BookingService#endAZ(java.time.LocalDate, java.time.LocalTime, java.time.LocalTime, java.util.Map)
	 */
	@Override
	public Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Map<String, Duration> items) {
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		Entry entry = new AZEntry(day, start, end, expectedTimes.get(DayOfWeek.from(day)), idleTimeHandler.getIdleTime(day, start, end), timeAssetsBefore, items);
		store.storeEntry(entry);
		return entry;
	}

	/* (non-Javadoc)
	 * @see com.webnobis.alltime.service.BookingService#book(java.time.LocalDate, com.webnobis.alltime.model.EntryType, java.util.Map)
	 */
	@Override
	public Entry book(LocalDate day, EntryType type, Map<String, Duration> items) {
		if (EntryType.AZ.equals(type)) {
			throw new IllegalStateException(String.format("Please use 'startAZ' and 'endAZ' for %s type.", EntryType.AZ));
		}
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		Entry entry;
		if (EntryType.GT.equals(type)) {
			entry = new GTEntry(day, expectedTimes.get(DayOfWeek.from(day)), timeAssetsBefore, items);
		} else {
			entry = new DayEntry(day, type, timeAssetsBefore, items);
		}
		store.storeEntry(entry);
		return entry;
	}

	/* (non-Javadoc)
	 * @see com.webnobis.alltime.service.BookingService#book(java.time.LocalDate, com.webnobis.alltime.model.EntryType)
	 */
	@Override
	public Entry book(LocalDate day, EntryType type) {
		return book(day, type, Collections.emptyMap());
	}

	/* (non-Javadoc)
	 * @see com.webnobis.alltime.service.BookingService#book(java.time.LocalDate, java.time.LocalDate, com.webnobis.alltime.model.EntryType, java.util.Map)
	 */
	@Override
	public List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type, Map<String, Duration> items) {
		long days = Period.between(fromDay, untilDay).getDays();
		return LongStream.rangeClosed(0, days)
				.mapToObj(l -> fromDay.plusDays(l))
				.map(day -> book(day, type, items))
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see com.webnobis.alltime.service.BookingService#book(java.time.LocalDate, java.time.LocalDate, com.webnobis.alltime.model.EntryType)
	 */
	@Override
	public List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type) {
		return book(fromDay, untilDay, type, Collections.emptyMap());
	}

	public List<Entry> getLastEntries() {
		return store.getLastEntries(maxCount);
	}

}
