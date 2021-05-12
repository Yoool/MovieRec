package com.work.movierec.domain;

import java.util.Date;

public class SeckillTicket {
	private Long id;
	private Long ticketId;
	private Double seckillPrice;
	private Integer ticketStock;
	private Date startDate;
	private Date endDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}

	public Double getSeckillPrice() {
		return seckillPrice;
	}

	public void setSeckillPrice(Double seckillPrice) {
		this.seckillPrice = seckillPrice;
	}

	public Integer getTicketStock() {
		return ticketStock;
	}

	public void setTicketStock(Integer ticketStock) {
		this.ticketStock = ticketStock;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
