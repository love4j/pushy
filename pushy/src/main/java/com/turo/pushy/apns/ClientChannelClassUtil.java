/*
 * Copyright (c) 2013-2017 Turo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.turo.pushy.apns;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.util.Objects;

class ClientChannelClassUtil {

    private static final String EPOLL_EVENT_LOOP_GROUP_CLASS = "io.netty.channel.epoll.EpollEventLoopGroup";
    private static final String EPOLL_SOCKET_CHANNEL_CLASS = "io.netty.channel.epoll.EpollSocketChannel";
    private static final String EPOLL_DATAGRAM_CHANNEL_CLASS = "io.netty.channel.epoll.EpollDatagramChannel";

    private static final String KQUEUE_EVENT_LOOP_GROUP_CLASS = "io.netty.channel.kqueue.KQueueEventLoopGroup";
    private static final String KQUEUE_SOCKET_CHANNEL_CLASS = "io.netty.channel.kqueue.KQueueSocketChannel";
    private static final String KQUEUE_DATAGRAM_CHANNEL_CLASS = "io.netty.channel.kqueue.KQueueDatagramChannel";

    /**
     * Returns a socket channel class suitable for specified event loop group.
     *
     * @param eventLoopGroup the event loop group for which to identify an appropriate socket channel class; must not
     * be {@code null}
     *
     * @return a socket channel class suitable for use with the given event loop group
     *
     * @throws IllegalArgumentException in case of null or unrecognized event loop group
     */
    static Class<? extends SocketChannel> getSocketChannelClass(final EventLoopGroup eventLoopGroup) {
        Objects.requireNonNull(eventLoopGroup);

        final Class<? extends SocketChannel> socketChannelClass;

        if (eventLoopGroup instanceof NioEventLoopGroup) {
            socketChannelClass = NioSocketChannel.class;
        } else if (eventLoopGroup instanceof OioEventLoopGroup) {
            socketChannelClass = OioSocketChannel.class;
        } else if (EPOLL_EVENT_LOOP_GROUP_CLASS.equals(eventLoopGroup.getClass().getName())) {
            socketChannelClass = loadSocketChannelClass(EPOLL_SOCKET_CHANNEL_CLASS);
        } else if (KQUEUE_EVENT_LOOP_GROUP_CLASS.equals(eventLoopGroup.getClass().getName())) {
            socketChannelClass = loadSocketChannelClass(KQUEUE_SOCKET_CHANNEL_CLASS);
        } else {
            throw new IllegalArgumentException("Could not find socket class for event loop group class: " + eventLoopGroup.getClass().getName());
        }

        return socketChannelClass;
    }

    /**
     * Returns a datagram channel class suitable for specified event loop group.
     *
     * @param eventLoopGroup the event loop group for which to identify an appropriate datagram channel class; must not
     * be {@code null}
     *
     * @return a datagram channel class suitable for use with the given event loop group
     *
     * @throws IllegalArgumentException in case of null or unrecognized event loop group
     */
    static Class<? extends DatagramChannel> getDatagramChannelClass(final EventLoopGroup eventLoopGroup) {
        Objects.requireNonNull(eventLoopGroup);

        final Class<? extends DatagramChannel> datagramChannelClass;

        if (eventLoopGroup instanceof NioEventLoopGroup) {
            datagramChannelClass = NioDatagramChannel.class;
        } else if (eventLoopGroup instanceof OioEventLoopGroup) {
            datagramChannelClass = OioDatagramChannel.class;
        } else if (EPOLL_EVENT_LOOP_GROUP_CLASS.equals(eventLoopGroup.getClass().getName())) {
            datagramChannelClass = loadDatagramChannelClass(EPOLL_DATAGRAM_CHANNEL_CLASS);
        } else if (KQUEUE_EVENT_LOOP_GROUP_CLASS.equals(eventLoopGroup.getClass().getName())) {
            datagramChannelClass = loadDatagramChannelClass(KQUEUE_DATAGRAM_CHANNEL_CLASS);
        } else {
            throw new IllegalArgumentException("Could not find datagram class for event loop group class: " + eventLoopGroup.getClass().getName());
        }

        return datagramChannelClass;
    }

    private static Class<? extends SocketChannel> loadSocketChannelClass(final String className) {
        try {
            return Class.forName(className).asSubclass(SocketChannel.class);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Class<? extends DatagramChannel> loadDatagramChannelClass(final String className) {
        try {
            return Class.forName(className).asSubclass(DatagramChannel.class);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
