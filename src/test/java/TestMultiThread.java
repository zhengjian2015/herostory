public class TestMultiThread {
    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            System.out.println("第" + i + "次测试");
            new TestMultiThread().test3();
        }
    }

    private void test3() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread[] threadArray = new Thread[2];

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i] = new Thread(() -> {
                newUser.currHp = newUser.currHp - 10;
            });
        }

        threadArray[0].start();
        threadArray[1].start();

        try {
            threadArray[0].join();
            threadArray[1].join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (newUser.currHp != 80) {
            throw new RuntimeException("当前血量错误，curHttp=" + newUser.currHp);
        }
    }
}
