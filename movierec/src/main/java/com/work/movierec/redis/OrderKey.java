package com.work.movierec.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
	}
	public static OrderKey getSeckillOrderByUserIdTicketId = new OrderKey("seckillOrderByUserIdTicketId");
}
