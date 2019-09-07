package vip.wush.zk.simple;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: ZKSimpleClient
 * @Description: ZooKeeper 简单API操作
 * @Author: wush
 * @Date: 2019/7/16 15:55
 */

public class ZKSimpleClient {

    private final String URL = "127.0.0.1:2181";

    private ZooKeeper zooKeeper;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZKSimpleClient client = new ZKSimpleClient();
        System.out.println(client.getChildren("/zookeeper"));
    }

    /**
     * 初始化
     */
    public ZKSimpleClient() {
        try {
            zooKeeper = new ZooKeeper(this.URL, 5000, (watchedEvent) -> {
                //监听
                if(Watcher.Event.KeeperState.SyncConnected==watchedEvent.getState()){
                    this.countDownLatch.countDown();
                    System.out.println(" zk 连接成功");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ZooKeeper getClient() throws InterruptedException {
        this.countDownLatch.await();
        return this.zooKeeper;
    }

    /**
     * 创建节点
     * @param path 节点
     * @param data 数据
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public String createNode(String path, String data) throws InterruptedException, KeeperException {
        ZooKeeper zkClient = this.getClient();
        String str = zkClient.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        return str;
    }

    /**
     * 更新节点数据
     * @param path 节点
     * @param data 数据
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public Stat updateNode(String path, String data) throws InterruptedException, KeeperException {
        ZooKeeper zkClient = this.getClient();
        Stat stat = zkClient.setData(path, data.getBytes(), -1);
        return stat;
    }

    /**
     * 检测节点是否存在
     * @param path 节点
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public Stat exist(String path) throws InterruptedException, KeeperException {
        ZooKeeper zkClient = this.getClient();
        Stat stat = zkClient.exists(path, Boolean.FALSE);
        return stat;
    }

    /**
     * 获取节点的子节点
     * @param path
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public List<String> getChildren(String path) throws InterruptedException, KeeperException {
        ZooKeeper zkClient = this.getClient();
        List<String> childrens = zkClient.getChildren(path, Boolean.FALSE);
        return childrens;
    }

    /**
     * 获取节点数据
     * @param path
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public String getData(String path) throws InterruptedException, KeeperException {
        ZooKeeper zkClient = this.getClient();
        byte[] bytes = zkClient.getData(path, Boolean.FALSE, null);
        return new String(bytes);
    }

    /**
     * 删除节点
     * @param path
     */
    public void deleteNode(String path) throws InterruptedException, KeeperException {
        ZooKeeper zkClient = this.getClient();
        zkClient.delete(path, -1);
        System.out.println("节点：" + path + "删除成功！" );
    }
}
