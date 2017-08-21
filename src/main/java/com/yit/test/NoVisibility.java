package com.yit.test;

public class NoVisibility {
    private static boolean ready;
    private  static int number;

    private static class ReaderThread extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.print(Thread.currentThread().getName() + "--->");
            System.out.println(number);

        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new ReaderThread().start();
            number = 42;
            ready = true;
        }
    }

}