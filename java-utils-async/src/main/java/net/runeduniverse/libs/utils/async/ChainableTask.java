package net.runeduniverse.libs.utils.async;

public class ChainableTask extends AChain<ChainableTask> {

	private Runnable task = null;

	public ChainableTask() {
		instance = this;
	}

	public ChainableTask(Runnable task) {
		instance = this;
		this.task = task;
	}

	public ChainableTask setTask(Runnable task) {
		this.task = task;
		return this;
	}

	@Override
	public void execute() {
		if (this.task != null)
			this.task.run();
		done();
	}
}
