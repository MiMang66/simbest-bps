package com.simbest.bps.app.web.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eos.workflow.api.BPSServiceClientFactory;
import com.eos.workflow.api.IWFWorkItemManager;
import com.eos.workflow.data.WFOptMsg;
import com.simbest.cores.admin.authority.service.ISysUserAdvanceService;
import com.simbest.cores.utils.json.JacksonUtils;
import com.simbest.bps.app.model.WFWorkItemModel;
import com.simbest.bps.app.service.IWFOptMsgModelService;
import com.simbest.bps.app.service.IWFWorkItemModelService;
import com.simbest.bps.listener.jobs.UserTaskSubmitor;
import com.simbest.bps.query.model.ActBusinessStatus;
import com.simbest.bps.query.service.IActBusinessStatusService;

/**
 *工作项启动，完成监听接口
 */
@Controller
@RequestMapping(value = {"/action/anonymous/bps/workItemlistener"})
public class WFWorkItemListener {

	private static transient final Log log = LogFactory.getLog(WFWorkItemListener.class);
	
	@Autowired
	private IWFWorkItemModelService wFWorkItemModelService;
	@Autowired
	private IWFOptMsgModelService wFOptMsgModelService;
    @Autowired
    private IActBusinessStatusService statusService;
    @Autowired
    private ISysUserAdvanceService sysUserAdvanceService;
	@Autowired
	private IWFWorkItemManager WFWorkItemManager;
	
    @Autowired
    private UserTaskSubmitor userTaskSubmitor;
	/**
	 * 工作项创建完成
	 */
	@RequestMapping(value = "/created", method = RequestMethod.POST)
	@ResponseBody
	public String created(String workItemID,String workItemName,String workItemDesc,String currentState,
			String participant,String priority,String isTimeOut,String createTime,String startTime,String endTime,String finalTime,String remindTime,
			String actionURL,String processInstID,String processInstName,String activityInstID,String activityInstName,String processDefID,
			String processDefName,String processChName,String activityDefID,String assistant,
			String bizState,String allowAgent,String urlType,String catalogUUID,String catalogName,
			String title,String receiptId,String code,String currentUserCode){
		Map<String, Object> o= new HashMap<String, Object>();
		if(!wFWorkItemModelService.userLogin(currentUserCode)){
			o.put("mes", "user login error");
			o.put("ret", 0);
			o.put("data", null);
			return JacksonUtils.writeValueAsString(o);
		}
		int ret = 0;
		try{
			ActBusinessStatus businessStatus = statusService.getByProcessInst(Long.parseLong(processInstID));
			businessStatus.setWorkItemID(Long.parseLong(workItemID));
			businessStatus.setActivityDefID(activityDefID);
			userTaskSubmitor.createUserTaskCallback(businessStatus, participant);
			ret = wFWorkItemModelService.created(workItemID, workItemName, workItemDesc, currentState, participant, priority, isTimeOut, createTime, startTime, endTime, finalTime, remindTime, actionURL, processInstID, processInstName, activityInstID, activityInstName, processDefID, processDefName, processChName, activityDefID, assistant, bizState, allowAgent, urlType, catalogUUID, catalogName,title, receiptId, code, currentUserCode); 
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
	 * 工作项完成后
	 */
	@RequestMapping(value = "/completed", method = RequestMethod.POST)
	@ResponseBody
	public String completed(String workItemID,String workItemName,String workItemDesc,String currentState,
			String participant,String priority,String isTimeOut,String createTime,String startTime,String endTime,String finalTime,String remindTime,
			String actionURL,String processInstID,String processInstName,String activityInstID,String activityInstName,String processDefID,
			String processDefName,String processChName,String activityDefID,String assistant,
			String bizState,String allowAgent,String urlType,String catalogUUID,String catalogName,
			String currentUserCode){
		Map<String, Object> o= new HashMap<String, Object>();
		if(!wFWorkItemModelService.userLogin(currentUserCode)){
			o.put("mes", "user login error");
			o.put("ret", 0);
			o.put("data", null);
			return JacksonUtils.writeValueAsString(o);
		}
		int ret = 0;
		try{
			ActBusinessStatus businessStatus = statusService.getByProcessInst(Long.parseLong(processInstID));
			businessStatus.setWorkItemID(Long.parseLong(workItemID));
			businessStatus.setActivityDefID(activityDefID);
			userTaskSubmitor.removeUserTaskCallback(businessStatus, assistant);
			
			ret = wFWorkItemModelService.updateByWorkItemID(workItemID, workItemName, workItemDesc, currentState, participant, priority, isTimeOut, createTime, startTime, endTime, finalTime, remindTime, actionURL, processInstID, processInstName, activityInstID, activityInstName, processDefID, processDefName, processChName, activityDefID, assistant, bizState, allowAgent, urlType, catalogUUID, catalogName, currentUserCode); 
			ret = statusService.updateListener(workItemID, workItemName, workItemDesc, currentState, participant, priority, isTimeOut, createTime, startTime, endTime, finalTime, remindTime, actionURL, processInstID, processInstName, activityInstID, activityInstName, processDefID, processDefName, processChName, activityDefID, assistant, bizState, allowAgent, urlType, catalogUUID, catalogName, currentUserCode);
			List<WFOptMsg> optMsgList= WFWorkItemManager.getApprovalMsgByTaskID(Long.parseLong(workItemID), null);
			WFWorkItemModel wFWorkItemModel = wFWorkItemModelService.getByWorkItemID(Long.parseLong(workItemID));
			wFOptMsgModelService.create(processDefID,processInstID,activityInstID,workItemID,optMsgList,wFWorkItemModel);
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
