package com.feed_the_beast.ftbl.api.client.gui.widgets;

import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.gui.IClickable;
import com.feed_the_beast.ftbl.api.client.gui.IGuiLM;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ButtonLM extends WidgetLM implements IClickable
{
    public ButtonLM(IGuiLM g, int x, int y, int w, int h)
    {
        super(g, x, y, w, h);
    }

    @Override
    public void mousePressed(MouseButton b)
    {
        if(mouseOver())
        {
            onClicked(b);
        }
    }
}