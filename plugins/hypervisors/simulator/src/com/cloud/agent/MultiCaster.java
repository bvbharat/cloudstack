// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MultiCaster implements Runnable {
    private static final Logger s_logger = Logger.getLogger(MultiCaster.class);

	public final int MAX_PACKET_SIZE = 8096;

	private List<MultiCasterListener> listeners;
	private DatagramSocket socket;
	private byte[] recvBuffer;

	private Thread driver;
	private volatile boolean stopRequested = false;

	public MultiCaster() {
		listeners = new ArrayList<MultiCasterListener>();
		recvBuffer = new byte[MAX_PACKET_SIZE];
	}

	public void addListener(MultiCasterListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(MultiCasterListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}

	public void cast(byte[] buf, int off, int len,
		InetAddress toAddress, int nToPort) throws IOException {

		if(socket == null)
			throw new IOException("multi caster is not started");

		if(len >= MAX_PACKET_SIZE)
			throw new IOException("packet size exceeds limit of " + MAX_PACKET_SIZE);

		DatagramPacket packet = new DatagramPacket(buf, off,
			len, toAddress, nToPort);

		socket.send(packet);
	}

	public void start(String strOutboundAddress,
		String strClusterAddress, int nPort) throws SocketException {
		assert(socket == null);

		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(strClusterAddress);
		} catch(IOException e) {
			s_logger.error("Unexpected exception " , e);
		}

		if(addr != null && addr.isMulticastAddress()) {
			try {
				socket = new MulticastSocket(nPort);
				socket.setReuseAddress(true);

				if(s_logger.isInfoEnabled())
					s_logger.info("Join multicast group : " + addr);

				((MulticastSocket)socket).joinGroup(addr);
				((MulticastSocket)socket).setTimeToLive(1);

				if(strOutboundAddress != null) {
					if(s_logger.isInfoEnabled())
						s_logger.info("set outgoing interface to : " + strOutboundAddress);

					InetAddress ia = InetAddress.getByName(strOutboundAddress);
					NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
					((MulticastSocket)socket).setNetworkInterface(ni);
				}
			} catch(IOException e) {
				s_logger.error("Unexpected exception " , e);
			}
		} else {
			socket = new DatagramSocket(nPort);
			socket.setReuseAddress(true);
		}

		driver = new Thread(this, "Multi-caster");
		driver.setDaemon(true);
		driver.start();
	}

	public void stop() {
		if(socket != null) {
			stopRequested = true;

			socket.close();
			if(driver != null) {
				try {
					driver.join();
				} catch(InterruptedException e) {
				}
				driver = null;
			}
		}

		socket = null;
		stopRequested = false;
	}

	public void run() {
		while(!stopRequested) {
			try {
				DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
				socket.receive(packet);

				for(Object listener : listeners.toArray()) {
					((MultiCasterListener)listener).onMultiCasting(packet.getData(),
						packet.getOffset(), packet.getLength(), packet.getAddress());
				}
			} catch(IOException e) {
			} catch(Throwable e) {
				s_logger.error("Unhandled exception : ", e);
			}
		}
	}
}
