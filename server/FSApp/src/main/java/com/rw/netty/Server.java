package com.rw.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.common.refOpt.RefOptClassGener;
import com.gm.GmHotFixManager;
import com.log.GameLog;
import com.playerdata.GambleMgr;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.playerdata.activityCommon.modifiedActivity.ActivityModifyMgr;
import com.rw.manager.DataCacheInitialization;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rw.service.platformgs.PlatformGSService;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.RequestProtos.Request;

public class Server {
	public static final boolean isDebug = true;

	// public static final boolean isDebug=false;
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		GameWorldFactory.init(64, 32, 16);
		// 预加载需要反射优化的类，如果出错则启动失败
		RefOptClassGener.getInstance().setOpen(true).preLoadClassList();

		DataCacheInitialization.init();
		PropertyConfigurator.configure(Server.class.getClassLoader().getResource("log4j.properties"));
		System.setProperty("io.netty.recycler.maxCapacity.default", "512");
		GameManager.initServerProperties();
		System.out.println("start init...");
		ServerSwitch.initProperty();

		// 初始化属性的映射关系
		AttributeBM.initAttributeMap();

		// GameManager.initBeforeLoading();
		new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext.xml" });

		EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
		int ioThreads = Runtime.getRuntime().availableProcessors() * 2;
		// new PrintServerState().startPrintState();
		EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(ioThreads);
		try {
			// 检查所有配置文件，如果配置有问题，请打印日志报告错误，并抛异常中断启动过程
			GameManager.CheckAllConfig();
			// 时效任务初始化
			com.rwbase.common.timer.core.FSGameTimerMgr.getInstance().init();
			// 初始化所有后台服务
			GameManager.initServiceAndCrontab();
			// 初始化每日热点数据
			GambleMgr.resetWhenStart();
			// GambleTest.Test();

			com.rwbase.common.timer.core.FSGameTimerMgr.getInstance().serverStartComplete(); // 初始化完畢
			GmHotFixManager.serverStartComplete(); // 自动执行hot fix
			
			//每次启服的时候，检查是否有GM修改过的活动配置（启服最后检查）
			ActivityModifyMgr.getInstance().checkModifiedActivity();
			//每次启服的时候，初始化活动配置表的时间(虽然配置表的init方法初始化过，但是那个时候没有开服时间，相对时间无法解析，所以这里需要重新初始化一次)
			ActivityMgrHelper.getInstance().initActivityTime();
			//从登录服同步活动的时间数据
			PlatformGSService.notifyServerStatusToPlatform(false);
			com.rwbase.common.timer.core.FSGameTimerMgr.getInstance().serverStartComplete(); // 初始化完畢
			GmHotFixManager.serverStartComplete(); // 自动执行hot fix

			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
			serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast("idle", new IdleStateHandler(90, 0, 160));
					ch.pipeline().addLast("frameDecoder", new FrameDecoder());
					// 构造函数传递要解码成的类型
					ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(Request.getDefaultInstance()));
					ch.pipeline().addLast("serverHandler", new ServerHandler());
					ch.pipeline().addLast("frameEncoder", new ProtobufFrameEncoder());
					ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
				};
			});

			serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			int port = Integer.valueOf(ServerConfig.getInstance().getServeZoneInfo().getPort());
			ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Throwable e) {
			e.printStackTrace();
			GameLog.error("Server", "Server[]main", "", e);
			System.exit(0);
		} finally {
			bossEventLoopGroup.shutdownGracefully();
			workerEventLoopGroup.shutdownGracefully();
		}
	}
}