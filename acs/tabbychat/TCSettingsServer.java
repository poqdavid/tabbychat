package acs.tabbychat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ServerData;

public class TCSettingsServer extends TCSettingsGUI {

	private static final int autoChannelSearchID = 9201;
	private static final int chatChannelDelimsID = 9202;
	private static final int delimColorBoolID = 9203;
	private static final int delimColorEnumID = 9204;
	private static final int delimFormatBoolID = 9205;
	private static final int delimFormatEnumID = 9206;
	private static final int defaultChansID = 9207;
	private static final int ignoredChansID = 9208;
	
	protected TCSettingBool autoChannelSearch = new TCSettingBool(true, "Auto-search for new channels", autoChannelSearchID);
	protected TCSettingEnum delimiterChars = new TCSettingEnum(ChannelDelimEnum.BRACKETS, "Chat-channel delimiters", chatChannelDelimsID);
	protected TCSettingBool delimColorBool = new TCSettingBool(false,"\u00A7oColored delimiters\u00A7r", delimColorBoolID);
	protected TCSettingEnum delimColorCode = new TCSettingEnum(ColorCodeEnum.DEFAULT, "", delimColorEnumID);
	protected TCSettingBool delimFormatBool = new TCSettingBool(false,"\u00A7oFormatted delimiters\u00A7r", delimFormatBoolID);
	protected TCSettingEnum delimFormatCode = new TCSettingEnum(FormatCodeEnum.DEFAULT, "", delimFormatEnumID);
	protected TCSettingTextBox defaultChannels = new TCSettingTextBox("Default channels", defaultChansID);
	protected TCSettingTextBox ignoredChannels = new TCSettingTextBox("Ignored channels", ignoredChansID);	
	
	public TCSettingsServer() {
		super();
		this.name = "Server Config";
		this.bgcolor = 0x66d6d643;
	}
	
	protected TCSettingsServer(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	public void initGui() {
		super.initGui();
		
		int effLeft = (this.width - this.displayWidth)/2;
		int absLeft = effLeft - this.margin;
		int effTop = (this.height - this.displayHeight)/2;
		int absTop = effTop - this.margin;
		int effRight = (this.width + this.displayWidth)/2;
		int col1x = (this.width - this.displayWidth)/2 + 100;
		int col2x = (this.width + this.displayWidth)/2 - 65;
		
		int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;
		
		this.autoChannelSearch.setButtonLoc(col1x, this.rowY(1));
		this.autoChannelSearch.labelX = col1x + 19;
		this.autoChannelSearch.buttonOnColor = buttonColor;
		this.buttonList.add(this.autoChannelSearch);
		
		this.delimiterChars.labelX = col1x;
		this.delimiterChars.setButtonLoc(col1x + 20 + mc.fontRenderer.getStringWidth(this.delimiterChars.description), this.rowY(2));
		this.delimiterChars.setButtonDims(80, 11);
		this.buttonList.add(this.delimiterChars);
		
		this.delimColorBool.setButtonLoc(col1x + 20, this.rowY(3));
		this.delimColorBool.labelX = col1x + 39;
		this.delimColorBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.delimColorBool);
		
		this.delimColorCode.setButtonLoc(effRight - 70, this.rowY(3));
		this.delimColorCode.setButtonDims(70, 11);
		this.buttonList.add(this.delimColorCode);
		
		this.delimFormatBool.setButtonLoc(col1x + 20, this.rowY(4));
		this.delimFormatBool.labelX = col1x + 39;
		this.delimFormatBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.delimFormatBool);
		
		this.delimFormatCode.setButtonLoc(this.delimColorCode.xPosition, this.rowY(4));
		this.delimFormatCode.setButtonDims(70, 11);
		this.buttonList.add(this.delimFormatCode);
		
