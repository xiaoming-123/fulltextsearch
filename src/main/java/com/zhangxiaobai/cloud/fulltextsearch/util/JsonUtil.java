package com.zhangxiaobai.cloud.fulltextsearch.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON工具类
 * @author zhangcq
 */
public final class JsonUtil {
	/**
	 * 构造方法，私有化，防止被实例化
	 */
	private JsonUtil(){}
	
	// 静态初始化
	private static ObjectMapper mapper = new ObjectMapper();
	static{
		// 反序列化的时候如果多了其他属性,不抛出异常
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 如果是空对象的时候,不抛异常  
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 允许无引号
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许单引号
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	/**
	 * 将json字符串转换为对象
	 * @param classname 需要转换的对象的class
	 * @param src  json字符串
	 * @return 转换后的对象
	 * @throws IOException 
	 * @throws Exception
	 */
	public static <T> T toBean(Class<T> classname, String src) throws IOException{
		if(src == null || src.length() == 0 || classname==null){
			return null;
		}
		ObjectReader reader = mapper.readerFor(classname);
		return reader.readValue(src);
	}
	
	/**
	 * 将json字符串转换为对象列表
	 * @param classname 需要转换的对象的class
	 * @param src json字符串
	 * @return 转换后的对象列表
	 * @throws IOException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> toBeanList(Class<T> classname, String src) throws IOException{
		ArrayList<T> list = new ArrayList<T>();
		if(src == null || src.length() == 0 || classname==null){
			return list;
		}
		try{
			// 尝试以数组方式解析字符串
			T[] temparr = (T[])Array.newInstance(classname, 0);
			ObjectReader reader = mapper.readerFor(temparr.getClass());
			T[] values = reader.readValue(src);
			for(T t : values){
				list.add(t);
			}
		}catch(IOException e){
			try{
				// 异常时再次尝试以对象方式解析字符串
				ObjectReader reader = mapper.readerFor(classname);
				list.add((T)reader.readValue(src));
			}catch(IOException e1){
				throw e;
			}
		}
		return list;
	}
	
	/**
	 * 将对象转成json格式的字符串
	 * @param t 需要转换的对象
	 * @return 转换后的json字符串
	 * @throws IOException 
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * @throws Exception
	 */
	public static <T> String toJson(T t) throws JsonGenerationException, JsonMappingException, IOException{
		if(t == null) {
			return null;
		}
		StringWriter writer = new StringWriter();
		ObjectWriter objectWriter = mapper.writerFor(t.getClass());
		objectWriter.writeValue(writer, t);
		return writer.toString();
	}
	
	/**
	 * 将对象转成json格式的字符串(带格式)
	 * @param t 需要转换的对象
	 * @return 转换后的json字符串
	 * @throws IOException 
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * @throws Exception
	 */
	public static <T> String toPrettyJson(T t) throws JsonGenerationException, JsonMappingException, IOException{
		if(t == null) {
			return null;
		}
		StringWriter writer = new StringWriter();
		ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
		objectWriter.writeValue(writer, t);
		return writer.toString();
	}
}
