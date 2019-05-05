package com.itheima.test;

import com.pinyougou.common.util.FastDFSClient;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.test *
 * @since 1.0
 */
public class FastdfsTest {
    @Test
    public void upload() throws Exception{
        //1.创建一个配置文件 用于配置链接服务器的地址。加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");

        //2.创建一个trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();

        //3.获取trackerserver 对象


        TrackerServer trackerServer = trackerClient.getConnection();

        //4.创建一个storageClient 才是真正上传图片 下载图片的类
        StorageClient storageClient = new StorageClient(trackerServer,null);

        //参数1：文件路径
        //参数2：文件的扩张名 不要带点
        //参数3：元数据
        String[] jpgs = storageClient.upload_file("C:\\Users\\Administrator\\Pictures\\5b13cd6cN8e12d4aa.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }

    }

    @Test
    public void uploadFile() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("C:\\Users\\Administrator\\IdeaProjects\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String jpg = fastDFSClient.uploadFile("C:\\Users\\Administrator\\Pictures\\timg.jpg", "jpg");

        System.out.println(jpg);
    }
}
