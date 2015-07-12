package ca.PvPCraft.PvPAPI.methods;

import org.apache.commons.lang.StringUtils;

import java.util.Random;

import ca.PvPCraft.PvPAPI.enums.LengthType;
import ca.PvPCraft.PvPAPI.utilities.Message;

public class ConvertTimings {

	public static String convertTime (int seconds, boolean simple){
		StringBuilder sb = new StringBuilder();


		if (seconds >= (60 * 60 * 24 * 31 * 12)) {
			int days = seconds / (60 * 60 * 24 * 31 * 12);

			sb.append(days);
			sb.append(" ");
			if (days == 1) {
				sb.append("year");
			} else {
				sb.append("years");
			}
			seconds = seconds % (60 * 60 * 24 * 31 * 12);
			if (!simple)
				sb.append(", and ");
		}


		if ((sb.length() == 0 && simple) || !simple)
			if (seconds >= (60 * 60 * 24 * 31)) {
				int days = seconds / (60 * 60 * 24 * 31);

				sb.append(days);
				sb.append(" ");
				if (days == 1) {
					sb.append("month");
				} else {
					sb.append("months");
				}
				seconds = seconds % (60 * 60 * 24 * 31);
				if (!simple)
					sb.append(", and ");
			}

		if ((sb.length() == 0 && simple) || !simple)
			if (seconds >= (60 * 60 * 24 * 7)) {
				int days = seconds / (60 * 60 * 24 * 7);

				sb.append(days);
				sb.append(" ");
				if (days == 1) {
					sb.append("week");
				} else {
					sb.append("weeks");
				}
				seconds = seconds % (60 * 60 * 24 * 7);
				if (!simple)
					sb.append(", and ");
			}

		if ((sb.length() == 0 && simple) || !simple)
			if (seconds >= (60 * 60 * 24)) {
				int days = seconds / (60 * 60 * 24);

				sb.append(days);
				sb.append(" ");
				if (days == 1) {
					sb.append(Message.Day);
				} else {
					sb.append(Message.Days);
				}
				seconds = seconds % (60 * 60 * 24);
				if (!simple)
					sb.append(", and ");
			}

		if ((sb.length() == 0 && simple) || !simple)
			if (seconds >= (60 * 60)) {
				int hours = seconds / (60 * 60);

				sb.append(hours);
				sb.append(" ");
				if (hours == 1) {
					sb.append(Message.Hour);
				} else {
					sb.append(Message.Hours);
				}
				seconds = seconds % (60 * 60);
				if (!simple)
					sb.append(", and ");
			}

		if ((sb.length() == 0 && simple) || !simple)
			if (seconds >= 60) {
				int minutes = seconds / 60;

				sb.append(minutes);
				sb.append(" ");
				if (minutes == 1) {
					sb.append(Message.Minute);
				} else {
					sb.append(Message.Minutes);
				}
				seconds = seconds % 60;
				if (!simple)
					sb.append(", and ");
			}


		if ((sb.length() == 0 && simple) || !simple)
			if (seconds > 0) {
				sb.append(seconds);
				sb.append(" ");
				if (seconds == 1) {
					sb.append(Message.Second);
				} else {
					sb.append(Message.Seconds);
				}
				if (!simple)
					sb.append(", and ");
			}

		String line = sb.toString();

		int ands = StringUtils.countMatches(line, ", and ");
		for (int i = 0; i < ands; i++) {
			line = line.replace(", and ", ", ");
		}

		return line;
	}

	@Deprecated
	public static String convertTime (int seconds){
		StringBuilder sb = new StringBuilder();

		if (seconds >= (60 * 60 * 24 * 31 * 12)) {
			int days = seconds / (60 * 60 * 24 * 31 * 12);

			sb.append(days);
			sb.append(" ");
			if (days == 1) {
				sb.append("year");
			} else {
				sb.append("years");
			}
			seconds = seconds % (60 * 60 * 24 * 31 * 12);
			sb.append(", and ");
		}



		if (seconds >= (60 * 60 * 24 * 31)) {
			int days = seconds / (60 * 60 * 24 * 31);

			sb.append(days);
			sb.append(" ");
			if (days == 1) {
				sb.append("month");
			} else {
				sb.append("months");
			}
			seconds = seconds % (60 * 60 * 24 * 31);
			sb.append(", and ");
		}

		if (seconds >= (60 * 60 * 24 * 7)) {
			int days = seconds / (60 * 60 * 24 * 7);

			sb.append(days);
			sb.append(" ");
			if (days == 1) {
				sb.append("week");
			} else {
				sb.append("weeks");
			}
			seconds = seconds % (60 * 60 * 24 * 7);
			sb.append(", and ");
		}


		if (seconds >= (60 * 60 * 24)) {
			int days = seconds / (60 * 60 * 24);

			sb.append(days);
			sb.append(" ");
			if (days == 1) {
				sb.append(Message.Day);
			} else {
				sb.append(Message.Days);
			}
			seconds = seconds % (60 * 60 * 24);
			sb.append(", and ");
		}

		if (seconds >= (60 * 60)) {
			int hours = seconds / (60 * 60);

			sb.append(hours);
			sb.append(" ");
			if (hours == 1) {
				sb.append(Message.Hour);
			} else {
				sb.append(Message.Hours);
			}
			seconds = seconds % (60 * 60);
			sb.append(", and ");
		}

		if (seconds >= 60) {
			int minutes = seconds / 60;

			sb.append(minutes);
			sb.append(" ");
			if (minutes == 1) {
				sb.append(Message.Minute);
			} else {
				sb.append(Message.Minutes);
			}
			seconds = seconds % 60;
			sb.append(", and ");
		}

		if (seconds > 0) {
			sb.append(seconds);
			sb.append(" ");
			if (seconds == 1) {
				sb.append(Message.Second);
			} else {
				sb.append(Message.Seconds);
			}
			sb.append(", and ");
		}

		String line = sb.substring(0, sb.length() - 6);

		int ands = StringUtils.countMatches(line, ", and ");
		for (int i = 0; i < ands; i++) {
			line = line.replace(", and ", ", ");
		}

		return line;
	}


