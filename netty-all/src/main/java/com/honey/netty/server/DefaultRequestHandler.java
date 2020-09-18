package com.honey.netty.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

@Sharable
public class DefaultRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>
		implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public static final String REQUEST_ID = "rid";

	public static final String UID = "uid";

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
		System.out.println(request);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println(msg);
		ctx.write(msg); // (1)
		ctx.flush(); // (2)
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}