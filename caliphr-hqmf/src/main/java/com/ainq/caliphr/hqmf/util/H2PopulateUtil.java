package com.ainq.caliphr.hqmf.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import com.ainq.caliphr.persistence.dao.SecureTableDao;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoSecureLight;
import com.zaxxer.hikari.HikariDataSource;

import lombok.val;

/**
 *
 * Utility to populate H2 with data needed for computation
 *
 * Notes:
 * 	- decided to use a straight JDBC approach, so data is copied row-by-row with no need of holding more rows
 * 	  than necessary in memory.  (Experimentation with utils like Spring's JbdcTemplate was not nearly as efficient
 * 	  and required many rows held in a collection).
 * 	- for a similar reason, will not use JPA for this purpose
 * 	- used a lamba function to populate the destination statement from the source result set
 * 	- optimized by using batching and appropriate fetch size
 * 	- note the use of the relatively new try-with-resources statement syntax below, 
 * 	  the jdbc resources are automatically closed
 *
 * @author drosenbaum
 *
 */
@Component
public class H2PopulateUtil {
	
	static Logger logger = LoggerFactory.getLogger(H2PopulateUtil.class);
			
	private static final int BATCH_SIZE = 75;
	private static final int FETCH_SIZE = 500;

	private JdbcTemplate jt;

	private volatile boolean initialized = false;

	// only capture the timing of the 1000 worst performing queries
	private StopWatch stopWatch = new StopWatch(1000);

	// Hold the provider ID and reporting period with the instance
	private Integer providerId;
	private String reportingPeriodStart;
	private String reportingPeriodEnd;
	
	@Autowired
	@Qualifier("dataSource")
	private DataSource srcDs;

	@Autowired
	private SecureTableDao secureTableDao;
	
	public int execSQLQuery(String sqlString, boolean outputResults) {
		return execSQLQuery(sqlString, outputResults, "");
	}

	public int execSQLQuery(String sqlString, boolean outputResults, String id) {
		List<Map<String, Object>> list = execSQLQueryForResults(sqlString);
		if (logger.isDebugEnabled()) {
			logger.debug("{} size = {} {}", id, list.size(), list.size() > 0 ? " >0" : "");
			if (outputResults) {
				logger.debug(list.toString().replaceAll("\\},", "},\n"));
			}
		}
		return list.size();
	}

	private List<Map<String, Object>> execSQLQueryForResults(String sqlString) {
		return jt.queryForList(sqlString);
	}

	public void execSQLUpdate(String sqlString) {
		jt.update(sqlString);
//		if (!sqlString.startsWith("CREATE TABLE")) {
//			execSQLQuery("EXPLAIN ANALYZE " + sqlString.replaceFirst("CREATE VIEW.*? AS" , ""), true);
//		}
	}

