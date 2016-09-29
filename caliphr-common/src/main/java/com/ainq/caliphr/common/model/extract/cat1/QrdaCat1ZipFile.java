package com.ainq.caliphr.common.model.extract.cat1;

import lombok.Data;

import java.util.List;

/**
 * Created by mmelusky on 10/22/2015.
 */
@Data
public class QrdaCat1ZipFile {

    private String fileName;
    private List<QrdaCat1XmlFile> zipFileContents;

}
