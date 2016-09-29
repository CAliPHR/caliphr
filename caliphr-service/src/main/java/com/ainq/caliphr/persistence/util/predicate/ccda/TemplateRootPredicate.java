package com.ainq.caliphr.persistence.util.predicate.ccda;

import static com.ainq.caliphr.persistence.model.obj.caliphrDb.QTemplateRoot.templateRoot;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

/**
 * Created by mmelusky on 11/10/2015.
 */
public class TemplateRootPredicate {
    public static Predicate findTemplateByRoot(String root) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(templateRoot.hl7Oid.eq(root));
        booleanBuilder.and(templateRoot.dateDisabled.isNull());
        return booleanBuilder;
    }
}
