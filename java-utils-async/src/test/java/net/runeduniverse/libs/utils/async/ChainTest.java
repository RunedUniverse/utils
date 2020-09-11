package net.runeduniverse.libs.utils.async;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.*;

public class ChainTest {

	public void print(String line) {
		System.out.println(LocalDateTime.now() + ": " + line);
	}

	@Test
	public void buildRunnable() throws InterruptedException {
		System.out.println("BUILD RUNNABLE");

		UUID taskUUID = UUID.randomUUID();

		TriggerMap startTriggerMap = new TriggerMap();
		TriggerMap timeoutTriggerMap = new TriggerMap();

		Thread thread = new Thread(

				startTriggerMap.create(taskUUID).burying(false).append(new ChainableTask(new Runnable() {

					@Override
					public void run() {
						print("INIT");
					}
				})).append(timeoutTriggerMap.create(taskUUID)).append(new ThreadPool()).add(new Runnable() {

					@Override
					public void run() {
						print("PROCESS 1");
					}
				}).add(new Runnable() {

					@Override
					public void run() {
						print("PROCESS 2");
					}
				}).add(new Runnable() {

					@Override
					public void run() {
						print("PROCESS 3");
					}
				}));

		thread.start();

		Thread.sleep(1000);

		print("START");
		startTriggerMap.trigger(taskUUID);

		Thread.sleep(1000);

		print("TIMEOUT");
		timeoutTriggerMap.trigger(taskUUID);

		thread.join();
		print("FINISHED");
	}

	@Test
	public void buildRunnableAsThread() throws InterruptedException {
		System.out.println("BUILD RUNNABLE - run as Thread");

		UUID taskUUID = UUID.randomUUID();

		TriggerMap startTriggerMap = new TriggerMap();
		TriggerMap timeoutTriggerMap = new TriggerMap();

		Thread thread = startTriggerMap.create(taskUUID).burying(false).append(new ChainableTask(new Runnable() {

			@Override
			public void run() {
				print("INIT");
			}
		})).append(timeoutTriggerMap.create(taskUUID)).append(new ThreadPool()).add(new Runnable() {

			@Override
			public void run() {
				print("PROCESS 1");
			}
		}).add(new Runnable() {

			@Override
			public void run() {
				print("PROCESS 2");
			}
		}).add(new Runnable() {

			@Override
			public void run() {
				print("PROCESS 3");
			}
		}).runAsThread();

		Thread.sleep(1000);

		print("START");
		startTriggerMap.trigger(taskUUID);

		Thread.sleep(1000);

		print("TIMEOUT");
		timeoutTriggerMap.trigger(taskUUID);

		thread.join();
		print("FINISHED");
	}
}
