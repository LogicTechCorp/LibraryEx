/*
 * LibraryEx
 * Copyright (c) 2017-2019 by LogicTechCorp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package logictechcorp.libraryex.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiScrollableButtonList extends GuiListExtended
{
    private final List<GuiScrollableButtonData> guiScrollableButtonData;
    private final IGuiButtonPressed guiButtonPressed;
    private final List<GuiScrollableButtonEntry> guiScrollableButtons = new ArrayList<>();

    public GuiScrollableButtonList(Minecraft mc, int width, int height, int top, int bottom, List<GuiScrollableButtonData> guiScrollableButtonData, IGuiButtonPressed guiButtonPressed)
    {
        super(mc, width, height, top, bottom, 25);
        this.centerListVertically = true;
        this.guiScrollableButtonData = guiScrollableButtonData;
        this.guiButtonPressed = guiButtonPressed;
        this.populateComponents();
    }

    private void populateComponents()
    {
        for(int i = 0; i < this.guiScrollableButtonData.size(); i += 2)
        {
            GuiScrollableButtonData guiScrollableButtonDataCurrent = this.guiScrollableButtonData.get(i);
            GuiScrollableButtonData guiScrollableButtonDataNext = i < this.guiScrollableButtonData.size() - 1 ? this.guiScrollableButtonData.get(i + 1) : null;
            Gui guiCurrent = this.createButton(guiScrollableButtonDataCurrent, 0);
            Gui guiNext = this.createButton(guiScrollableButtonDataNext, 160);
            GuiScrollableButtonEntry guiScrollableButtonEntry = new GuiScrollableButtonEntry(guiCurrent, guiNext);
            this.guiScrollableButtons.add(guiScrollableButtonEntry);
        }
    }

    private Gui createButton(GuiScrollableButtonData data, int x)
    {
        if(data != null)
        {
            return new GuiScrollableButton(data.getId(), this.width / 2 - 155 + x, 0, data.getText(), this.guiButtonPressed);
        }

        return null;
    }

    @Override
    public int getListWidth()
    {
        return 400;
    }

    @Override
    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 32;
    }

    @Override
    public GuiScrollableButtonEntry getListEntry(int index)
    {
        return this.guiScrollableButtons.get(index);
    }

    @Override
    protected int getSize()
    {
        return this.guiScrollableButtons.size();
    }

    @SideOnly(Side.CLIENT)
    public static class GuiScrollableButtonData
    {
        private final int id;
        private final String text;

        public GuiScrollableButtonData(int id, String text)
        {
            this.id = id;
            this.text = text;
        }

        public int getId()
        {
            return this.id;
        }

        public String getText()
        {
            return this.text;
        }
    }

    @SideOnly(Side.CLIENT)
    private static class GuiScrollableButtonEntry implements IGuiListEntry
    {
        private final Minecraft minecraft = Minecraft.getMinecraft();
        private final Gui guiCurrent;
        private final Gui guiNext;

        private GuiScrollableButtonEntry(Gui guiCurrent, Gui guiNext)
        {
            this.guiCurrent = guiCurrent;
            this.guiNext = guiNext;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            this.renderComponent(this.guiCurrent, y, mouseX, mouseY, false, partialTicks);
            this.renderComponent(this.guiNext, y, mouseX, mouseY, false, partialTicks);
        }

        private void renderComponent(Gui gui, int y, int mouseX, int mouseY, boolean updated, float partialTicks)
        {
            if(gui instanceof GuiButton)
            {
                GuiButton guiButton = (GuiButton) gui;
                guiButton.y = y;

                if(!updated)
                {
                    guiButton.drawButton(this.minecraft, mouseX, mouseY, partialTicks);
                }
            }
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks)
        {
            this.renderComponent(this.guiCurrent, y, 0, 0, true, partialTicks);
            this.renderComponent(this.guiNext, y, 0, 0, true, partialTicks);
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
        {
            boolean pressedPrimaryButton = this.pressButton(this.guiCurrent, mouseX, mouseY);
            boolean pressedSecondaryButton = this.pressButton(this.guiNext, mouseX, mouseY);
            return pressedPrimaryButton || pressedSecondaryButton;
        }

        private boolean pressButton(Gui gui, int mouseX, int mouseY)
        {
            if(!(gui instanceof GuiButton))
            {
                return false;
            }

            return ((GuiButton) gui).mousePressed(this.minecraft, mouseX, mouseY);
        }

        @Override
        public void mouseReleased(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
        {
            this.releaseComponent(this.guiCurrent, mouseX, mouseY);
            this.releaseComponent(this.guiNext, mouseX, mouseY);
        }

        private void releaseComponent(Gui gui, int mouseX, int mouseY)
        {
            if(gui instanceof GuiButton)
            {
                ((GuiButton) gui).mouseReleased(mouseX, mouseY);
            }
        }
    }
}
