package com.feed_the_beast.ftblib.client.teamsgui;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.SimpleTextButton;
import com.feed_the_beast.ftblib.lib.gui.WidgetType;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiButtonListBase;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftblib.net.MessageMyTeamAction;
import com.feed_the_beast.ftblib.net.MessageMyTeamGui;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class GuiMyTeam extends GuiButtonListBase
{
	private static class TeamActionButton extends SimpleTextButton
	{
		private final MessageMyTeamGui.Action action;

		private TeamActionButton(Panel panel, MessageMyTeamGui.Action a)
		{
			super(panel, a.title.getFormattedText(), a.icon);
			action = a;
		}

		@Override
		public void onClicked(MouseButton button)
		{
			GuiHelper.playClickSound();

			if (action.requiresConfirm)
			{
				ClientUtils.MC.displayGuiScreen(new GuiYesNo((result, id) ->
				{
					getGui().openGui();

					if (result)
					{
						new MessageMyTeamAction(action.id, new NBTTagCompound()).sendToServer();
					}
				}, action.title.getFormattedText() + "?", "", 0)); //LANG
			}
			else
			{
				new MessageMyTeamAction(action.id, new NBTTagCompound()).sendToServer();
			}
		}

		@Override
		public boolean renderTitleInCenter()
		{
			return false;
		}

		@Override
		public WidgetType getWidgetType()
		{
			return action.enabled ? WidgetType.mouseOver(isMouseOver()) : WidgetType.DISABLED;
		}
	}

	private Collection<MessageMyTeamGui.Action> actions;

	public GuiMyTeam(ITextComponent t, Collection<MessageMyTeamGui.Action> l)
	{
		setTitle(t.getFormattedText());
		actions = l;
	}

	@Override
	public void addButtons(Panel panel)
	{
		for (MessageMyTeamGui.Action action : actions)
		{
			panel.add(new TeamActionButton(panel, action));
		}
	}
}