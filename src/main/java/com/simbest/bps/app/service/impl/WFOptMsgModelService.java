package com.simbest.bps.app.service.impl;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.eos.workflow.data.WFOptMsg;
import com.simbest.cores.utils.DateUtil;
import com.simbest.bps.app.mapper.WFOptMsgModelMapper;
import com.simbest.bps.app.model.WFOptMsgModel;
import com.simbest.bps.app.model.WFWorkItemModel;
import com.simbest.bps.app.service.IWFOptMsgModelService;

@Service(value="wFOptMsgModelService")
public class WFOptMsgModelService extends WFBPSModelService<WFOptMsgModel,Long> implements IWFOptMsgModelService{

	public final static Log log = LogFactory.getLog(WFOptMsgModelService.class);
	
	private WFOptMsgModelMapper mapper;
	
	public WFOptMsgModelService(SqlSession sqlSession, Class<WFOptMsgModel> persistentMapper) {
		super(sqlSession,persistentMapper);
	}
	
	@Autowired
	public WFOptMsgModelService(@Qualifier(value="sqlSessionTemplateSimple") SqlSession sqlSession) {
		super(sqlSession);
		this.mapper = sqlSession.getMapper(WFOptMsgModelMapper.class);
        super.setMapper(mapper);
	}

	@Override
	public void create(String processDefID, String processInstID,
			String activityInstID, String workItemID, List<WFOptMsg> optMsgList, WFWorkItemModel wFWorkItemModel) {
		for(WFOptMsg WFOptMsg : optMsgList){
			WFOptMsgModel wFOptMsgModel = new WFOptMsgModel();
			wFOptMsgModel.setMessageid(WFOptMsg.getMessageID());
			wFOptMsgModel.setProducer(WFOptMsg.getProducer());
			wFOptMsgModel.setReceiver(WFOptMsg.getReceiver());
			wFOptMsgModel.setOperationtype(WFOptMsg.getOperationType());
			wFOptMsgModel.setCorrelationtype(WFOptMsg.getCorrelationType());
			wFOptMsgModel.setCorrelationid(WFOptMsg.getCorrelationID());
			wFOptMsgModel.setContent(WFOptMsg.getContent());
			wFOptMsgModel.setCreatetime((WFOptMsg.getCreateTime()!=null && !WFOptMsg.getCreateTime().equals(""))?DateUtil.parseCustomDate(WFOptMsg.getCreateTime(), "yyyyMMddHHmmss"):null);
			wFOptMsgModel.setProcessdefid(Long.parseLong(processDefID));
			wFOptMsgModel.setProcessinstid(Long.parseLong(processInstID));
			wFOptMsgModel.setActivityinstid(Long.parseLong(activityInstID));
			wFOptMsgModel.setWorkitemid(Long.parseLong(workItemID));
			wFOptMsgModel.setTitle(wFWorkItemModel.getTitle());
			wFOptMsgModel.setReceiptid(wFWorkItemModel.getReceiptid());
			wFOptMsgModel.setCode(wFWorkItemModel.getCode());
//			org.springframework.beans.BeanUtils.copyProperties(WFOptMsg, wFOptMsgModel);
			wrapCreateInfo(wFOptMsgModel);
			wFOptMsgModel.setEnabled(true);
			wFOptMsgModel.setRemoved(false);
			create(wFOptMsgModel);
		}
		
	}

	@Override
	public void updateTitleByInstID(Long processInstID, String title) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("processInstID", processInstID);
		map.put("title", title);
		mapper.updateTitleByInstID(map);
	}

	@Override
	public void deleteByInstID(Long processInstID) {
		mapper.deleteByInstID(processInstID);
		
	}
}
