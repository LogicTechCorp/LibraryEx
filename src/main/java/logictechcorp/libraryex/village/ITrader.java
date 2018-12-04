package logictechcorp.libraryex.village;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;

public interface ITrader extends INpc, IMerchant
{
    boolean useAlternateTexture();
}
