package com.lesson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程打印字符串面试题解决方案
 * 要求：定义一个类，包含两个方法，一个打印"hello"，一个打印"world"
 * 通过多线程调度，输出5遍"hello, world"
 */
public class MultithreadPrintTest {

    public static void main(String[] args) {
        System.out.println("=== 方案1：使用synchronized + wait/notify ===");
        testSynchronizedSolution();
        
        System.out.println("\n=== 方案2：使用ReentrantLock + Condition ===");
        testReentrantLockSolution();
        
        System.out.println("\n=== 方案3：使用Semaphore ===");
        testSemaphoreSolution();
        
        System.out.println("\n=== 方案4：使用CountDownLatch ===");
        testCountDownLatchSolution();
    }

    /**
     * 方案1：使用synchronized + wait/notify
     */
    public static void testSynchronizedSolution() {
        SynchronizedPrinter printer = new SynchronizedPrinter();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printHello();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printWorld();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方案2：使用ReentrantLock + Condition
     */
    public static void testReentrantLockSolution() {
        ReentrantLockPrinter printer = new ReentrantLockPrinter();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printHello();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printWorld();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方案3：使用Semaphore
     */
    public static void testSemaphoreSolution() {
        SemaphorePrinter printer = new SemaphorePrinter();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printHello();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printWorld();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方案4：使用CountDownLatch
     */
    public static void testCountDownLatchSolution() {
        CountDownLatchPrinter printer = new CountDownLatchPrinter();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printHello();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                printer.printWorld();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方案1：使用synchronized + wait/notify实现
     */
    static class SynchronizedPrinter {
        private final Object lock = new Object();
        private volatile boolean isHelloTurn = true;

        public void printHello() {
            synchronized (lock) {
                while (!isHelloTurn) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.print("hello");
                isHelloTurn = false;
                lock.notify();
            }
        }

        public void printWorld() {
            synchronized (lock) {
                while (isHelloTurn) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println(", world");
                isHelloTurn = true;
                lock.notify();
            }
        }
    }

    /**
     * 方案2：使用ReentrantLock + Condition实现
     */
    static class ReentrantLockPrinter {
        private final java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock();
        private final java.util.concurrent.locks.Condition helloCondition = lock.newCondition();
        private final java.util.concurrent.locks.Condition worldCondition = lock.newCondition();
        private volatile boolean isHelloTurn = true;

        public void printHello() {
            lock.lock();
            try {
                while (!isHelloTurn) {
                    helloCondition.await();
                }
                System.out.print("hello");
                isHelloTurn = false;
                worldCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }

        public void printWorld() {
            lock.lock();
            try {
                while (isHelloTurn) {
                    worldCondition.await();
                }
                System.out.println(", world");
                isHelloTurn = true;
                helloCondition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 方案3：使用Semaphore实现
     */
    static class SemaphorePrinter {
        private final Semaphore helloSemaphore = new Semaphore(1);
        private final Semaphore worldSemaphore = new Semaphore(0);

        public void printHello() {
            try {
                helloSemaphore.acquire();
                System.out.print("hello");
                worldSemaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void printWorld() {
            try {
                worldSemaphore.acquire();
                System.out.println(", world");
                helloSemaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 方案4：使用CountDownLatch实现
     */
    static class CountDownLatchPrinter {
        private final AtomicInteger counter = new AtomicInteger(0);
        private final Object lock = new Object();

        public void printHello() {
            synchronized (lock) {
                while (counter.get() % 2 != 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.print("hello");
                counter.incrementAndGet();
                lock.notifyAll();
            }
        }

        public void printWorld() {
            synchronized (lock) {
                while (counter.get() % 2 != 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println(", world");
                counter.incrementAndGet();
                lock.notifyAll();
            }
        }
    }
}


