package net.runeduniverse.libs.utils.async;

public class CleanupTask extends AChain<CleanupTask> {

	private Runnable task = null;

	public CleanupTask() {
		this(null);
	}

	public CleanupTask(Runnable task) {
		instance = this;
		this.ignoreStop = true;
		this.task = task;
	}

	public CleanupTask setTask(Runnable task) {
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
