package com.keeper.controllers;

/*
 * Created by @GoodforGod on 28.03.2017.
 */

import com.keeper.entity.Location;
import com.keeper.entity.Route;
import com.keeper.util.ApiResolver;
import com.keeper.util.WebappResolver;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default Comment
 */
@RestController
public class RouteRestController {


    private final String restPath = ApiResolver.API + ApiResolver.REST_LOCATION;

    @RequestMapping(value = restPath,
                    method = RequestMethod.GET,
                    produces = ApiResolver.PRODUCER_JSON)
    public String get(Model model) {

        return null;
    }

    @RequestMapping(value = restPath,
                    method = RequestMethod.GET,
                    produces = ApiResolver.PRODUCER_JSON)
    public String post(Model model) {

        return null;
    }

    @RequestMapping(value = restPath,
                    method = RequestMethod.GET,
                    produces = ApiResolver.PRODUCER_JSON)
    public String patch(Model model) {

        return null;
    }

    @RequestMapping(value = restPath,
                    method = RequestMethod.GET,
                    produces = ApiResolver.PRODUCER_JSON)
    public String delete(Model model) {

        return null;
    }
}
