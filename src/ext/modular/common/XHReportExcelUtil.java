package ext.modular.common;

import ext.modular.calculate.PPMCalculateDetailEntity;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.Region;
import wt.util.WTProperties;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XHReportExcelUtil {
    public static String wthome = null;
    public HSSFWorkbook workbook;
    public ReportExcelGenerateUtil rgu = null;


    static {
        try {
            wthome = (String) (WTProperties.getLocalProperties()).getProperty("wt.home", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 存放写入数据的List
    private List<PPMCalculateDetailEntity> dataList = new ArrayList<PPMCalculateDetailEntity>();

    public List<PPMCalculateDetailEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<PPMCalculateDetailEntity> dataList) {
        this.dataList = dataList;
    }


    /**
     * 按格式要求生成报表1.写入数据 2.关闭输出流，得到Excel文件
     *
     * @param fileName 生成的报表文件名
     * @throws Exception
     */
    public String writeExcelFile(String fileName) throws Exception {
        String toPath = wthome + File.separator + "temp" + File.separator + fileName;
        String fromPath = wthome + File.separator + "codebase" + File.separator + "ext" + File.separator + "ppm"
                + File.separator + "templates" + File.separator + "型号PPM.xls";
        rgu = new ReportExcelGenerateUtil(fromPath);


        workbook = rgu.getWorkbook();


        rgu.setSheet(0);// 设置第一个Sheet
        workbook = rgu.getWorkbook();

        //逐行写入
        writeLine(dataList);

        System.out.println("-----型号明细表 end -----");
        try {
            FileOutputStream fos = new FileOutputStream(toPath);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toPath;
    }

    /**
     * 遍历excelDataList，并将其写入相应Excel
     *
     * @param excelDataList
     * @throws Exception
     */
    private void writeLine(List<PPMCalculateDetailEntity> excelDataList) throws Exception {

        rgu.setSheet(0);
        int rowNumber = 1;
        int sheetIndex = 0;
 //       HSSFSheet sheet = rgu.getWorkbook().getSheetAt(0);
        /** 若此部件没有BOM结构，即其下没有挂子part，则excelDataList大小为0，仅设置表头 */
        if (excelDataList.size() == 0) {
            System.out.println("-----------excelDataList.size() == 0-----------------");
        } else if (excelDataList.size() > 0) {


                System.out.println("-----excelDataList.size()----" + excelDataList.size());
                //将list中的数据写到excel中
                for (PPMCalculateDetailEntity bean : excelDataList) {


                    System.out.println("写入第 " + (rowNumber - 1) + "行数据,此时rowNumber为 " + rowNumber);

//                    int n = bean.getCharacName().size();
//                    rgu.mergedCell(sheet,rowNumber,n,0,1);
//                    sheet.addMergedRegion(new CellRangeAddress(rowNumber, n, 0, 1));
//                    sheet.addMergedRegion(new Region(rowNumber, (short)4, n, (short)5));
//                   sheet.addMergedRegion(new CellRangeAddress(rowNumber, n, 4, 5));
//                    rgu.mergedCell(sheet,rowNumber,n,4,5);
                    rgu.setCellWithStyle(rowNumber, 0, bean.getProductName());
                    rgu.setCellWithStyle(rowNumber, 1, String.valueOf(bean.getProductPPM()));
/*                    if (n > 1){
                        for (String name:bean.getCharacName()) {
                            rgu.setCellWithStyle(rowNumber, 2,name);
                            rowNumber++;
                        }
                    }else{
                        rgu.setCellWithStyle(rowNumber, 2, bean.getCharacName().get(0));
                    }*/

                    rowNumber++;//2 第三行
                }
        }
    }


    /**
     * 计算总的sheet数量
     *
     * @param totalSize 总共的数据量的大小
     * @param pageRow   sheet续页能存放的数据量
     * @return
     */
    @SuppressWarnings("unused")
    private int getTotalSheet(int totalSize, int pageRow) {
        if (totalSize > pageRow) {
            int totalSheet = (totalSize % pageRow) == 0 ? (totalSize / pageRow) : (totalSize / pageRow + 1);
            return totalSheet;
        }
        return totalSize / pageRow + 1;
    }

    /**
     * 实现下载文件
     *
     * @param fileName 文件名
     * @param response
     * @throws IOException
     */
    public static void downloadFile(String fileName, HttpServletResponse response) throws IOException {
        boolean windowsFlag = false; // Default OS is no Windows OS
        String[] displyNameArr = null;
        String osName = System.getProperty("os.name");
        System.out.println("osname is : " + osName);
        if ((osName != null && !("".equals(osName))) && osName.toUpperCase().startsWith("WINDOWS")) {
            windowsFlag = true;
        }

        if (windowsFlag) {
            System.out.println("The OS is Windows");
            displyNameArr = fileName.split("\\\\");
            if (fileName.indexOf("\\") == -1) {
                return;
            }
        } else {
            System.out.println("The OS is no Windows");
            displyNameArr = fileName.split(File.separator);
            if (fileName.indexOf(File.separator) == -1) {
                return;
            }
        }
        String displayName = "";
        for (String str : displyNameArr) {
            if (str.indexOf(".") != -1) {
                displayName = str;
            }
        }
        String filename = java.net.URLEncoder.encode(displayName, "UTF-8");
        OutputStream os = response.getOutputStream();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        File temp = new File(fileName);
        InputStream input = new FileInputStream(temp);

        byte[] buff = new byte[512];

        int len = 0;
        while ((len = input.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
        input.close();
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
        os.flush();
    }


}
