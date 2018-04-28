package com.chainway.dispatchercore.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;

public class ExcelUtils {

	/**
	 * @param fileName
	 *            文件名：123.xlsx
	 * @param legalSuffix
	 *            合法的文件后缀：[xls,xlsx]
	 * @return
	 */
	public static void checkFileType(String fileName, JSONObject template) throws Exception {
		if (template == null) {
			return;
		}
		// 校验文件格式
		JSONArray fileTypes = template.getJSONArray("fileTypes");
		if (fileTypes == null) {
			return;
		}
		List<String> legalSuffix = new ArrayList<String>();
		Iterator<Object> it = fileTypes.iterator();
		while (it.hasNext()) {
			legalSuffix.add((String) it.next());
		}
		// 截取文件后缀
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (StringUtils.isEmpty(fileName)) {
			throw new ServiceException(ExceptionCode.ERROR_UPLOAD_FILE_TYPE_ILLEGAL, "文件格式不合法" + legalSuffix, fileName);
		}
		if (legalSuffix == null || legalSuffix.isEmpty()) {
			return;
		}
		if (StringUtils.isEmpty(suffix)) {
			throw new ServiceException(ExceptionCode.ERROR_UPLOAD_FILE_TYPE_ILLEGAL, "文件格式不合法" + legalSuffix, fileName);
		}
		if (legalSuffix.contains(suffix)) {
			return;
		}
	}

	/**
	 * @param template
	 *            文件格式模板
	 * @param data
	 *            文件数据
	 * @param fileName
	 *            文件名（包含后缀）
	 * @param startRowIndex
	 *            解析的开始行（因为有些模板第一行不是数据行）
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> parseFile(JSONObject template, byte[] data, String fileName,
			int startRowIndex) throws Exception {
		if (data == null || data.length <= 0) {
			return new ArrayList<Map<String, Object>>();
		}
		JSONArray cels = template.getJSONArray("cels");
		if (cels == null) {
			throw new ServiceException(ExceptionCode.ERROR_EXCEL_TEMPLATE_CELS_ERROR, "excel模板设置错误");
		}

		List<Map<String, Object>> celsRole = new ArrayList<Map<String, Object>>();
		// 解析出模板
		Iterator<Object> its = cels.iterator();
		while (its.hasNext()) {
			JSONObject cel = (JSONObject) its.next();
			if (cel == null) {
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", cel.getString("name"));

			String dataType = cel.getString("dataType");
			if (StringUtils.isNotEmpty(dataType)) {
				// 校验数据格式
				map.put("dataType", dataType);
			}

			Integer maxValue = cel.getInteger("maxValue");
			if (maxValue != null) {
				map.put("maxValue", maxValue);
			}

			Integer minValue = cel.getInteger("minValue");
			if (minValue != null) {
				map.put("minValue", minValue);
			}

			Boolean require = cel.getBoolean("require");
			if (require != null) {
				map.put("require", require);
			} else {
				map.put("require", true);// 默认是必填项
			}

			celsRole.add(map);
		}
		if (celsRole == null || celsRole.isEmpty()) {
			throw new ServiceException(ExceptionCode.ERROR_EXCEL_TEMPLATE_CELS_ERROR, "excel模板设置错误");
		}

		// 根据模板解析数据
		Workbook wb = null;
		InputStream is = new ByteArrayInputStream(data);
		if (fileName.contains(".xlsx")) {
			wb = new XSSFWorkbook(is);
		} else {
			wb = new HSSFWorkbook(is);
		}
		// 装载解析出的数据
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

		for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
			Sheet sheet = wb.getSheetAt(numSheet);
			if (sheet == null)
				continue;
			for (int rowNum = startRowIndex; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row == null)
					continue;
				Map<String, Object> rowData = new HashMap<String, Object>();
				// 读取每列的值
				for (int i = 0; i < celsRole.size(); i++) {
					Map<String, Object> role = celsRole.get(i);
					Cell cell = null;
					try {
						// 有可能某些行，没有那么多列，会报数组越界
						cell = row.getCell(i);
					} catch (Exception e) {
						e.printStackTrace();
					}

					String name = (String) role.get("name");
					String dataType = (String) role.get("dataType");
					Boolean require = (Boolean) role.get("require");

					if (cell == null) {
						if (!require) {
							// 可空
							continue;
						}
						// 抛异常
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_DATA_EMPTY, "单元格数据为空",
								(rowNum + 1) + "," + (i + 1));
					}
					/*
					 * CELL_TYPE_BLANK 空值（cell不为空） CELL_TYPE_BOOLEAN 布尔
					 * CELL_TYPE_ERROR 错误 CELL_TYPE_FORMULA 公式 CELL_TYPE_STRING
					 * 字符串 CELL_TYPE_NUMERIC 数值
					 * 
					 */

