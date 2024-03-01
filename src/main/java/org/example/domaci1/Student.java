package org.example.domaci1;

import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Getter
public class Student implements Runnable{

    private String name;
    private double arrivalTime;
    private double durationOfDefense;
    private int grade;
    private CyclicBarrier barrier;
    public Student(String name, double arrivalTime, double durationOfDefense, CyclicBarrier barrier) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.durationOfDefense = durationOfDefense;
        this.barrier = barrier;
    }
    public static List<Student> generateRandomStudents(int n, CyclicBarrier barrier, CyclicBarrier barrier2) {
        List<Student> students = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++)
            students.add(new Student("Student " + (i+1), random.nextDouble() + 0.01,
                    random.nextDouble() * 0.5 + 0.5, random.nextDouble() < 0.5 ? barrier : barrier2));
        return students;
    }

    @Override
    public void run() {
        String profOrAssist = barrier.getParties() == 2 ? "professor" : "assistant";
        LocalTime time;
        try {

            Date date = new Date();
            double sleepTime = (date.getTime() - DefendingProcess.time.getTime());

            if(sleepTime < 1000)
            {
                sleepTime /= 1000;
                if(arrivalTime - sleepTime > 0)
                    Thread.sleep((long) ((arrivalTime - sleepTime)*1000));
            }
            if(DefendingProcess.isFirst)
                DefendingProcess.startBarrier.await();  // wait for all students to arrive
            // so that professor and assistant can start at the same time

            barrier.await();
            time = LocalTime.now();

            Thread.sleep((long) (durationOfDefense * 1000));
            synchronized (Student.class) {
                grade = new Random().nextInt(6) + 5;
                DefendingProcess.totalScore.addAndGet(grade);
                DefendingProcess.totalStudents.incrementAndGet();
            }

            System.out.println("Thread: " + name + " Arrival: " + String.format("%.2f", arrivalTime) + " Prof: " + profOrAssist +
                    " TTC: " + String.format("%.2f", durationOfDefense) + " : " + time + " Score: " + grade);

            if(DefendingProcess.counter == DefendingProcess.totalStudents.get()) // if all students have defended before 5 seconds have passed
                DefendingProcess.thread.interrupt();


        } catch (InterruptedException e) {
            System.out.println("INTERRUPTED - Thread: " + name + " Arrival: " + String.format("%.2f", arrivalTime) + " Prof: " + profOrAssist);
        } catch (BrokenBarrierException e) {
            System.out.println("BROKEN BARRIER - Thread: " + name + " Arrival: " + String.format("%.2f", arrivalTime) + " Prof: " + profOrAssist);
        }
    }

}
