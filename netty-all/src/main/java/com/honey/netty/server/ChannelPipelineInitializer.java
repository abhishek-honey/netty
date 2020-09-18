package com.honey.netty.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.EventExecutorGroup;

@Component
public class ChannelPipelineInitializer extends ChannelInitializer<Channel> {

	@Autowired
	private EventExecutorGroup executorGroup;

	@Autowired
	private DefaultRequestHandler defaultRequestHandler;

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("httpcodec", new HttpServerCodec());
		pipeline.addLast("inflater", new HttpContentDecompressor());
		pipeline.addLast("httpAggregator", new HttpObjectAggregator(2097152));
		pipeline.addLast("deflater", new HttpContentCompressor());

		pipeline.addLast(executorGroup, "defaultHandler", defaultRequestHandler);
	}
}
