package com.maveric.pdf.utility;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.commons.io.FileUtils;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PDFUtil {

	private final static Logger logger = Logger.getLogger(PDFUtil.class.getName());
	private String imageDestinationPath;
	private boolean bTrimWhiteSpace;
	private boolean bHighlightPdfDifference;
	private Color imgColor;
	private PDFTextStripper stripper;
	private boolean bCompareAllPages;
	private CompareMode compareMode;
	private String[] excludePattern;
	private int startPage = 1;
	private int endPage = -1;
	

	
	public PDFUtil(){
		this.bTrimWhiteSpace = true;
		this.bHighlightPdfDifference = false;
		this.imgColor = Color.MAGENTA;
		this.bCompareAllPages = false;
		this.compareMode = CompareMode.TEXT_MODE;
		logger.setLevel(Level.OFF);
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
	}
	

	public void enableLog(){
		logger.setLevel(Level.INFO);
	}


	public void setCompareMode(CompareMode mode){
		this.compareMode = mode;
	}
	
  	public CompareMode getCompareMode(){
		return this.compareMode;
	}	
		
  	public void setLogLevel(java.util.logging.Level level){
		logger.setLevel(level);
	}
		

	public void trimWhiteSpace(boolean flag){
		this.bTrimWhiteSpace = flag;
	}
	

	public String getImageDestinationPath(){
		return this.imageDestinationPath;
	}

	public void setImageDestinationPath(String path){
		this.imageDestinationPath = path;
	}
	
  	public void highlightPdfDifference(boolean flag){
		this.bHighlightPdfDifference = flag;
	}	

   	public void highlightPdfDifference(Color colorCode){
		this.bHighlightPdfDifference = true;
		this.imgColor = colorCode;
	}	
		
  	public void compareAllPages(boolean flag){
		this.bCompareAllPages = flag;
	}	
	

    public void useStripper(PDFTextStripper stripper){
        this.stripper = stripper;
    }   	
				

	public int getPageCount(String file) throws IOException{
		logger.info("file :" + file);
		PDDocument doc = PDDocument.load(new File(file));
		int pageCount = doc.getNumberOfPages();
		logger.info("pageCount :" + pageCount);
		doc.close();
		return pageCount;
	}
				

	public String getText(String file) throws IOException{
		return this.getPDFText(file,-1, -1);
	}
	
   /**
   * Get the content of the document as plain text.
   *  
   * @param file Absolute file path
   * @param startPage Starting page number of the document
   * @return String document content in plain text.
   * @throws java.io.IOException when file is not found.
   */
	public String getText(String file, int startPage) throws IOException{
		return this.getPDFText(file,startPage, -1);
	}
	
  	public String getText(String file, int startPage, int endPage) throws IOException{
		return this.getPDFText(file,startPage, endPage);
	}

   /**
   * This method returns the content of the document
   */
	private String getPDFText(String file, int startPage, int endPage) throws IOException{
		
		logger.info("file : " + file);
		logger.info("startPage : " + startPage);
		logger.info("endPage : " + endPage);
		
		PDDocument doc = PDDocument.load(new File(file));
		
		PDFTextStripper localStripper = new PDFTextStripper();
		if(null!=this.stripper){
		    localStripper = this.stripper;
		}
		
		this.updateStartAndEndPages(file, startPage, endPage);
		localStripper.setStartPage(this.startPage);
		localStripper.setEndPage(this.endPage);
		
		String txt = localStripper.getText(doc);
		logger.info("PDF Text before trimming : " + txt);
		if(this.bTrimWhiteSpace){
			txt = txt.trim().replaceAll("\\s+", " ").trim();
			logger.info("PDF Text after  trimming : " + txt);	
		}
		
		doc.close();
		return txt;
	}
	
	
	public void excludeText(String... regexs){
		this.excludePattern = regexs;
	}
	
	
   /**
   * Compares two given pdf documents.
   * 

   */
	public boolean compare(String file1, String file2) throws IOException{
		return this.comparePdfFiles(file1, file2, -1, -1);
	}
	

	public boolean compare(String file1, String file2, int startPage, int endPage) throws IOException{
		return this.comparePdfFiles(file1, file2, startPage, endPage);
	}
	

	public boolean compare(String file1, String file2, int startPage) throws IOException{
		return this.comparePdfFiles(file1, file2, startPage, -1);
	}
	
	private boolean comparePdfFiles(String file1, String file2, int startPage, int endPage)throws IOException{
		if(CompareMode.TEXT_MODE==this.compareMode)
			return comparepdfFilesWithTextMode(file1, file2, startPage, endPage);
		else
			return comparePdfByImage(file1, file2, startPage, endPage);
	}
		
	private boolean comparepdfFilesWithTextMode(String file1, String file2, int startPage, int endPage) throws IOException{
		
		String file1Txt = this.getPDFText(file1, startPage, endPage).trim();
		String file2Txt = this.getPDFText(file2, startPage, endPage).trim();
		
		if(null!=this.excludePattern && this.excludePattern.length>0){
			for(int i=0; i<this.excludePattern.length; i++){
				file1Txt = file1Txt.replaceAll(this.excludePattern[i], "");
				file2Txt = file2Txt.replaceAll(this.excludePattern[i], "");
			}
		}
		
		logger.info("File 1 Txt : " + file1Txt);
		logger.info("File 2 Txt : " + file2Txt);
		
		boolean result = file1Txt.equalsIgnoreCase(file2Txt);
		
		if(!result){
			logger.warning("PDF content does not match");
		}
		
		return result; 
	}
	
	public List<String> savePdfAsImage(String file, int startPage) throws IOException{
		return this.saveAsImage(file, startPage, -1);	
	}
	
   /**
   * Save each page of the pdf as image
   * 
   * @param file Absolute file path of the file
   * @param startPage Starting page number of the document
   * @param endPage Ending page number of the document
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */
	public List<String> savePdfAsImage(String file, int startPage, int endPage) throws IOException{
		return this.saveAsImage(file, startPage, endPage);	
	}
	
   /**
   * Save each page of the pdf as image
   * 
   * @param file Absolute file path of the file
   * @return List list of image file names with absolute path
   * @throws java.io.IOException when file is not found.
   */	
	public List<String> savePdfAsImage(String file) throws IOException{
		return this.saveAsImage(file, -1, -1);
	}
	
   /**
   * This method saves the each page of the pdf as image
   */
	private List<String> saveAsImage(String file, int startPage, int endPage) throws IOException{
		
		logger.info("file : " + file);
		logger.info("startPage : " + startPage);
		logger.info("endPage : " + endPage);
		
		ArrayList<String> imgNames = new ArrayList<String>();
		
		try {
			File sourceFile = new File(file);
			this.createImageDestinationDirectory(file);			
			this.updateStartAndEndPages(file, startPage, endPage);
			
			String fileName = sourceFile.getName().replace(".pdf", "");
			
			PDDocument document = PDDocument.load(sourceFile);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for(int iPage=this.startPage-1;iPage<this.endPage;iPage++){
				logger.info("Page No : " + (iPage+1));
				String fname = this.imageDestinationPath + fileName + "_" + (iPage + 1) + ".png";
				BufferedImage image = pdfRenderer.renderImageWithDPI(iPage, 300, ImageType.RGB);
				ImageIOUtil.writeImage(image, fname , 300);
				imgNames.add(fname);
				logger.info("PDf Page saved as image : " + fname);
			}
			document.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return imgNames;  	
	}
		
   /**
   * Compare 2 pdf documents pixel by pixel for the content and format.
   * 
   * @param file1 Absolute file path of the expected file
   * @param file2 Absolute file path of the actual file
   * @param startPage Starting page number of the document
   * @param endPage Ending page number of the document   
   * @param highlightImageDifferences To highlight differences in the images
   * @param showAllDifferences To compare all the pages of the PDF (by default as soon as a mismatch is found in a page, this method exits)
   * @return boolean true if matches, false otherwise
   * @throws java.io.IOException when file is not found.
   */		
	public boolean compare(String file1, String file2,int startPage, int endPage, boolean highlightImageDifferences, boolean showAllDifferences) throws IOException{
		this.compareMode = CompareMode.VISUAL_MODE;
		this.bHighlightPdfDifference = highlightImageDifferences;
		this.bCompareAllPages = showAllDifferences;
		return this.comparePdfByImage(file1, file2, startPage, endPage);
	}		
	
   /**
   * This method reads each page of a given doc, converts to image 
   * compare. If it fails, exits immediately.
   */
	private boolean comparePdfByImage(String file1, String file2, int startPage, int endPage) throws IOException{
		
		logger.info("file1 : " + file1);
		logger.info("file2 : " + file2);
		
		int pgCount1 = this.getPageCount(file1);
		int pgCount2 = this.getPageCount(file2);
		
		if(pgCount1!=pgCount2){
			logger.warning("files page counts do not match - returning false");
			return false;
		}
		
		if(this.bHighlightPdfDifference)
			this.createImageDestinationDirectory(file2);
		
		this.updateStartAndEndPages(file1, startPage, endPage);		
		
		return this.convertToImageAndCompare(file1, file2, this.startPage, this.endPage);
	}



	private boolean convertToImageAndCompare(String file1, String file2, int startPage, int endPage) throws IOException{

		boolean result = true;

		PDDocument doc1=null;
		PDDocument doc2=null;

		PDFRenderer pdfRenderer1 = null;
		PDFRenderer pdfRenderer2 = null;

		try {

			doc1 = PDDocument.load(new File(file1));
			doc2 = PDDocument.load(new File(file2));

			pdfRenderer1 = new PDFRenderer(doc1);
			pdfRenderer2 = new PDFRenderer(doc2);


			for(int iPage=startPage-1;iPage<endPage;iPage++){
				String fileName = new File(file1).getName().replace(".pdf", "_") + (iPage + 1);
				fileName = this.getImageDestinationPath() + "/" + fileName + "_diff.png";

				logger.info("Comparing Page No : " + (iPage+1));
				BufferedImage image1 = pdfRenderer1.renderImageWithDPI(iPage, 300, ImageType.RGB);
				BufferedImage image2 = pdfRenderer2.renderImageWithDPI(iPage, 300, ImageType.RGB);
				result = ImageUtil.compareAndHighlight(image1, image2, fileName, this.bHighlightPdfDifference, this.imgColor.getRGB()) && result;
				if(!this.bCompareAllPages && !result){
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			doc1.close();
			doc2.close();
		}
		return result;
	}
/*
	private boolean convertToImageAndCompare(String file1, String file2, int startPage, int endPage) throws IOException{
		
		boolean result = true;
		
		PDDocument doc1=null;
		PDDocument doc2=null;
		
		PDFRenderer pdfRenderer1 = null;
		PDFRenderer pdfRenderer2 = null;
		
		try {
			
				doc1 = PDDocument.load(new File(file1));
				doc2 = PDDocument.load(new File(file2));
			 
				pdfRenderer1 = new PDFRenderer(doc1);
				pdfRenderer2 = new PDFRenderer(doc2);

				
				for(int iPage=startPage-1;iPage<endPage;iPage++){
					String fileName = new File(file1).getName().replace(".pdf", "_") + (iPage + 1);
					fileName = this.getImageDestinationPath() + "/" + fileName + "_diff.png";
					
					logger.info("Comparing Page No : " + (iPage+1));
					BufferedImage image1 = pdfRenderer1.renderImageWithDPI(iPage, 300, ImageType.RGB);
					BufferedImage image2 = pdfRenderer2.renderImageWithDPI(iPage, 300, ImageType.RGB);
					result = ImageUtil.compareAndHighlight(image1, image2, fileName, this.bHighlightPdfDifference, this.imgColor.getRGB()) && result;
					if(!this.bCompareAllPages && !result){
						break;
					}
				}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			doc1.close();
			doc2.close();
		}
		return result;  	
	}*/
	



	public List<String> extractImages(String file, int startPage) throws IOException{
		return this.extractimages(file, startPage, -1);	
	}
	

	public List<String> extractImages(String file, int startPage, int endPage) throws IOException{
		return this.extractimages(file, startPage, endPage);	
	}
	


	public List<String> extractImages(String file) throws IOException{
		return this.extractimages(file, -1, -1);
	}
	
   /**
   * This method extracts all the embedded images of the pdf document
   */	
	private List<String> extractimages(String file, int startPage, int endPage){
		
		logger.info("file : " + file);
		logger.info("startPage : " + startPage);
		logger.info("endPage : " + endPage);
		
		ArrayList<String> imgNames = new ArrayList<String>();
		boolean bImageFound = false;
		try {

			this.createImageDestinationDirectory(file);
			String fileName = this.getFileName(file).replace(".pdf", "_resource");
			
			PDDocument document = PDDocument.load(new File(file));
			PDPageTree list = document.getPages();
			
			this.updateStartAndEndPages(file, startPage, endPage);
			
			int totalImages = 1;
			for(int iPage=this.startPage-1;iPage<this.endPage;iPage++){	
				logger.info("Page No : " + (iPage+1));
				PDResources pdResources = list.get(iPage).getResources();
				for (COSName c : pdResources.getXObjectNames()) {
		            PDXObject o = pdResources.getXObject(c);
		            if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
		            	bImageFound = true;
		            	String fname = this.imageDestinationPath + "/" + fileName+ "_" + totalImages + ".png";
		                ImageIO.write(((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject)o).getImage(), "png", new File(fname));
		                imgNames.add(fname);
		                totalImages++;
		            }
		        }
			}
			document.close();		
			if(bImageFound)
				logger.info("Images are saved @ " + this.imageDestinationPath);
			else
				logger.info("No images were found in the PDF");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return imgNames;  	
	}

	private void createImageDestinationDirectory(String file) throws IOException{
		if(null==this.imageDestinationPath){
			File sourceFile = new File(file);
			String destinationDir = sourceFile.getParent() + "/temp/";
			this.imageDestinationPath=destinationDir;
			this.createFolder(destinationDir);
		}
	}
	
	private boolean createFolder(String dir) throws IOException{
	    FileUtils.deleteDirectory(new File(dir));
		return new File(dir).mkdir();
	}
	
	private String getFileName(String file){
		return new File(file).getName();
	}

	private void updateStartAndEndPages(String file, int start, int end) throws IOException{
		
		PDDocument document = PDDocument.load(new File(file));
		int pagecount = document.getNumberOfPages();
		logger.info("Page Count : " + pagecount);
		logger.info("Given start page:" + start);
		logger.info("Given end   page:" + end);
		
		if((start > 0 && start <= pagecount)){
			this.startPage = start;
		}else{
			this.startPage = 1;
		}
		if((end > 0 && end >= start && end <= pagecount)){
			this.endPage = end;
		}else{
			this.endPage = pagecount;
		}
		document.close();
		logger.info("Updated start page:" + this.startPage);
		logger.info("Updated end   page:" + this.endPage);
	}


	public static void combineImagesIntoPDF(String pdfPath, String... inputDirsAndFiles) throws IOException {
		try (PDDocument doc = new PDDocument()) {
			for (String input : inputDirsAndFiles) {
				Files.find(Paths.get(input),
						Integer.MAX_VALUE,
						(path, basicFileAttributes) -> Files.isRegularFile(path))
						.forEachOrdered(path -> addImageAsNewPage(doc, path.toString()));
			}
			doc.save(pdfPath);
		}
	}

	private static void addImageAsNewPage(PDDocument doc, String imagePath) {
		try {
			PDImageXObject image          = PDImageXObject.createFromFile(imagePath, doc);
			PDRectangle    pageSize       = PDRectangle.A4;

			int            originalWidth  = image.getWidth();
			int            originalHeight = image.getHeight();
			float          pageWidth      = pageSize.getWidth();
			float          pageHeight     = pageSize.getHeight();
			float          ratio          = Math.min(pageWidth / originalWidth, pageHeight / originalHeight);
			float          scaledWidth    = originalWidth  * ratio;
			float          scaledHeight   = originalHeight * ratio;
			float          x              = (pageWidth  - scaledWidth ) / 2;
			float          y              = (pageHeight - scaledHeight) / 2;

			PDPage         page           = new PDPage(pageSize);
			doc.addPage(page);
			try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
				contents.drawImage(image, x, y, scaledWidth, scaledHeight);
			}
			System.out.println("Added: " + imagePath);
		} catch (IOException e) {
			System.err.println("Failed to process: " + imagePath);
			e.printStackTrace(System.err);
		}
	}
}