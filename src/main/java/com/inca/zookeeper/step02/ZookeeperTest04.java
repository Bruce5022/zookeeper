package com.inca.zookeeper.step02;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

/**
 * 
 * @author Bruce
 *
 */
public class ZookeeperTest04 {
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
	
	private static void showRootSon(ZooKeeper zk) throws KeeperException, InterruptedException {
		System.out.println("==================根的子节点start======================");
		List<String> children = zk.getChildren("/", false);
		for (String child : children) {
			System.out.println("节点:" + child);
		}
		System.out.println("==================根的子节点end======================");
	}

	private static final String GROUPNODE = "/szwcsdata";
	private static final String SUBNODE = "/szwcsdata/bigdata";

	public static void main(String[] args) throws Exception {
		ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, watcher);
		waitForConnection(zk);
		Stat exists = zk.exists(GROUPNODE, false);
		if (exists == null) {
			zk.create(GROUPNODE, "szwcsdata:is it right?".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
		}
		exists = zk.exists(SUBNODE, false);
		if (exists == null) {
			zk.create(SUBNODE, "bigdata is son ".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		showRootSon(zk);
		zk.close();
	}

	

}

//打印结果
//2018-10-21 10:36:49,270 [myid:] - INFO  [main-SendThread(192.168.159.50:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server 192.168.159.50/192.168.159.50:2181. Will not attempt to authenticate using SASL (unknown error)
//2018-10-21 10:36:49,277 [myid:] - INFO  [main-SendThread(192.168.159.50:2181):ClientCnxn$SendThread@879] - Socket connection established to 192.168.159.50/192.168.159.50:2181, initiating session
//2018-10-21 10:36:49,319 [myid:] - INFO  [main-SendThread(192.168.159.50:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server 192.168.159.50/192.168.159.50:2181, sessionid = 0x3000006b5490004, negotiated timeout = 4000
//事件:WatchedEvent state:SyncConnected type:None path:null
//类型:None
//路径:null
//状态:SyncConnected
//==================根的子节点start======================
//节点:zookeeper
//节点:szwcsdata
//==================根的子节点end======================
//2018-10-21 10:36:49,345 [myid:] - INFO  [main:ZooKeeper@693] - Session: 0x3000006b5490004 closed
