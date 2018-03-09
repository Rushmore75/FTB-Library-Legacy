package com.feed_the_beast.ftblib.client.teamsgui;

import com.feed_the_beast.ftblib.lib.EnumTeamColor;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import net.minecraft.util.text.ITextComponent;

/**
 * @author LatvianModder
 */
public class PublicTeamData extends FinalIDObject implements Comparable<PublicTeamData>
{
	public enum Type
	{
		CAN_JOIN,
		NEEDS_INVITE,
		ENEMY
	}

	public static final DataOut.Serializer<PublicTeamData> SERIALIZER = (data, d) ->
	{
		data.writeString(d.getName());
		data.writeTextComponent(d.displayName);
		data.writeString(d.description);
		data.write(d.color, EnumTeamColor.NAME_MAP);
		data.writeIcon(d.icon);
		data.writeByte(d.type.ordinal());
	};

	public static final DataIn.Deserializer<PublicTeamData> DESERIALIZER = PublicTeamData::new;

	public final ITextComponent displayName;
	public final String description;
	public final EnumTeamColor color;
	public final Icon icon;
	public final Type type;

	public PublicTeamData(DataIn data)
	{
		super(data.readString());
		displayName = data.readTextComponent();
		description = data.readString();
		color = data.read(EnumTeamColor.NAME_MAP);
		icon = data.readIcon();
		type = Type.values()[data.readUnsignedByte()];
	}

	public PublicTeamData(ForgeTeam team, Type c)
	{
		super(team.getName());
		displayName = team.getTitle();
		description = team.getDesc();
		color = team.getColor();
		icon = team.getIcon();
		type = c;
	}

	@Override
	public int compareTo(PublicTeamData o)
	{
		int i = type.compareTo(o.type);

		if (i == 0)
		{
			i = displayName.getUnformattedText().compareToIgnoreCase(o.displayName.getFormattedText());
		}

		return i;
	}
}