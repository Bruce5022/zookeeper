package com.inca.zookeeper.step01;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Test {
	// 多个地址逗号分隔,不写端口,默认2181

	public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
		String connectString = "192.168.159.10,192.168.159.30,192.168.159.50";
		int sessionTimeout = 20000000;
		ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			public void process(WatchedEvent event) {
				System.out.println("事件:" + event);
				System.out.println("类型:" + event.getType());
				System.out.println("路径:" + event.getPath());
				System.out.println("状态:" + event.getState());
			}
		});
		List<String> children = zk.getChildren("/", false);
		for (String child : children) {
			System.out.println("节点:" + child);
		}
		zk.close();
	}

}