	public void initH2() {
		synchronized (H2PopulateUtil.class) {

			if (jt == null) {
				HikariDataSource destDs = new HikariDataSource();
				destDs.addDataSourceProperty(
						"URL",
						"jdbc:h2:mem:test;" +
						"MODE=PostgreSQL;" +
						"DB_CLOSE_DELAY=-1;" +
						"LOG=0;" +
						"CACHE_SIZE=65536;" +
						//"LOCK_MODE=0;" +
						"MVCC=true;" +
						"LOCK_TIMEOUT=20000;" +
						"UNDO_LOG=0;" +
						"MULTI_THREADED=true;" +
						//"TRACE_LEVEL_FILE=3;" + 
						//"TRACE_LEVEL_SYSTEM_OUT=3;" +
						""
				);
				destDs.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
				destDs.setMaximumPoolSize(20);
				jt=new JdbcTemplate(destDs);
			}

			ResourceDatabasePopulator populator=new ResourceDatabasePopulator();
			if (!initialized) {
				populator.addScript(new ClassPathResource("common_h2.sql"));
			}
			populator.addScript(new ClassPathResource("populate_h2.sql"));
			populator.addScript(new ClassPathResource("views.sql"));

			try (Connection connection = jt.getDataSource().getConnection()) {
				populator.populate(connection);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			SystemUtil.outputMemoryInfo();

			long start = System.currentTimeMillis();

			// load the data in multiple threads
			ExecutorService executor = Executors.newWorkStealingPool();
			//ExecutorService executor = Executors.newSingleThreadExecutor();
			//ExecutorService executor = Executors.newFixedThreadPool(3);
			if (!initialized) {
				executor.execute(() -> copyValueSetCodes());
			}
			executor.execute(() -> copyPatientInfo());
			executor.execute(() -> copyPatientProcedures());
			executor.execute(() -> copyPatientAllergies());
			executor.execute(() -> copyPatientImmunizations());
			executor.execute(() -> copyPatientMedications());
			executor.execute(() -> copyPatientVitalSigns());
			executor.execute(() -> copyPatientEncounters());
			executor.execute(() -> copyPatientProblems());
			executor.execute(() -> copyPatientResults());
			executor.execute(() -> copyMedicalEquipment());
			executor.execute(() -> copyPatientPlanOfCare());
			executor.execute(() -> copyPatientSocialHistories());
			executor.shutdown();
			try {
				executor.awaitTermination(600, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			logger.debug("loads took {} seconds", String.format("%.2f", ((double)System.currentTimeMillis()-start)/1000));
			initialized = true;
			
			testSQLUpdate("ANALYZE", "analysis for index optimization");

			SystemUtil.outputMemoryInfo();
		}
	}

	private void copyValueSetCodes() {
		String columns = "record_id, value_set_id, code_id";
		doLoad(
				"value_set_code",
				"select " + columns + " from caliphr.value_set_code",
				"INSERT INTO common.value_set_code VALUES (?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					return true;
				}
		);
	}

	
	private void copyPatientProcedures() {
		
		// use COALESCE(connected_record_id, record_id) so connected records get inserted using the same record_id

		String pgColumns = "COALESCE(p.connected_record_id, p.record_id), patient_id, COALESCE(to_code_id, p.code_id), effective_time_start, effective_time_end, status_code_name, value_string, value_code_id, result_value_code_id, CASE WHEN p.negation_detail_id is not null THEN coalesce(n.code_id, 0) ELSE null END as negation_code_id, reason_code_id";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name, value_string, value_code_id, result_value_code_id, negation_code_id, reason_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_procedure p left outer join caliphr.patient_negation_detail n on p.negation_detail_id = n.record_id left outer join caliphr.code_mapping cm on p.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where p.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and p.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_procedure",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_procedure (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					return true;
				}
		);
	}

