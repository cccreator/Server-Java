package com.hisense.hiose.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.GenericGenerator;

 /**
 * @Description 系统处理日志表
 * 	表 SYS_OPERATELOG 的 Model 对象
 */
@Entity
@Table(name = "SYS_OPERATELOG")
public class SystemLog {
	
	private static final long serialVersionUID = 1L;
	
    /**
    * 日志ID
    **/
	private  String logId;  
    /**
    * 业务类型
    **/
	private  String bussType;  
    /**
    * 业务类型描述
    **/
	private  String bussTypeDesc;  
    /**
    * 用户ID
    **/
	private  double userId;  
    /**
    * 操作类型
    **/
	private  String operateType;  
    /**
    * 操作类型描述
    **/
	private  String operateTypeDesc;  
    /**
    * 请求IP
    **/
	private  String requestIp;  
    /**
    * 请求地址
    **/
	private  String requestUrl;  
    /**
    * 服务器IP，用于分布式
    **/
	private  String serverIp;  
    /**
    * 模块代码
    **/
	private  String moudleCode;  
    /**
    * 模块名称
    **/
	private  String moudleName;  
    /**
    * 错误信息,如果发生错误
    **/
	private  String errMsg;  
    /**
    * 日志时间
    **/
	private  Date logDate;
	/**
	 * 操作用户名
	 **/
	private String userName;

	/**无参数构造函数**/	
	public SystemLog(){};
	
	/**有参数构造函数**/
	public SystemLog (String logId,String bussType,String bussTypeDesc,double userId,String operateType,String operateTypeDesc,String requestIp,String requestUrl,String serverIp,String moudleCode,String moudleName,String errMsg,Date logDate){
	
		this.logId = logId;
		this.bussType = bussType;
		this.bussTypeDesc = bussTypeDesc;
		this.userId = userId;
		this.operateType = operateType;
		this.operateTypeDesc = operateTypeDesc;
		this.requestIp = requestIp;
		this.requestUrl = requestUrl;
		this.serverIp = serverIp;
		this.moudleCode = moudleCode;
		this.moudleName = moudleName;
		this.errMsg = errMsg;
		this.logDate = logDate;
	}
	
	
	//get||set方法
	@Id
	@Column(name="LOG_ID")
	@GeneratedValue(generator = "UUIDGenerator") 
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid") 
	public String getLogId() {
		return this.logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}	
	
	@Column(name="BUSS_TYPE")
	public String getBussType() {
		return this.bussType;
	}
	public void setBussType(String bussType) {
		this.bussType = bussType;
	}
	
	@Column(name="BUSS_TYPE_DESC")
	public String getBussTypeDesc() {
		return this.bussTypeDesc;
	}
	public void setBussTypeDesc(String bussTypeDesc) {
		this.bussTypeDesc = bussTypeDesc;
	}
	
	@Column(name="USER_ID")
	public double getUserId() {
		return this.userId;
	}
	public void setUserId(double userId) {
		this.userId = userId;
	}
	
	@Column(name="OPERATE_TYPE")
	public String getOperateType() {
		return this.operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	
	@Column(name="OPERATE_TYPE_DESC")
	public String getOperateTypeDesc() {
		return this.operateTypeDesc;
	}
	public void setOperateTypeDesc(String operateTypeDesc) {
		this.operateTypeDesc = operateTypeDesc;
	}
	
	@Column(name="REQUEST_IP")
	public String getRequestIp() {
		return this.requestIp;
	}
	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}
	
	@Column(name="REQUEST_URL")
	public String getRequestUrl() {
		return this.requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	@Column(name="SERVER_IP")
	public String getServerIp() {
		return this.serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	@Column(name="MOUDLE_CODE")
	public String getMoudleCode() {
		return this.moudleCode;
	}
	public void setMoudleCode(String moudleCode) {
		this.moudleCode = moudleCode;
	}
	
	@Column(name="MOUDLE_NAME")
	public String getMoudleName() {
		return this.moudleName;
	}
	public void setMoudleName(String moudleName) {
		this.moudleName = moudleName;
	}
	
	@Column(name="ERR_MSG")
	public String getErrMsg() {
		return this.errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	@Column(name="LOG_DATE")
	public Date getLogDate() {
		return this.logDate;
	}
	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	@Column(name="username")
	 public String getUserName() {
		 return userName;
	 }
	 public void setUserName(String userName) {
		 this.userName = userName;
	 }
 }