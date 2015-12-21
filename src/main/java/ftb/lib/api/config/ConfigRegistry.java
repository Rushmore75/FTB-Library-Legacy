package ftb.lib.api.config;

import java.io.File;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import ftb.lib.FTBLib;
import latmod.lib.*;
import latmod.lib.config.*;

public class ConfigRegistry
{
	public static final FastList<ConfigGroup> list = new FastList<ConfigGroup>();
	public static final FastList<ConfigGroup> synced = new FastList<ConfigGroup>();
	private static ConfigGroup temp = null;
	
	public static void add(ConfigGroup l)
	{
		if(l != null && !list.contains(l))
		{
			list.add(l);
			
			for(ConfigEntry e : l.entries)
			{
				if(e.shouldSync())
				{
					ConfigGroup l1 = ConfigRegistry.synced.getObj(e.parentGroup.ID);
					if(l1 == null)
					{
						l1 = new ConfigGroup(e.parentGroup.ID);
						ConfigRegistry.synced.add(l1);
					}
					
					ConfigGroup g1 = l1.getGroup(e.parentGroup.ID);
					if(g1 == null)
					{
						g1 = new ConfigGroup(e.parentGroup.ID);
						l1.add(g1);
					}
					
					g1.add(e);
				}
			}
		}
	}
	
	public static void add(ConfigFile e)
	{ if(e != null) add(e.configGroup); }
	
	public static void setTemp(ConfigGroup g)
	{ temp = g; }
	
	public static ConfigGroup getTemp(boolean remove)
	{ ConfigGroup g = temp; if(remove) temp = null; return g; }
	
	public static void reload()
	{
		for(int i = 0; i < list.size(); i++)
		{
			ConfigGroup l = list.get(i);
			if(l.parentFile != null) l.parentFile.load();
		}
		
		Map<String, ConfigGroup> overrides = LMJsonUtils.fromJsonFile(new File(FTBLib.folderModpack, "overrides.json"), new TypeToken<Map<String, ConfigGroup>>(){}.getType());
		
		if(overrides != null && !overrides.isEmpty())
		{
			for(String key : overrides.keySet())
			{
				ConfigGroup ol = overrides.get(key);
				
				int result;
				ConfigGroup o = list.getObj(key);
				if(o != null && (result = o.loadFromGroup(ol)) > 0)
				{
					FTBLib.dev_logger.info("Config overriden: " + result);
					o.parentFile.save();
				}
				else FTBLib.dev_logger.info("Didnt load anything from " + ol);
			}
		}
	}
}