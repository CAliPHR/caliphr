package com.ainq.caliphr.website.controller.secure.xslx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ainq.caliphr.common.model.result.Measure;
import com.ainq.caliphr.common.model.result.PopulationSetResult;
import com.ainq.caliphr.website.controller.api.hqmf.MeasureRestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

/**
 * Created by oadhali on 1/21/2016.
 */

@Controller
public class ExcelExport {

    @Autowired
    private MeasureRestController measureRestController;

    //private Integer userId;

    @RequestMapping(value = "/extract/excel_format/exportPracticeLevel", method = RequestMethod.POST)
    public HttpEntity<byte[]> exportExcelPracticeLevel(
            @RequestParam(value = "practiceName") String practiceName,
            @RequestParam(value = "organizationName") String organizationName,
            @RequestParam(value = "providerNPI") String[] providerNpi,
            @RequestParam(value = "providerName") String[] providerName,
            @RequestParam(value = "providerList") String[] providerList ){

        CsvMapper mapper = new CsvMapper();
        //if (SecurityHelper.isLoggedIn()) {
        //    SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
        //    userId = securityUser.getId();
        //} else {
        //    userId = null;
        // }

        List<Integer> providersList = new ArrayList<>();
        for (String k : providerList){
            providersList.add(Integer.valueOf(k));
        }

        List<Measure> providerSpecificMeasures = new ArrayList<>();



        //APACHE POI
        //Date format
        DateFormat format = new SimpleDateFormat("yyyyMMddhhmm", Locale.ENGLISH);
        SimpleDateFormat print = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));

        XSSFWorkbook workbook = new XSSFWorkbook();


        String[] titles = {"Provider Name","Provider NPI","Reporting Period Start","Reporting Period End",
                "Sub-Measure Title","Initial Patient Population","Numerator","Denominator","Exclusion","Exception"};

        //Making the header bold
        //XSSFColor myColor = new XSSFColor(new java.awt.Color(128, 128, 128));
        CellStyle style = workbook.createCellStyle();//Create style
        Font font = workbook.createFont();//Create font
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);//set it to bold
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_MEDIUM);
        style.setBorderLeft(CellStyle.BORDER_MEDIUM);
        style.setBorderRight(CellStyle.BORDER_MEDIUM);
        style.setBorderTop(CellStyle.BORDER_MEDIUM);

        CellStyle style1 = workbook.createCellStyle();
        style1.setAlignment(CellStyle.ALIGN_CENTER);
        style1.setBorderBottom(CellStyle.BORDER_THIN);
        style1.setBorderLeft(CellStyle.BORDER_THIN);
        style1.setBorderRight(CellStyle.BORDER_THIN);
        style1.setBorderTop(CellStyle.BORDER_THIN);
        style1.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style1.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style1.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style1.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

        CellStyle style3 = workbook.createCellStyle();
        style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style3.setBorderBottom(CellStyle.BORDER_THIN);
        style3.setBorderLeft(CellStyle.BORDER_THIN);
        style3.setBorderRight(CellStyle.BORDER_THIN);
        style3.setBorderTop(CellStyle.BORDER_THIN);
        style3.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle style4 = workbook.createCellStyle();
        Font font4 = workbook.createFont();
        font4.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style4.setFont(font4);
        style4.setAlignment(CellStyle.ALIGN_RIGHT);

        CellStyle style5 = workbook.createCellStyle();
        style5.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style5.setBorderBottom(CellStyle.BORDER_THIN);
        style5.setBorderLeft(CellStyle.BORDER_THIN);
        style5.setBorderRight(CellStyle.BORDER_THIN);
        style5.setBorderTop(CellStyle.BORDER_THIN);
        style5.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setAlignment(CellStyle.ALIGN_LEFT);



        int providerNameList = 0;
        int rowid = 0;
        XSSFSheet spreadsheet = null;
        XSSFRow row = null;
        int styleCounter = 1;
        for(Integer s : providersList){
            System.out.println("****************************** " +s);
            // Map<String, PopulationSetResult> MeasurePopulationSet = new TreeMap<>();
            providerSpecificMeasures = measureRestController.getAllActiveMeasures(s);
            List<Measure> measurePojos = mapper.convertValue(providerSpecificMeasures, new TypeReference<List<Measure>>() { });
            boolean foundMatch = false;

            for(Measure measure : measurePojos){

                String sheetName = measure.getCmsId();
                //workbook.createSheet(measure.getCmsId());
                // System.out.println("**********************"+measure.getCmsId()+"********"+sheetName);
                if(workbook.getNumberOfSheets()>0){
                    for(int l=0;l<workbook.getNumberOfSheets();l++){
                        // System.out.println("HAKUNA MATAT 1 *******************"+workbook.getSheetName(l)+"**"+sheetName);
                        if(sheetName.equals(workbook.getSheetName(l))){
                            //   System.out.println("HAKUNA MATAT 2 *******************");
                            spreadsheet = workbook.getSheetAt(l);
                            //  System.out.println("*****************"+measure.getCmsId()+"*************"+spreadsheet.getSheetName());
                            foundMatch = true;
                            rowid = spreadsheet.getLastRowNum()+1;
                            break;
                        } else {
                            continue;
                        }
                    }
                    if(!foundMatch) {
                        // System.out.println("SPRADSHETT NOT FOUND NAMING A NEW ONE ********"+spreadsheet.getSheetName()+"**********"+workbook.getNumberOfSheets()+"********"+workbook.getSheetName(0));
                        spreadsheet = workbook.createSheet(measure.getCmsId());
                        rowid = 5;//rowid+2;
                        row = spreadsheet.createRow(rowid);
                        for (int i = 0; i < titles.length; i++) {
                            row.createCell(i).setCellValue(titles[i]);
                            row.getCell(i).setCellStyle(style);
                        }
                        rowid++;
                    }
                } else {
                    spreadsheet = workbook.createSheet(measure.getCmsId());
                    rowid = 5;//rowid+2;
                    row = spreadsheet.createRow(rowid);
                    for (int i = 0; i < titles.length; i++) {
                        row.createCell(i).setCellValue(titles[i]);
                        row.getCell(i).setCellStyle(style);
                    }
                    rowid++;
                }



                List<PopulationSetResult> populationSetResults = measure.getPopulationSetResults();

                if(populationSetResults.size()<=1){
                    row = spreadsheet.createRow(rowid++);
                    row.createCell(0).setCellValue(providerName[providerNameList]);
                    row.createCell(1).setCellValue(providerNpi[providerNameList]);
                    // row.createCell(2).setCellValue(measure.getCmsId());
                    //row.createCell(3).setCellValue(measure.getTitle());
                    try {
                        row.createCell(2).setCellValue(print.format(format.parse(measure.getReportingPeriodStart())));
                        row.createCell(3).setCellValue(print.format(format.parse(measure.getReportingPeriodEnd())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    row.createCell(4).setCellValue(populationSetResults.get(0).getSubmeasureTitle());
                    if(populationSetResults.get(0).getIppCount() != null) {
                        row.createCell(5).setCellValue(populationSetResults.get(0).getIppCount());
                    } else {
                        row.createCell(5).setCellValue("");
                    }
                    if(populationSetResults.get(0).getNumeratorCount() != null) {
                        row.createCell(6).setCellValue(populationSetResults.get(0).getNumeratorCount());
                    } else {
                        row.createCell(6).setCellValue("");
                    }
                    if(populationSetResults.get(0).getDenominatorCount() !=null) {
                        row.createCell(7).setCellValue(populationSetResults.get(0).getDenominatorCount());
                    } else {
                        row.createCell(7).setCellValue("");
                    }
                    if(populationSetResults.get(0).getDenexCount()!=null) {
                        row.createCell(8).setCellValue(populationSetResults.get(0).getDenexCount());
                    } else {
                        row.createCell(8).setCellValue("");
                    }
                    if(populationSetResults.get(0).getDenexcepCount()!=null) {
                        row.createCell(9).setCellValue(populationSetResults.get(0).getDenexcepCount());
                    } else {
                        row.createCell(9).setCellValue("");
                    }
                    if(styleCounter%2 == 0){
                        row.getCell(0).setCellStyle(style5);
                        for(int j = 1;j<=9;j++){
                            row.getCell(j).setCellStyle(style3);
                        }

                    } else {
                        for(int k = 1;k<=9;k++){
                            row.getCell(k).setCellStyle(style1);
                        }
                    }
                } else {
                    for(int k = 0;k<populationSetResults.size();k++){
                        row = spreadsheet.createRow(rowid++);
                        row.createCell(0).setCellValue(providerName[providerNameList]);
                        row.createCell(1).setCellValue(providerNpi[providerNameList]);
                        // row.createCell(2).setCellValue(measure.getCmsId());
                        //row.createCell(3).setCellValue(measure.getTitle());
                        try {
                            row.createCell(2).setCellValue(print.format(format.parse(measure.getReportingPeriodStart())));
                            row.createCell(3).setCellValue(print.format(format.parse(measure.getReportingPeriodEnd())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        row.createCell(4).setCellValue(populationSetResults.get(k).getSubmeasureTitle());
                        if(populationSetResults.get(0).getIppCount() != null) {
                            row.createCell(5).setCellValue(populationSetResults.get(0).getIppCount());
                        } else {
                            row.createCell(5).setCellValue("");
                        }
                        if(populationSetResults.get(0).getNumeratorCount() != null) {
                            row.createCell(6).setCellValue(populationSetResults.get(0).getNumeratorCount());
                        } else {
                            row.createCell(6).setCellValue("");
                        }
                        if(populationSetResults.get(0).getDenominatorCount() !=null) {
                            row.createCell(7).setCellValue(populationSetResults.get(0).getDenominatorCount());
                        } else {
                            row.createCell(7).setCellValue("");
                        }
                        if(populationSetResults.get(k).getDenexCount()!=null) {
                            row.createCell(8).setCellValue(populationSetResults.get(k).getDenexCount());
                        } else {
                            row.createCell(8).setCellValue("");
                        }
                        if(populationSetResults.get(k).getDenexcepCount()!=null) {
                            row.createCell(9).setCellValue(populationSetResults.get(k).getDenexcepCount());
                        } else {
                            row.createCell(9).setCellValue("");
                        }
                        if(styleCounter%2 == 0){
                            row.getCell(0).setCellStyle(style5);
                            for(int j = 1;j<=9;j++){
                                row.getCell(j).setCellStyle(style3);
                            }
                        }else {
                            for(int n = 1;n<=9;n++){
                                row.getCell(n).setCellStyle(style1);
                            }
                        }


                    }

                }
                if(!foundMatch) {
                    for (int v = 0; v <= 9; v++) {
                        spreadsheet.autoSizeColumn(v);
                    }
                } else {
                    for (int g = 0; g <= 9; g++) {
                        spreadsheet.autoSizeColumn(g);
                    }
                }

            }
            providerNameList++;
            foundMatch=false;
            styleCounter++;

        }
        providerSpecificMeasures = measureRestController.getAllActiveMeasures(providersList.get(0));
        List<Measure> measurePojos = mapper.convertValue(providerSpecificMeasures, new TypeReference<List<Measure>>() { });
        for(int c = 0; c<workbook.getNumberOfSheets();c++){
            spreadsheet = workbook.getSheetAt(c);
            rowid=0;
            row = spreadsheet.createRow(rowid);
            row.createCell(0).setCellValue("Organization");
            row.getCell(0).setCellStyle(style4);
            row.createCell(1).setCellValue(organizationName);
            rowid++;
            row = spreadsheet.createRow((rowid));
            row.createCell(0).setCellValue("Practice");
            row.getCell(0).setCellStyle(style4);
            row.createCell(1).setCellValue(practiceName);
            rowid++;
            row = spreadsheet.createRow(rowid);
            row.createCell(0).setCellValue("CMSID");
            row.getCell(0).setCellStyle(style4);
            row.createCell(1).setCellValue(measurePojos.get(c).getCmsId());
            rowid++;
            row = spreadsheet.createRow(rowid);
            row.createCell(0).setCellValue("Title");
            row.getCell(0).setCellStyle(style4);
            row.createCell(1).setCellValue(measurePojos.get(c).getTitle());
            workbook.getSheetAt(c).createFreezePane(0,6);

        }

        try {
            workbook.write(baos);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] documentBody = baos.toByteArray();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "xlsx"));
        header.set("Content-Disposition",
                "attachment; filename=" + practiceName+".xlsx");
        header.setContentLength((documentBody == null) ? 0 : documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);
    }

    @RequestMapping(value = "/extract/excel_format/exportProviderLevel", method = RequestMethod.POST)
    public HttpEntity<byte[]> exportExcelProviderLevel(
            @RequestParam(value = "practiceName") String practiceName,
             @RequestParam(value = "organizationName") String organizationName,
            @RequestParam(value = "selectedProviderNPI") String selectedProviderNPI,
            @RequestParam(value = "selectedProviderName") String selectedProviderName,
            @RequestParam(value = "selectedProviderid") String selectedProviderid ,
            @RequestParam(value = "measuresSelected") String[] measuresSelected) {

        CsvMapper mapper = new CsvMapper();
        //if (SecurityHelper.isLoggedIn()) {
        //    SecurityUser securityUser = (SecurityUser) SecurityHelper.getUserDetails();
        //    userId = securityUser.getId();
        //} else {
        //    userId = null;
        //}


        List<String> cmsIdList = new ArrayList<>();
        for (String c : measuresSelected){
            System.out.println("________________________ :"+c);
            cmsIdList.add(c);
        }

        //APACHE POI
        //Date format
        DateFormat format = new SimpleDateFormat("yyyyMMddhhmm", Locale.ENGLISH);
        SimpleDateFormat print = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XSSFWorkbook workbook = new XSSFWorkbook();


        String[] titles = {"CMSID","Title","Reporting Period Start","Reporting Period End",
                "Sub-Measure Title","Initial Patient Population","Numerator","Denominator","Exclusion","Exception"};

        CellStyle style = workbook.createCellStyle();//Create style
        Font font = workbook.createFont();//Create font
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);//set it to bold
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_MEDIUM);
        style.setBorderLeft(CellStyle.BORDER_MEDIUM);
        style.setBorderRight(CellStyle.BORDER_MEDIUM);
        style.setBorderTop(CellStyle.BORDER_MEDIUM);

        CellStyle style1 = workbook.createCellStyle();
        style1.setAlignment(CellStyle.ALIGN_CENTER);
        style1.setBorderBottom(CellStyle.BORDER_THIN);
        style1.setBorderLeft(CellStyle.BORDER_THIN);
        style1.setBorderRight(CellStyle.BORDER_THIN);
        style1.setBorderTop(CellStyle.BORDER_THIN);
        style1.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style1.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style1.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style1.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

        CellStyle style3 = workbook.createCellStyle();
        style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style3.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style3.setBorderBottom(CellStyle.BORDER_THIN);
        style3.setBorderLeft(CellStyle.BORDER_THIN);
        style3.setBorderRight(CellStyle.BORDER_THIN);
        style3.setBorderTop(CellStyle.BORDER_THIN);
        style3.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style3.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle style4 = workbook.createCellStyle();
        Font font4 = workbook.createFont();
        font4.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style4.setFont(font4);
        style4.setAlignment(CellStyle.ALIGN_RIGHT);

        CellStyle style5 = workbook.createCellStyle();
        style5.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style5.setBorderBottom(CellStyle.BORDER_THIN);
        style5.setBorderLeft(CellStyle.BORDER_THIN);
        style5.setBorderRight(CellStyle.BORDER_THIN);
        style5.setBorderTop(CellStyle.BORDER_THIN);
        style5.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style5.setAlignment(CellStyle.ALIGN_LEFT);

        int rowid = 0;
        int styleCounter = 1;
        XSSFSheet spreadsheet = null;
        XSSFRow row = null;
        spreadsheet = workbook.createSheet(selectedProviderName);

        rowid=0;
        row = spreadsheet.createRow(rowid);
        row.createCell(0).setCellValue("Organization");
        row.getCell(0).setCellStyle(style4);
        row.createCell(1).setCellValue(organizationName);
        rowid++;
        row = spreadsheet.createRow((rowid));
        row.createCell(0).setCellValue("Practice");
        row.getCell(0).setCellStyle(style4);
        row.createCell(1).setCellValue(practiceName);
        rowid++;
        row = spreadsheet.createRow(rowid);
        row.createCell(0).setCellValue("Provider Name");
        row.getCell(0).setCellStyle(style4);
        row.createCell(1).setCellValue(selectedProviderName);
        rowid++;
        row = spreadsheet.createRow(rowid);
        row.createCell(0).setCellValue("Provider NPI");
        row.getCell(0).setCellStyle(style4);
        row.createCell(1).setCellValue(selectedProviderNPI);
        spreadsheet.createFreezePane(0,6);

        rowid = 5;//rowid+2;
        row = spreadsheet.createRow(rowid);
        for (int i = 0; i < titles.length; i++) {
            row.createCell(i).setCellValue(titles[i]);
            row.getCell(i).setCellStyle(style);
        }
        rowid++;
        List<Measure> measure = measureRestController.getAllActiveMeasures(Integer.valueOf(selectedProviderid));
        List<Measure> measures = mapper.convertValue(measure, new TypeReference<List<Measure>>() {});
        for(String m : cmsIdList){
            for(int i =0;i<measures.size();i++){
                System.out.println("------------------------ : "+measures.get(i).getCmsId());
                if(m.equals(measures.get(i).getCmsId())){
                    System.out.println("Comparing :"+m+" to this :"+measures.get(i).getCmsId());
                    List<PopulationSetResult> populationSetResults = measures.get(i).getPopulationSetResults();
                    if(populationSetResults.size()<=1){
                        row = spreadsheet.createRow(rowid++);
                        row.createCell(0).setCellValue(measures.get(i).getCmsId());
                        row.createCell(1).setCellValue(measures.get(i).getTitle());
                        // row.createCell(2).setCellValue(measure.getCmsId());
                        //row.createCell(3).setCellValue(measure.getTitle());
                        try {
                            row.createCell(2).setCellValue(print.format(format.parse(measures.get(i).getReportingPeriodStart())));
                            row.createCell(3).setCellValue(print.format(format.parse(measures.get(i).getReportingPeriodEnd())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        row.createCell(4).setCellValue(populationSetResults.get(0).getSubmeasureTitle());
                        if(populationSetResults.get(0).getIppCount() != null) {
                            row.createCell(5).setCellValue(populationSetResults.get(0).getIppCount());
                        } else {
                            row.createCell(5).setCellValue("");
                        }
                        if(populationSetResults.get(0).getNumeratorCount() != null) {
                            row.createCell(6).setCellValue(populationSetResults.get(0).getNumeratorCount());
                        } else {
                            row.createCell(6).setCellValue("");
                        }
                        if(populationSetResults.get(0).getDenominatorCount() !=null) {
                            row.createCell(7).setCellValue(populationSetResults.get(0).getDenominatorCount());
                        } else {
                            row.createCell(7).setCellValue("");
                        }
                        if(populationSetResults.get(0).getDenexCount()!=null) {
                            row.createCell(8).setCellValue(populationSetResults.get(0).getDenexCount());
                        } else {
                            row.createCell(8).setCellValue("");
                        }
                        if(populationSetResults.get(0).getDenexcepCount()!=null) {
                            row.createCell(9).setCellValue(populationSetResults.get(0).getDenexcepCount());
                        } else {
                            row.createCell(9).setCellValue("");
                        }
                        if(styleCounter%2 == 0){
                            row.getCell(0).setCellStyle(style5);
                            for(int j = 1;j<=9;j++){
                                row.getCell(j).setCellStyle(style3);
                            }

                        } else {
                            for(int k = 1;k<=9;k++){
                                row.getCell(k).setCellStyle(style1);
                            }
                        }
                    } else {
                        for(int k = 0;k<populationSetResults.size();k++){
                            row = spreadsheet.createRow(rowid++);
                            row.createCell(0).setCellValue(measures.get(i).getCmsId());
                            row.createCell(1).setCellValue(measures.get(i).getTitle());
                            // row.createCell(2).setCellValue(measure.getCmsId());
                            //row.createCell(3).setCellValue(measure.getTitle());
                            try {
                                row.createCell(2).setCellValue(print.format(format.parse(measures.get(i).getReportingPeriodStart())));
                                row.createCell(3).setCellValue(print.format(format.parse(measures.get(i).getReportingPeriodEnd())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            row.createCell(4).setCellValue(populationSetResults.get(k).getSubmeasureTitle());
                            if(populationSetResults.get(0).getIppCount() != null) {
                                row.createCell(5).setCellValue(populationSetResults.get(0).getIppCount());
                            } else {
                                row.createCell(5).setCellValue("");
                            }
                            if(populationSetResults.get(0).getNumeratorCount() != null) {
                                row.createCell(6).setCellValue(populationSetResults.get(0).getNumeratorCount());
                            } else {
                                row.createCell(6).setCellValue("");
                            }
                            if(populationSetResults.get(0).getDenominatorCount() !=null) {
                                row.createCell(7).setCellValue(populationSetResults.get(0).getDenominatorCount());
                            } else {
                                row.createCell(7).setCellValue("");
                            }
                            if(populationSetResults.get(k).getDenexCount()!=null) {
                                row.createCell(8).setCellValue(populationSetResults.get(k).getDenexCount());
                            } else {
                                row.createCell(8).setCellValue("");
                            }
                            if(populationSetResults.get(k).getDenexcepCount()!=null) {
                                row.createCell(9).setCellValue(populationSetResults.get(k).getDenexcepCount());
                            } else {
                                row.createCell(9).setCellValue("");
                            }
                            if(styleCounter%2 == 0){
                                row.getCell(0).setCellStyle(style5);
                                for(int j = 1;j<=9;j++){
                                    row.getCell(j).setCellStyle(style3);
                                }
                            }else {
                                for(int n = 1;n<=9;n++){
                                    row.getCell(n).setCellStyle(style1);
                                }
                            }


                        }

                    }

                    for (int v = 0; v <= 9; v++) {
                        spreadsheet.autoSizeColumn(v);
                    }


                    styleCounter ++;
                }
            }

        }



        try {
            workbook.write(baos);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] documentBody = baos.toByteArray();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "xlsx"));
        header.set("Content-Disposition",
                "attachment; filename=" + practiceName + ".xlsx");
        header.setContentLength((documentBody == null) ? 0 : documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);

    }

}
