package com.keeper.controllers;

/*
 * Created by GoodforGod on 19.03.2017.
 */

import com.keeper.entity.Location;
import com.keeper.entity.Profile;
import com.keeper.util.ApiResolver;
import com.keeper.util.WebappResolver;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Control Profile Rest End points
 */
@RestController
public class ProfileRestController {

    private final String restPath = ApiResolver.API + ApiResolver.REST_PROFILE;

    @RequestMapping(value = restPath,
                    method = RequestMethod.GET,
                    produces = ApiResolver.PRODUCER_JSON)
    public String get(Model model) {

        return null;
    }

    @RequestMapping(value = restPath,
                    method = RequestMethod.GET,
                    produces = ApiResolver.PRODUCER_JSON)
    public String patch(Model model) {

        return null;
    }

}
