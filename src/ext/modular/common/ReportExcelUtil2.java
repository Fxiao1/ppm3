package ext.modular.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import wt.util.WTProperties;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportExcelUtil2 {
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
    private Map<String,Map<String,Integer>> mapData = new LinkedHashMap<>();

    public Map<String,Map<String,Integer>> getMapData() {
        return mapData;
    }

    public void setDataList(Map<String,Map<String,Integer>> mapData) {

        this.mapData = mapData;
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
                + File.separator + "templates" + File.separator + "PPM统计.xls";
        rgu = new ReportExcelGenerateUtil(fromPath);


        workbook = rgu.getWorkbook();


        rgu.setSheet(0);// 设置第一个Sheet
        workbook = rgu.getWorkbook();

        //逐行写入
        writeLine(mapData);

        System.out.println("-----明细表 end -----");
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
     * 将mapData写入相应Excel
     *
     * @param mapData
     * @throws Exception
     */
    private void writeLine(Map<String,Map<String,Integer>> mapData) throws Exception {

        rgu.setSheet(0);
        int rowNumber = 1;
        int sheetIndex = 0;
 //       HSSFSheet sheet = rgu.getWorkbook().getSheetAt(0);
        /** 若此部件没有BOM结构，即其下没有挂子part，则excelDataList大小为0，仅设置表头 */
        if (mapData.size() == 0) {
            System.out.println("-----------excelDataList.size() == 0-----------------");
        } else if (mapData.size() == 5) {


            System.out.println("-----mapData.size()----" + mapData.size());

            Map<String,Integer> data1 = mapData.get("当月");
            Map<String,Integer> data2 = mapData.get("上月");
            Map<String,Integer> data3 = mapData.get("去年本月");
            Map<String,Integer> data4 = mapData.get("环比");
            Map<String,Integer> data5 = mapData.get("同比");
            for (String name:data1.keySet()) {
                rgu.setCellWithStyle(rowNumber, 0, name);
                rgu.setCellWithStyle(rowNumber, 1, String.valueOf(data1.get(name)));
                rgu.setCellWithStyle(rowNumber, 2, String.valueOf(data2.get(name)));
                rgu.setCellWithStyle(rowNumber, 3, String.valueOf(data3.get(name)));
                System.out.println("data4.get(name) " + data4.get(name));
                rgu.setCellWithStyle(rowNumber, 4, String.valueOf(data4.get(name)) + "%");
                rgu.setCellWithStyle(rowNumber, 5, String.valueOf(data5.get(name)) + "%");
                rowNumber++;//2 第三行
            }
            
        } else if (mapData.size() == 3){
            System.out.println("-----mapData.size()----" + mapData.size());

            Map<String,Integer> data1 = mapData.get("当年");
            Map<String,Integer> data2 = mapData.get("去年");
            Map<String,Integer> data3 = mapData.get("环比");

            for (String name:data1.keySet()) {
                rgu.setCellWithStyle(rowNumber, 0, name);
                rgu.setCellWithStyle(rowNumber, 1, String.valueOf(data1.get(name)));
                rgu.setCellWithStyle(rowNumber, 2, String.valueOf(data2.get(name)));
                rgu.setCellWithStyle(rowNumber, 4, String.valueOf(data3.get(name)) + "%");

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
