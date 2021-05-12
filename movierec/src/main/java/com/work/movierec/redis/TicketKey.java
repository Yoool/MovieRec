package com.work.movierec.redis;

public class TicketKey extends BasePrefix{

	private TicketKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static TicketKey getTicketList = new TicketKey(60, "ticketList");
	public static TicketKey getTicketDetail = new TicketKey(60, "ticketDetail");
	public static TicketKey getSeckillTicketStock= new TicketKey(0, "seckillTicketStock");
}
