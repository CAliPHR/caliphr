package com.ainq.caliphr.persistence.util.predicate.ccda;

import com.ainq.caliphr.persistence.model.obj.caliphrDb.QDocumentType;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

public class DocumentTypePredicate {

    public static Predicate searchByHl7Oid(String hl7Oid) {
        QDocumentType qDocumentType = QDocumentType.documentType;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qDocumentType.hl7Oid.equalsIgnoreCase(hl7Oid));
        return booleanBuilder;
    }

}
