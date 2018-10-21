package com.inca.zookeeper.step03;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

/**
 * 节点数据操作
 * 
 * @author Bruce
 *
 */
public class ZookeeperTest06 {
	private static CountDownLatch sampleLatch = new CountDownLatch(1);
	private static String connectString = "192.168.159.10,192.168.159.30,192.168.159.50";
	private static int sessionTimeout = 2000;
	
	private static Watcher watcher = new Watcher() {
		public void process(WatchedEvent event) {
			if (event.getState() == KeeperState.SyncConnected) {
				sampleLatch.countDown();/* ZK连接成功时，计数器由1减为0 */
			}
			System.out.println("事件:" + event);
			System.out.println("类型:" + event.getType());
			System.out.println("路径:" + event.getPath());
			System.out.println("状态:" + event.getState());
			try {
				zk.exists(GROUPNODE, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private static ZooKeeper zk = null;

	private static void waitForConnection(ZooKeeper zk) {
		if (zk.getState() == States.CONNECTING) {
			try {
				sampleLatch.await();
			} catch (InterruptedException err) {
				System.out.println("Latch exception");
			}
		}
	}

	private static final String GROUPNODE = "/szwcsdata";

	public static void main(String[] args) throws Exception {
		zk = new ZooKeeper(connectString, sessionTimeout, watcher);
		waitForConnection(zk);
		zk.exists(GROUPNODE, true);// 对一个节点监听
		TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
		zk.close();
	}
}
