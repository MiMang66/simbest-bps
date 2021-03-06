package com.simbest.bps.app.web.listener;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simbest.cores.utils.json.JacksonUtils;
import com.simbest.bps.app.service.IWFProcessInstModelService;
import com.simbest.bps.query.service.IActBusinessStatusService;

/**
 *流程启动，结束监听接口
 */
@Controller
@RequestMapping(value = {"/action/anonymous/bps/processlistener"})
public class WFProcessInstListener {
	
	private static transient final Log log = LogFactory.getLog(WFProcessInstListener.class);
	
	@Autowired
	private IWFProcessInstModelService wFProcessInstModelService;
	
    @Autowired
    private IActBusinessStatusService statusService;
	
	/**
	 * 流程启动
	 */
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	@ResponseBody
	public String start(String processInstID,String processInstName,String processInstDesc,String creator,
			String owner,String currentState,String createTime,String startTime,String endTime,String finalTime,String remindTime,
			String parentProcID,String parentActID,String processDefID,String isTimeOut,String timeOutNum,String timeOutNumDesc,
			String updateVersion,String processDefName,String catalogUUID,String catalogName,
			String title,String receiptId,String code,String currentUserCode){
		Map<String, Object> o= new HashMap<String, Object>();
		if(!wFProcessInstModelService.userLogin(currentUserCode)){
			o.put("mes", "user login error");
			o.put("ret", 0);
			o.put("data", null);
			return JacksonUtils.writeValueAsString(o);
		}
		int ret = 0;
		try{
			ret = wFProcessInstModelService.start(processInstID, processInstName, processInstDesc, creator, owner, currentState, createTime, startTime, endTime, finalTime, remindTime, parentProcID, parentActID, processDefID, isTimeOut, timeOutNum, timeOutNumDesc, updateVersion, processDefName, catalogUUID, catalogName, title, receiptId, code, currentUserCode);
			ret = statusService.createListener(processInstID, processInstName, processInstDesc, creator, owner, currentState, createTime, startTime, endTime, finalTime, remindTime, parentProcID, parentActID, processDefID, isTimeOut, timeOutNum, timeOutNumDesc, updateVersion, processDefName, catalogUUID, catalogName, title, receiptId, code, currentUserCode);
			log.debug(ret);
			o.put("mes", ret > 0 ? "操作成功!" : "操作失败!"); // 返回值兼容批量更新
			o.put("ret", ret);
			o.put("data", null);
		}catch(Exception e){
			log.error(ret);
			log.error(e.getStackTrace());
			e.printStackTrace();
		}
		return JacksonUtils.writeValueAsString(o);
	}
	
	/**
	 * 流程完成
	 */
	@RequestMapping(value = "/completed", method = RequestMethod.POST)
	@ResponseBody
	public String completed(String processInstID,String processInstName,String processInstDesc,String creator,
			String owner,String currentState,String createTime,String startTime,String endTime,String finalTime,String remindTime,
			String parentProcID,String parentActID,String processDefID,String isTimeOut,String timeOutNum,String timeOutNumDesc,
			String updateVersion,String processDefName,String catalogUUID,String catalogName,
			String title,String receiptId,String code,String currentUserCode){
		Map<String, Object> o= new HashMap<String, Object>();
		if(!wFProcessInstModelService.userLogin(currentUserCode)){
			o.put("mes", "user login error");
			o.put("ret", 0);
			o.put("data", null);
			return JacksonUtils.writeValueAsString(o);
		}
		int ret = 0;
		try{
			/*流程结束更新结束时间*/
			ret = wFProcessInstModelService.updateListenerByProcess(processInstID, processInstName, processInstDesc, creator, owner, currentState, createTime, startTime, endTime, finalTime, remindTime, parentProcID, parentActID, processDefID, isTimeOut, timeOutNum, timeOutNumDesc, updateVersion, processDefName, catalogUUID, catalogName, title, receiptId, code, currentUserCode);
			/*流程结束更新结束时间*/
			ret = statusService.updateListenerByProcess(processInstID, processInstName, processInstDesc, creator, owner, currentState, createTime, startTime, endTime, finalTime, remindTime, parentProcID, parentActID, processDefID, isTimeOut, timeOutNum, timeOutNumDesc, updateVersion, processDefName, catalogUUID, catalogName, title, receiptId, code, currentUserCode);
			log.debug(ret);
			o.put("mes", ret > 0 ? "操作成功!" : "操作失败!"); // 返回值兼容批量更新
			o.put("ret", ret);
			o.put("data", null);
		}catch(Exception e){
			log.error(ret);
			log.error(e.getStackTrace());
			e.printStackTrace();
		}
		return JacksonUtils.writeValueAsString(o);
	}
	

}
