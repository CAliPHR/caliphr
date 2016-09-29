package com.ainq.caliphr.hqmf.util;

import java.sql.Timestamp;

/**
 * These functions are added as aliases to H2 and available to call in SQL.  The purpose is to calculate
 * time differences as defined in the Quality Data Mode (QDM).
 * 
 * Mostly ported from the PopHealth hqmf2js project, file hqmf_util.js.coffee
 * 
 * @author drosenbaum
 *
 */
public class H2TemporalComparisonUtil {
	
	/**
	 * Number of whole years between the two time stamps
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long yearsDifference(long ts1_millis, long ts2_millis) {
		Timestamp ts1 = new Timestamp(ts1_millis), ts2 = new Timestamp(ts2_millis);
	    if (ts2.getMonth() < ts1.getMonth()) {
	    	return ts2.getYear() - ts1.getYear() - 1;
	    } else if (ts2.getMonth() == ts1.getMonth() && ts2.getDate() >= ts1.getDate()) {
	    	return ts2.getYear() - ts1.getYear();
	    } else if (ts2.getMonth() == ts1.getMonth() && ts2.getDate() < ts1.getDate()) {
	    	return ts2.getYear() - ts1.getYear() - 1;
	    } else {
	    	return ts2.getYear() - ts1.getYear();
	    }
	}
	
	/**
	 * Number of whole months between the two time stamps
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long monthsDifference(long ts1_millis, long ts2_millis) {
		Timestamp ts1 = new Timestamp(ts1_millis), ts2 = new Timestamp(ts2_millis);
	    if (ts2.getDate() >= ts1.getDate()) {
	      return (ts2.getYear() - ts1.getYear()) * 12 + ts2.getMonth() - ts1.getMonth();
	    }
	    else {
	      return (ts2.getYear() - ts1.getYear()) * 12 + ts2.getMonth() - ts1.getMonth() - 1;
	    }
	}
	
	/**
	 * Number of whole minutes between the two time stamps
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	public static long minutesDifference(long ts1_millis, long ts2_millis) {
		return minutesDifference(new Timestamp(ts1_millis), new Timestamp(ts2_millis));
	}
	
	private static long minutesDifference(Timestamp ts1, Timestamp ts2) {
		ts1 = dropSeconds(ts1);
		ts2 = dropSeconds(ts2);
		return (long) Math.floor(((ts2.getTime()-ts1.getTime())/1000)/60);
	}
	
	/**
	 * Number of whole hours between the two time stamps
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	public static long hoursDifference(long ts1_millis, long ts2_millis) {
		return (long) Math.floor(minutesDifference(ts1_millis, ts2_millis)/60);
	}
	
	private static long hoursDifference(Timestamp ts1, Timestamp ts2) {
		return (long) Math.floor(minutesDifference(ts1, ts2)/60);
	}
	
	/**
	 * Number of days between the two time stamps
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long daysDifference(long ts1_millis, long ts2_millis) {
		Timestamp ts1 = new Timestamp(ts1_millis), ts2 = new Timestamp(ts2_millis);
		
		// have to discard time portion for day difference calculation purposes
		ts1 = new Timestamp(ts1.getYear(), ts1.getMonth(), ts1.getDate(), 0, 0, 0, 0);
		ts2 = new Timestamp(ts2.getYear(), ts2.getMonth(), ts2.getDate(), 0, 0, 0, 0);
    	return (long) Math.floor(hoursDifference(ts1,ts2)/24);
	}
	
	/**
	 * Number of whole weeks between the two time stamps
	 * 
	 * @param ts1
	 * @param ts2
	 * @return
	 */
	public static long weeksDifference(long ts1_millis, long ts2_millis) {
		return (long) Math.floor(daysDifference(ts1_millis, ts2_millis) / 7);
	}
	
	/**
	 * Drop the seconds from the supplied timeStamps
	 * 
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Timestamp dropSeconds(Timestamp ts) {
		Timestamp tsCopy = new Timestamp(ts.getTime());
		tsCopy.setSeconds(0);
		return tsCopy;
	}
	
	/**
	 * Number of milliseconds since Epoch.  Mostly used for more precise temporal comparisons
	 * 
	 * @param ts
	 * @return
	 */
	public static Long millisSinceEpoch(Timestamp ts) {
		return ts != null ? ts.getTime() : null;
	}
	
	public static Timestamp getTimestamp(long millis) {
		return new Timestamp(millis);
	}
	
	@SuppressWarnings("deprecation")
	public static long dropTime(long millis) {
		Timestamp ts = new Timestamp(millis);
		ts.setHours(0);
		ts.setMinutes(0);
		ts.setSeconds(0);
		ts.setNanos(0);
	    return ts.getTime();
	}

}
