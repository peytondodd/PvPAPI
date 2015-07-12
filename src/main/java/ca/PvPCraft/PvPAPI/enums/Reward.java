package ca.PvPCraft.PvPAPI.enums;

import ca.PvPCraft.PvPAPI.methods.ConvertTimings;

public class Reward{
	String rewardString = null;

	public Reward(String reward){
		rewardString = reward;
	}

	public String getRewardName(){
		return (rewardString.contains(";") ? rewardString.split(";")[0] : rewardString);
	}
	public void setRewardString(String reward){
		rewardString = reward;
	}
	public Integer getCount(){
		return (hasCount() ? (ConvertTimings.isInteger(rewardString.split(";")[1]) ? Integer.parseInt(rewardString.split(";")[1]) : 1) : 1);
	}
	public boolean hasCount(){
		return (rewardString.contains(";") ? true : false);
	}
}
