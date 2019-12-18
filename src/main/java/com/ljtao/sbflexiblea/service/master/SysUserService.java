package com.ljtao.sbflexiblea.service.master;

import com.ljtao.sbflexiblea.common.config.Global;
import com.ljtao.sbflexiblea.common.core.domain.AjaxResult;
import com.ljtao.sbflexiblea.common.utils.StringUtils;
import com.ljtao.sbflexiblea.dao.master.SysUserMapper;
import com.ljtao.sbflexiblea.domian.Params;
import com.ljtao.sbflexiblea.domian.master.SysDept;
import com.ljtao.sbflexiblea.domian.master.SysUser;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * Excel sheet最大行数，默认65536
     */
    public static final int sheetSize = 65536;
    /*
        用于user.html页面的 userTable查询
     */
    public List<SysUser> selectUserList(SysUser user, Params params){

        int offset=(params.getPageNum()-1)*params.getPageSize();
        params.setOffset(offset);
        params.setOrderByColumn(StringUtils.toUnderScoreCase(params.getOrderByColumn()));
        return sysUserMapper.selectUserList(user,params);
    }
    public int selectUserListCount(SysUser user, Params params){
        int total = sysUserMapper.selectUserListCount(user, params);
        return total;
    }
    //获取要导出的数据
    public List<SysUser> selectUserListForExport(SysUser sysUser, Params params) {
        params.setOffset(0);
        params.setPageSize(sysUserMapper.selectUserListCount(sysUser, params));
        params.setOrderByColumn("create_time");
        params.setIsAsc("desc");
        return sysUserMapper.selectUserList(sysUser,params);
    }
    //自己将数据写入到Excel
    public AjaxResult export(List<SysUser> userList, String sheetName) throws IOException {
        //创建工作簿
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
        OutputStream out=null;
        //返回需要创建多少个sheet
        double sheetNo=Math.ceil(userList.size()/sheetSize);
        for (int index = 0; index <=sheetNo; index++) {
            //创建一个sheet
            SXSSFSheet sheet = wb.createSheet();
            Map<String, CellStyle> styles=createStyles(wb);
            //设置sheet的名称
            if(sheetNo==0){
                wb.setSheetName(index,sheetName);
            }
            else{
                wb.setSheetName(index,sheetName+index);
            }
            //产生一行
            Row row=sheet.createRow(0);
            int colum=0;
            //对Excel头部进行处理
            for(int i=0;i<=2;i++){
                Cell cell = row.createCell(i);
                cell.setCellValue("列名."+i);
                //设置宽高
//                sheet.setColumnWidth(0,256);
//                row.setHeight((short) (2 * 20));
                cell.setCellStyle(styles.get("header"));
            }
            //将数据插入到Excel表
            for (int i = 0; i < userList.size(); i++) {
                Row row1=sheet.createRow(i+1);
                Cell c0=row1.createCell(0);
                c0.setCellValue(userList.get(i).getLoginName());
                c0.setCellStyle(styles.get("data"));
                Cell c1=row1.createCell(1);
                c1.setCellValue(userList.get(i).getEmail());
                c1.setCellStyle(styles.get("data"));
                Cell c2=row1.createCell(2);
                c2.setCellValue(userList.get(i).getDeptId());
                c2.setCellStyle(styles.get("data"));
            }

        }
        String fileName="";
        fileName = UUID.randomUUID().toString() + "_" + sheetName + ".xlsx";
        String downloadPath= Global.getDownloadPath()+fileName;
        File desc=new File(downloadPath);
        if(!desc.getParentFile().exists()){
            desc.getParentFile().mkdirs();
        }
        try{
            out=new FileOutputStream(desc);
            wb.write(out);

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(wb!=null){
                wb.close();
            }
            if(out!=null){
                out.close();
            }
        }
        return AjaxResult.success(fileName);
    }



    private Map<String, CellStyle> createStyles(SXSSFWorkbook wb) {
        // 写入各条记录,每条记录对应excel表中的一行
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }
    //将Excel的数据加入到List
    public List<SysUser> importExcel(String sheetName,InputStream is) throws Exception {
        Workbook wb = WorkbookFactory.create(is);
        List<SysUser> list = new ArrayList<SysUser>();
        Sheet sheet = null;
        if (StringUtils.isNotEmpty(sheetName))
        {
            // 如果指定sheet名,则取指定sheet中的内容.
            sheet = wb.getSheet(sheetName);
        }
        else
        {
            // 如果传入的sheet名不存在则默认指向第1个sheet.
            sheet = wb.getSheetAt(0);
        }
        if(sheet==null){
            throw new Exception("文件sheet不存在");
        }
        int rows = sheet.getPhysicalNumberOfRows();
        if(rows<1){
            return list;
        }
        //获取标题，如果对标题有处理就在这后面进行操作，，后期这里需要根据实际需要优化
        Row heardRow=sheet.getRow(0);
        //获取有多少行
        int cellLength = heardRow.getPhysicalNumberOfCells();
        for (int  i= 1; i < rows; i++) {
            SysUser user=new SysUser();
            Row row=sheet.getRow(i);
            /*
                这里暂时不判断数据的类型，后期这里需要根据实际需要优化
             */
            user.setLoginName(row.getCell(0).getStringCellValue());
            user.setEmail(row.getCell(1).getStringCellValue());
            user.setDeptId((long)row.getCell(2).getNumericCellValue());
            list.add(user);
        }
        if (is!=null){
            is.close();
        }
        return list;
    }
}
