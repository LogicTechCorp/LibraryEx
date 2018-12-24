/*
 * The MIT License (MIT)
 *
 * Test Mod 3 - Copyright (c) 2015-2017 Choonster
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Original: https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/0bb16686f598d4aecb6a67cea2cd57d50939de3d/src/main/java/choonster/testmod3/util/InjectionUtil.java
 */

package logictechcorp.libraryex.util;

import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

/**
 * Utility methods for automatic injection systems like {@link GameRegistry.ObjectHolder} and {@link CapabilityInject}.
 *
 * @author Choonster
 */
public class InjectionHelper
{
    /**
     * Returns <code>null</code>.
     * <p>
     * This is used in the initialisers of <code>static final</code> fields instead of using <code>null</code> directly
     * to suppress the "Argument might be null" warnings from IntelliJ IDEA's "Constant conditions &amp; exceptions" inspection.
     * <p>
     * Based on diesieben07's solution <a href="http://www.minecraftforge.net/forum/topic/60980-solved-disable-%E2%80%9Cconstant-conditions-exceptions%E2%80%9D-inspection-for-field-in-intellij-idea/?do=findCommentcomment=285024">here</a>.
     *
     * @param <T> The field's type.
     * @return null
     */
    @Nonnull
    @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
    public static <T> T nullValue()
    {
        return null;
    }
}
