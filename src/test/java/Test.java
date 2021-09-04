public class Test {
  private static final Object lock = new Object();
  private static volatile int  now = 1;
  @org.junit.jupiter.api.Test
  public void test() throws InterruptedException {
    new Thread1(14).start();
    new Thread2(14).start();
    Thread.sleep(41000);
  }
  static class Thread1 extends Thread{
    private int max;
    Thread1(int max) {
      this.max = max;
      setName("奇数");
    }

    @Override
    public void run() {
      synchronized (lock){
        while (now <= max) {
          while (now % 2 != 1) {
            try {
              lock.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          if (now <= max) {
            System.out.println(Thread.currentThread() + " " + (now++));
            lock.notifyAll();
          }
        }
      }
    }
  }
  static class Thread2 extends Thread{
    private int max;
    Thread2(int max) {
      this.max = max;
      setName("偶数");
    }

    @Override
    public void run() {
      synchronized (lock){
        while (now <= max) {
          while (now % 2 != 0) {
            try {
              lock.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          if (now <= max){
            System.out.println(Thread.currentThread() + " " + (now++));
            lock.notifyAll();
          }
        }
      }
    }
  }


}
