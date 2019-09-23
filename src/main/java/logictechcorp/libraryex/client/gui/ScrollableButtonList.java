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

import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScrollableButtonList extends ExtendedList<ScrollableButtonList.Entry>
{
    private final CreateWorldScreen createWorldScreen;
    private final List<ButtonData> buttonDataList;

    public ScrollableButtonList(CreateWorldScreen createWorldScreen, int width, int height, int top, int bottom, List<ButtonData> buttonDataList)
    {
        super(createWorldScreen.getMinecraft(), width, height, top, bottom, 25);
        this.createWorldScreen = createWorldScreen;
        this.buttonDataList = buttonDataList;
        this.centerListVertically = false;
        this.populateComponents();
    }

    private void populateComponents()
    {
        for(int i = 0; i < this.buttonDataList.size(); i += 2)
        {
            ButtonData buttonDataCurrent = this.buttonDataList.get(i);
            ButtonData buttonDataNext = i < this.buttonDataList.size() - 1 ? this.buttonDataList.get(i + 1) : null;
            this.addEntry(new Entry(this.createButton(buttonDataCurrent), this.createButton(buttonDataNext)));
        }
    }

    private Button createButton(ButtonData buttonData)
    {
        if(buttonData != null)
        {
            return new Button(this.width / 2 - 155 + buttonData.getHorizontalPlacement(), 0, 150, 20, buttonData.getText(), buttonData.getPressable());
        }

        return null;
    }

    @Override
    protected boolean isFocused()
    {
        return this.createWorldScreen.getFocused() == this;
    }

    @Override
    public int getRowWidth()
    {
        return super.getRowWidth() + 130;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 32;
    }

    @OnlyIn(Dist.CLIENT)
    public static class ButtonData
    {
        private final String text;
        private final int horizontalPlacement;
        private final Button.IPressable pressable;

        public ButtonData(String text, int horizontalPlacement, Button.IPressable pressable)
        {
            this.text = text;
            this.horizontalPlacement = horizontalPlacement;
            this.pressable = pressable;
        }

        public String getText()
        {
            return this.text;
        }

        public int getHorizontalPlacement()
        {
            return this.horizontalPlacement;
        }

        public Button.IPressable getPressable()
        {
            return this.pressable;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Entry extends AbstractList.AbstractListEntry<Entry>
    {
        private final Button firstButton;
        private final Button secondButton;

        private Entry(Button firstButton, Button secondButton)
        {
            this.firstButton = firstButton;
            this.secondButton = secondButton;
        }

        @Override
        public void render(int slotIndex, int x, int y, int width, int height, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            this.renderButton(this.firstButton, y, mouseX, mouseY, partialTicks);
            this.renderButton(this.secondButton, y, mouseX, mouseY, partialTicks);
        }

        private void renderButton(Button button, int y, int mouseX, int mouseY, float partialTicks)
        {
            if(button != null)
            {
                button.y = y;
                button.render(mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int index)
        {
            boolean pressedPrimaryButton = this.pressButton(this.firstButton, mouseX, mouseY, index);
            boolean pressedSecondaryButton = this.pressButton(this.secondButton, mouseX, mouseY, index);
            return pressedPrimaryButton || pressedSecondaryButton;
        }

        private boolean pressButton(Button button, double mouseX, double mouseY, int index)
        {
            if(button != null)
            {
                return button.mouseClicked(mouseX, mouseY, index);
            }

            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int index)
        {
            return this.releaseComponent(this.firstButton, mouseX, mouseY, index) | this.releaseComponent(this.secondButton, mouseX, mouseY, index);
        }

        private boolean releaseComponent(Button button, double mouseX, double mouseY, int index)
        {
            if(button != null)
            {
                return button.mouseReleased(mouseX, mouseY, index);
            }

            return false;
        }
    }
}