		this.defaultChannels.labelX = col1x;
		this.defaultChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.defaultChannels.description), this.rowY(5));
		this.defaultChannels.setButtonDims(effRight - this.defaultChannels.xPosition, 11);
		this.defaultChannels.setCharLimit(300);
		this.buttonList.add(this.defaultChannels);
		
		this.ignoredChannels.labelX = col1x;
		this.ignoredChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.ignoredChannels.description), this.rowY(6));
		this.ignoredChannels.setButtonDims(effRight - this.ignoredChannels.xPosition, 11);
		this.ignoredChannels.setCharLimit(300);
		this.buttonList.add(this.ignoredChannels);
		
		this.validateButtonStates();
	}

	public void validateButtonStates() {
		this.delimColorCode.enabled = this.delimColorBool.getTempValue();
		this.delimFormatCode.enabled = this.delimFormatBool.getTempValue();
	}

	protected void storeTempVars() {
		this.autoChannelSearch.save();
		this.delimiterChars.save();
		this.delimColorBool.save();
		this.delimColorCode.save();
		this.delimFormatBool.save();
		this.delimFormatCode.save();
		this.defaultChannels.save();
		this.ignoredChannels.save();
	}
	
	protected void resetTempVars() {
		this.autoChannelSearch.reset();
		this.delimiterChars.reset();
		this.delimColorBool.reset();
		this.delimColorCode.reset();
		this.delimFormatBool.reset();
		this.delimFormatCode.reset();
		this.defaultChannels.reset();
		this.ignoredChannels.reset();
	}

	protected void loadSettingsFile() {
		ServerData server = Minecraft.getMinecraft().getServerData();
		String sname = server.serverName;
		String ip = server.serverIP;
	
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		File settingsDir = new File(tabbyChatDir, ip);
		this.settingsFile = new File(settingsDir, "settings.cfg");
	
		if (!this.settingsFile.exists())
			return;		
		Properties settingsTable = new Properties();
		
		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			settingsTable.load(fInStream);
			fInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from server settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
		
		try {
			this.autoChannelSearch.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("autoChannelSearch")));
			this.delimiterChars.setValue(ChannelDelimEnum.valueOf((String)settingsTable.getProperty("delimiterChars")));
			this.delimColorBool.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("delimColorBool")));
			this.delimColorCode.setValue(ColorCodeEnum.valueOf((String)settingsTable.getProperty("delimColorCode")));
			this.delimFormatBool.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("delimFormatBool")));
			this.delimFormatCode.setValue(FormatCodeEnum.valueOf((String)settingsTable.getProperty("delimFormatCode")));
			this.defaultChannels.setValue((String)settingsTable.getProperty("defaultChannels"));
			this.ignoredChannels.setValue((String)settingsTable.getProperty("ignoredChannels"));
		} catch (Exception e) {
			TabbyChat.printErr("Invalid property found in general settings file.");
			this.autoChannelSearch.setValue(true);
			this.delimiterChars.setValue(ChannelDelimEnum.BRACKETS);
			this.delimColorBool.setValue(false);
			this.delimColorCode.setValue(ColorCodeEnum.DEFAULT);
			this.delimFormatBool.setValue(false);
			this.delimFormatCode.setValue(FormatCodeEnum.DEFAULT);
			this.defaultChannels.setValue("");
			this.ignoredChannels.setValue("");
		}
		this.resetTempVars();
	}
	
	protected void saveSettingsFile() {
		ServerData server = Minecraft.getMinecraft().getServerData();
		String sname = server.serverName;
		String ip = server.serverIP;
	
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		File settingsDir = new File(tabbyChatDir, ip);
	
		if (!settingsDir.exists())
			settingsDir.mkdirs();
		settingsFile = new File(settingsDir, "settings.cfg");
		Properties settingsTable = new Properties();
		
		settingsTable.put("autoChannelSearch", this.autoChannelSearch.getValue().toString());
		settingsTable.put("delimiterChars", this.delimiterChars.getValue().name());
		settingsTable.put("delimColorBool", this.delimColorBool.getValue().toString());
		settingsTable.put("delimColorCode", this.delimColorCode.getValue().name());
		settingsTable.put("delimFormatBool", this.delimFormatBool.getValue().toString());
		settingsTable.put("delimFormatCode", this.delimFormatCode.getValue().name());
		settingsTable.put("defaultChannels", this.defaultChannels.getValue());
		settingsTable.put("ignoredChannels", this.ignoredChannels.getValue());
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(settingsFile);
			settingsTable.store(fOutStream, "Custom filters");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to filter settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
}
