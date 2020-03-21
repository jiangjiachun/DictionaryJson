/**
 * 
 */
package com.dictionary.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 字典数据通过excel文件导入处理
 * 
 * @author jjc
 * @date 2020年1月4日
 */
@Component
public final class DictionaryExcelHolder {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private HSSFWorkbook hssfWorkbook;

	private ObjectMapper objectMapper = new ObjectMapper();

	private String[] titles;

	/**
	 * excel路径，按照dictionary.xls中的填写数据方式，一个sheet生成一个json字典
	 */
	public final static String EXCEL_PATH = "dictionary/dictionary.xls";

	/**
	 * 前端处理json字典插件，具体使用见dictionary.utils.js
	 */
	public final static String JS_PLUGIN_PATH = "dictionary/dictionary.utils.js";

	/**
	 * 最终生成完整的js文件，合并EXCEL_PATH、JS_PLUGIN_PATH
	 */
	public final static String JS_JSON_PATH = "static/dictionary.js";

	private List<Map<String, Object>> dictionarys = new ArrayList<Map<String, Object>>();

	private File file(String path) throws IOException {
		File file = new ClassPathResource(path).getFile();
		return file;
	}

	/**
	 * 解析数据
	 * 
	 * @throws IOException 
	 *
	 */
	@PostConstruct
	public void analyze() throws IOException {

		analyze(EXCEL_PATH, JS_PLUGIN_PATH, JS_JSON_PATH);
	}

	/**
	 * 解析数据
	 * 
	 * @param excelPath excel字典数据路径
	 * @param jsPluginPath 前端读字典插件路径
	 * @param jsJsonPath excel分析出的json字典与前端读字典插件合并成js文件
	 */
	public void analyze(String excelPath, String jsPluginPath, String jsJsonPath) {
		log.info("初始化字典数据开始...");
		try (FileInputStream fileInputStream = new FileInputStream(file(excelPath));
				BufferedWriter fileWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file(jsJsonPath)), "utf-8"))) {
			log.info("解析路径：{}", file(excelPath).getPath());
			
			hssfWorkbook = new HSSFWorkbook(fileInputStream);
			hssfWorkbook.sheetIterator().forEachRemaining(sheet -> sheet(sheet, fileWriter));
			
			plugin(fileWriter, jsPluginPath);
			
			fileWriter.close();
			fileInputStream.close();
			
			log.info("初始化字典数据完成...");
		} catch (Exception e) {
			log.error("初始化字典数据错误：", e);
		}
		
	}

	private void sheet(Sheet sheet, BufferedWriter fileWriter) {
		// 变量名
		String var = sheet.getRow(0).getCell(0).getStringCellValue();
		// 标题
		titles = title(sheet.getRow(1));

		List<Map<String, Object>> dictionary = rows(sheet, 2);
		// 所有字典数据放到list，便于后台查询
		dictionarys.addAll(dictionary);
		try {
			String json = objectMapper.writeValueAsString(dictionary);
			json = js(var, json);
			log.info("解析字典数据：{}", sheet.getSheetName());
			fileWriter.write(json);
		} catch (IOException e) {
			log.error("解析字典数据错误", e);
		}
	}

	/**
	 * 写入js plugin
	 * @param fileWriter
	 * @param jsPluginPath
	 */
	private void plugin(BufferedWriter fileWriter, String jsPluginPath) {
		try {
			File js = file(jsPluginPath);
			byte[] bytes = Files.readAllBytes(js.toPath());
			String plugin = new String(bytes, "utf-8");
			fileWriter.append(plugin);
			log.info("JS插件写入完成...");
		} catch (IOException e) {
			log.error("JS插件写入错误", e);
		}
	}

	/**
	 * 拼接字典json
	 * @param var
	 * @param json
	 * @return
	 */
	private String js(String var, String json) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("var " + var + " = ");
		stringBuffer.append(json);
		stringBuffer.append(";" + System.getProperty("line.separator"));
		return stringBuffer.toString();
	}

	/**
	 * 通过标题获取json的key值
	 * 
	 * @param titles
	 * @return
	 */
	private String[] title(Row row) {
		short minColIx = row.getFirstCellNum();
		short maxColIx = row.getLastCellNum();
		String[] title = new String[maxColIx];
		for (short colIx = minColIx; colIx < maxColIx; colIx++) {
			Cell cell = row.getCell(colIx);
			if (cell == null) {
				continue;
			}
			title[colIx] = cell.getStringCellValue();
		}
		return title;
	}

	/**
	 * 返回所有字典数据
	 * 
	 * @param sheet
	 * @param startRowNum 跳过前两行，第一行表示字典名，第二行表示标题，第三行开始字典数据
	 * @return
	 */
	private List<Map<String, Object>> rows(Sheet sheet, int startRowNum) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		// 获取最后一行行号
		int lastRowNum = sheet.getLastRowNum();
		for (int i = startRowNum; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			putData(row, nodes);
		}
		return nodes;
	}

	/**
	 * 设置值
	 * 
	 * @param row
	 * @return
	 */
	private void putData(Row row, List<Map<String, Object>> nodes) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		short minColIx = row.getFirstCellNum();
		short maxColIx = row.getLastCellNum();
		for (short colIx = minColIx; colIx < maxColIx; colIx++) {
			Cell cell = row.getCell(colIx);
			if (cell == null) {
				continue;
			}
			if(cell.getStringCellValue() == null || cell.getStringCellValue() == "") {
				continue;
			}
			if(titles[colIx] == titles[0]) {
				data.put(titles[colIx], row.getSheet().getSheetName() + "_" + cell.getStringCellValue());
			}
			else {
				data.put(titles[colIx], cell.getStringCellValue());
			}
			
		}
		nodes.add(data);
	}

	/**
	 * 根据字典ID查询字典名
	 * 
	 * @param id
	 * @return
	 */
	public String findDicName(String id) {
		for (Map<String, Object> map : dictionarys) {
			if (map.get(titles[0]).equals(id)) {
				return map.get(titles[1]).toString();
			}
		}
		return "";
	}

	/**
	 * 根据字典名查询字典ID
	 * 
	 * @param name
	 * @return
	 */
	public String findDicId(String name) {
		for (Map<String, Object> map : dictionarys) {
			if (map.get(titles[1]) != null && map.get(titles[1]).equals(name)) {
				return map.get(titles[0]).toString();
			}
		}
		return "";
	}
}