					if (StringUtils.isNotEmpty(dataType)) {
						// 要校验数据的数据类型
						if ("number".equals(dataType)) {
							// 数字类型
							if (HSSFCell.CELL_TYPE_NUMERIC != cell.getCellType()) {
								throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
										"单元格值格式不正确", (rowNum + 1) + "," + (i + 1));
							}
							double v = cell.getNumericCellValue();

							Integer maxValue = (Integer) role.get("maxValue");
							if (maxValue != null && v > maxValue) {
								throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_MAX_VALUE_LIMIT,
										"单元格数值超过最大值", (rowNum + 1) + "," + (i + 1) + "," + maxValue);
							}
							Integer minValue = (Integer) role.get("minValue");
							if (minValue != null && v < minValue) {
								throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_MIN_VALUE_LIMIT,
										"单元格数值小于最小值", (rowNum + 1) + "," + (i + 1) + "," + minValue);
							}
							rowData.put(name, v);
							if (require && v <= 0) {
								// 比较特殊，因为基础类型不能直接判空
								rowData.put(name, null);
							}
						} else if ("date".equals(dataType)) {
							// 日期类型
							try {
								rowData.put(name, cell.getDateCellValue());
							} catch (Exception e) {
								e.printStackTrace();
								throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
										"单元格值格式不正确", (rowNum + 1) + "," + (i + 1));
							}
						} else if ("boolean".equals(dataType)) {
							// 布尔类型
							if (HSSFCell.CELL_TYPE_BOOLEAN != cell.getCellType()) {
								throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
										"单元格值格式不正确", (rowNum + 1) + "," + (i + 1));
							}
							rowData.put(name, cell.getBooleanCellValue());
						} else if ("string".equals(dataType)) {
							// 字符类型
							// 比较特殊，兼容任何类型都转成字符
							try {
								if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
									rowData.put(name, null);
								} else if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
									rowData.put(name, "" + cell.getBooleanCellValue());
								} else if (HSSFCell.CELL_TYPE_ERROR == cell.getCellType()) {
									byte[] b = { cell.getErrorCellValue() };
									rowData.put(name, new String(b));
								} else if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {
									// 公式
									rowData.put(name, cell.getCellFormula());
								} else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
									rowData.put(name, cell.getNumericCellValue() + "");
								} else if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
									rowData.put(name, cell.getStringCellValue());
								} else {
									rowData.put(name, cell.getStringCellValue());
								}
							} catch (Exception e) {
								e.printStackTrace();
								throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR,
										"单元格值格式不正确", (rowNum + 1) + "," + (i + 1));
							}
						}
					} else {
						// 默认是字符串类型的值
						// 字符类型
						// 比较特殊，兼容任何类型都转成字符
						try {
							if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
								rowData.put(name, null);
							} else if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
								rowData.put(name, "" + cell.getBooleanCellValue());
							} else if (HSSFCell.CELL_TYPE_ERROR == cell.getCellType()) {
								byte[] b = { cell.getErrorCellValue() };
								rowData.put(name, new String(b));
							} else if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {
								// 公式
								rowData.put(name, cell.getCellFormula());
							} else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
								rowData.put(name, cell.getNumericCellValue() + "");
							} else if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
								rowData.put(name, cell.getStringCellValue());
							} else {
								rowData.put(name, cell.getStringCellValue());
							}
						} catch (Exception e) {
							e.printStackTrace();
							throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR, "单元格值格式不正确",
									(rowNum + 1) + "," + (i + 1));
						}
					}

					if (require && rowData.get(name) == null) {
						throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_DATA_IS_REQUIRE, "单元格数据必填",
								(rowNum + 1) + "," + (i + 1));
					}

					if ("string".equals(dataType) || StringUtils.isEmpty(dataType)) {
						Integer maxLength = (Integer) role.get("maxLength");
						if (maxLength != null && maxLength.intValue() > 0 && rowData.get(name) != null
								&& rowData.get(name).toString().length() > maxLength) {
							throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_MAX_LENGTH_LIMIT,
									"单元格数据长度超过最大值", (rowNum + 1) + "," + (i + 1) + "," + maxLength);
						}
						Integer minLength = (Integer) role.get("minLength");
						if (minLength != null && minLength.intValue() >= 0 && rowData.get(name) != null
								&& rowData.get(name).toString().length() < minLength) {
							throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_MIN_LENGTH_LIMIT,
									"单元格数据长度小于最小值", (rowNum + 1) + "," + (i + 1) + "," + minLength);
						}
						String regex = (String) role.get("regex");
						if (regex != null && Pattern.matches(regex, rowData.get(name).toString())) {
							throw new ServiceException(ExceptionCode.ERROR_EXCEL_CELL_VALUE_REGX_LIMIT,
									"单元格数据不符合规范，非法字符", (rowNum + 1) + "," + (i + 1) + "," + "包含非小写和数组字符");
						}
					}

				}

				datas.add(rowData);
			}
		}

		return datas;
	}

	/**
	 * @param template
	 *            {name:"导入文件名",cells:[{textField:"车牌号",valueField:"plateNo"}]}
	 * @param data
	 *            List<Map>或者List<Object>
	 * @param timeZone 用户时区
	 * @return
	 * @throws Exception
	 */
