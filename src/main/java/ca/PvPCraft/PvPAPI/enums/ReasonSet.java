package ca.PvPCraft.PvPAPI.enums;

import ca.PvPCraft.PvPAPI.utilities.Message;

	public class ReasonSet{
		String playerName;
		String reason;
		Integer length;
		
		public ReasonSet(String pName, String reasonString, Integer length){
			playerName = pName;
			reason = reasonString;
			String[] values = null;
			punishType[] punishs = null;
			reason = reason.toLowerCase();
			if (reason.contains("spam")){
				reason = "Spamming - Repeating the same text.";
				values = new String[]{"10m","1h","6h", "1d", "3d", "7d", "0"};
				punishs = new punishType[]{punishType.Warn, punishType.Mute};

			}
			else if (reason.contains("hacking")){
				reason = "Hacking - Modified Minecraft.jar";
				values = new String[]{"1d","3d","7d", "14d", "0"};
				punishs = new punishType[]{punishType.Ban};
			}
			else if (reason.contains("alt")){
				reason = "Bypassing ban using another account.";
				values = new String[]{"0"};
			}
			else if (reason.contains("paypal")){
				reason = "PayPal Related Issue";
				values = new String[]{"0"};
			}
			else if (reason.contains("advert")){
				reason = "Advertising";
				values = new String[]{"0"};
			}
			else if (reason.contains("aura")){
				reason = "Hacking - Kill Aura";
				values = new String[]{"1d","3d","7d", "14d", "0"};
			}
			else if (reason.contains("disrespect")){
				reason = "Disrespect - Towards players and/or staff.";
				values = new String[]{"1d","3d","7d", "14d", "0"};
			}
			else if (reason.contains("grief")){
				reason = "Griefing";
				values = new String[]{"7d","14d", "0"};
			}
			else if (reason.contains("glitch")){
				reason = "Glitching exploits without reporting";
				values = new String[]{"7d","14d", "0"};
			}
			else if (reason.contains("swear")){
				reason = "Excessive Swearing";
				values = new String[]{"10m","1h","6h", "1d", "3d"};
			}
			else{
				reason = Message.CleanCapitalize(reason);
			}
			
		}
		
		
		private String getReason() {
			return reason;
		}
		
		private Integer getLength() {

			return length;
		}
		
	}
	