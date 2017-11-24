//package com.wuyiqukuai.fabric.util;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//
///**
// * Lock实现的生产者和消费者
// *
// */
//public class ProducerCustomerWithLock {
//	
//	ExecutorService pool = Executors.newFixedThreadPool(2);
//	
//    
//    int hasRead = 0;
//    
//    private long pointe = 0;
//    
//    boolean exit = false;
//    
//    byte[] buff = new byte[1024];
//    
//    final int MAX_SIZE = 1024;
//    
//    final int ZERO = 0;
//    
//    //获取锁对象
//    private Lock lock = new ReentrantLock();
//    
//    //仓库满了，绑定生产者线程
//    private Condition full = lock.newCondition();
//    
//    //仓库为空，绑定消费者线程
//    private Condition empty = lock.newCondition();
//    
//    //生产者线程
//    private class Producer implements Runnable{
//        
//        //生产方法，需同步
//        private void produce(){
//            if(lock.tryLock()) {
//                System.out.println(Thread.currentThread().getName()+"进入仓库，准备生产！");
//                try {
//                    if(hasRead == MAX_SIZE || hasRead > 0) {
//                        System.out.println("仓库已满！等待消费者消费");
//                        Thread.sleep(1000);
//                        full.await();//生产者线程加入线程等待池
//                    }
//                    
//                    if(hasRead == -1 || hasRead < MAX_SIZE) {
//                    	System.out.println("文件读完成");
//                    	exit = true;
//                    }
//                    
//                    if(hasRead == ZERO){
//                    	RandomAccessFile raf = null;
//						try {
//							raf = new RandomAccessFile("C:/res/test.txt", "r");
//							raf.seek(pointe);
//							System.out.println("RandomAccessFile文件指针的初始位置:"+raf.getFilePointer());
//							
//							hasRead = raf.read(buff);
//							
//							System.out.println("hasRead" + hasRead);
//							
//							pointe += hasRead;
//							System.out.println("RandomAccessFile文件指针的初始位置:"+raf.getFilePointer());
//							
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							if(raf != null) {
//								try {
//									raf.close();
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//							}
//						}
//                    	
//                    }
//                    Thread.sleep(1000);
//                    empty.signalAll();//唤醒消费者线程
//                        
//                }catch(InterruptedException ie) {
//                    System.out.println("中断异常");
//                    ie.printStackTrace();
//                }finally{
//                    lock.unlock();
//                }
//            }
//        }
//
//        @Override
//        public void run() {
//            while(!exit) {
//                produce();
//            }
//        }
//    }
//    
//    //消费者线程
//    private class Customer implements Runnable{
//        
//        //消费方法，需同步
//        private void consume() {
//        	
//            if(lock.tryLock()) {
//            	
//                System.out.println(Thread.currentThread().getName()+"进入仓库，准备消费！");
//                
//                try {
//                	
//                	if(hasRead == -1) {
//                		exit = true;
//                	}
//                	
//                    if(hasRead == ZERO) {
//                        System.out.println("仓库已空！等待生产者生产");
//                        Thread.sleep(1000);
//                        empty.await();//消费者线程加入线程等待池
//                    }
//                    
//                    if(hasRead > ZERO) {
//                    	
//                    	RandomAccessFile raf=new RandomAccessFile("C:/res/testMul2.txt", "rw");
//                    	
//                    	raf.seek(raf.length());
//                    	
//                    	System.out.println("写入-------------------------------------------------");
//                    	
//                    	raf.write(buff, 0, hasRead);
//                    	
//                    	hasRead = 0;
//                    	
//                    	if(raf != null) {
//                    		raf.close();
//                    	}
//                    }
//                    
//                    Thread.sleep(1000);
//                    full.signalAll();//唤醒生产者线程
//                }catch(Exception ie) {
//                    System.out.println("中断异常");
//                    ie.printStackTrace();
//                }finally{
//                    lock.unlock();
//                }
//            }
//        }
//
//        @Override
//        public void run() {
//            while(!exit) {
//                consume();
//            }
//        }
//        
//    }
//    
//    //启动生产者和消费者线程
//    public void start() {
//            pool.execute(new Producer());
//            pool.execute(new Customer());
//    }
//    
//    public static void main(String[] args) {
//        ProducerCustomerWithLock pc = new ProducerCustomerWithLock();
//        pc.start();
//    }
//}
