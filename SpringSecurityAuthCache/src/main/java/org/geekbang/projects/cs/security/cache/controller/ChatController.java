package org.geekbang.projects.cs.security.cache.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @GetMapping(value = "/")
    Boolean getChats() {

        return true;
    }
}
