package org.example;

public class ASD_Lab_1 {

    static class ArrayQueue {
        private double[] arr;
        private int size;
        private int rear;

        public ArrayQueue(int capacity) {
            arr = new double[capacity];
            size = 0;
            rear = -1;
        }

        public boolean isFull() {
            return size == arr.length;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean enqueue(double value) {
            if (isFull()) return false;
            rear++;
            arr[rear] = value;
            size++;
            return true;
        }

        public double dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue is empty");
            double val = arr[0];
            for (int i = 1; i < size; i++) {
                arr[i-1] = arr[i];
            }
            size--;
            rear--;
            return val;
        }

        public void print() {
            System.out.print("Queue: ");
            for (int i = 0; i < size; i++) System.out.print(arr[i] + " ");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Vector Queue ===");
        ArrayQueue q = new ArrayQueue(5);
        q.enqueue(1.1);
        q.enqueue(2.2);
        q.enqueue(3.3);
        q.print();
        q.dequeue();
        q.print();
    }
}
