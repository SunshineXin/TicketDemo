package com.luckysun.ticketdemo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Ticket implements Serializable{
	
	/**
	 * id
	 */
	private Integer id;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 回复详细ID
	 */
	private Integer fkTicketDetailId;
	
	/**
	 * 详情
	 */
	private TicketDetail ticketDetail;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getFkTicketDetailId() {
		return fkTicketDetailId;
	}

	public void setFkTicketDetailId(Integer fkTicketDetailId) {
		this.fkTicketDetailId = fkTicketDetailId;
	}

	public TicketDetail getTicketDetail() {
		return ticketDetail;
	}

	public void setTicketDetail(TicketDetail ticketDetail) {
		this.ticketDetail = ticketDetail;
	}

}
