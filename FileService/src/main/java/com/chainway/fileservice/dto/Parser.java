package com.chainway.fileservice.dto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;

public class Parser<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8113684176206911483L;

	public List<T>parse(byte[]data,FileTemplate tpl,Render<T>render) throws Exception{
		if(data==null||data.length==0){
			System.err.println("数据为空");
			return null;
		}
		if(tpl==null){
			System.err.println("模板错误");
			return null;
		}
		
		//获取模板
		String templateStr=tpl.getVerifyRule();
		if(StringUtils.isEmpty(templateStr)){
			System.err.println("模板错误");
			return null;
		}
		JSONObject template=JSONObject.parseObject(templateStr);
		if(template==null){
			System.err.println("模板错误");
			return null;
		}
		/*
		 * {
		 * 	startIndex:1,//第一行是表头
		 * 	fileTypes:["xls","xlsx"],
		 * 	cels:[{
		 * 		name:"plateNo"
		 * 	},{
		 * 		name:"mileage",
		 * 		dataType:"number",
		 * 		maxValue:999999,
		 * 		minValue:0
		 * 	},{
		 * 		name:"test",
		 * 		require:false
		 * 	},{
		 * 		name:"time",
		 * 		dataType:"date"
		 * 	}]
		 * 
		 * }
		 * 
		 * */
		
		DecimalFormat numberStringFormat=new DecimalFormat("#");
		
		JSONArray cells=template.getJSONArray("cels");
		if(cells==null){
			System.err.println("模板错误");
			return null;
		}
		
		Integer startIndex=template.getInteger("startIndex");
		startIndex=startIndex==null?0:startIndex;
		
		//二进制转成inputStream
		InputStream input=new ByteArrayInputStream(data); 
		XSSFWorkbook workBook=new XSSFWorkbook(input);
		XSSFSheet sheet=workBook.getSheetAt(0);
		if(sheet==null){
			System.err.println("数据错误");
			return null;
		}
		
		List<T>list=new ArrayList<T>();
		
		for(int i=startIndex;i<sheet.getPhysicalNumberOfRows();i++){
			XSSFRow row=sheet.getRow(i);
			if(row==null){
				continue;
			}
			
			T t=render.genObject();
			list.add(t);
			//解析列
			//如果列和模板列不一致，则抛异常
			if(cells.size()!=row.getLastCellNum()){
				throw new ServiceException(ExceptionCode.ERROR_FILE_TEMPLATE_UNMATCHED_DATA,"模板列和上传数据列不匹配");
			}
			for(int j=0;j<row.getLastCellNum();j++){
				XSSFCell cell=row.getCell(j);
				JSONObject cellRule=cells.getJSONObject(j);
				String name=cellRule.getString("name");//对应的列名
				String dataType=cellRule.getString("dataType");//对应数据格式(string-字符串,number-数字,date-日期,boolean-布尔) 
				dataType=dataType==null?"string":dataType;//默认字符串类型
				
				Boolean require=cellRule.getBoolean("require");
				if(require==null){
					//默认要求必填
					require=true;
				}
				if(!require&&cell==null){
					//允许为空
					continue;
				}
				//参数必填
				if(cell==null){
					throw new ServiceException(ExceptionCode.ERROR_PARAM_IS_REQUIRED,name);
				}
				
				if("number".equals(dataType)){
					// 数字类型
					if (HSSFCell.CELL_TYPE_NUMERIC != cell.getCellType()) {
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
								"单元格值格式不正确", (i + 1) + "," + (j + 1));
					}
					double v=cell.getNumericCellValue();
					//最大值限制
					Integer maxValue=cellRule.getInteger("maxValue");
					if (maxValue != null && v > maxValue) {
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_MAX_VALUE_LIMIT,
								"单元格数值超过最大值", (i + 1) + "," + (j + 1) + "," + maxValue);
					}
					//最小值限制
					Integer minValue=cellRule.getInteger("minValue");
					if (minValue != null && v < minValue) {
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_MIN_VALUE_LIMIT,
								"单元格数值小于最小值", (i + 1) + "," + (j + 1) + "," + minValue);
					}
					//装载数据
					render.fill(t, name, v);
				}else if ("date".equals(dataType)) {
					// 日期类型
					try {
						//装载数据
						render.fill(t, name, cell.getDateCellValue());
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
								"单元格值格式不正确", (i + 1) + "," + (j + 1));
					}
				}else if ("boolean".equals(dataType)) {
					// 布尔类型
					if (HSSFCell.CELL_TYPE_BOOLEAN != cell.getCellType()) {
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
								"单元格值格式不正确", (i + 1) + "," + (j + 1));
					}
					//rowData.put(name, cell.getBooleanCellValue());
					//装载数据
					render.fill(t, name, cell.getBooleanCellValue());
				}else{
					//默认为字符串类型
					// 比较特殊，兼容任何类型都转成字符
					try {
						if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
							//rowData.put(name, null);
							//装载数据
							render.fill(t, name, null);
						} else if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
							//rowData.put(name, "" + cell.getBooleanCellValue());
							//装载数据
							render.fill(t, name, "" + cell.getBooleanCellValue());
						} else if (HSSFCell.CELL_TYPE_ERROR == cell.getCellType()) {
							byte[] b = { cell.getErrorCellValue() };
							//rowData.put(name, new String(b));
							//装载数据
							render.fill(t, name, new String(b));
						} else if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {
							// 公式
							//rowData.put(name, cell.getCellFormula());
							//装载数据
							render.fill(t, name, cell.getCellFormula());
						} else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
							//rowData.put(name, cell.getNumericCellValue() + "");
							//装载数据
							//System.out.println(numberStringFormat.format(cell.getNumericCellValue()));
							render.fill(t, name, numberStringFormat.format(cell.getNumericCellValue()));
						} else if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
							//rowData.put(name, cell.getStringCellValue());
							//装载数据
							render.fill(t, name, cell.getStringCellValue());
						} else {
							//rowData.put(name, cell.getStringCellValue());
							//装载数据
							render.fill(t, name, cell.getStringCellValue());
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
								"单元格值格式不正确", (i + 1) + "," + (j + 1));
					}
					
				}
				
			}
			
			
			
			
		}
		
		return list;
	}
}
