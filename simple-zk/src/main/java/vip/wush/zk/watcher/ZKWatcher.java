package vip.wush.zk.watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_ADDED;
import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_UPDATED;

/**
 * @ClassName: ZKWatcher
 * @Description: zookeeper监听事件
 * @Author: wush
 * @Date: 2019/7/16 16:58
 */

public class ZKWatcher {

    static final String URL = "127.0.0.1:2181";

    private CuratorFramework client;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    static final String nodeStr = "/curator/test5";    //操作节点


    public static void main(String[] args) throws Exception {

        ZKWatcher zkWatcher = new ZKWatcher();
        zkWatcher.initClient();
        Thread.sleep(2000);

        Model model = new Model();
        model.setName("吴少和");
        model.setAge(25);

        zkWatcher.create(nodeStr, model.toJson());

        Thread.sleep(2000);

        Model modify = new Model();
        modify.setName("wushaohe");
        modify.setAge(25);
        zkWatcher.update(nodeStr, modify.toJson());

        Thread.sleep(Integer.MAX_VALUE);
    }


    public void initClient() throws Exception {
        /**
         * 创建zk客户端
         */
        client = CuratorFrameworkFactory.builder()
                .connectString(URL)  //服务器地址
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(100, 3))
                .namespace("curator")
                .build();
        /**
         * 启动客户端
         */
        client.start();
        watcher(nodeStr);
    }

    /**
     * 创建节点
     * @param path
     * @param data
     * @throws Exception
     */
    public void create(String path, String data) throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                forPath(path, data.getBytes());
    }

    public void update(String path, String data) throws Exception {
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        client.setData().withVersion(stat.getVersion()).forPath(path, data.getBytes());
    }


    /**
     * 监听
     * @param path
     * @throws Exception
     */
    public void watcher(String path) throws Exception {
        TreeCache cache = new TreeCache(this.client, path);
        cache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                if (event.getType().equals(NODE_ADDED) || event.getType().equals(NODE_UPDATED)){
                    System.out.println("监听到事件 testWatcher");
                    String data = new String(event.getData().getData(),"utf-8");
                    System.out.println("data: "+data);
                    Model model = Model.fromJson(data);
                    System.out.println("model: " +model.toJson());
                }
            }
        });
        cache.start();
    }

}
