package com.honey.netty.server;

import java.util.Date;
import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.SystemPropertyUtil;

public class NettyHttpServer {

	private ApplicationContext appContext;
	private ChannelGroup channelGroup;
	private EventLoopGroup workerGroup;
	private EventLoopGroup bossGroup;

	private void initSpringContext() {
		String[] locations = { "classpath*:**/*beans.xml" };
		appContext = new ClassPathXmlApplicationContext(locations);
		if (appContext instanceof AbstractApplicationContext) {
			AbstractApplicationContext abstractAppContext = (AbstractApplicationContext) appContext;
			abstractAppContext.registerShutdownHook();
		}
	}

	private Class<? extends ServerChannel> configureServer(int bossThreads, int workerThreads) {
		String name = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
		Class<? extends ServerChannel> clazz = null;
		if (name.startsWith("linux")) {
			bossGroup = new EpollEventLoopGroup(bossThreads);
			workerGroup = new EpollEventLoopGroup(workerThreads);
			clazz = EpollServerSocketChannel.class;
		} else {
			bossGroup = new NioEventLoopGroup(bossThreads);
			workerGroup = new NioEventLoopGroup(workerThreads);
			clazz = NioServerSocketChannel.class;
		}
		return clazz;
	}

	private void run() throws Throwable {
		try {
			initSpringContext();

			int bossThreads = 3;
			int workerThreads = 3;
			int port = 8080;
			System.out.println(
					"Port : " + port + ", boss threads : " + bossThreads + " , worker threads : " + workerThreads);

			Class<? extends ServerChannel> clazz = configureServer(bossThreads, workerThreads);

			ServerBootstrap bootstrap = new ServerBootstrap();

			ChannelFuture future = bootstrap.group(bossGroup, workerGroup)
					.channel(clazz)
					.childHandler(appContext.getBean(ChannelPipelineInitializer.class))
					.localAddress(port)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.bind()
					.syncUninterruptibly();

			String infoString = "Server started on port " + port + " @ " + new Date(System.currentTimeMillis());
			System.out.println(infoString);

			channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
			channelGroup.add(future.channel());

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					shutdown();
				}
			});
			future.channel().closeFuture().sync();
		} catch (Throwable th) {
			String errorMessage = "Server could not be started, ERROR: " + th.getMessage();
			System.err.println(errorMessage);
			throw th;
		} finally {
			this.shutdown();
			System.exit(-1);
		}
	}

	private void shutdown() {
		if (null != workerGroup) {
			Future<?> shutdownFuture = workerGroup.shutdownGracefully();
			shutdownFuture.syncUninterruptibly();
		}
		if (null != channelGroup) {
			ChannelGroupFuture future = channelGroup.close();
			future.syncUninterruptibly();
		}
	}

	public static void main(String[] args) throws Throwable {
		NettyHttpServer iwServer = new NettyHttpServer();
		iwServer.run();
	}
}
