package com.ainq.caliphr.persistence.dao.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ainq.caliphr.common.model.patient.Patient;
import com.ainq.caliphr.persistence.dao.SecureTableDao;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoSecureLight;
import com.ainq.caliphr.persistence.model.ccda.PatientInfoHolder.PatientPhoneNumberHolder;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.*;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.PatientInfoRepository;
import com.ainq.caliphr.persistence.model.obj.caliphrDb.repository.PatientPhoneNumberRepository;
import com.ainq.caliphr.persistence.util.DatabaseEncyptionUtil;

import lombok.val;

/**
 * Use Postgres column encryption for certain data fields that are considered PHI/PII
 * 
 * @author drosenbaum
 *
 */
@Repository
public class SecureTableDaoImpl implements SecureTableDao {

    @Autowired
    private PatientInfoRepository patientInfoRepository;

    @Autowired
    private PatientPhoneNumberRepository patientPhoneNumberRepository;

    private static final List<String> PATIENT_INFO_SECURE_NON_ENCRYPTED_COLUMN_NAMES =
            Arrays.asList(new String[]{"patient_id", "date_created", "user_created", "date_updated", "user_updated", "mrn_hash"});

    private static final List<String> PATIENT_INFO_SECURE_ENCRYPTED_COLUMN_NAMES =
            Arrays.asList(new String[]{
                    "medical_record_number", "ssn", "first_name", "last_name", "address", "address2", "city", "state_id",
                    "state_value", "zipcode", "country", "birth_time", "death_date"});

    private static final List<String> PATIENT_ALL_INFO_NON_ENCRYPTED_COLUMN_NAMES =
            Arrays.asList(new String[]{"p.patient_id", "gc.code_name as gender_code_name", "p.date_created", "p.user_created", "p.date_updated", "p.user_updated", "mrn_hash, rc.code_name as race_code_name, ec.code_name as ethnicity_code_name, p.language_communication"});

    private static final List<String> PATIENT_ALL_INFO_ENCRYPTED_COLUMN_NAMES =
            Arrays.asList(new String[]{
                    "medical_record_number", "ssn", "first_name", "last_name", "address", "address2", "city", "state_id",
                    "state_value", "zipcode", "country", "birth_time", "death_date"});
    private static final List<String> PATIENT_INFO_SECURE_LIGHT_NON_ENCRYPTED_COLUMN_NAMES = Arrays.asList(new String[]{"patient_id"});
    private static final List<String> PATIENT_INFO_SECURE_LIGHT_ENCRYPTED_COLUMN_NAMES = Arrays.asList(new String[]{"birth_time", "death_date"});
    private static final List<String> PATIENT_BASIC_INFO_NON_ENCRYPTED_COLUMN_NAMES = Arrays.asList(new String[]{"p.patient_id", "gc.code_name as gender_code_name"});
    private static final List<String> PATIENT_BASIC_INFO_ENCRYPTED_COLUMN_NAMES = Arrays.asList(new String[]{"first_name", "last_name", "birth_time"});

    private static final List<String> PATIENT_PHONE_NUMBER_NON_ENCRYPTED_COLUMN_NAMES = Arrays.asList(new String[]{"s.record_id"});
    private static final List<String> PATIENT_PHONE_NUMBER_ENCRYPTED_COLUMN_NAMES = Arrays.asList(new String[]{"phone_number"});

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseEncyptionUtil databaseEncyptionUtil;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void savePatientInfoHolder(PatientInfoHolder holder) {
        PatientInfo patientInfo = holder.getPatientInfo();

        val nonEncryptedClmnVals = new MapSqlParameterSource();
        addParamValue(nonEncryptedClmnVals, "patient_id", patientInfo.getId());
        addParamValue(nonEncryptedClmnVals, "date_created", patientInfo.getDateCreated());
        addParamValue(nonEncryptedClmnVals, "user_created", patientInfo.getUserCreated());
        addParamValue(nonEncryptedClmnVals, "date_updated", patientInfo.getDateUpdated());
        addParamValue(nonEncryptedClmnVals, "user_updated", patientInfo.getUserUpdated());
        addParamValue(nonEncryptedClmnVals, "mrn_hash", getHashValue(holder.getMedicalRecordNumber()));


        val encryptedClmnVals = new MapSqlParameterSource();
        addParamValue(encryptedClmnVals, "medical_record_number", holder.getMedicalRecordNumber());
        addParamValue(encryptedClmnVals, "ssn", holder.getSsn());
        addParamValue(encryptedClmnVals, "first_name", holder.getFirstName());
        addParamValue(encryptedClmnVals, "last_name", holder.getLastName());
        addParamValue(encryptedClmnVals, "address", holder.getAddress());
        addParamValue(encryptedClmnVals, "address2", holder.getAddress2());
        addParamValue(encryptedClmnVals, "city", holder.getCity());
        addParamValue(encryptedClmnVals, "state_id", holder.getStateId() != null ? holder.getStateId().toString() : null);
        addParamValue(encryptedClmnVals, "state_value", holder.getStateValue());
        addParamValue(encryptedClmnVals, "zipcode", holder.getZipcode());
        addParamValue(encryptedClmnVals, "country", holder.getCountry());
        addParamValue(encryptedClmnVals, "birth_time", holder.getBirthTime() != null ? String.valueOf(holder.getBirthTime().getTime()) : null);
        addParamValue(encryptedClmnVals, "death_date", holder.getDeathDate() != null ? String.valueOf(holder.getDeathDate().getTime()) : null);

        insert("caliphr.patient_info_secure", nonEncryptedClmnVals, encryptedClmnVals);
    }

