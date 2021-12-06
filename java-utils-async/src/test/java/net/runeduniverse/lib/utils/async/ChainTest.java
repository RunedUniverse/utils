/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.utils.async;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.utils.async.ChainableTask;
import net.runeduniverse.lib.utils.async.ThreadPool;
import net.runeduniverse.lib.utils.async.TriggerMap;
import net.runeduniverse.lib.utils.async.TriggeredParamTask;

public class ChainTest {

	public void print(String line) {
		System.out.println(LocalDateTime.now() + ": " + line);
	}

	@Test
	public void buildRunnable() throws InterruptedException {
		System.out.println("BUILD RUNNABLE");

		UUID taskUUID = UUID.randomUUID();

		TriggerMap<UUID> startTriggerMap = new TriggerMap<UUID>();
		TriggerMap<UUID> timeoutTriggerMap = new TriggerMap<UUID>();

		Thread thread = new Thread(

				startTriggerMap.create(taskUUID)
						.burying(false)
						.append(new ChainableTask(new Runnable() {

							@Override
							public void run() {
								print("INIT");
							}
						}))
						.append(timeoutTriggerMap.create(taskUUID))
						.append(new ThreadPool())
						.add(new Runnable() {

							@Override
							public void run() {
								print("PROCESS 1");
							}
						})
						.add(new Runnable() {

							@Override
							public void run() {
								print("PROCESS 2");
							}
						})
						.add(new Runnable() {

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

		TriggerMap<UUID> startTriggerMap = new TriggerMap<UUID>();
		TriggerMap<UUID> timeoutTriggerMap = new TriggerMap<UUID>();

		Thread thread = startTriggerMap.create(taskUUID)
				.burying(false)
				.append(new ChainableTask(new Runnable() {

					@Override
					public void run() {
						print("INIT");
					}
				}))
				.append(timeoutTriggerMap.create(taskUUID))
				.append(new ThreadPool())
				.add(new Runnable() {

					@Override
					public void run() {
						print("PROCESS 1");
					}
				})
				.add(new Runnable() {

					@Override
					public void run() {
						print("PROCESS 2");
					}
				})
				.add(new Runnable() {

					@Override
					public void run() {
						print("PROCESS 3");
					}
				})
				.runAsThread();

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
	public void paramTrigger() throws InterruptedException {
		TriggeredParamTask<UUID> task = new TriggeredParamTask<UUID>();

		task.setConsumer(uuid -> {
			System.out.println("UUID loaded! >> " + uuid);
		});

		Thread thread = task.runAsThread();

		UUID uuid = UUID.randomUUID();
		System.out.println("UUID generated! >> " + uuid);

		task.trigger(uuid);
		thread.join();
	}
}
