package com.yit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import com.yit.export.ExportRunner2.SPUInfo;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 成本核算   初始化模板导出
 *
 */
public class EXCELUtils {

    private static int[] widths = null;
    private static HSSFCellStyle cellStyle;
    private static HSSFSheet sheet;
    public static void doExport(List<SPUInfo> spuInfos, String name) throws IOException {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        sheet = hssfWorkbook.createSheet("sheet1");//创建一个sheet页
        sheet.setDefaultColumnWidth(200);
        cellStyle = hssfWorkbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        HSSFDataFormat dataFormat = hssfWorkbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));

        HSSFRow hssfRow = sheet.createRow(0);//创建标题行

        String[] values = new String[] {
            "供应商名称",
            "合同主体名称",
            "合同号",
            "负责BD",
            "SKUID",
            "商品编码Barcode",
            "商品名称&规格",
            "日常结算方案",
            "日常结算方案",
            "日常结算方案",
            "普通促销结算方案",
            "普通促销结算方案",
            "大促结算方案",
            "大促结算方案"
        };

        for (int i = 0; i < values.length; i++) {
            Cell cell = hssfRow.createCell(i);
            cell.setCellValue(values[i]);
            HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
            cellStyle.setDataFormat(dataFormat.getFormat("@"));
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cell.setCellStyle(cellStyle);//设置样式
            cell.setCellType(Cell.CELL_TYPE_STRING);

        }
        calculateWidth(values);

        HSSFRow hssfRow2 = sheet.createRow(1);

        String[] value2 = new String[] {
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "厂商指导价",
            "日常售价",
            "日常结算价格／结算扣点（%）（二选一填写",
            "普促售价",
            "普促结算价格／结算扣点（%）（二选一填写",
            "大促售价",
            "大促结算价格／结算扣点（%）（二选一填写"
        };
        for (int i = 0; i < value2.length; i++) {
            Cell cell = hssfRow2.createCell(i);
            cell.setCellValue(value2[i]);
            cell.setCellStyle(cellStyle);//设置样式
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        calculateWidth(value2);

        //合并单元格
        CellRangeAddress cra1 = new CellRangeAddress(0, 1, 0, 0);
        CellRangeAddress cra2 = new CellRangeAddress(0, 1, 1, 1);
        CellRangeAddress cra3 = new CellRangeAddress(0, 1, 2, 2);
        CellRangeAddress cra4 = new CellRangeAddress(0, 1, 3, 3);
        CellRangeAddress cra5 = new CellRangeAddress(0, 1, 4, 4);
        CellRangeAddress cra6 = new CellRangeAddress(0, 1, 5, 5);
        CellRangeAddress cra7 = new CellRangeAddress(0, 1, 6, 6);
        CellRangeAddress cra8 = new CellRangeAddress(0, 0, 7, 9);
        CellRangeAddress cra9 = new CellRangeAddress(0, 0, 10, 11);
        CellRangeAddress cra10 = new CellRangeAddress(0, 0, 12, 13);

        sheet.addMergedRegion(cra1);
        sheet.addMergedRegion(cra2);
        sheet.addMergedRegion(cra3);
        sheet.addMergedRegion(cra4);
        sheet.addMergedRegion(cra5);
        sheet.addMergedRegion(cra6);
        sheet.addMergedRegion(cra7);
        sheet.addMergedRegion(cra8);
        sheet.addMergedRegion(cra9);
        sheet.addMergedRegion(cra10);

        for (SPUInfo info : spuInfos) {
            HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);

            String[] value3 = new String[] {
                info.supplierName != null ? info.supplierName.trim() : "",
                info.contractHead != null ? info.contractHead.trim() : "",
                "",
                info.bdName != null ? info.bdName.trim() : "",
                String.valueOf(info.skUId).trim(),
                info.vendorSKU != null ? info.vendorSKU.trim() : "",
                info.productNameAndOption != null ? info.productNameAndOption.trim() : "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
            };

            for (int i = 0; i < value3.length; i++) {
                Cell cell = dataRow.createCell(i);
                cell.setCellValue(value3[i]);
                cell.setCellStyle(cellStyle);//设置样式
                cell.setCellType(Cell.CELL_TYPE_STRING);
            }
            calculateWidth(value3);
        }

        setAutoSizeWidth(name, hssfWorkbook);
        OutputStream os = new FileOutputStream("/Users/sober/Desktop/8月28日导出/" + name + ".xls");
        hssfWorkbook.write(os);
        os.close();
    }

    private static void setAutoSizeWidth(String name, HSSFWorkbook hssfWorkbook) throws IOException {
        sheet.autoSizeColumn((short)3);
        sheet.setColumnWidth(2, 1300);
        sheet.autoSizeColumn((short)4);
        sheet.autoSizeColumn((short)5);
        sheet.autoSizeColumn((short)6);
        sheet.autoSizeColumn((short)7);
        sheet.autoSizeColumn((short)8);
        sheet.autoSizeColumn((short)9);
        sheet.autoSizeColumn((short)10);
        sheet.autoSizeColumn((short)11);
        sheet.autoSizeColumn((short)12);
        sheet.autoSizeColumn((short)13);

        //为每一列设置宽度
        for (int index = 0; index < 3; ++index) {
            sheet.setColumnWidth(index, widths[index]);
        }

    }

    private static void calculateWidth(String[] values) {
        if (widths == null) {
            widths = new int[values.length];
            for (int index = 0; index < widths.length; ++index) {
                widths[index] = 200;
            }
        }

        // 计算宽度
        for (int index = 0; index < widths.length; ++index) {
            int oldWidth = widths[index];
            int newWidth = (int)(values[index].length() * 256
                + (values[index].getBytes().length - values[index].length()) * 128);
            if (newWidth > oldWidth) {
                widths[index] = newWidth;
            }
        }
    }

}