    private List<PatientInfoHolder> getPatientInfoHolder(String whereSql, MapSqlParameterSource paramValues, List<Integer> whereClmnTypes) {
        return select("caliphr.patient_info_secure",
                PATIENT_INFO_SECURE_NON_ENCRYPTED_COLUMN_NAMES,
                PATIENT_INFO_SECURE_ENCRYPTED_COLUMN_NAMES,
                whereSql,
                paramValues,
                (rs, rowNum) -> {
                    PatientInfo patientInfo = patientInfoRepository.findOne(rs.getInt("patient_id"));
                    PatientInfoHolder holder = new PatientInfoHolder(patientInfo);
                    holder.setAddress(rs.getString("address"));
                    holder.setAddress2(rs.getString("address2"));
                    holder.setBirthTime(new Date(Long.parseLong(rs.getString("birth_time"))));

                    String deathDateStr = rs.getString("death_date");
                    holder.setDeathDate(deathDateStr != null ? new Date(Long.parseLong(deathDateStr)) : null);

                    holder.setCity(rs.getString("city"));
                    holder.setCountry(rs.getString("country"));
                    holder.setFirstName(rs.getString("first_name"));
                    holder.setLastName(rs.getString("last_name"));
                    holder.setMedicalRecordNumber(rs.getString("medical_record_number"));
                    holder.setSsn(rs.getString("ssn"));
                    holder.setStateId(rs.getInt("state_id"));
                    holder.setStateValue(rs.getString("state_value"));
                    holder.setZipcode(rs.getString("zipcode"));
                    return holder;
                }
        );
    }

    @Override
    public PatientInfoHolder findPatientBySSN(String ssn) {
        List<PatientInfoHolder> result = getPatientInfoHolder(
                "ssn_hash = :ssn_hash",
                new MapSqlParameterSource("ssn_hash", getHashValue(ssn)),
                Arrays.asList(new Integer[]{Types.BINARY}));
        if (result.size() > 1) {
            throw new IllegalStateException("more than one patient record matched the given SSN");
        }
        return result.size() == 1 ? result.get(0) : null;
    }

    @Override
    public PatientInfoHolder findPatientBySourceAndMRN(PracticeGroup practiceGroup, String medicalRecordNumber) {
        List<PatientInfoHolder> result = getPatientInfoHolder(
                "mrn_hash = :mrn_hash",
                new MapSqlParameterSource("mrn_hash", getHashValue(medicalRecordNumber)),
                Arrays.asList(new Integer[]{Types.BINARY}));

        //
        // Find the patient that matches the source
        if (result != null && practiceGroup != null && practiceGroup.getId() != null) {
            for (PatientInfoHolder patientInfoHolder : result) {
                if (patientInfoHolder.getPatientInfo() != null && patientInfoHolder.getPatientInfo().getGroup() != null
                        && patientInfoHolder.getPatientInfo().getGroup().getId() != null
                        && practiceGroup != null && practiceGroup.getId() != null
                        && patientInfoHolder.getPatientInfo().getGroup().getId().equals(practiceGroup.getId())) {
                    return patientInfoHolder;
                }
            }
        }

        return null;
    }

    @Override
    public Byte[] createMrnHash(String medicalRecordNumber) {
        return ArrayUtils.toObject(getHashValue(medicalRecordNumber));
    }

