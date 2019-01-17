package gdou.laiminghai;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DelayQueueTest {
    public static void main(String[] args){
        DelayQueue<DelayTask> delayQueue = new DelayQueue<>();
        DelayTask<String> delayTask = new DelayTask<>(1000, "1s后执行");
        DelayTask<String> delayTask2 = new DelayTask<>(3000, "3s后执行");

        delayQueue.add(delayTask);
        delayQueue.add(delayTask2);

        for (;;){
            try {
                DelayTask<String> task = delayQueue.take();
//                System.out.println(task.getData());
//                new Thread(new DelayTaskRunnable<>(task.getData(), System.out::println)).start();
                new Thread(() -> {
                    System.out.println(task.getData());
                }).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(delayQueue.isEmpty()){
                break;
            }
        }
    }
}

class DelayTaskRunnable<T> implements Runnable{

    private Consumer<T> action;

    private T data;

    public DelayTaskRunnable(T data, Consumer<T> action) {
        this.data = data;
        this.action = action;
    }

    @Override
    public void run() {
        action.accept(data);
    }
}

class DelayTask<T> implements Delayed {

    //延迟时间
    private long delayTime;

    //任务开始时间
    private long startTime;

    //任务消息
    private T data;

    public DelayTask(long delayTime, T data) {
        this.delayTime = delayTime;
        this.data = data;
        this.startTime = System.currentTimeMillis() + delayTime;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diff = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return diff > 0 ? 1 : (diff < 0) ? -1 : 0;
    }
}
