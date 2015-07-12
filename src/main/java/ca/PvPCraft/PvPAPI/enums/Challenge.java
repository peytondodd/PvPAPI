package ca.PvPCraft.PvPAPI.enums;

import io.netty.util.internal.ConcurrentSet;
//import net.minecraft.io.netty.util.internal.ConcurrentSet;

import ca.PvPCraft.PvPAPI.utilities.Message;

public class Challenge{
	String challengeName = null;
	ConcurrentSet<String> challenges = new ConcurrentSet<String>();
	ConcurrentSet<String> rewards = new ConcurrentSet<String>();
	String challengeDescShort = null;
	String challengeDescLong = null;
	public Challenge(String nameChallenge, String descShort){
		challengeName = nameChallenge;
		challengeDescShort = descShort;
	}
	public void setLongDesc(String longDesc){
		challengeDescLong = longDesc;
	}
	public void setShortDesc(String shortDesc){
		challengeDescShort = shortDesc;
	}
	public String getName(){
		return challengeName;
	}
	public void addRequirement(String challenge){
		challenges.add(challenge);
	}
	public void removeRequirement(String challengeNeeded){
		for (String challenge : challenges){
			if (challenge.equalsIgnoreCase(challengeNeeded)){
				challenges.remove(challengeNeeded);
				return;
			}
		}
		for (String challenge : challenges){
			if (challenge.toLowerCase().contains(challengeNeeded.toLowerCase())){
				challenges.remove(challenge);
				return;
			}
		}
	}
	public ConcurrentSet<Tasks> getChallenges(){
		ConcurrentSet<Tasks> tasks = new ConcurrentSet<Tasks>();
		for (String task : challenges){
			tasks.add(new Tasks(task));
		}
		return tasks;
	}
	public void setChallenges(ConcurrentSet<String> list){
		challenges = list;
	}

	public void addReward(String reward){
		rewards.add(reward);
	}
	public void removeReward(String rewardNeeded){
		for (String reward : rewards){
			if (reward.equalsIgnoreCase(rewardNeeded)){
				rewards.remove(rewardNeeded);
				return;
			}
		}
		for (String reward : rewards){
			if (reward.toLowerCase().contains(rewardNeeded.toLowerCase())){
				rewards.remove(reward);
				return;
			}
		}
	}
	public ConcurrentSet<Reward> getRewards(){
		ConcurrentSet<Reward> rewardList = new ConcurrentSet<Reward>();
		for (String reward : rewards){
			rewardList.add(new Reward(reward));
		}
		return rewardList;
	}
	public void setRewards(ConcurrentSet<String> list){
		rewards = list;
	}

	public String getLongDesc(){
		String newString = "";
		for (Tasks task : getChallenges()){
			newString = Message.Replacer(challengeDescLong, ""+task.getCount(), "%val" + task.getTaskName().charAt(0));
		}
		return newString;
	}
	public String getShortDesc(){
		String newString = "";
		for (Tasks task : getChallenges()){
			newString = Message.Replacer(challengeDescShort, ""+task.getCount(), "%val" + task.getTaskName().charAt(0));
		}
		return newString;
	}
}