//	public static byte[] exportFile(JSONObject template, List<Object> data,String timeZone) throws Exception {
//
//		/*
//		 * if (data == null || data.isEmpty()) { return null; }
//		 */
//
//		if (template == null) {
//			throw new ServiceException(ExceptionCode.ERROR_EXCEL_TEMPLATE_CELS_ERROR, "模板错误");
//		}
//		// {name:"导入文件名",cells:[{textField:"车牌号",valueField:"plateNo"}]}
//		JSONArray cells = template.getJSONArray("cells");
//		if (cells == null) {
//			throw new ServiceException(ExceptionCode.ERROR_EXCEL_TEMPLATE_CELS_ERROR, "模板错误");
//		}
//
//		Workbook wb = new XSSFWorkbook();
//		Sheet sheet = wb.createSheet();
//		//CellStyle cellStyle = wb.createCellStyle();// 创建一个样式
//		//DataFormat format = wb.createDataFormat();// 创建一个DataFormat对象
//		//cellStyle.setDataFormat(format.getFormat("@"));// 这样才能真正的控制单元格格式，@就是指文本型
//
//		// 生成表头
//		Row headerRow = sheet.createRow(0);
//		CellStyle headStyle = getHeadStyle(wb);
//		CellStyle bodyStyle = getBodyStyle(wb);
//		for (int i = 0; i < cells.size(); i++) {
//			JSONObject cell = cells.getJSONObject(i);
//			if (cell == null) {
//				continue;
//			}
//			String textField = cell.getString("textField");
//			addCell(headerRow, i, textField, headStyle);
//		}
//
//		int dataRowStartIndex = 1;// 从第几行开始是数据行
//		if (data != null) { // 为空的时候导出模板
//			for (int i = 0; i < data.size(); i++) {
//				if (data.get(i) == null) {
//					continue;
//				}
//				Row row = sheet.createRow(dataRowStartIndex);
//				dataRowStartIndex++;
//				if (data.get(i) instanceof Map) {
//					// 是map
//					writeRow(row, (Map<String, Object>) data.get(i), cells, bodyStyle,timeZone);
//				} else {
//					// 是实体对象，则用反射机制获取值
//					writeRowEntity(row, data.get(i), cells, bodyStyle,timeZone);
//				}
//			}
//		} else {// 下拉选项或其他单元格设置
//			for (int i = 0; i < cells.size(); i++) {
//				JSONObject cell = cells.getJSONObject(i);
//				if (cell == null) {
//					continue;
//				}
//				JSONArray dropDownArray = cell.getJSONArray("dropDownList");
//				if (dropDownArray != null && dropDownArray.size() > 0) {
//					String[] dropDownList = new String[dropDownArray.size()];
//					for (int j = 0; j < dropDownArray.size(); j++) {
//						dropDownList[j] = dropDownArray.getString(j);
//					}
//					setXSSFValidation((XSSFSheet) sheet, dropDownList, dataRowStartIndex, 500, i, i);
//				}
//
//			}
//		}
//
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		wb.write(os);
//		return os.toByteArray();
//
//	}

	
	public static <E>byte[] exportFile(JSONObject template, List<E> data,String timeZone) throws Exception {

		/*
		 * if (data == null || data.isEmpty()) { return null; }
		 */

		if (template == null) {
			throw new ServiceException(ExceptionCode.ERROR_EXCEL_TEMPLATE_CELS_ERROR, "模板错误");
		}
		// {name:"导入文件名",cells:[{textField:"车牌号",valueField:"plateNo"}]}
		JSONArray cells = template.getJSONArray("cells");
		if (cells == null) {
			throw new ServiceException(ExceptionCode.ERROR_EXCEL_TEMPLATE_CELS_ERROR, "模板错误");
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		//CellStyle cellStyle = wb.createCellStyle();// 创建一个样式
		//DataFormat format = wb.createDataFormat();// 创建一个DataFormat对象
		//cellStyle.setDataFormat(format.getFormat("@"));// 这样才能真正的控制单元格格式，@就是指文本型

		// 生成表头
		Row headerRow = sheet.createRow(0);
		CellStyle headStyle = getHeadStyle(wb);
		CellStyle bodyStyle = getBodyStyle(wb);
		for (int i = 0; i < cells.size(); i++) {
			JSONObject cell = cells.getJSONObject(i);
			if (cell == null) {
				continue;
			}
			String textField = cell.getString("textField");
			addCell(headerRow, i, textField, headStyle);
		}

		int dataRowStartIndex = 1;// 从第几行开始是数据行
		if (data != null) { // 为空的时候导出模板
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i) == null) {
					continue;
				}
				Row row = sheet.createRow(dataRowStartIndex);
				dataRowStartIndex++;
				if (data.get(i) instanceof Map) {
					// 是map
					writeRow(row, (Map<String, Object>) data.get(i), cells, bodyStyle,timeZone);
				} else {
					// 是实体对象，则用反射机制获取值
					writeRowEntity(row, data.get(i), cells, bodyStyle,timeZone);
				}
			}
		} else {// 下拉选项或其他单元格设置
			for (int i = 0; i < cells.size(); i++) {
				JSONObject cell = cells.getJSONObject(i);
				if (cell == null) {
					continue;
				}
				JSONArray dropDownArray = cell.getJSONArray("dropDownList");
				if (dropDownArray != null && dropDownArray.size() > 0) {
					String[] dropDownList = new String[dropDownArray.size()];
					for (int j = 0; j < dropDownArray.size(); j++) {
						dropDownList[j] = dropDownArray.getString(j);
					}
					setXSSFValidation((XSSFSheet) sheet, dropDownList, dataRowStartIndex, 500, i, i);
				}

			}
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		wb.write(os);
		return os.toByteArray();

	}
	
	private static void addCell(Row row, int i, Object value, CellStyle cellStyle) {
		Cell cell = row.createCell(i);

		if (value == null) {// 为空
			cell.setCellStyle(cellStyle);
			cell.setCellValue("");
		} else if (value instanceof String) {// 字符串
			cell.setCellStyle(cellStyle);
			cell.setCellValue(value.toString());
		} else if (value instanceof Integer) {// 整形数字
			cell.setCellValue((Integer) value);
		} else if (value instanceof Long) {// 长整形数字
			cell.setCellValue((Long) value);
		} else if (value instanceof Float) {// 浮点数字
			cell.setCellValue((Float) value);
		} else if (value instanceof Double) {// 双精度数字
			cell.setCellValue((Double) value);
		} else if (value instanceof Date) {// 时间
			// TODO 时间格式暂时固定
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			cell.setCellValue(fmt.format((Date) value));
		} else {// 其他格式默认导出为文本
			cell.setCellValue(value.toString());
		}

	}

	private static void writeRow(Row row, Map<String, Object> rowMap, JSONArray cells, CellStyle cellStyle,String newTimeZone) {
		for (int i = 0; i < cells.size(); i++) {
			JSONObject cell = cells.getJSONObject(i);
			if (cell == null) {
				continue;
			}
			String valueField = cell.getString("valueField");
			Object value = rowMap.get(valueField);
			if(newTimeZone!=null&&value instanceof Date){
				Date date = (Date)value;
				Date d=changeTimeZone(date,Constant.GLOBAL_TIMEZONE,newTimeZone);//得到转换过时区的时间
				String newDate=TimeUtil.date2Str(d, TimeUtil.FORMAT_TIME);
				value = newDate;
			}
			addCell(row, i, value, cellStyle);
		}
	}

	private static void writeRowEntity(Row row, Object obj, JSONArray cells, CellStyle cellStyle,String newTimeZone) {
		for (int i = 0; i < cells.size(); i++) {
			JSONObject cell = cells.getJSONObject(i);
			if (cell == null) {
				continue;
			}
			String valueField = cell.getString("valueField");

			String s1 = valueField.substring(0, 1);// 第一个字母
			String s2 = valueField.substring(1, valueField.length());
			String getMethodName = "get" + s1.toUpperCase() + s2;
			Object value = null;
			try {
				Method m = obj.getClass().getMethod(getMethodName);
				value = m.invoke(obj);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if(newTimeZone!=null&&value instanceof Date){
				Date date = (Date)value;
				Date d=changeTimeZone(date,Constant.GLOBAL_TIMEZONE,newTimeZone);//得到转换过时区的时间
				String newDate=TimeUtil.date2Str(d, TimeUtil.FORMAT_TIME);
				value = newDate;
			}
			addCell(row, i, value, cellStyle);
		}
	}

	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框.
	 * 
	 * @param sheet
	 *            要设置的sheet.
	 * @param textlist
	 *            下拉框显示的内容
	 * @param firstRow
	 *            开始行
	 * @param endRow
	 *            结束行
	 * @param firstCol
	 *            开始列
	 * @param endCol
	 *            结束列
	 * @return 设置好的sheet.
	 */
	public static Sheet setXSSFValidation(XSSFSheet sheet, String[] textlist, int firstRow, int endRow, int firstCol,
			int endCol) {
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		// 加载下拉列表内容
		XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(constraint, regions);
		validation.createErrorBox("输入值有误", "请从下拉框中选择");
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
		return sheet;
	}

	double d;

	public static void main(String[] args) throws Exception {
		/*Integer i = 1;
		double d = 2;
		if (d > i) {
			System.out.println("dddd");
		}

		ExcelUtils t = new ExcelUtils();
		System.out.println(t.d);

		String tpl = "{name:\"导出文件名.xlsx\",cells:[{textField:\"车牌号\",valueField:\"plateNo\",dropDownList:[\"下拉列表1\",\"下拉列表2\",\"下拉列表3\",\"下拉列表4\",\"下拉列表5\",\"下拉列表6\",\"下拉列表7\",\"下拉列表8\",\"下拉列表9\",\"下拉列表10\"]},{textField:\"司机\",valueField:\"driverName\"}]}";
		JSONObject template = JSONObject.parseObject(tpl);
		List data = new ArrayList();
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("plateNo", "粤B0001");
		m1.put("driverName", "张三");
		data.add(m1);

		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("plateNo", "粤B0002");
		m2.put("driverName", "张四");
		data.add(m2);

		String fileName = template.getString("name");
		byte[] datas = exportFile(template, null);
		File file = new File("D:/" + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(datas);
		// 关闭流
		if (bos != null) {
			try {
				bos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (fos != null) {
			try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}*/
	}
	
	/**
	 * 设置表头的单元格样式
	 * @return
	 */
	public static CellStyle getHeadStyle(Workbook wb) {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为淡蓝色
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);
		// 设置单元格字体样式
		Font font = wb.createFont();
		// 设置字体加粗
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		//font.setFontName("宋体");
		//font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		//cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		//cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		//cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		//cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		return cellStyle;
	}

	/**
	 * 设置表体的单元格样式
	 * @return
	 */
	public static CellStyle getBodyStyle(Workbook wb) {
		// 创建单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		// 设置单元格居中对齐
		//cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		// 设置单元格垂直居中对齐
		//cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		// 创建单元格内容显示不下时自动换行
		//cellStyle.setWrapText(true);
		// 设置单元格字体样式
		//Font font = wb.createFont();
		// 设置字体加粗
		//font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		//font.setFontName("宋体");
		//font.setFontHeight((short) 200);
		//cellStyle.setFont(font);
		// 设置单元格边框为细线条
		//cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		//cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		//cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		//cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		return cellStyle;
	}
	private static Date changeTimeZone(Date date,String oldTimeZone,String newTimeZone){
		//Constant.GLOBAL_TIMEZONE
		if(date==null){
			return null;
		}
		try {
			String timeStr=TimeUtil.changeZoneTime(TimeUtil.date2Str(date, TimeUtil.FORMAT_TIME), TimeUtil.FORMAT_TIME, oldTimeZone, TimeUtil.FORMAT_TIME, newTimeZone);
			Date newDate=TimeUtil.str2Date(timeStr, TimeUtil.FORMAT_TIME);
			return newDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}
}
