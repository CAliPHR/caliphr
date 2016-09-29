CREATE SCHEMA IF NOT EXISTS common;

-- The following functions are for time comparison purposes as defined in the QDM
CREATE ALIAS YEARS_DIFFERENCE FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.yearsDifference";
CREATE ALIAS MONTHS_DIFFERENCE FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.monthsDifference";
CREATE ALIAS MINUTES_DIFFERENCE FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.minutesDifference";
CREATE ALIAS HOURS_DIFFERENCE FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.hoursDifference";
CREATE ALIAS DAYS_DIFFERENCE FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.daysDifference";
CREATE ALIAS WEEKS_DIFFERENCE FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.weeksDifference";
CREATE ALIAS MILLIS_EPOCH FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.millisSinceEpoch";
--CREATE ALIAS TO_TIMESTAMP FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.getTimestamp";
CREATE ALIAS DROP_TIME FOR "com.ainq.caliphr.hqmf.util.H2TemporalComparisonUtil.dropTime";
CREATE ALIAS CLEAN_CAST_TO_DOUBLE FOR "com.ainq.caliphr.hqmf.util.H2NormalizationUtil.cleanCastToDouble";

CREATE TABLE common.value_set_code
(
  record_id integer NOT NULL primary key,
  value_set_id integer,
  code_id integer
);

CREATE INDEX common.value_set_code_1_IDX ON common.value_set_code(value_set_id);
CREATE INDEX common.value_set_code_2_IDX ON common.value_set_code(code_id);
