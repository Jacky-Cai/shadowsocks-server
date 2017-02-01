package shadowsocks;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import shadowsocks.crypto.CryptoFactory;

public class ShadowSocksServer {

    public void start(String serverHost, int serverPort, String cryptoMethod, String password) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(serverHost, serverPort)
                    .option(ChannelOption.SO_TIMEOUT, 5000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new AddressHandler(CryptoFactory.create(cryptoMethod, password)));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind().sync();
            System.out.println("started and listen on " + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }

    }
}
