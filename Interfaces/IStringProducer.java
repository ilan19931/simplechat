package com.simplechat.Interfaces;

public interface IStringProducer {
    public void addConsumer(IStringConsumer sc);

    public void removeConsumer(IStringConsumer sc);
}
