package cn.liyongwei.utils;


import java.util.concurrent.TimeUnit;

class LocalCacheUtilTest {

    public static void main(String[] args) throws InterruptedException {
        LocalCacheUtil.set("name", "张三", TimeUnit.SECONDS.toMillis(2));
        String name = LocalCacheUtil.get("name", String.class);
        System.out.println("name=" + name);
        Thread.sleep(TimeUnit.SECONDS.toMillis(3));
        String name1 = LocalCacheUtil.get("name", String.class);
        if (name1 == null) {
            System.out.println("未读取到数据");
        } else {
            System.out.println("next name=" + name1);
        }
    }
}