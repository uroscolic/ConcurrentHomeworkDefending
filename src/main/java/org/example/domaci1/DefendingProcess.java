package org.example.domaci1;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DefendingProcess {

    public static AtomicInteger totalScore = new AtomicInteger(0);
    public static AtomicInteger totalStudents = new AtomicInteger(0);
    public static boolean isFirst = true;
    public static CyclicBarrier startBarrier = new CyclicBarrier(3, () -> isFirst = false);
    public static Date time = null;
    public static Thread thread;
    public static int counter = 0;

    public static void main(String[] args) {
        int n;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of students: ");
        n = scanner.nextInt();
        counter = n;
        if(n < 1) {
            System.out.println("Invalid input");
            return;
        }
        scanner.close();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ExecutorService executorService1 = Executors.newFixedThreadPool(1);

        CyclicBarrier professorBarrier = new CyclicBarrier(2);
        CyclicBarrier assistantBarrier = new CyclicBarrier(1);

        List<Student> students = Student.generateRandomStudents(n, professorBarrier, assistantBarrier);
        thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("Total score: " + totalScore);
                System.out.println("Total students: " + totalStudents);
                System.out.println("Average score: " + (double) totalScore.get() / totalStudents.get());
                executorService.shutdownNow();
                executorService1.shutdownNow();

            } catch (InterruptedException e) {
                System.out.println("Total score: " + totalScore);
                System.out.println("Total students: " + totalStudents);
                System.out.println("Average score: " + (double) totalScore.get() / totalStudents.get());
                executorService.shutdownNow();
                executorService1.shutdownNow();
            }

        });
        time = new Date();
        students.sort((o1, o2) -> {
            if (o1.getArrivalTime() < o2.getArrivalTime())
                return -1;
             else if (o1.getArrivalTime() > o2.getArrivalTime())
                return 1;
            return 0;
        });

        thread.start();

        for (Student student : students) {
            if (student.getBarrier().getParties() == 2)
                executorService.execute(student);
            else
                executorService1.execute(student);
        }
    }

}
