package com.feed_the_beast.ftbl.gui;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.config.ConfigEntry;
import com.feed_the_beast.ftbl.api.config.ConfigEntryColor;
import com.feed_the_beast.ftbl.api.config.ConfigEntryDouble;
import com.feed_the_beast.ftbl.api.config.ConfigEntryInt;
import com.feed_the_beast.ftbl.api.config.ConfigEntryString;
import com.feed_the_beast.ftbl.api.config.ConfigEntryType;
import com.feed_the_beast.ftbl.api.config.IConfigContainer;
import com.feed_the_beast.ftbl.api.gui.GuiHelper;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.GuiLang;
import com.feed_the_beast.ftbl.api.gui.IClickable;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.gui.widgets.EnumDirection;
import com.feed_the_beast.ftbl.api.gui.widgets.PanelLM;
import com.feed_the_beast.ftbl.api.gui.widgets.SliderLM;
import com.feed_the_beast.ftbl.api_impl.MouseButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.LMColor;
import com.latmod.lib.annotations.Flags;
import com.latmod.lib.io.Bits;
import com.latmod.lib.util.LMColorUtils;
import com.latmod.lib.util.LMJsonUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GuiEditConfig extends GuiLM
{
    public static final Comparator<Map.Entry<String, ConfigEntry>> COMPARATOR = (o1, o2) -> o1.getKey().compareTo(o2.getKey());

    public class ButtonConfigEntry extends ButtonLM
    {
        public final String ID;
        public final ConfigEntry entry;

        public ButtonConfigEntry(String id, ConfigEntry e)
        {
            super(0, 0, 0, 16, e.getDisplayName() == null ? id : e.getDisplayName().getFormattedText());
            ID = id;
            entry = e;
        }

        @Override
        public boolean shouldRender(IGui gui)
        {
            int ay = getAY();
            return ay > -getHeight() && ay < gui.getScreenHeight();
        }

        @Override
        public void renderWidget(IGui gui)
        {
            boolean mouseOver = getMouseX() >= 20 && gui.isMouseOver(this);

            int ax = getAX();
            int ay = getAY();

            if(mouseOver)
            {
                GlStateManager.color(1F, 1F, 1F, 0.13F);
                GuiHelper.drawBlankRect(ax, ay, getWidth(), getHeight());
                GlStateManager.color(1F, 1F, 1F, 1F);
            }

            getFont().drawString(getTitle(gui), (int) (ax + 4D), (int) (ay + 4D), mouseOver ? 0xFFFFFFFF : 0xFF999999);

            String s = entry.getAsString();

            int slen = getFont().getStringWidth(s);

            if(slen > 150)
            {
                s = getFont().trimStringToWidth(s, 150) + "...";
                slen = 152;
            }

            int textCol = 0xFF000000 | entry.getColor();
            if(mouseOver)
            {
                textCol = LMColorUtils.addBrightness(textCol, 60);
            }

            if(mouseOver && getMouseX() > ax + getWidth() - slen - 9)
            {
                GlStateManager.color(1F, 1F, 1F, 0.13F);
                GuiHelper.drawBlankRect(ax + getWidth() - slen - 8, ay, slen + 8, getHeight());
                GlStateManager.color(1F, 1F, 1F, 1F);
            }

            getFont().drawString(s, getScreenWidth() - (slen + 20), (int) (ay + 4D), textCol);
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            if(getMouseY() >= 20 && !Bits.getFlag(entry.getFlags(), Flags.CANT_EDIT))
            {
                GuiHelper.playClickSound();

                ConfigEntryType type = entry.getConfigType();

                if(entry instanceof IClickable)
                {
                    ((IClickable) entry).onClicked(button);
                    GuiEditConfig.this.onChanged(ID, entry.getSerializableElement());
                }
                else
                {
                    switch(type)
                    {
                        case COLOR:
                        {
                            GuiSelectColor.display(null, ((ConfigEntryColor) entry).value, (id, val) ->
                            {
                                ((ConfigEntryColor) entry).value.set((LMColor) val);
                                GuiEditConfig.this.onChanged(ID, entry.getSerializableElement());
                                GuiEditConfig.this.openGui();
                            });

                            break;
                        }
                        case INT:
                        {
                            GuiSelectField.display(null, GuiSelectField.FieldType.INTEGER, entry.getAsInt(), (id, val) ->
                            {
                                ((ConfigEntryInt) entry).set((Integer) val);
                                GuiEditConfig.this.onChanged(ID, entry.getSerializableElement());
                                GuiEditConfig.this.openGui();
                            });

                            break;
                        }
                        case DOUBLE:
                        {
                            GuiSelectField.display(null, GuiSelectField.FieldType.DOUBLE, entry.getAsDouble(), (id, val) ->
                            {
                                ((ConfigEntryDouble) entry).set((Double) val);
                                GuiEditConfig.this.onChanged(ID, entry.getSerializableElement());
                                GuiEditConfig.this.openGui();
                            });

                            break;
                        }
                        case STRING:
                        {
                            GuiSelectField.display(null, GuiSelectField.FieldType.STRING, entry.getAsString(), (id, val) ->
                            {
                                ((ConfigEntryString) entry).set(val.toString());
                                GuiEditConfig.this.onChanged(ID, entry.getSerializableElement());
                                GuiEditConfig.this.openGui();
                            });

                            break;
                        }
                        case CUSTOM:
                        case INT_ARRAY:
                        case STRING_ARRAY:
                        {
                            GuiSelectField.display(null, GuiSelectField.FieldType.STRING, entry.getSerializableElement().toString(), (id, val) ->
                            {
                                entry.fromJson(LMJsonUtils.fromJson(val.toString()));
                                GuiEditConfig.this.onChanged(ID, entry.getSerializableElement());
                                GuiEditConfig.this.openGui();
                            });

                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void addMouseOverText(IGui gui, List<String> l)
        {
            if(getMouseY() > 18)
            {
                if(getMouseX() < getAX() + getFont().getStringWidth(title) + 10)
                {
                    String[] info = entry.getInfo();

                    if(info.length > 0)
                    {
                        for(String s : info)
                        {
                            l.addAll(getFont().listFormattedStringToWidth(s, 230));
                        }
                    }
                }

                if(entry.getAsGroup() == null && getMouseX() > getScreenWidth() - (Math.min(150, getFont().getStringWidth(entry.getAsString())) + 25))
                {
                    String def = entry.getDefValueString();
                    String min = entry.getMinValueString();
                    String max = entry.getMaxValueString();

                    if(def != null)
                    {
                        l.add(TextFormatting.AQUA + "Def: " + def);
                    }
                    if(min != null)
                    {
                        l.add(TextFormatting.AQUA + "Min: " + min);
                    }
                    if(max != null)
                    {
                        l.add(TextFormatting.AQUA + "Max: " + max);
                    }
                }
            }
        }
    }

    private final IConfigContainer configContainer;
    private final NBTTagCompound extraNBT;
    private final JsonObject modifiedConfig;

    private final String title;
    private final List<ButtonConfigEntry> configEntryButtons;
    private final PanelLM configPanel;
    private final ButtonLM buttonAccept, buttonCancel;
    private final SliderLM scroll;
    private int shouldClose = 0;

    public GuiEditConfig(@Nullable NBTTagCompound nbt, IConfigContainer cc)
    {
        super(0, 0);
        configContainer = cc;

        ITextComponent title0 = configContainer.getConfigTitle().createCopy();
        title0.getStyle().setBold(true);
        title = title0.getFormattedText() + TextFormatting.DARK_GRAY + " [WIP GUI]";
        extraNBT = nbt;
        modifiedConfig = new JsonObject();

        configEntryButtons = new ArrayList<>();

        List<Map.Entry<String, ConfigEntry>> list = new ArrayList<>();
        list.addAll(configContainer.createGroup().getFullEntryMap().entrySet());
        Collections.sort(list, COMPARATOR);

        for(Map.Entry<String, ConfigEntry> entry : list)
        {
            if(!Bits.getFlag(entry.getValue().getFlags(), Flags.HIDDEN))
            {
                configEntryButtons.add(new ButtonConfigEntry(entry.getKey(), entry.getValue().copy()));
            }
        }

        configPanel = new PanelLM(0, 0, 0, 20)
        {
            @Override
            public void addWidgets()
            {
                setHeight(0);
                for(ButtonConfigEntry b : configEntryButtons)
                {
                    b.posY = getHeight();
                    add(b);
                    setHeight(getHeight() + b.getHeight());
                }
            }
        };

        buttonAccept = new ButtonLM(0, 2, 16, 16, GuiLang.BUTTON_ACCEPT.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                shouldClose = 1;
                closeGui();
            }
        };

        buttonCancel = new ButtonLM(0, 2, 16, 16, GuiLang.BUTTON_CANCEL.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                shouldClose = 2;
                closeGui();
            }
        };

        scroll = new SliderLM(-16, 20, 16, 0, 10)
        {
            @Override
            public boolean canMouseScroll(IGui gui)
            {
                return true;
            }

            @Override
            public EnumDirection getDirection()
            {
                return EnumDirection.VERTICAL;
            }

            @Override
            public double getScrollStep()
            {
                return 40D / (configPanel.getHeight() + 20D);
            }
        };
    }

    @Override
    public boolean isFullscreen()
    {
        return true;
    }

    @Override
    public void onInit()
    {
        buttonAccept.posX = getWidth() - 18;
        buttonCancel.posX = getWidth() - 38;
        scroll.posX = getWidth() - 16;
        scroll.setHeight(getHeight() - 20);
        configPanel.posY = 20;
        scroll.setValue(this, 0);

        for(ButtonConfigEntry b : configEntryButtons)
        {
            b.setWidth(getWidth() - 16);
        }
    }

    @Override
    public void addWidgets()
    {
        configPanel.setHeight(20);
        configPanel.setWidth(getWidth());
        configPanel.posX = 0;
        configPanel.posY = 20;

        add(buttonAccept);
        add(buttonCancel);
        add(configPanel);
        add(scroll);
    }

    @Override
    public void onClosed()
    {
        if(shouldClose > 0)
        {
            if(!modifiedConfig.entrySet().isEmpty() && shouldClose == 1)
            {
                configContainer.saveConfig(mc.thePlayer, extraNBT, modifiedConfig);
            }
        }
    }

    @Override
    public boolean onClosedByKey()
    {
        buttonCancel.onClicked(this, MouseButton.LEFT);
        return false;
    }

    private void onChanged(String id, JsonElement val)
    {
        modifiedConfig.add(id, val);
    }

    @Override
    public void renderWidgets()
    {
    }

    @Override
    public void drawBackground()
    {
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);

        if(configPanel.getHeight() + 20D > getHeight())
        {
            scroll.updateSlider(this);
            configPanel.posY = (int) (scroll.getValue(this) * (getHeight() - configPanel.getHeight() - 20D) + 20D);
        }
        else
        {
            scroll.setValue(this, 0D);
            configPanel.posY = 20;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissor(0, 20, getWidth(), getHeight() - 20);
        configPanel.renderWidget(this);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        FTBLibClient.setGLColor(0x99333333);
        GuiHelper.drawBlankRect(0, 0, getWidth(), 20);
        GlStateManager.color(1F, 1F, 1F, 1F);
        getFont().drawString(title, 6, 6, 0xFFFFFFFF);

        FTBLibClient.setGLColor(0x99333333);
        GuiHelper.drawBlankRect(scroll.posX, scroll.posY, scroll.getWidth(), scroll.getHeight());
        FTBLibClient.setGLColor(0x99666666);
        GuiHelper.drawBlankRect(scroll.posX, scroll.posY + (int) (scroll.getValue(this) * (scroll.getHeight() - scroll.sliderSize)), scroll.getWidth(), scroll.sliderSize);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);

        buttonAccept.render(GuiIcons.ACCEPT);
        buttonCancel.render(GuiIcons.CANCEL);
    }

    @Override
    public String getTitle(IGui gui)
    {
        return title;
    }

    @Override
    public void onClientDataChanged()
    {
        refreshWidgets();
    }
}