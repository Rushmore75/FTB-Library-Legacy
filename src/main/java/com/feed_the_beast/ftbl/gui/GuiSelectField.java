package com.feed_the_beast.ftbl.gui;

import com.feed_the_beast.ftbl.api.GuiLang;
import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.LMGuis;
import com.feed_the_beast.ftbl.api.gui.callback.FieldSelected;
import com.feed_the_beast.ftbl.api.gui.callback.IFieldCallback;
import com.feed_the_beast.ftbl.api.gui.widgets.ButtonSimpleLM;
import com.feed_the_beast.ftbl.api.gui.widgets.TextBoxLM;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectField extends GuiLM
{
    public final Object ID;
    public final LMGuis.FieldType type;
    public final String def;
    public final IFieldCallback callback;
    
    public final ButtonSimpleLM buttonCancel, buttonAccept;
    public final TextBoxLM textBox;
    
    public GuiSelectField(Object id, LMGuis.FieldType typ, String d, IFieldCallback c)
    {
        super(null, null);
        ID = id;
        type = typ;
        def = d;
        callback = c;
        
        mainPanel.width = 100;
        mainPanel.height = 40;
        
        int bsize = mainPanel.width / 2 - 4;
        
        buttonCancel = new ButtonSimpleLM(this, 2, mainPanel.height - 18, bsize, 16)
        {
            @Override
            public void onClicked(MouseButton button)
            {
                FTBLibClient.playClickSound();
                callback.onFieldSelected(new FieldSelected(ID, false, def, true));
            }
        };
        
        buttonCancel.title = GuiLang.button_cancel.translate();
        
        buttonAccept = new ButtonSimpleLM(this, mainPanel.width - bsize - 2, mainPanel.height - 18, bsize, 16)
        {
            @Override
            public void onClicked(MouseButton button)
            {
                FTBLibClient.playClickSound();
                if(textBox.isValid())
                {
                    callback.onFieldSelected(new FieldSelected(ID, true, textBox.getText(), true));
                }
            }
        };
        
        buttonAccept.title = GuiLang.button_accept.translate();
        
        textBox = new TextBoxLM(this, 2, 2, mainPanel.width - 4, 18)
        {
            @Override
            public boolean isValid()
            { return type.isValid(getText()); }
            
            @Override
            public void returnPressed()
            { buttonAccept.onClicked(MouseButton.LEFT); }
        };
        
        textBox.setText(def);
        textBox.textRenderX = -1;
        textBox.textRenderY = 6;
        textBox.textColor = 0xFFEEEEEE;
    }
    
    public GuiSelectField setCharLimit(int i)
    {
        textBox.charLimit = i;
        return this;
    }
    
    @Override
    public void addWidgets()
    {
        mainPanel.add(buttonCancel);
        mainPanel.add(buttonAccept);
        mainPanel.add(textBox);
    }
    
    @Override
    public void drawBackground()
    {
        int size = 8 + getFontRenderer().getStringWidth(textBox.getText());
        if(size > mainPanel.width)
        {
            mainPanel.width = size;
            int bsize = size / 2 - 4;
            buttonAccept.width = buttonCancel.width = bsize;
            buttonAccept.posX = mainPanel.width - bsize - 2;
            textBox.width = mainPanel.width - 4;
            initGui();
        }
        
        GlStateManager.color(0.4F, 0.4F, 0.4F, 0.66F);
        drawBlankRect(mainPanel.posX, mainPanel.posY, zLevel, mainPanel.width, mainPanel.height);
        GlStateManager.color(0.2F, 0.2F, 0.2F, 1F);
        drawBlankRect(textBox.getAX(), textBox.getAY(), zLevel, textBox.width, textBox.height);
        GlStateManager.color(1F, 1F, 1F, 1F);
        buttonAccept.renderWidget();
        buttonCancel.renderWidget();
        textBox.renderWidget();
    }
}