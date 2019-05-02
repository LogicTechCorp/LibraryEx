package logictechcorp.libraryex.api.internal;

import logictechcorp.libraryex.trade.ITradeManager;

public interface ITradeAPI
{
    /**
     * Returns the trade manger.
     *
     * @return The trade manger.
     */
    ITradeManager getTradeManager();
}
