package com.epam.rd.java.basic.topic05.task05;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {
	
	public static final String FILE_NAME = "data.txt";
	
	private static RandomAccessFile raf;

	private static Thread[] threads;
	//private static AtomicInteger rowIndex = new AtomicInteger();
	private static int rowIndex;
	public static void createRAF(int numberOfThreads, int numberOfIterations, int pause) throws IOException {
		//rowIndex.set(0);
		rowIndex = 0;
		clearFile();

		Runnable logic = () -> {
			//rws Same as rw mode. It also supports to update file content synchronously to device storage.
			try(RandomAccessFile file =  new RandomAccessFile(FILE_NAME, "rws");)
			{

				synchronized (Task.class) {
					//int rowFiller = rowIndex.getAndAdd(1);
					int rowFiller = rowIndex++;

					if (rowFiller == 0) {
						file.seek(0);
					}
					//довжина рядка = numberOfIterations * на кількість рядків + кількість переносів строки
					else {
						file.seek(numberOfIterations * rowFiller + rowFiller);
					}

					for (int i = 0; i < numberOfIterations; i++) {
						//file.writeInt(rowFiller);
						file.write(Integer.toString(rowFiller).getBytes(StandardCharsets.UTF_8));
						Thread.sleep(pause);
					}
					file.write("\n".getBytes(StandardCharsets.UTF_8));

				}
			} catch (IOException | InterruptedException e) {e.printStackTrace();}
		};

		createThreads(logic, numberOfThreads);
		startThreads();
		joinThreads();
	}

	private static void clearFile()
	{
		try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw")) {
			raf.setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void createThreads(Runnable r, int n){
		threads = new Thread[n];
		for(int i = 0; i<n; i++){
			threads[i] = new Thread(r);
		}
	}
	private static void startThreads()
	{
		for(Thread t : threads) t.start();
	}
	private static void joinThreads(){
		for(Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e){e.printStackTrace();}
		}
	}


	public static void main(String[] args) throws IOException {
		createRAF(5, 20, 10);
		
		Files.readAllLines(Paths.get(FILE_NAME))
			.stream()
			.forEach(System.out::println);
	}
}
