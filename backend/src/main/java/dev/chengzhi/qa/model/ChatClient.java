package dev.chengzhi.qa.model;

public interface ChatClient {

    ChatResult complete(ChatCommand command);
}
