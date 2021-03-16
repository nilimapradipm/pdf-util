package com.maveric.pdf.utility;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PDFUtilTest {

    PDFUtil pdfutil = new PDFUtil();


  // @Test
    public void comparePDFTextModeDiff1() throws IOException {
       String file1 = getFilePath("compare/samp.pdf");
       String file2 = getFilePath("compare/sampwithchange.pdf");

        pdfutil.setCompareMode(CompareMode.TEXT_MODE);
        boolean result = pdfutil.compare(file1, file2);

        Assert.assertFalse(result);
        System.out.println("Text Mode - MisMatch as expected so comparison flag is:" + result);

    }

    //@Test
    public void comparePDFTextModeSame1() throws IOException {
        String file1 = getFilePath("compare/sample-ce-notice-arabic.pdf");
        String file2 = getFilePath("compare/sample-ce-notice-arabicnochange.pdf");


        pdfutil.setCompareMode(CompareMode.TEXT_MODE);

        String s = pdfutil.getText(file1);
        String d = pdfutil.getText(file2);

        System.out.println(s);

        System.out.println(d);
        boolean result = pdfutil.compare(file1, file2);

        Assert.assertTrue(result);
        System.out.println("Text Mode - Match as expected so comparison flag is:" + result);
    }





  // @Test
    public void comparePDFImageModeSame() throws IOException {

        String file1 = getFilePath("compare/table.pdf");
        String file2 = getFilePath("compare/tablesame.pdf");
        pdfutil.highlightPdfDifference(Color.BLUE);
        pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
        pdfutil.compare(file1, file2);
        //pdfutil.highlightPdfDifference(true);

        pdfutil.setImageDestinationPath("./src/test/resources");
        boolean result = pdfutil.compare(file1, file2);
        Assert.assertTrue(result);
       System.out.println("Visual Mode - Match as expected so comparison flag is:" + result);
    }

    //@Test
    public void comparePDFTextModeDiff() throws IOException {
        {
            File folderOldVersion = new File("./src/test/resources/OldVersion/");
            File[] listOfFilesInOldVersion = folderOldVersion.listFiles();
            // System.out.println (listOfFilesInA);
            File folderNewVersion = new File("./src/test/resources/NewVersion/");
            File[] listOfFilesInNewVersion = folderNewVersion.listFiles();
            //System.out.println (listOfFilesInB);
            for (File fileA : listOfFilesInOldVersion) {
                // System.out.println("File : " + fileA.getName());
                if (fileA.isFile()) {
                    for (File fileB : listOfFilesInNewVersion) {
                        // System.out.println("File : " + fileB.getName());
                        if (fileB.isFile()) {
                            //System.out.println("File names matched");

                            if (fileA.getName().equals(fileB.getName())) {
                                System.out.println("");
                                System.out.println("TEST STARTS");
                                System.out.println("Old Version File : " + fileA.getName());
                                System.out.println("New Version File : " + fileB.getName());

                                String file1 = fileA.toString();
                                String file2 = fileB.toString();
                                System.out.println("Comparing files in Text Mode");
                                pdfutil.setCompareMode(CompareMode.TEXT_MODE);
                                // pdfutil.highlightPdfDifference(true);
                                //pdfutil.highlightPdfDifference(Color.GREEN);
                                //pdfutil.setImageDestinationPath("./src/test/resources/result");
                                String textfile1 = pdfutil.getText(file1);
                                String textfile2 = pdfutil.getText(file2);
                                boolean result = pdfutil.compare(file1, file2);
                                System.out.println(textfile1);
                                System.out.println(textfile2);
                                System.out.println("Comparison Flag for Text Mode is : " + result);
                                System.out.println("TEST ENDS");
                                System.out.println("");
                            }
                        }

                    }
                }
            }
        }

    }


    @Test
    public void comparePDFImageModeDiff() throws IOException {
        long startTime = System.currentTimeMillis();
        {
            File folderOldVersion  = new File("./src/test/resources/OldVersion/");
            File[] listOfFilesInOldVersion = folderOldVersion.listFiles();

            File folderNewVersion = new File("./src/test/resources/NewVersion/");
            File[] listOfFilesInNewVersion = folderNewVersion.listFiles();

            for (File fileOldVersion : listOfFilesInOldVersion ) {

                if (fileOldVersion.isFile()) {
                    for (File fileNewVersion : listOfFilesInNewVersion) {

                        if (fileNewVersion.isFile()) {


                            if (fileOldVersion.getName().equals(fileNewVersion.getName())) {
                                System.out.println("");
                                System.out.println("TEST STARTS");
                                long startTimeForTest = System.currentTimeMillis();
                                System.out.println("Old Version File : " + fileOldVersion.getName());
                                System.out.println("New Version File : " + fileNewVersion.getName());
                                String file1 = fileOldVersion .toString();
                                String file2 = fileNewVersion.toString();

                                System.out.println("Comparing files in Visual Mode");
                                pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
                                pdfutil.compareAllPages(true);
                                pdfutil.highlightPdfDifference(true);
                                //System.out.println(fileOldVersion .getName().split("\\.")[0]);
                                //System.out.println(fileOldVersion .getName().split("\\.")[0]);
                                //pdfutil.createFolder ("./src/test/resources/CompareResult" + "/" + fileA.getName().split("\\.")[0]);
                                pdfutil.setImageDestinationPath("./src/test/resources/CompareResult" + "/" + fileOldVersion.getName().split("\\.")[0]);
                                String path = pdfutil.getImageDestinationPath();
                               // System.out.println(path);
                                File file = new File(path);
                                file.mkdir();
                                boolean result = pdfutil.compare(file1, file2);
                                //System.out.println("Comparison Results for Visual Mode is : " + result + "which means differences exist in pdf");
                                if (!result)
                                {
                                    System.out.println("Comparison Results for Visual Mode is : " + result + " which means differences exist in pdf");
                                    String pdf = "./src/test/resources/CompareResult/" + fileOldVersion.getName().split("\\.")[0] + "/" + fileOldVersion.getName().split("\\.")[0] + ".pdf";
                                    PDFUtil.combineImagesIntoPDF(pdf, path);
                                }
                                else
                                {
                                    System.out.println("Comparison Results for Visual Mode is : " + result + " which means no differences exist in pdf");
                                }

                                long endTimeForTest = System.currentTimeMillis();

                                long timeElapsedForTest = endTimeForTest  - startTimeForTest;

                                System.out.println("Execution time for this test is : " + timeElapsedForTest + " ms");

                            }
                        }

                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;
        System.out.println("");
        System.out.println("Total Execution time : " + timeElapsed + " ms");
    }





    private String getFilePath(String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile()).getAbsolutePath();
    }



}
