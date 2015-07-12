package ca.PvPCraft.PvPAPI.enums;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;















import ca.PvPCraft.PvPAPI.methods.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;















import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ScoreboardBuilder {

	private HashMap<Integer, Team> teamList = new HashMap<Integer, Team>();


	public ScoreboardBuilder() {
	}

	public void blankLine(Objective sideBar) {
		add(" ", sideBar, 0);
	}

	public void add(String text, Objective sideBar, int fixedIndex) {
		add(text, null, sideBar, fixedIndex);
	}

	public Objective add(String text, Integer score, Objective sideBar, int fixedIndex) {
		Preconditions.checkArgument(text.length() < 48, "text cannot be over 48 characters in length");
		text = fixDuplicates(text);
		Map.Entry<Team, String> team = createTeam(text, sideBar, fixedIndex);
		OfflinePlayer player = Bukkit.getOfflinePlayer(team.getValue());
		if (team.getKey() != null)
			team.getKey().addPlayer(player);
		sideBar.getScore(player).setScore(score);
		return sideBar;
	}

	private String fixDuplicates(String text) {
		if (text.length() > 48)
			text = text.substring(0, 47);
		return text;
	}

	private Map.Entry<Team, String> createTeam(String text, Objective sideBar, int fixedIndex) {
		if (text.length() <= 16)
			return new AbstractMap.SimpleEntry<>(null, text);
			Team team;

			if (!teamList.containsKey(fixedIndex)){
				team = sideBar.getScoreboard().registerNewTeam("text-" + fixedIndex);
				teamList.put(fixedIndex, team);
			}
			else
				team = teamList.get(fixedIndex);

			Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
			String pre = iterator.next();
			String mid = iterator.next();
			String suf = null;
			if (text.length() > 32)
				suf = iterator.next();
			team.setDisplayName(mid);
			if (!ChatColor.stripColor(pre).equalsIgnoreCase(ChatColor.stripColor(team.getPrefix())))
				team.setPrefix(pre);
			if (text.length() > 32)
				if (ChatColor.stripColor(team.getSuffix()) != ChatColor.stripColor(suf))
					team.setSuffix(suf);
			return new AbstractMap.SimpleEntry<>(team, mid);
	}
	/*
	public Objective updateScores(Player p){
		int index = scores.size();
		Objective obj = Scoreboards.scoreboardsList.get(p.getName()).getObjective();
		for (Map.Entry<String, Integer> text : scores.entrySet()) {
			Map.Entry<Team, String> team = createTeam(p, text.getKey());
			Integer score = text.getValue() != null ? text.getValue() : index;
			OfflinePlayer player = Bukkit.getOfflinePlayer(team.getValue());
			if (team.getKey() != null)
				team.getKey().addPlayer(player);
			obj.getScore(player).setScore(score);
			index -= 1;
		}
		scores.clear();
		return obj;
	}
	 */
}