package com.honey.netty.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

@Sharable
public class DefaultRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>
		implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public static final String REQUEST_ID = "rid";

	public static final String UID = "uid";

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
		System.out.println(request);
		Object responseObj = "honey";
		ctx.write(responseObj);
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println(msg);
		Long result = 5L;
		sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED),
				result.toString());
		ctx.fireChannelRead(result);
	}

	public static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpResponse res, String content) {
		// Generate an error page if response getStatus code is not OK (200).
		ByteBuf buf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
		res.content().writeBytes(buf);

		HttpUtil.setContentLength(res, res.content().readableBytes());

		ctx.channel().writeAndFlush(res);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}