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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiScrollableButtonList extends ExtendedList
{
    private final List<GuiScrollableButtonData> guiScrollableButtonData;
    private final Button.IPressable guiButtonPressed;
    private final List<Entry> guiScrollableButtons = new ArrayList<>();

    public GuiScrollableButtonList(Minecraft mc, int width, int height, int top, int bottom, List<GuiScrollableButtonData> guiScrollableButtonData, Button.IPressable guiButtonPressed)
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
            AbstractGui guiCurrent = this.createButton(guiScrollableButtonDataCurrent);
            AbstractGui guiNext = this.createButton(guiScrollableButtonDataNext);
            Entry guiScrollableButtonEntry = new Entry(guiCurrent, guiNext);
            this.guiScrollableButtons.add(guiScrollableButtonEntry);
        }
    }

    private AbstractGui createButton(GuiScrollableButtonData data)
    {
        if(data != null)
        {
            return new Button(this.width / 2 - 155 + data.getXPlacement(), 0, 200, 20, data.getText(), this.guiButtonPressed);
        }

        return null;
    }

    @Override
    public int getRowWidth()
    {
        return 400;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 32;
    }

    @Override
    public Entry getEntry(int index)
    {
        return this.guiScrollableButtons.get(index);
    }

    @Override
    protected int getItemCount()
    {
        return this.guiScrollableButtons.size();
    }

    @OnlyIn(Dist.CLIENT)
    public static class GuiScrollableButtonData
    {
        private final int xPlacement;
        private final String text;

        public GuiScrollableButtonData(int xPlacement, String text)
        {
            this.xPlacement = xPlacement;
            this.text = text;
        }

        public int getXPlacement()
        {
            return this.xPlacement;
        }

        public String getText()
        {
            return this.text;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class Entry extends AbstractList.AbstractListEntry<Entry>
    {
        private final AbstractGui guiCurrent;
        private final AbstractGui guiNext;

        private Entry(AbstractGui guiCurrent, AbstractGui guiNext)
        {
            this.guiCurrent = guiCurrent;
            this.guiNext = guiNext;
        }

        @Override
        public void render(int slotIndex, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            this.renderComponent(this.guiCurrent, y, mouseX, mouseY, partialTicks);
            this.renderComponent(this.guiNext, y, mouseX, mouseY, partialTicks);
        }

        private void renderComponent(AbstractGui gui, int y, int mouseX, int mouseY, float partialTicks)
        {
            if(gui instanceof Button)
            {
                Button guiButton = (Button) gui;
                guiButton.y = y;
                guiButton.render(mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int index)
        {
            boolean pressedPrimaryButton = this.pressButton(this.guiCurrent, mouseX, mouseY, index);
            boolean pressedSecondaryButton = this.pressButton(this.guiNext, mouseX, mouseY, index);
            return pressedPrimaryButton || pressedSecondaryButton;
        }

        private boolean pressButton(AbstractGui gui, double mouseX, double mouseY, int index)
        {
            if(!(gui instanceof Button))
            {
                return false;
            }

            return ((Button) gui).mouseClicked(mouseX, mouseY, index);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int index)
        {
            return this.releaseComponent(this.guiCurrent, mouseX, mouseY, index) | this.releaseComponent(this.guiNext, mouseX, mouseY, index);
        }

        private boolean releaseComponent(AbstractGui gui, double mouseX, double mouseY, int index)
        {
            if(gui instanceof Button)
            {
                return ((Button) gui).mouseReleased(mouseX, mouseY, index);
            }

            return false;
        }
    }
}
