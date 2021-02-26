package com.maveric.pdf.utility;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PDFUtilTest {

    PDFUtil pdfutil = new PDFUtil();


   @Test
    public void comparePDFTextModeDiff() throws IOException {
       String file1 = getFilePath("compare/samp.pdf");
       String file2 = getFilePath("compare/sampwithchange.pdf");

        pdfutil.setCompareMode(CompareMode.TEXT_MODE);
        boolean result = pdfutil.compare(file1, file2);

        Assert.assertFalse(result);
        System.out.println("Text Mode - MisMatch as expected so comparison flag is:" + result);

    }

    @Test
    public void comparePDFTextModeSame() throws IOException {
        String file1 = getFilePath("compare/samp.pdf");
        String file2 = getFilePath("compare/sampwithnochange.pdf");
        pdfutil.setCompareMode(CompareMode.TEXT_MODE);
        boolean result = pdfutil.compare(file1, file2);

        Assert.assertTrue(result);
        System.out.println("Text Mode - Match as expected so comparison flag is:" + result);
    }





   @Test
    public void comparePDFImageModeSame() throws IOException {

        String file1 = getFilePath("compare/table.pdf");
        String file2 = getFilePath("compare/tablesame.pdf");
        pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
        pdfutil.compare(file1, file2);
        pdfutil.highlightPdfDifference(true);
        pdfutil.setImageDestinationPath("./src/test/resources");
        boolean result = pdfutil.compare(file1, file2);
        Assert.assertTrue(result);
       System.out.println("Visual Mode - Match as expected so comparison flag is:" + result);
    }

    @Test
    public void comparePDFImageModeDiff() throws IOException {
        String file1 = getFilePath("compare/table.pdf");
        String file2 = getFilePath("compare/tablenotsame.pdf");
        pdfutil.setCompareMode(CompareMode.VISUAL_MODE);
        pdfutil.compare(file1, file2);
        pdfutil.highlightPdfDifference(true);
        pdfutil.setImageDestinationPath("./src/test/resources");
        boolean result = pdfutil.compare(file1, file2);
        Assert.assertFalse(result);
        System.out.println("Visual Mode -  MisMatch as expected so comparison flag is:" + result);
    }



    private String getFilePath(String filename) {
        return new File(getClass().getClassLoader().getResource(filename).getFile()).getAbsolutePath();
    }

}
