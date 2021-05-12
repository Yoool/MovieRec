//package com.work.seckill.service;
//
//import com.work.seckill.domain.SeckillOrder;
//import com.work.seckill.redis.RedisService;
//import com.work.seckill.redis.SeckillKey;
//import com.work.seckill.util.MD5Util;
//import com.work.seckill.util.UUIDUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.work.seckill.domain.SeckillUser;
//import com.work.seckill.domain.OrderInfo;
//import com.work.seckill.vo.TicketVo;
//import com.work.seckill.util.CALCUtil;
//
////import javax.script.ScriptEngine;
////import javax.script.ScriptEngineManager;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.util.Random;
//
//@Service
//public class SeckillService {
//
//	@Autowired
//	TicketService ticketService;
//
//	@Autowired
//	OrderService orderService;
//
//	@Autowired
//	RedisService redisService;
//	// 使用事务操作
//	@Transactional
//	public OrderInfo seckill(SeckillUser user, TicketVo ticket) {
//		//减库存 下订单 写入秒杀订单
//		boolean success = ticketService.reduceStock(ticket);
//		if(success) {
//			return orderService.createOrder(user, ticket);
//		}else {
//			setTicketOver(ticket.getId());
//			return null;
//		}
//	}
//
//	public boolean checkPath(SeckillUser user, long ticketId, String path) {
//		if(user == null || path == null) {
//			return false;
//		}
//		String pathInRedis = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ ticketId, String.class);
//		return path.equals(pathInRedis);
//	}
//
//	private void setTicketOver(Long ticketId) {
//		redisService.set(SeckillKey.isTicketOver, ""+ticketId, true);
//	}
//
//	private boolean getTicketOver(long ticketId) {
//		return redisService.exists(SeckillKey.isTicketOver, ""+ticketId);
//	}
//
//	/**
//	 * @param userId
//	 * @param ticketId
//	 * @return 成功返回OrderId，排队中0，失败-1
//	 */
//	public long getSeckillResult(Long userId, long ticketId) {
//		SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdTicketId(userId, ticketId);
//		if(seckillOrder != null) {//秒杀成功
//			return seckillOrder.getOrderId();
//		}else {
//			return getTicketOver(ticketId) ? -1:0;
//		}
//	}
//
//	/**
//	 * 生成随机秒杀链接
//	 * @param user
//	 * @param ticketId
//	 * @return
//	 */
//	public String createSeckillPath(SeckillUser user, long ticketId) {
//		if(user == null || ticketId <=0) {
//			return null;
//		}
//		String str = MD5Util.md5(UUIDUtil.uuid()+"2020");
//		redisService.set(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ ticketId, str);
//		return str;
//	}
//
//	/**
//	 * 生成验证码图片
//	 * @param user
//	 * @param ticketId
//	 * @return
//	 */
//	public BufferedImage createVerifyCode(SeckillUser user, long ticketId) {
//		if(user == null || ticketId <=0) {
//			return null;
//		}
//		int width = 80;
//		int height = 32;
//		//create the image
//		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		Graphics g = image.getGraphics();
//		// set the background color
//		g.setColor(new Color(0xDCDCDC));
//		g.fillRect(0, 0, width, height);
//		// draw the border
//		g.setColor(Color.black);
//		g.drawRect(0, 0, width - 1, height - 1);
//		// create a random instance to generate the codes
//		Random rdm = new Random();
//		// make some confusion
//		for (int i = 0; i < 50; i++) {
//			int x = rdm.nextInt(width);
//			int y = rdm.nextInt(height);
//			g.drawOval(x, y, 0, 0);
//		}
//		// generate a random code
//		String verifyCode = generateVerifyCode(rdm);
//		g.setColor(new Color(0, 100, 0));
//		g.setFont(new Font("Candara", Font.BOLD, 24));
//		g.drawString(verifyCode, 8, 24);
//		g.dispose();
//		//把验证码存到redis中
//		int rnd = CALCUtil.calc(verifyCode);
//		redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+","+ticketId, rnd);
//		//输出图片
//		return image;
//	}
//
//	public boolean checkVerifyCode(SeckillUser user, long ticketId, int verifyCode) {
//		if(user == null || ticketId <=0) {
//			return false;
//		}
//		Integer codeInRedis = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId()+","+ticketId, Integer.class);
//		System.out.println(codeInRedis+" =? "+verifyCode);//MATKSOUT
//		if(codeInRedis == null || codeInRedis != verifyCode ) {
//			return false;
//		}
//		redisService.delete(SeckillKey.getSeckillVerifyCode, user.getId()+","+ticketId);
//		return true;
//	}
//
//	private static char[] ops = new char[] {'+', '-', '*'};
//	private String generateVerifyCode(Random rdm) {
//		int num1 = rdm.nextInt(10);
//		int num2 = rdm.nextInt(10);
//		int num3 = rdm.nextInt(10);
//		char op1 = ops[rdm.nextInt(3)];
//		char op2 = ops[rdm.nextInt(3)];
//		String exp = ""+ num1 + op1 + num2 + op2 + num3;
//		return exp;
//	}
//}
