package com.inca.zookeeper.step01;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

/**
 * 这种方式可以是可以,到底为什么这么慢??? 我本地配置跟不上了吗?
 * 
 * @author Bruce
 *
 */
public class ZookeeperTest02 {
	private static CountDownLatch sampleLatch = new CountDownLatch(1);

	public static void main(String[] args) {
		try {
			String connectString = "192.168.159.10,192.168.159.30,192.168.159.50";
			int sessionTimeout = 2000;
			ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
				public void process(WatchedEvent event) {
					if (event.getState() == KeeperState.SyncConnected) {
						System.out.println("事件:" + event);
						System.out.println("类型:" + event.getType());
						System.out.println("路径:" + event.getPath());
						System.out.println("状态:" + event.getState());
						sampleLatch.countDown();/* ZK连接成功时，计数器由1减为0 */
					}
				}
			});
			if (zk.getState() == States.CONNECTING) {
				try {
					sampleLatch.await();
				} catch (InterruptedException err) {
					System.out.println("Latch exception");
				}
			}
			List<String> children = zk.getChildren("/", false);
			for (String child : children) {
				System.out.println("节点:" + child);
			}
			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
