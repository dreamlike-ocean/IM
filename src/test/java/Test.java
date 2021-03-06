import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;

public class Test {
  private static final Object lock = new Object();
  private static volatile int  now = 1;
  @org.junit.jupiter.api.Test
  public void test() throws InterruptedException, IOException {

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
