package ru.artyrian.statusmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class MainMenuController {


    @RequestMapping("/index")
    @ResponseStatus(value = org.springframework.http.HttpStatus.OK)
    public String showHomePage() {
        return "/index";
    }

}
