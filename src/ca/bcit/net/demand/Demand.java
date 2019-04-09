package ca.bcit.net.demand;

import ca.bcit.net.Network;
import ca.bcit.net.PartedPath;

import java.util.ArrayList;

public abstract class Demand {

	private final boolean reallocate;
	private final boolean allocateBackup;
	private final int volume;
	private final int squeezedVolume;
	private int ttl;
	private final int initialTTL;
	
	PartedPath workingPath;
	private PartedPath backupPath;

	public Demand(boolean reallocate, boolean allocateBackup, int volume, int squeezedVolume, int ttl) {
		this.reallocate = reallocate;
		this.allocateBackup = allocateBackup;
		this.volume = volume;
		this.squeezedVolume = squeezedVolume < 10 ? 10 : squeezedVolume;
		this.ttl = ttl;
		initialTTL = ttl;
	}

	public Demand(boolean reallocate, boolean allocateBackup, int volume, float squeezeRatio, int ttl) {
		this(reallocate, allocateBackup, volume, (int) Math.round(volume * squeezeRatio), ttl);
	}
	
	public PartedPath getWorkingPath() {
		return workingPath;
	}
	
	public PartedPath getBackupPath() {
		return backupPath;
	}
	
	public abstract ArrayList<PartedPath> getCandidatePaths(boolean backup, Network network);
	
	public boolean reallocate() {
		return reallocate;
	}
	
	public boolean allocateBackup() {
		return allocateBackup;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public int getSqueezedVolume() {
		return squeezedVolume;
	}
	
	public int getTTL() {
		return ttl;
	}
	
	public void tick() {
		ttl--;
	}
	
	public boolean isDead() {
		return ttl <= 0;
	}
	
	public boolean isDisjoint(Demand other) {
		return workingPath.isDisjoint(other.workingPath);
	}
	
	public boolean allocate(Network network, PartedPath path) {
		if (workingPath == null)
			if (path.allocate(this)) {
				this.workingPath = path;
				return true;
			} else return false;
		else
			if (path.allocate(this)) {
				this.backupPath = path;
				return true;
			} else return false;
	}
	
	public boolean onWorkingFailure() {
		workingPath.deallocate(this);
		if (backupPath == null) {
			workingPath = null;
			this.ttl = initialTTL;
			return false;
		}
		workingPath = backupPath;
		backupPath = null;
		workingPath.toWorking(this);
		return true;
	}
	
	public void onBackupFailure() {
		if (backupPath != null)
			backupPath.deallocate(this);
		backupPath = null;
	}
	
	public void deallocate() {
		workingPath.deallocate(this);
		if (backupPath != null) backupPath.deallocate(this);
	}
}
