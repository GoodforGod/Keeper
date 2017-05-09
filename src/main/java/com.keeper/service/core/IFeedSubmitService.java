package com.keeper.service.core;

/*
 * Created by @GoodforGod on 02.05.2017.
 */

import com.keeper.model.dao.GeoPoint;
import com.keeper.model.dao.Route;
import com.keeper.model.dao.Task;

import java.util.List;

/**
 * Default Comment
 */
public interface IFeedSubmitService {
    Task submit(Task task);
    GeoPoint submit(GeoPoint point);
    Route submit(Route route);

    void loadTasks(List<Task> task);
    void loadPoints(List<GeoPoint> point);
    void loadRoutes(List<Route> route);

    void removeTask(Long taskId);
    void removeGeo(Long geoId);
    void removeRoute(Long routeId);
}
