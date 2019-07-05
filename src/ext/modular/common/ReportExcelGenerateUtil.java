package ext.modular.common;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportExcelGenerateUtil {
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private HSSFRow row;
	private HSSFCell cell;
	private HSSFFont font;
	private static FileInputStream in;

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public ReportExcelGenerateUtil(String templatePath) {
		try {
			if ((templatePath != null) && (!templatePath.equals(""))) {
				in = new FileInputStream(templatePath);
				workbook = new HSSFWorkbook(in);
			} else {
				workbook = new HSSFWorkbook();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSheet(int index) {
		sheet = workbook.getSheetAt(index);
	}

	public void removeSheetAt(int index) {
		workbook.removeSheetAt(index);
	}

	public void setSheet(String index) {
		sheet = workbook.getSheet(index);
	}

	public void setColumnWidth(int column, int width) {
		sheet.setColumnWidth((short) column, (short) width);
	}

	public void cloneSheet(int index) {
		workbook.cloneSheet(index);
	}

	public void createSheet(String sheetName) {
		sheet = workbook.createSheet(sheetName);
	}

	@SuppressWarnings("deprecation")
	public void setCellContent(int a, int b, String s) {
		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		s = s == null ? "" : s;
		cell.setCellValue(s);

	}

	@SuppressWarnings("deprecation")
	public String getCellContent(int a, int b) {
		String cellvalue = null;
		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		try {
			cellvalue = cell.getStringCellValue();
		} catch (NullPointerException e) {
			cellvalue = "0";
			setCellContent(a, b, "");
		}
		return cellvalue;
	}

	@SuppressWarnings("deprecation")
	public void setCellWithCenterAlign(int a, int b, String s) {
		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cell.setCellStyle(style);
		s = s == null ? "" : s;
		cell.setCellValue(s);
	}

	@SuppressWarnings("deprecation")
	public void setCellWithStyle(int a, int b, String s) {

		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		s = s == null ? "" : s;
		cell.setCellValue(s);
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cell.setCellStyle(style);
	}

	@SuppressWarnings("deprecation")
	public void setCellWithStyle(int a, int b, String s, HSSFSheet sheet) {

		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		cell.setCellStyle(style);
		s = s == null ? "" : s;
		cell.setCellValue(s);
	}

	@SuppressWarnings("deprecation")
	public void setCellWithLeftBorder(int a, int b, String s) {
		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell.setCellStyle(style);
		s = s == null ? "" : s;
		cell.setCellValue(s);
	}

	@SuppressWarnings("deprecation")
	public void setCellWithRightBorder(int a, int b, String s) {
		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell.setCellStyle(style);
		s = s == null ? "" : s;
		cell.setCellValue(s);
	}

	@SuppressWarnings("deprecation")
	public void setCellWithColorStyle(int a, int b, String s, short point, short color) {
		row = sheet.getRow(a);
		if (row == null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if (cell == null) {
			cell = row.createCell((short) b);
			// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();

		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setFillBackgroundColor(HSSFColor.DARK_BLUE.index);
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		style.setFillForegroundColor(color);

		font = workbook.createFont();
		font.setFontName("宋体");
		font.setCharSet(HSSFFont.DEFAULT_CHARSET);
		font.setFontHeightInPoints(point);
		style.setFont(font);
		cell.setCellStyle(style);
		s = s == null ? "" : s;
		cell.setCellValue(s);
	}

	/**
	 * 合并单元格
	 * @param sheet 操作的sheet
	 * @param beginrow 起始行
	 * @param endrow 结束行
	 * @param begincol 起始列
	 * @param endcol 结束列
	 * @return
	 */
	public String mergedCell(Sheet sheet, int beginrow, int endrow, int begincol, int endcol) {
		try {
			sheet.addMergedRegion(new CellRangeAddress(
					beginrow, //first row (0-based)
					endrow, //last row  (0-based)
					begincol, //first column (0-based)
					endcol  //last column  (0-based)
			));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
