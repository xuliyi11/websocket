package com.example.websocket.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebsocketNettyServer {

    private final int port;

    public WebsocketNettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup group = new NioEventLoopGroup(100);

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new WsbsocketChannelInitializer());
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(WebsocketNettyServer.class + " 启动正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }

    static class WsbsocketChannelInitializer extends ChannelInitializer<SocketChannel> {


        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {

            System.out.println("收到新连接");
            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
            socketChannel.pipeline().addLast(new HttpServerCodec());
            //以块的方式来写的处理器
            socketChannel.pipeline().addLast(new ChunkedWriteHandler());
            socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));
            socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536 * 10));
            socketChannel.pipeline().addLast(new ProtocolHandle());
        }

    }
}
