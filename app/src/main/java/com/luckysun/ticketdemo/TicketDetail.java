package com.luckysun.ticketdemo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TicketDetail implements Serializable {

	/**
	 * 回复详细id
	 */
	private Long id;
	
	/**
	 * 1:客户发送；2:客服发送
	 */
	private Integer senderType;
	
	/**
	 * 是否已读
	 */
	private boolean isRead;
	
	/**
	 * 发送人
	 */
	private String sender;
	
	/**
	 * 创建时间
	 */
	private Long createTime;
	
	/**
	 * 问题id
	 */
	private Long fkTicketId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSenderType() {
		return senderType;
	}

	public void setSenderType(Integer senderType) {
		this.senderType = senderType;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getFkTicketId() {
		return fkTicketId;
	}

	public void setFkTicketId(Long fkTicketId) {
		this.fkTicketId = fkTicketId;
	}
}
