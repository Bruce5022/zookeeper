package com.inca.zookeeper.step02;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

/**
 * 整理代码,主要看授权控制代码
 * 
 * @author Bruce
 *
 */
public class ZookeeperTest03 {
	private static CountDownLatch sampleLatch = new CountDownLatch(1);
	private static String connectString = "192.168.159.10,192.168.159.30,192.168.159.50";
	private static int sessionTimeout = 2000;
	private static Watcher watcher = new Watcher() {
		public void process(WatchedEvent event) {
			if (event.getState() == KeeperState.SyncConnected) {
				System.out.println("事件:" + event);
				System.out.println("类型:" + event.getType());
				System.out.println("路径:" + event.getPath());
				System.out.println("状态:" + event.getState());
				sampleLatch.countDown();/* ZK连接成功时，计数器由1减为0 */
			}
		}
	};

	private static void waitForConnection(ZooKeeper zk) {
		if (zk.getState() == States.CONNECTING) {
			try {
				sampleLatch.await();
			} catch (InterruptedException err) {
				System.out.println("Latch exception");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String userInfo = "szw:inca";
		ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, watcher);
		waitForConnection(zk);
		zk.addAuthInfo("digest", userInfo.getBytes());// 进行认证授权
		List<String> children = zk.getChildren("/", false);
		for (String child : children) {
			System.out.println("节点:" + child);
		}
		zk.close();
	}

}
