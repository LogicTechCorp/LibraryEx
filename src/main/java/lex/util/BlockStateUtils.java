package lex.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class BlockStateUtils
{
    public static IProperty getProperty(IBlockState state, String propertyName)
    {
        for(IProperty property : state.getProperties().keySet())
        {
            if(property.getName().equalsIgnoreCase(propertyName))
            {
                return property;
            }
        }

        return null;
    }

    public static Comparable getPropertyValue(IProperty property, String propertyValue)
    {
        for(Comparable value : (ImmutableSet<Comparable>) property.getAllowedValues())
        {
            if(value.toString().equalsIgnoreCase(propertyValue))
            {
                return value;
            }
        }

        return null;
    }
}
