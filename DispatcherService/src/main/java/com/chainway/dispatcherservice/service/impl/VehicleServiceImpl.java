package com.chainway.dispatcherservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.dto.Vehicle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.DeptDao;
import com.chainway.dispatcherservice.biz.dao.VehicleDao;
import com.chainway.dispatcherservice.service.VehicleService;

@Component 
@Service
public class VehicleServiceImpl implements VehicleService {
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private VehicleDao vehicleDao;
	
	@Autowired
	private DeptDao deptDao;
	
	@Override
	@WriteDataSource
	@Transactional
	public String add(Map<String, Object> paramMap) throws Exception {
		//接收参数
		User user = (User) paramMap.get("user");
		Vehicle vehicle = (Vehicle) paramMap.get("vehicle");
		
		//判断车牌号是否重复
		Map<String,Object>para=new HashMap<String,Object>();
		para.put("plateNo", vehicle.getPlateNo());
		List<Map<String, Object>> vehicleList = vehicleDao.getVehicleList(para);
		if(null != vehicleList && vehicleList.size()>0) {
		   throw new ServiceException(ExceptionCode.ERROR_VIHICLE_ADD_PLATENO_REPEAT, "车牌号重复");
		}
		
		//调用接口新增其他平台车辆
		IVCApiUtils apiUtils = IVCApiUtils.getInstance();
		Map<String, Object>param=new HashMap<>();
		param.put("userName", user.getName());
		param.put("carEngine", "未知");
		param.put("carVim", "未知");
		// 查询用户顶级部门 对应的2.0平台部门
		String topDeptId = user.getDeptDNA().split("-")[0];
		Dept deptParam = new Dept();
		deptParam.setId(Integer.parseInt(topDeptId));
		Dept dept = deptDao.getDept(deptParam);
		param.put("deptId", dept.getThirdPartyDept());
		param.put("gmtZone", user.getGmtZone());
		param.put("plateNo", vehicle.getPlateNo());
		log.info("====================>开始添加车辆");
		Integer vehicleIdOther = apiUtils.addVehicle(param);
		//判断新增是否成功
		if(null==vehicleIdOther) {
		    throw new ServiceException(ExceptionCode.ERROR_VIHICLE_ADD_OTHER__FAIL, "其他平台新增车辆失败");
		}
		
		//本平台新增车辆
		vehicle.setLoadRate(0);//车辆运载百分比为0
		vehicle.setStatus(1);//1为车辆有效
		vehicle.setVehicleIdOther(vehicleIdOther);
		vehicle.setDeptId(user.getDeptId());
		vehicle.setCreater(user.getId());
		vehicle.setUpdater(user.getId());
		vehicleDao.add(vehicle);
		Integer vehicleId = vehicle.getVehicleId();
		if(null==vehicleId) {
			throw new ServiceException(ExceptionCode.ERROR_VIHICLE_ADD__FAIL, "新增车辆失败");
		}
		
		//新增车辆与载货类型关系
		Map<String, Object> _param=new HashMap<>();
		_param.put("vehicleId", vehicleId);
		_param.put("carryTypeIds",vehicle.getCarryTypeIds());
		vehicleDao.addVehicleCarryTypeRel(_param);
		return "新增成功";
	}

	@Override
	public Map<String, Object> getVehicle(Map<String, Object> paramMap) {
		return vehicleDao.getVehicle(paramMap);
	}
	
	@Override
	public List<Map<String, Object>> getVehicleList(Map<String, Object> paramMap) {
		return vehicleDao.getVehicleList(paramMap);
	}

	@Override
	@WriteDataSource
	@Transactional
	public void update(Vehicle vehicle) throws Exception {
		vehicleDao.update(vehicle);
		if(vehicle.getCarryTypeIds()!=null&&vehicle.getCarryTypeIds().size()>0) {
			//先删除车辆与载货类型关系
			vehicleDao.deleteVehicleCarryTypeRel(vehicle.getVehicleId());
			//再新增车俩与载货类型关系
			Map<String, Object> param=new HashMap<>();
			param.put("vehicleId", vehicle.getVehicleId());
			param.put("carryTypeIds",vehicle.getCarryTypeIds());
			vehicleDao.addVehicleCarryTypeRel(param);
		}
	}

	@Override
	@WriteDataSource
	@Transactional
	public Boolean delete(Integer vehicleId) {
		//删除车辆与载货类型关系
		vehicleDao.deleteVehicleCarryTypeRel(vehicleId);
		//删除车辆
		int num = vehicleDao.delete(vehicleId);
		return num>0?true:false;
	}

	@Override
	public Integer getVehicleListCount(Map<String, Object> paramMap) {
		return vehicleDao.getVehicleListCount(paramMap);
	}

	@Override
	public List<Map<String, Object>> getCommonVehicles(Map<String, Object> paramMap) {
		return vehicleDao.getCommonVehicles(paramMap);
	}

}