	public static String stringToTime (String timeString) {
		String[] infoSet = timeString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		LengthType lengthType = null;
		Integer lengthNum = null;
		Integer totalSeconds = 0;

		for (String set : infoSet){
			if (lengthNum != null && lengthType != null){
				totalSeconds += calculateLength(lengthNum, lengthType);

				lengthType = null;
				lengthNum = null;
			}

			if (isInteger(set)){
				// This is a number.
				lengthNum = Integer.parseInt(set);
			}
			else{
				// This is a type of length
				if (lengthNum != null){
					LengthType type = null;
					if (set.equalsIgnoreCase("s") || set.equalsIgnoreCase("second") || set.equalsIgnoreCase("seconds"))
						type = LengthType.seconds;
					else if (set.equals("m") || set.equalsIgnoreCase("minute") || set.equalsIgnoreCase("minutes"))
						type = LengthType.minutes;
					else if (set.equalsIgnoreCase("h") || set.equalsIgnoreCase("hour") || set.equalsIgnoreCase("hours"))
						type = LengthType.hours;
					else if (set.equalsIgnoreCase("d") || set.equalsIgnoreCase("day") || set.equalsIgnoreCase("days"))
						type = LengthType.days;
					else if (set.equalsIgnoreCase("w") || set.equalsIgnoreCase("week") || set.equalsIgnoreCase("weeks"))
						type = LengthType.weeks;
					else if (set.equals("M") || set.equalsIgnoreCase("month") || set.equalsIgnoreCase("months"))
						type = LengthType.months;
					else if (set.equalsIgnoreCase("y") || set.equalsIgnoreCase("year") || set.equalsIgnoreCase("years"))
						type = LengthType.years;
					lengthType = type;
				}
				else
					return null;
			}
		}

		return null;
	}


	private static Integer calculateLength(Integer lengthNum, LengthType lengthType) {
		if (lengthType == LengthType.minutes)
			return lengthNum * 60;
		else if (lengthType == LengthType.hours)
			return lengthNum * 60 * 60;
		else if (lengthType == LengthType.days)
			return lengthNum * 60 * 60 * 24;
		else if (lengthType == LengthType.weeks)
			return lengthNum * 60 * 60 * 24 * 7;
		else if (lengthType == LengthType.months)
			return lengthNum * 60 * 60 * 24 * 31;
		else if (lengthType == LengthType.years)
			return lengthNum * 60 * 60 * 24 * 31 * 12;
		return lengthNum;
	}


	public static String getTime (int seconds) {
		StringBuilder sb = new StringBuilder();
		if (seconds >= 31104000) {
			sb.append(seconds / 31104000);
			sb.append("Y");
			seconds = seconds % 31104000;
		}

		if (seconds >= 2678400) {
			sb.append(seconds / 2678400);
			sb.append("M");
			seconds = seconds % 2678400;
		}

		if (seconds >= 604800) {
			sb.append(seconds / 604800);
			sb.append("w");
			seconds = seconds % 604800;
		}

		if (seconds >= 86400) {
			sb.append(seconds / 86400);
			sb.append("d");
			seconds = seconds % 86400;
		}

		if (seconds >= 3600) {
			sb.append(seconds / 3600);
			sb.append("h");
			seconds = seconds % 3600;
		}

		if (seconds >= 60) {
			sb.append(seconds / 60);
			sb.append("m");
			seconds = seconds % 60;
		}

		if (seconds > 0) {
			sb.append(seconds);
			sb.append("s");
		}

		return sb.toString();
	}

	public static int randomInt(int Low, int Max) {
		Random r = new Random();
		int Random = Low;

		if(Low != Max) {
			Random = r.nextInt(Max-Low) + Low;
		}
		return Random;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}