	private void copyPatientAllergies() {

		String pgColumns = "a.record_id, patient_id, COALESCE(to_code_id, code_id), effective_time_start, effective_time_end, status_code_name";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_allergy a left outer join caliphr.code_mapping cm on a.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where a.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and a.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_allergy",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_allergy (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					return true;
				}
		);
	}

	private void copyPatientMedications() {

		String pgColumns = "m.record_id, patient_id, effective_time_start, effective_time_end, status_code_name, COALESCE(to_code_id, product_code_id), reason_code_id";
		String h2Columns = "record_id, patient_id, effective_time_start, effective_time_end, status_code_name, product_code_id, reason_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_medication m left outer join caliphr.code_mapping cm on m.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where m.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and m.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_medication",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_medication (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					return true;
				}
		);
	}

	private void copyPatientImmunizations() {

		String pgColumns = "i.record_id, patient_id, effective_time_start, effective_time_end, status_code_name, COALESCE(to_code_id, product_code_id)";
		String h2Columns = "record_id, patient_id, effective_time_start, effective_time_end, status_code_name, product_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_immunization i left outer join caliphr.code_mapping cm on i.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where i.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and i.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_immunization",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_immunization (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					return true;
				}
		);
	}

	private void copyPatientVitalSigns() {

		String pgColumns = "v.record_id, patient_id, COALESCE(to_code_id, code_id), effective_time_start, effective_time_end, status_code_name, record_value, record_value_unit, value_code_id";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name, record_value, record_value_unit, value_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_vital_sign v left outer join caliphr.code_mapping cm on v.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where v.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and v.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_vital_sign",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_vital_sign (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					return true;
				}
		);
	}

	private void copyPatientEncounters() {

		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, facility_location_code_id";
		String sql = getPatientEncounterSQL();

		doLoad(
				"patient_encounter",
				sql,
				"INSERT INTO caliphr.patient_encounter (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setObject(++i, srcRs.getObject(i));
					return true;
				}
		);
	}
	
	protected String getPatientEncounterSQL() {
		String pgColumns = "e.record_id, patient_id, COALESCE(to_code_id, e.code_id), effective_time_start, effective_time_end, facility_location_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_encounter e left outer join caliphr.code_mapping cm on e.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where e.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and e.patient_id in ").append(getPatientIdsForProviderSubquery());
		}
		return srcSql.toString();
	}

	private void copyPatientProblems() {

		// use COALESCE(connected_record_id, record_id) so connected records get inserted using the same record_id
		
		String pgColumns = "COALESCE(connected_record_id, p.record_id), patient_id, code_id, effective_time_start, effective_time_end, status_code_name, COALESCE(to_code_id, problem_code_id), ordinality_code_id, severity_code_id";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name, problem_code_id, ordinality_code_id, severity_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_problem p left outer join caliphr.code_mapping cm on p.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where p.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and p.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_problem",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_problem (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					return true;
				}
		);
	}

	private void copyPatientResults() {
		
		// use COALESCE(connected_record_id, record_id) so connected records get inserted using the same record_id

		String pgColumns = "COALESCE(connected_record_id, r.record_id), patient_id, COALESCE(to_code_id, code_id), effective_time_start, effective_time_end, status_code_name, result_value, result_value_unit, value_code_id";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name, result_value, result_value_unit, value_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_result r left outer join caliphr.code_mapping cm on r.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where r.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and r.patient_id in ").append(getPatientIdsForProviderSubquery());
		}
		
		doLoad(
				"patient_result",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_result (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setString(++i, srcRs.getString(i));
					return true;
				}
		);
	}

	private void copyMedicalEquipment() {

		String pgColumns = "m.record_id, patient_id, COALESCE(to_code_id, code_id), effective_time_start, effective_time_end, status_code_name";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_medical_equipment m left outer join caliphr.code_mapping cm on m.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where m.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and m.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_medical_equipment",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_medical_equipment (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					return true;
				}
		);
	}

	private void copyPatientPlanOfCare() {

		String pgColumns = "p.record_id, patient_id, COALESCE(to_code_id, code_id), effective_time_start, effective_time_end, status_code_name";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_plan_of_care p left outer join caliphr.code_mapping cm on p.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where p.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and p.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_plan_of_care",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_plan_of_care (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					return true;
				}
		);
	}

	private void copyPatientSocialHistories() {

		String pgColumns = "s.record_id, patient_id, COALESCE(to_code_id, code_id), effective_time_start, effective_time_end, status_code_name, value_code_id";
		String h2Columns = "record_id, patient_id, code_id, effective_time_start, effective_time_end, status_code_name, value_code_id";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_social_history s left outer join caliphr.code_mapping cm on s.code_mapping_id = cm.record_id left outer join caliphr.code_mapping_to_code cmc on cm.record_id = cmc.code_mapping_id where s.date_disabled is null");
		if (providerId != null && providerId > 0) {
			srcSql.append(" and s.patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		doLoad(
				"patient_social_history",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_social_history (" + h2Columns + ")" + " VALUES (?,?,?,?,?,?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					destStmt.setLong(++i, srcRs.getLong(i));
					destStmt.setInt(++i, srcRs.getInt(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					destStmt.setLong(++i, normalizeEffectiveStartTime(srcRs.getTimestamp(i)));
					destStmt.setLong(++i, normalizeEffectiveEndTime(srcRs.getTimestamp(i)));
					destStmt.setString(++i, srcRs.getString(i));
					destStmt.setObject(++i, srcRs.getObject(i));
					return true;
				}
		);
	}
	
	private String getPatientIdsForProviderSubquery() {
		return String.format(
			"(select distinct patient_id from caliphr.patient_encounter e1" +
			" where e1.provider_id = %s and e1.date_disabled is null" +
			" and e1.effective_time_start >= '%s' and e1.effective_time_start <= '%s')", providerId, reportingPeriodStart, reportingPeriodEnd);
	}

	private void doLoad(String loadType, String srcSql, String destSql, ResultSetCallback rsCallback) {
		long start = System.currentTimeMillis();

		try (
				Connection srcCon = srcDs.getConnection();
				Statement srcStmt = srcCon.createStatement();

				Connection destCon = jt.getDataSource().getConnection();
				PreparedStatement destStmt = destCon.prepareStatement(destSql);
		) {
			int count = 0;
			srcStmt.setFetchSize(FETCH_SIZE);

//			logger.debug("\n" + SQLFormatterUtil.format(srcSql));
			
			ResultSet rs = srcStmt.executeQuery(srcSql);
			while (rs.next()) {
				if (rsCallback.assign(destStmt, rs)) {
					destStmt.addBatch();
	
					// execute the batch every 75 records
					if (++count % BATCH_SIZE == 0) {
						destStmt.executeBatch();
						destStmt.clearBatch();
					}
				}
			}
			destStmt.executeBatch(); // insert remaining records

			logger.debug("{} data load took {}  ms for {} records", loadType, (System.currentTimeMillis()-start), count);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		// jdbc resources will be closed automatically due to try-with-resources statement syntax

	}

	private interface ResultSetCallback {
		boolean assign(PreparedStatement destStmt, ResultSet sourcsRs) throws SQLException;
	}
	
	// patient_info needs special handling due to encrypted columns
	private void copyPatientInfo() {

		String pgColumns = "patient_id, c.code_name";
		String h2Columns = "patient_id, gender";
		StringBuilder srcSql = new StringBuilder();
		srcSql.append("select ").append(pgColumns).append(" from caliphr.patient_info p left outer join caliphr.code c on p.gender_code_id = c.code_id");
		if (providerId != null && providerId > 0) {
			srcSql.append(" where patient_id in ").append(getPatientIdsForProviderSubquery());
		}

		val patientIds = new ArrayList<Integer>();
		
		doLoad(
				"patient_info",
				srcSql.toString(),
				"INSERT INTO caliphr.patient_info (" + h2Columns + ")" + " VALUES (?,?)",
				(destStmt, srcRs) -> {
					int i = 0;
					int patientId = srcRs.getInt(++i);
					patientIds.add(patientId);
					destStmt.setInt(i, patientId);
					destStmt.setString(++i, srcRs.getString(i));
					return true;
				}
		);
		
		List<PatientInfoSecureLight> patientSecureData = secureTableDao.findPatientInfoSecureLight(patientIds);
		jt.batchUpdate("UPDATE caliphr.patient_info SET birth_time=?, death_date=? where patient_id=?", patientSecureData, BATCH_SIZE,
				(ps, patient) -> { 
					if (patient.getBirthTime() != null) {
						ps.setLong(1, patient.getBirthTime().getTime());
					} else {
						ps.setNull(1, Types.NUMERIC);
					}
					
					if (patient.getDeathDate() != null) {
						ps.setLong(2, patient.getDeathDate().getTime());
					} else {
						ps.setNull(2, Types.NUMERIC);
					}
					ps.setInt(3,  patient.getId());
				}
		);
	}


	/**
	 * For data elements with unknown/missing start times, set it to a date far in the past
	 */
	@SuppressWarnings("deprecation")
	private static final long NULL_EFFECTIVE_START_TIME = new Timestamp(0,0,0,0,0,0,0).getTime();
	
	private long normalizeEffectiveStartTime(Timestamp timestamp) {
		return timestamp != null ? timestamp.getTime() : NULL_EFFECTIVE_START_TIME;
	}

	/**
	 * For data elements with unknown/missing end times, set it to a date far in the future
	 */
	@SuppressWarnings("deprecation")
	private static final long NULL_EFFECTIVE_END_TIME = new Timestamp(9999,0,0,0,0,0,0).getTime();
	
	@SuppressWarnings("deprecation")
	private long normalizeEffectiveEndTime(Timestamp timestamp) {
		if (timestamp == null) {
			return NULL_EFFECTIVE_END_TIME;
		}
		
		// if there is no time portion specified, set to last second of the day 23:59:59
		if (timestamp.getHours() == 0 && timestamp.getMinutes() == 0) {
			timestamp.setHours(23);
			timestamp.setMinutes(59);
			timestamp.setSeconds(59);
		}
		return timestamp.getTime();
	}

	public boolean testSQLQuery(String sqlString, String id) {
		return testSQLQuery(sqlString, id, false);
	}

	public boolean testSQLQuery(String sqlString, String id, boolean outputResults) {
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + SQLFormatterUtil.format(sqlString));
			stopWatch.start(id);
		}
		try {
			int count = execSQLQuery(sqlString, outputResults, id);
			
			if (logger.isDebugEnabled()) {
				long elapsed = stopWatch.stop(id, count);
				logger.debug("**SUCCESS** {} (took {} seconds)", id, String.format("%.2f", (double)elapsed/1000));
				if (count > 0 && id.contains("_CMS")) {
					logger.debug("** POP returned result!!! {}", id);
				}
			}
			//count = execSQLQuery("EXPLAIN ANALYZE " + sqlString, true); 
		} catch (Exception e) {
			logException(id, e);
			throw new RuntimeException(e);
			//return false;
		}
		return true;
	}

	public boolean testSQLUpdate(String sqlString, String id) {
		//System.out.println(sqlString);
		
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + SQLFormatterUtil.format(sqlString));
			stopWatch.start(id);
		}
		try {
			execSQLUpdate(sqlString);
			
			if (logger.isDebugEnabled()) {
				long elapsed = stopWatch.stop(id);
				logger.debug("**SUCCESS** {} (took {} seconds)", id, String.format("%.2f", (double)elapsed/1000));
			}
		} catch (Exception e) {
			logException(id, e);
			throw new RuntimeException(e);
			//return false;
		}
		return true;
	}

	public List<Map<String, Object>> testSQLQueryWithResults(String sqlString, String id) {
		if (logger.isDebugEnabled()) {
			logger.debug("\n" + SQLFormatterUtil.format(sqlString));
			stopWatch.start(id);
		}
		try {
			List<Map<String, Object>> list = execSQLQueryForResults(sqlString);
			
			if (logger.isDebugEnabled()) {
				int count = list.size();
				long elapsed = stopWatch.stop(id, count);
				logger.debug("**SUCCESS** {} (took {} seconds)", id, String.format("%.2f", (double)elapsed/1000));
				if (count > 0 && id.contains("_CMS")) {
					logger.debug("** POP returned result!!! {}", id);
				}
			}
			//count = execSQLQuery("EXPLAIN ANALYZE " + sqlString, true); 
			return list;
		} catch (Exception e) {
			logException(id, e);
			throw new RuntimeException(e);
			//return false;
		}
	}

	public void resetToFreshDatabase(Integer providerId, Date reportingPeriodStart, Date reportingPeriodEnd) {

		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
		
		// Set the provider and reporting period for this instance
		this.providerId = providerId;
		this.reportingPeriodStart = dateFormat.format(reportingPeriodStart) + " 00:00:00";
		this.reportingPeriodEnd   = dateFormat.format(reportingPeriodEnd) + " 23:59:59";

		if (initialized) {
			dropAllObjects();
		}
		initH2();
		
		execSQLQuery("select 1", false);
		stopWatch.reset();
	}
	
	public void dropAllObjects() {
		logger.debug("dropping all H2 objects to release memory and resources");
		jt.update("DROP SCHEMA caliphr IF EXISTS");
	}

	private void logException(String id, Exception e) {
		Throwable deepest = e;
		while (deepest.getCause() != null) {
			deepest = deepest.getCause();
		}
		String message = deepest.getMessage();
		logger.error("**FAILED (" + id + ")**: " + message, e);
	}

	public String outputByElapsedTime() {
		return stopWatch.outputByElapsedTime();
	}

	public String outputByTaskName() {
		return stopWatch.outputByTaskName();
	}
	
}
