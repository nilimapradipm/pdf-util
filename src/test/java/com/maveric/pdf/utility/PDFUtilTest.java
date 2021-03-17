package com.maveric.pdf.utility;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFUtilTest {

    PDFUtil pdfutil = new PDFUtil();



    @Test
    public void comparePDFImageModeDiff() throws IOException {
        long startTime = System.currentTimeMillis();

        File file = new File("");
        try {
            file = new File("./src/test/resources/CompareResult/OverallReport.html");
            if (!file.exists()) {
                file.createNewFile();

            }

            String header1 = "<html>" + "<head>";
            String header2 = "<h1 style = \"background-color:powderblue; text-align:center;font-size:30px;\">";
            String header3 = "<img src = \"http://localhost:63343/pdf-util/maveric.png\" align = \"right\" alt = \"Italian Trulli\" width = \"100\" height = \"35\" >PDF COMPARISON DASHBOARD</h1>";
            String header4 = "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://localhost:63343/pdf-util/mystyle.css\">";
            String header5 = "</head>";
            String header6 =

                    "<table align=\"center\"  CELLSPACING=0 CELLPADDING=5 border=\"1\"></br>"
                            +
                            "<tr>" +
                            "<td style=\"font-weight:bold\" align=\"center\" >REPORT</td>" +
                            "<td style=\"font-weight:bold\" align=\"center\" >FILESIZE (OLD VERSION vs NEW VERSION)</td>" +
                            "<td style=\"font-weight:bold\" align=\"center\" >EXECUTION TIME</td>" +
                            "<td style=\"font-weight:bold\" align=\"center\" >STATUS</td>" +
                            "</tr>";
            FileUtils.writeStringToFile(file, header1, true);
            FileUtils.writeStringToFile(file, header2, true);
            FileUtils.writeStringToFile(file, header3, true);
            FileUtils.writeStringToFile(file, header4, true);
            FileUtils.writeStringToFile(file, header5, true);
            FileUtils.writeStringToFile(file, header6, true);


      /*  } catch (IOException e) {
            e.printStackTrace();
        }*/
        File folderOldVersion = new File("./src/test/resources/OldVersion/");
        File[] listOfFilesInOldVersion = folderOldVersion.listFiles();

        File folderNewVersion = new File("./src/test/resources/NewVersion/");
        File[] listOfFilesInNewVersion = folderNewVersion.listFiles();

        for (File fileOldVersion : listOfFilesInOldVersion) {
            if (fileOldVersion.isFile()) {
                for (File fileNewVersion : listOfFilesInNewVersion) {
                    if (fileNewVersion.isFile()) {
                        if (fileOldVersion.getName().equals(fileNewVersion.getName())) {
                            System.out.println("");
                            System.out.println("TEST STARTS");
                            long startTimeForTest = System.currentTimeMillis();
                            System.out.println("Old Version File : " + fileOldVersion.getName());
                            System.out.println("New Version File : " + fileNewVersion.getName());
                            String file1 = fileOldVersion.toString();
                            String file2 = fileNewVersion.toString();


                            File fileold = new File(file1);
                            long fileoldsize = fileold.length();

                            File filenew = new File(file2);
                            long filenewsize = filenew.length();

                            String filesize = fileoldsize + " bytes" + " vs " + filenewsize + " bytes";

                            System.out.println("Comparing files in Visual Mode");
                            pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
                            pdfutil.compareAllPages(true);
                            pdfutil.highlightPdfDifference(true);

                            pdfutil.setImageDestinationPath("./src/test/resources/CompareResult" + "/" + fileOldVersion.getName().split("\\.")[0]);
                            String path = pdfutil.getImageDestinationPath();

                            File filecompare = new File(path);
                            filecompare.mkdir();
                            boolean result = pdfutil.compare(file1, file2);

                            String pdf = "";
                            String size = "";
                            String status = "";
                            String pdfname = "";
                            String pdflink = "";
                            String pdflink1 = "";
                            String pdflink2 = "";
                            if (!result) {
                                System.out.println("Comparison Results for Visual Mode is : " + result + " which means differences exist in pdf");
                                pdf = "./src/test/resources/CompareResult/" + fileOldVersion.getName().split("\\.")[0] + "/" + fileOldVersion.getName().split("\\.")[0] + ".pdf";
                                PDFUtil.combineImagesIntoPDF(pdf, path);
                                pdfname = fileOldVersion.getName().split("\\.")[0] + ".pdf";
                                pdflink1 =  "http://localhost:63343/pdf-util/src/test/resources/CompareResult/" + fileOldVersion.getName().split("\\.")[0] + "/" + fileOldVersion.getName().split("\\.")[0] + ".pdf";
                                pdflink = "<a" + " " + "href=" + pdflink1 + " " + "target=\"blank\"> " + pdfname + "</a>";
                                status = "Differences";
                            } else {
                                System.out.println("Comparison Results for Visual Mode is : " + result + " which means no differences exist in pdf");
                                pdf = "./src/test/resources/OldVersion/" + fileOldVersion.getName().split("\\.")[0] + "/" + fileOldVersion.getName().split("\\.")[0] + ".pdf";
                                pdfname = fileOldVersion.getName().split("\\.")[0] + ".pdf";
                                pdflink2 =  "http://localhost:63343/pdf-util/src/test/resources/OldVersion/" + pdfname ;
                                pdflink = "<a" + " " + "href=" + pdflink2 + " " + "target=\"blank\"> " + pdfname + "</a>";
                               status = "Matched";
                            }

                            long endTimeForTest = System.currentTimeMillis();

                            long timeElapsedForTest = endTimeForTest - startTimeForTest;
                            String time = String.valueOf(timeElapsedForTest);
                            System.out.println("Execution time for this test is : " + timeElapsedForTest + " ms");


                            FileUtils.writeStringToFile(file, "<tr>", true);
                            FileUtils.writeStringToFile(file, "<td align=\"center\">", true);
                            FileUtils.writeStringToFile(file, pdflink, true);
                            FileUtils.writeStringToFile(file, "</td><td align=\"center\">", true);
                            FileUtils.writeStringToFile(file, filesize, true);
                            FileUtils.writeStringToFile(file, "</td><td align=\"center\">", true);
                            FileUtils.writeStringToFile(file, time + " ms", true);
                            FileUtils.writeStringToFile(file, "</td><td align=\"center\">", true);
                            FileUtils.writeStringToFile(file, status, true);
                            FileUtils.writeStringToFile(file, "</td>", true);
                            FileUtils.writeStringToFile(file, "</tr>", true);

                        }
                    }
                }
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        System.out.println("");
        System.out.println("Total Execution time : " + timeElapsed + " ms");
        String overallexecutiontime = "<p align=\"center\">Overall Execution Time : " + timeElapsed + " sec" + "</p>";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String reportgeneratedtime = "<p align=\"center\">Report Generated TimeStamp : " + dtf.format(now) + "</p>";

        FileUtils.writeStringToFile(file, "</table>", true);
        FileUtils.writeStringToFile(file, "</html>", true);
        FileUtils.writeStringToFile(file, overallexecutiontime, true);
        FileUtils.writeStringToFile(file, reportgeneratedtime, true);
    }



    private String getFilePath(String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile()).getAbsolutePath();
    }



}

