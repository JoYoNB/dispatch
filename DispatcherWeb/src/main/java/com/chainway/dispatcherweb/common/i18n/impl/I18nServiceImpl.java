package com.chainway.dispatcherweb.common.i18n.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatcherweb.common.i18n.I18nService;

@Service("i18nService")
public class I18nServiceImpl implements I18nService {

	private static Logger log=Logger.getLogger(I18nServiceImpl.class);
	
	//@Autowired
	private ResourceBundleMessageSource messageSource;
	
	private static List<String>SUPPORT_LANGS=new ArrayList<String>();
	static{
		String _supports=PropertiesUtil.getString("langues");
		if(_supports==null){
			//防止部署人员忘记配置
			_supports="zh,en";
		}
		String[]langs=_supports.split(",");
		SUPPORT_LANGS=Arrays.asList(langs);
	}
	
	
	@Override
	public String getProperty(String key, Object[] params, String lang) {
		if(StringUtils.isEmpty(key)){
			return "";
		}
		Object[]_p=null;
		if(params!=null){
			_p=params.clone();//浅克隆
		}
		
		String _lang="";
		if(StringUtils.isNotEmpty(lang)){
			_lang=lang;
			//不支持一种语言多种版本，如：en_US,en_AR,zh_CN,zh_TW
			String[]items=_lang.split("_");
			if(items.length>0){
				_lang=items[0];
			}
		}
		//如果是不支持的语种，则默认用英语
		if(!SUPPORT_LANGS.contains(_lang)){
			_lang="en";
		}
		log.info("current langue:"+lang);
		Locale locale=new Locale(_lang);
		//如果是中文，则要把参数转成ISO-8859-1
		_p=transEncoding(_p);
		
		String ret=messageSource.getMessage(key, _p, locale);
		//如果是中文，则要把结果转成utf-8
		try {
			ret=new String(ret.getBytes("ISO-8859-1"),"UTF8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			log.error(e);
		}
		return ret;
	}

	private Object[] transEncoding(Object[]params){
		if(params==null){
			return params;
		}
		Object[]_newParams=new Object[params.length];
		for(int i=0;i<params.length;i++){
			Object o=params[i];
			if(o instanceof String){
				String _temp=(String) o;
				try {
					_temp=new String(_temp.getBytes(),"ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				_newParams[i]=_temp;
			}else{
				_newParams[i]=o;
			}
		}
		
		return _newParams;
	}
	
	public static void main(String[]args) throws Exception{
		String[] configs = {"beans.xml"};
		ApplicationContext ctx=new ClassPathXmlApplicationContext(configs);
		
		I18nServiceImpl i18nService=(I18nServiceImpl) ctx.getBean("i18nService");
		ResourceBundleMessageSource messageSource=(ResourceBundleMessageSource) ctx.getBean("messageSource");
		i18nService.messageSource=messageSource;
		String[]langs="zh,en,es,ar".split(",");
		SUPPORT_LANGS=Arrays.asList(langs);
		
		String key="stirng.v.terPackageMonth.fmtLimit";
		Object[]params={"小明 ",123};
		String lang="en";
		String ret=i18nService.getProperty(key, params, lang);
		System.out.println("-----"+ret);
	}
}
