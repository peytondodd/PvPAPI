package ca.PvPCraft.PvPAPI.enums;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerScoreboard{
	ScoreboardBuilder simpleScoreboard;
	Scoreboard scoreboard;
	Objective obj;

	public void setScoreboardBuilder(ScoreboardBuilder simpleScore) {
		simpleScoreboard = simpleScore;
	}
	public ScoreboardBuilder getSimpleScoreboard(){
		return simpleScoreboard;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard score){
		scoreboard = score;
	}
	public void setObjective(Objective objectiveSb) {
		obj = objectiveSb;
	}
	public Objective getObjective (){
		return obj;
	}
}
