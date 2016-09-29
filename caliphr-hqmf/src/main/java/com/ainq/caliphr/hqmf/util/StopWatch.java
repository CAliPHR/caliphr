package com.ainq.caliphr.hqmf.util;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;


public class StopWatch {
	private Map<String, StopWatchEntry> entries = new HashMap<String, StopWatchEntry>();
	
	private long firstTaskStarted = 0;
	private long lastTaskEnded = 0;
	
	private int maxEntries = Integer.MAX_VALUE;
	
	public StopWatch() {
	
	}
	
	public StopWatch(int maxEntries) {
		this.maxEntries = maxEntries;
	}
 
	public void start(String taskId) {
		synchronized (this) {
			if (firstTaskStarted == 0) {
				firstTaskStarted = System.currentTimeMillis();
			}
			entries.put(taskId, new StopWatchEntry(System.currentTimeMillis()));
		}
	}
 
	public long stop(String taskId) {
		return stop(taskId, null, null);
	}
	
	public long stop(String taskId, String comment) {
		return stop(taskId, null, comment);
	}
	
	public long stop(String taskId, Integer numRows) {
		return stop(taskId, numRows, null);
	}
	
	public long stop(String taskId, Integer numRows, String comment) {
		synchronized (this) {
			lastTaskEnded = System.currentTimeMillis();
			StopWatchEntry stopEntry = entries.get(taskId);
			stopEntry.setElapsedTime(lastTaskEnded - stopEntry.getStart());
			stopEntry.setNumRows(numRows);
			stopEntry.setComment(comment);
			if (entries.size() >= maxEntries) {
				Map.Entry<String, StopWatchEntry> leastElapsedEntry = null;
				for (Map.Entry<String, StopWatchEntry> entry : entries.entrySet()) {
					if (leastElapsedEntry == null) {
						if (entry.getValue().getElapsedTime() != null) {
							leastElapsedEntry = entry;
						}
					}
					else if (entry.getValue().getElapsedTime() != null && leastElapsedEntry.getValue().getElapsedTime() > entry.getValue().getElapsedTime()) {
						leastElapsedEntry = entry;
					}
				}
				entries.remove(leastElapsedEntry.getKey());
			}
			if (entries.size() < maxEntries) { 
				// it is possible for the size to still be greater than the max in the case
				// that the least elapsed entry is the same as the one currently being processed
				entries.put(taskId, stopEntry);
			}
			return stopEntry.getElapsedTime();
		}
	}
 
	public long totalElapsed() {
		return lastTaskEnded - firstTaskStarted;
	}
	
	public String outputByElapsedTime() {
		return outputOrdered("Elapsed Time", Collections.reverseOrder((e1, e2) -> {
			if (e1.getValue().getElapsedTime() == null) {
				return -1;
			}
			if (e2.getValue().getElapsedTime() == null) {
				return 1;
			}
			return Long.compare(e1.getValue().getElapsedTime(), e2.getValue().getElapsedTime());
		}));
	}
	
	public String outputByTaskName() {
		return outputOrdered("Task Name", Comparator.comparing(e -> e.getKey()));
	}
	
	private String outputOrdered(String orderType, Comparator<Entry<String, StopWatchEntry>> comparator) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		//nf.setMinimumIntegerDigits(5);
		nf.setGroupingUsed(true);
				
		NumberFormat pf = NumberFormat.getPercentInstance();
		//pf.setMinimumIntegerDigits(3);
		pf.setGroupingUsed(false);

		int maxKeyLength = 0;
		for (String key : entries.keySet()) {
			maxKeyLength = Math.max(key.length(), maxKeyLength);
		}
		final int targetTab = (maxKeyLength / 8) + 1; 

		StringBuilder out = new StringBuilder();
		long totalElapsed = totalElapsed();
		double seconds = (double)totalElapsed/1000;
		
		String title = "Top entries ordered by " + orderType;
		out.append(title).append("\n");
		IntStream.rangeClosed(1, title.length()).forEach(c -> out.append("="));
		out.append("\n");
		
		entries.entrySet().stream().sorted(comparator)
			.forEach(e -> {
				out.append(e.getKey());
				int numTabs = targetTab-(e.getKey().length() / 8);
				for (int i = 0; i < numTabs; i++) {
					out.append("\t");
				}
				Integer numRows = e.getValue().getNumRows();
				if (e.getValue().getElapsedTime() != null) {
					double elapsedTime = e.getValue().getElapsedTime();
					out.append(nf.format(elapsedTime/1000))
					   .append("\t(") 
					   .append(pf.format(elapsedTime / totalElapsed))
					   .append(")\t")
					   .append(numRows != null ? numRows : "")
					   .append("\t")
					   .append(e.getValue().getComment() != null ? e.getValue().getComment() : "");
				}
				out.append("\n");
			}
		);
		out.append("\nTotal elapsed time: " + nf.format(seconds) + " (" + nf.format(seconds/60) + " minutes), total " + entries.size() + " entries\n\n");
		return out.toString();	
	}
	
	public void reset() {
		this.firstTaskStarted = this.lastTaskEnded = 0;
		this.entries.clear();
	}
	
	private class StopWatchEntry {
		long start;
		Long elapsedTime;
		Integer numRows;
		private String comment;
		public StopWatchEntry(long start) {
			this.start = start;
		}
		public long getStart() {
			return start;
		}
		public Long getElapsedTime() {
			return elapsedTime;
		}
		public void setElapsedTime(Long elapsedTime) {
			this.elapsedTime = elapsedTime;
		}
		public Integer getNumRows() {
			return numRows;
		}
		public void setNumRows(Integer numRows) {
			this.numRows = numRows;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			if (comment != null && comment.length() > 140) {
				comment = comment.substring(0, 139);
			}
			this.comment = comment;
		}
	}
}
