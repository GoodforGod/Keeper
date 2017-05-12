package com.keeper.util.validation.util.executors;

import com.keeper.util.validation.util.IValidator;

import java.util.List;

/**
 * Default Comment
 *
 * @author @GoodforGod
 * @since 12.05.2017
 */
public class GeoStringListValidator implements IValidator<List<String>>{

    private static final GeoValidator geoValidator = new GeoValidator();

    @Override
    public boolean validate(List<String> geos) {
        return geos.stream().noneMatch(geoValidator::validate);
    }
}
