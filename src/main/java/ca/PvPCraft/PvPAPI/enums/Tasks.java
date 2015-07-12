package ca.PvPCraft.PvPAPI.enums;

import ca.PvPCraft.PvPAPI.methods.ConvertTimings;
import io.netty.util.internal.ConcurrentSet;

public class Tasks{
	String taskString = null;

	public Tasks(String reward){
		taskString = reward;
	}

	public String getTaskName(){
		return (taskString.contains(";") ? taskString.split(";")[0] : taskString);
	}
	public void setTaskString(String reward){
		taskString = reward;
	}
	public Integer getCount(){
		return (hasCount() ? (ConvertTimings.isInteger(taskString.split(";")[1]) ? Integer.parseInt(taskString.split(";")[1]) : 1) : 1);
	}
	public boolean hasCount(){
		return (taskString.contains(";") ? true : false);
	}
}
