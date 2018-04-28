package com.chainway.dispatcherappweb.aop;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.chainway.dispatcherappweb.annotation.ValidateFiled;
import com.chainway.dispatcherappweb.annotation.ValidateGroup;
import com.chainway.dispatcherappweb.common.ReturnCodeConstant;
import com.chainway.dispatcherappweb.exception.BizException;


@Component  
@Aspect 
public class ValidateParamAop extends BaseAop{

	protected final Logger log=Logger.getLogger(this.getClass());
	
	public static Map<String,Integer>DATABASE=new HashMap<String,Integer>();
	public static Set<Class<?>>DTO_CLAZZ_SET=new LinkedHashSet<Class<?>>();
	/*
	static{
		DATABASE.put("t_user.userAccount_maxLen", 10);
		DATABASE.put("t_user.password_maxLen", 10);
	}
	*/
	@Override
	public int getOrder() {
		//如果有多个层是，order越小，越优先执行
		return 1;
	}
	
	@Around("@annotation(com.chainway.dispatcherappweb.annotation.ValidateGroup)")
	public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable{
		System.err.println(DTO_CLAZZ_SET);
		ValidateGroup an=null;  
	    Object[] args=null;
	    Method method=null;
	    Object target=null;
	    String methodName=null;
		 
	    methodName=joinPoint.getSignature().getName();
	    target=joinPoint.getTarget();
	    method=getMethodByClassAndName(target.getClass(), methodName);//得到拦截的方法
	    args=joinPoint.getArgs();//方法的参数 
	    an=(ValidateGroup)getAnnotationByMethod(method ,ValidateGroup.class);
	    //校验参数
	    validateFiled(an.fileds() , args);
		return joinPoint.proceed();
	}
	
	
	private Object getFieldByObjectAndFileName(Object targetObj , String fileName){
		//如果参数是request
		if(targetObj instanceof HttpServletRequest){
			//则需要先获取该request中的参数
			HttpServletRequest request=(HttpServletRequest) targetObj;
			//如果是数组
			if(fileName.indexOf("[")>-1&&fileName.indexOf("]")>-1){
				//fileName=fileName.replace("[", "").replace("]", "");
				String[]paramValue=request.getParameterValues(fileName);
				return paramValue;
			}else{
				String paramValue=request.getParameter(fileName);
		    	return paramValue;
			}
	    }else{
	    	String s1=fileName.substring(0, 1);//第一个字母
			String s2=fileName.substring(1, fileName.length());
			String getMethodName="get"+s1.toUpperCase()+s2;
			
			try {
				Method m=(Method) targetObj.getClass().getMethod(getMethodName);
				Object o=m.invoke(targetObj);
				return o;
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
			return "";
	    	
	    }
	}
	   
	private void validateFiled(ValidateFiled[] valiedatefiles , Object[] args) throws Exception{
		for(ValidateFiled validateFiled:valiedatefiles){
			Object arg=null;//参数值
			//页面要校验的字段名
			String fileName=validateFiled.filedName();
			//数据库中的列名
	        String dataFileName=validateFiled.dataAlias();
			
			if("".equals(validateFiled.filedName())){
				//没有fileName，则默认去对应的参数，做值
				arg=args[validateFiled.index()];
			}else{
				//从第几个参数中，解析出参数值
				arg=getFieldByObjectAndFileName(args[validateFiled.index()] ,fileName);
			}
			
			if(arg instanceof String[]){
				String[] values=(String[]) arg;
				if(validateFiled.notNull()){
					if(values==null||values.length==0){
						throw new BizException(ReturnCodeConstant.ERROR_PARAM_IS_REQUIRED,fileName);
					}
					//在不空的情况下做校验
					for(String value:values){
						check(value, fileName,dataFileName,validateFiled);
					}
				}
			}else{
				check(arg, fileName,dataFileName,validateFiled);
			}
		}
		
	}
	
	private void check(Object value,String fileName,String dataFileName,ValidateFiled validateFiled)throws Exception{
		//必填判断
		if(validateFiled.notNull()){
			if(value==null||(value instanceof String && "".equals(value))){
				//直接抛异常
				throw new BizException(ReturnCodeConstant.ERROR_PARAM_IS_REQUIRED,fileName);
			}
		}else if(value==null||"".equals(value)){
			//如果允许为空，且为空，则继续验证下一个参数
			return;
		}
		
		//最大长度判断
		if(validateFiled.checkMaxLen()){
			//是否检查最大长度
        	String cv=value.toString();
        	int argLen=cv.length();
        	//优先取配置的值
        	Integer maxLen=null;
        	maxLen=validateFiled.maxLen();
        	if(maxLen==-1){
        		//如果没有配置的值，则取数据库实际长度
        		String fName=fileName.toLowerCase();//全部转成小写
        		String key=StringUtils.isNotEmpty(dataFileName)?dataFileName+"."+fName+"_maxLen":fName+"_maxLen";
        		maxLen=DATABASE.get(key);
        	}
        	if(maxLen==null){
        		//如果数据库也没有，则取最大值
        		maxLen=Integer.MAX_VALUE;
        	}
        	if(argLen>maxLen){
        		throw new BizException(ReturnCodeConstant.ERROR_PARAM_MAXLEN_LIMIT,fileName);
        	}
		}
		
		//最小长度判断
		if(validateFiled.checkMinLen()){
			String cv=value.toString();
        	int argLen=cv.length();
        	
        	Integer minLen=null;
        	minLen=validateFiled.minLen();//优先取配置的值
        	if(minLen==-1){
        		//如果没有配置，则取数据中的值
        		String fName=fileName.toLowerCase();//全部转成小写
        		String key=StringUtils.isNotEmpty(dataFileName)?dataFileName+"."+fName+"_minLen":fName+"_minLen";
        		minLen=DATABASE.get(key);
        		
        	}
        	if(minLen==null){
        		minLen=0;
        	}
        	if(argLen<minLen){
        		throw new BizException(ReturnCodeConstant.ERROR_PARAM_MINLEN_LIMIT,fileName);
        	}
		}
		
		//校验正则
		if(StringUtils.isNotEmpty(validateFiled.regStr())){
			String cv=value.toString();
			String regType=validateFiled.regStr();
			String regex="";//正则表达式
			if("email".equals(regType)){
				regex="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
			}else if("phone".equals(regType)){
				//regex="^(13[0-9]|15[0-9]|18[0-9]|17[0-9]|14[0-9])\\d{8}$";
				regex="^(\\d{5,20})$";
			}else if("alphanumeric".equals(regType)){
				regex="^[A-Za-z0-9]+$";
			}else if("datetime".equals(regType)){
				regex="^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$";
			}else{
				//自定义表达式
				regex=regType;
			}
			
			Pattern pattern=Pattern.compile(regex);
			Matcher matcher = pattern.matcher(cv);
			boolean ok=matcher.matches();
			if(!ok){
				throw new BizException(ReturnCodeConstant.ERROR_PARAM_FORMAT_ERROR,fileName);
			}
		}
		//正整数类型
		if("int".equals(validateFiled.dataType())){
			String cv=value.toString();
			int temp=0;
			//验证格式
			try {
				temp = Integer.parseInt(cv);
			} catch (Exception e) {
				throw new BizException(ReturnCodeConstant.ERROR_PARAM_FORMAT_ERROR,fileName);
			}
			//验证最小值
			if(validateFiled.minVal()!=-1D){
				if(temp<validateFiled.minVal()){
					throw new BizException(ReturnCodeConstant.ERROR_PARAM_MINVAL_LIMIT,fileName);
				}
			}
			//验证最大值
			if(validateFiled.maxVal()!=-1D){
				if(temp>validateFiled.maxVal()){
					throw new BizException(ReturnCodeConstant.ERROR_PARAM_MAXVAL_LIMIT,fileName);
				}
			}
		}
		//正浮点数类型
		if("double".equals(validateFiled.dataType())){
			String cv=value.toString();
			double temp=0;
			//验证格式
			try {
				temp = Double.parseDouble(cv);
			} catch (Exception e) {
				throw new BizException(ReturnCodeConstant.ERROR_PARAM_FORMAT_ERROR,fileName);
			}
			//验证最小值
			if(validateFiled.minVal()!=-1D){
				if(temp<validateFiled.minVal()){
					throw new BizException(ReturnCodeConstant.ERROR_PARAM_MINVAL_LIMIT,fileName);
				}
			}
			//验证最大值
			if(validateFiled.maxVal()!=-1D){
				if(temp>validateFiled.maxVal()){
					throw new BizException(ReturnCodeConstant.ERROR_PARAM_MAXVAL_LIMIT,fileName);
				}
			}
		}
	}
	
	public static void main(String[]args){
		String cv="中文 asaa";
		
		//String regex="([0-9]+:([0-9])+(\\.[0-9]+)?(,)?)+";//多个  "1:1.0,2:5.1,3:9.6";
		//String regex="([0-9]+:([0-9])+(\\.[0-9]+)?)+";//单个1:1
		String regex="^[a-zA-Z\u4e00-\u9fa5]+$";//单个1:1
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher = pattern.matcher(cv);
		boolean ok=matcher.matches();
		System.out.println(ok);
		
		String fileName="Nameee1";
		String s1=fileName.substring(0, 1);//第一个字母
		String s2=fileName.substring(1, fileName.length());
		String getMethodName="get"+s1.toUpperCase()+s2;
		System.out.println(getMethodName);
		String time = "2017-04-04 17:16:50";
		regex = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$";
		pattern=Pattern.compile(regex);
		matcher = pattern.matcher(time);
		ok=matcher.matches();
		System.out.println(ok);
		
	}
}