    @Override
    public List<PatientInfoSecureLight> findPatientInfoSecureLight(List<Integer> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return Collections.emptyList();
        }
        return select("caliphr.patient_info_secure",
                PATIENT_INFO_SECURE_LIGHT_NON_ENCRYPTED_COLUMN_NAMES,
                PATIENT_INFO_SECURE_LIGHT_ENCRYPTED_COLUMN_NAMES,
                "patient_id IN (:patientIds)",
                new MapSqlParameterSource("patientIds", patientIds),
                (rs, rowNum) -> {
                    PatientInfoSecureLight patientInfoSecureLight = new PatientInfoSecureLight();
                    patientInfoSecureLight.setId(rs.getInt("patient_id"));
                    patientInfoSecureLight.setBirthTime(rs.getString("birth_time") != null ? new Date(Long.parseLong(rs.getString("birth_time"))) : null);

                    String deathDateStr = rs.getString("death_date");
                    patientInfoSecureLight.setDeathDate(deathDateStr != null ? new Date(Long.parseLong(deathDateStr)) : null);

                    return patientInfoSecureLight;
                }
        );
    }

    @Override
    public List<Patient> findAllPatientInfo(List<Integer> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return Collections.emptyList();
        }
        val params = new MapSqlParameterSource();
        params.addValue("patientIds", patientIds);
        params.addValue("key", databaseEncyptionUtil.getKey());

        return select("caliphr.patient_info p JOIN caliphr.patient_info_secure s ON p.patient_id = s.patient_id " +
        		"left outer join caliphr.code gc on p.gender_code_id = gc.code_id " +
        		"left outer join caliphr.code rc on p.race_code_id = rc.code_id " +
        		"left outer join caliphr.code ec on p.ethnicity_code_id = ec.code_id ",
                PATIENT_ALL_INFO_NON_ENCRYPTED_COLUMN_NAMES,
                PATIENT_ALL_INFO_ENCRYPTED_COLUMN_NAMES,
                "p.patient_id IN (:patientIds)",
                params,
                (rs, rowNum) -> {
                    Patient patient = new Patient();
                    patient.setId(rs.getInt("patient_id"));
                    patient.setMedicalRecordNumber(rs.getString("medical_record_number"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    patient.setGender(rs.getString("gender_code_name"));
                    patient.setAddress(rs.getString("address"));
                    patient.setAddress2(rs.getString("address2"));
                    patient.setCity(rs.getString("city"));
                    patient.setStateId(rs.getString("state_id"));
                    patient.setStateValue(rs.getString("state_value"));
                    patient.setZipcode(rs.getString("zipcode"));
                    patient.setCountry(rs.getString("country"));
                    patient.setBirthTime(rs.getString("birth_time") != null ? new Date(Long.parseLong(rs.getString("birth_time"))) : null);
                    patient.setDeathDate(rs.getString("death_date") != null ? new Date(Long.parseLong(rs.getString("death_date"))) : null);
                    patient.setRace(rs.getString("race_code_name"));
                    patient.setEthnicity(rs.getString("ethnicity_code_name"));
                    patient.setLanguage(rs.getString("language_communication"));
                    return patient;
                }
        );
    }

    @Override
    public List<Patient> findPatientBasicInfo(List<Integer> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return Collections.emptyList();
        }
        val params = new MapSqlParameterSource();
        params.addValue("patientIds", patientIds);
        params.addValue("key", databaseEncyptionUtil.getKey());

        return select("caliphr.patient_info p JOIN caliphr.patient_info_secure s ON p.patient_id = s.patient_id " +
        		"left outer join caliphr.code gc on p.gender_code_id = gc.code_id ",
                PATIENT_BASIC_INFO_NON_ENCRYPTED_COLUMN_NAMES,
                PATIENT_BASIC_INFO_ENCRYPTED_COLUMN_NAMES,
                "p.patient_id IN (:patientIds)",
                params,
                (rs, rowNum) -> {
                    Patient patient = new Patient();
                    patient.setId(rs.getInt("patient_id"));
                    patient.setGender(rs.getString("gender_code_name"));
                    patient.setFirstName(rs.getString("first_name"));
                    patient.setLastName(rs.getString("last_name"));
                    patient.setBirthTime(new Date(Long.parseLong(rs.getString("birth_time"))));
                    return patient;
                }
        );
    }

    @Override
    public PatientPhoneNumberHolder findPhoneNumberByNumberAndType(Integer patientId, String phoneNumber, String phoneNumberType) {

        val params = new MapSqlParameterSource();
        params.addValue("patientId", patientId);
        params.addValue("phoneNumber", phoneNumber);
        params.addValue("phoneNumberType", phoneNumberType);

        val result = select(
                "caliphr.patient_phone_number_secure s JOIN caliphr.patient_phone_number p ON s.record_id=p.record_id",
                PATIENT_PHONE_NUMBER_NON_ENCRYPTED_COLUMN_NAMES,
                PATIENT_PHONE_NUMBER_ENCRYPTED_COLUMN_NAMES,
                "p.patient_id=:patientId AND phone_number_type=:phoneNumberType AND " +
                        decryptedColumnSql("phone_number") + "= :phoneNumber",
                params,
                (rs, rowNum) -> {
                    PatientPhoneNumberHolder holder = new PatientPhoneNumberHolder(patientPhoneNumberRepository.findOne(rs.getInt("record_id")));
                    holder.setPhoneNumber(rs.getString("phone_number"));
                    return holder;
                }
        );
        if (result.size() > 1) {
            throw new IllegalStateException("more than one phone number record matched the given SSN");
        }
        return result.size() == 1 ? (PatientPhoneNumberHolder) result.get(0) : null;
    }

    @Override
    public void savePatientPhoneNumberHolder(PatientPhoneNumberHolder phoneHolder) {
        val nonEncryptedClmnVals = new MapSqlParameterSource();
        PatientPhoneNumber patientPhoneNumber = phoneHolder.getPatientPhoneNumber();
        addParamValue(nonEncryptedClmnVals, "record_id", patientPhoneNumber.getId());
        addParamValue(nonEncryptedClmnVals, "date_created", patientPhoneNumber.getDateCreated());
        addParamValue(nonEncryptedClmnVals, "user_created", patientPhoneNumber.getUserCreated());
        addParamValue(nonEncryptedClmnVals, "date_updated", patientPhoneNumber.getDateUpdated());
        addParamValue(nonEncryptedClmnVals, "user_updated", patientPhoneNumber.getUserUpdated());

        val encryptedClmnVals = new MapSqlParameterSource();
        addParamValue(encryptedClmnVals, "phone_number", phoneHolder.getPhoneNumber());

        insert("caliphr.patient_phone_number_secure", nonEncryptedClmnVals, encryptedClmnVals);
    }

    private void insert(String table, MapSqlParameterSource nonEncryptedClmnVals, MapSqlParameterSource encryptedClmnVals) {

        Set<String> nonEncryptedClmnNames = nonEncryptedClmnVals.getValues().keySet();
        Set<String> encryptedClmnNames = encryptedClmnVals.getValues().keySet();

        String columnSql = Stream.concat(nonEncryptedClmnNames.stream(), encryptedClmnNames.stream())
                .collect(Collectors.joining(", "));

        String encryptedColumnSql = encryptedClmnNames.stream()
                .map(column -> encryptedColumnSql(column) + " as " + column)
                .collect(Collectors.joining(", "));

        String paramSql = Stream.concat(nonEncryptedClmnNames.stream(), encryptedClmnNames.stream())
                .map(column -> ":" + column)
                .collect(Collectors.joining(","));

        String nonEncryptedColumnSql = nonEncryptedClmnVals.getValues().entrySet().stream()
                .map(entry -> "c." + entry.getKey() + (entry.getValue() instanceof Date ? "::timestamp" : ""))
                .collect(Collectors.joining(", "));

        String sql = String.format("INSERT INTO %s (%s)\nSELECT %s,%s\nFROM (VALUES (%s) ) as c(%s)\nCROSS JOIN (SELECT :key as symkey) as keys",
                table, columnSql, nonEncryptedColumnSql, encryptedColumnSql, paramSql, columnSql
        );

        //System.out.println(sql);

        val clmnVals = new MapSqlParameterSource();
        clmnVals.addValues(nonEncryptedClmnVals.getValues());
        clmnVals.addValues(encryptedClmnVals.getValues());
        clmnVals.addValue("key", databaseEncyptionUtil.getKey());

        this.jdbcTemplate.update(sql, clmnVals);

    }

    private <T> List<T> select(String table, List<String> nonEncryptedClmnNames, List<String> encryptedClmnNames,
                               String whereSql, MapSqlParameterSource paramValues, RowMapper<T> rowMapper) {

        String nonEncyptedColumnSql = nonEncryptedClmnNames.stream().collect(Collectors.joining(", "));
        String decryptedColumnSql = encryptedClmnNames.stream().map(column -> decryptedColumnSql(column) + " as " + column).collect(Collectors.joining(", "));

        String sql = String.format("SELECT %s, %s \nFROM %s\nCROSS JOIN (SELECT :key as symkey) as keys\nWHERE %s",
                nonEncyptedColumnSql, decryptedColumnSql, table, whereSql
        );

        //System.out.println(sql);

        paramValues.addValue("key", databaseEncyptionUtil.getKey());

        return this.jdbcTemplate.query(sql, paramValues, rowMapper);

    }

    private String encryptedColumnSql(String column) {
        return "caliphr.pgp_sym_encrypt(c." + column + ", keys.symkey, 'cipher-algo=aes256')";
    }

    private String decryptedColumnSql(String column) {
        return "caliphr.pgp_sym_decrypt(" + column + ", keys.symkey, 'cipher-algo=aes256')";
    }

    private byte[] getHashValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest((value + "&" + databaseEncyptionUtil.getKey()).getBytes("UTF-8"));  // use the key as a salt
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addParamValue(MapSqlParameterSource params, String paramName, Object value) {
        if (value != null) {
            params.addValue(paramName, value);
        }
    }

}
