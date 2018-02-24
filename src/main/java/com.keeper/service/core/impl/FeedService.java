package com.keeper.service.core.impl;

/*
 * Created by @GoodforGod on 30.04.2017.
 */

import com.keeper.model.dao.GeoPoint;
import com.keeper.model.dao.Route;
import com.keeper.model.dao.Task;
import com.keeper.model.dto.TaskDTO;
import com.keeper.model.types.FeedType;
import com.keeper.model.util.GeoLocations;
import com.keeper.service.core.IFeed;
import com.keeper.service.core.IFeedChartRate;
import com.keeper.service.core.IFeedSubmit;
import com.keeper.util.GeoComputer;
import com.keeper.util.ModelTranslator;
import com.keeper.util.Validator;
import com.keeper.util.resolvers.ErrorMessageResolver;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Aggregates Feed based on Tasks
 */
@Service
@Primary
public class FeedService implements IFeed, IFeedSubmit, IFeedChartRate {

    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    private final int CHART_FEED_SIZE = 20;
    private final int RECENT_FEED_SIZE = 30;

    private final Map<Long, GeoPoint> points = new ConcurrentHashMap<>(); // all geopoints
    private final Map<Long, Route>   routes = new ConcurrentHashMap<>(); // all routes
    private final Map<Long, Task>    tasks  = new ConcurrentHashMap<>(); // all tasks

    // Map < TaskId | Hot Counter >
    private final Map<Long, Long> taskChart = new ConcurrentHashMap<>(CHART_FEED_SIZE); // Rang of hot tasks ids

    // Map < User Id | Map<Task id, locations> >
    private final Map<Long, Map<Long, GeoLocations>> localizedTasks = new ConcurrentHashMap<>();

    protected final SubscriptionService subsService;

    @Autowired
    public FeedService(SubscriptionService subsService) {
        this.subsService = subsService;
    }



    //<editor-fold desc="Model Loads">

    private boolean taskLoader  = false;
    private boolean pointLoader = false;
    private boolean routeLoader = false;

    public void loadTasks(List<Task> repoTasks) {
        if(taskLoader)
            return;

        tasks.putAll((repoTasks).stream().collect(Collectors.toMap(Task::getId, Function.identity())));
        tasksToProceed.addAll(tasks.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        taskLoader = true;
    }

    public void loadRoutes(List<Route> repoRoutes) {
        if(routeLoader)
            return;

        routes.putAll((repoRoutes).stream().collect(Collectors.toMap(Route::getId, Function.identity())));
        routesToProceed.addAll(routes.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        routeLoader = true;
    }

    public void loadPoints(List<GeoPoint> repoPoints) {
        if(pointLoader)
            return;

        points.putAll((repoPoints).stream().collect(Collectors.toMap(GeoPoint::getId, Function.identity())));
        pointsToProceed.addAll(points.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        pointLoader = true;
    }

    //</editor-fold>

    //<editor-fold desc="Model Submits">

    // Model ID
    private final Set<Long>  routesToProceed = new HashSet<>();
    private final Set<Long>  pointsToProceed = new HashSet<>();
    private final Set<Long>  tasksToProceed  = new HashSet<>();

    @Override
    public Task submit(Task task) {
        Task dto = tasks.get(task.getId());

        if(dto == null)
            tasks.put(task.getId(), task);
        else
            tasks.replace(task.getId(), task);

        tasksToProceed.add(task.getId());
        return task;
    }

    @Override
    public GeoPoint submit(GeoPoint point) {
        points.put(point.getId(), point);
        pointsToProceed.add(point.getId());
        return point;
    }

    @Override
    public Route submit(Route route) {
        routes.put(route.getId(), route);
        routesToProceed.add(route.getId());
        return route;
    }

    //</editor-fold>

    //<editor-fold desc="Model Removal">

    // Model ID | User Id
    private final Map<Long, Long> removedRoutes = new ConcurrentHashMap<>();
    private final Map<Long, Long> removedPoints = new ConcurrentHashMap<>();
    private final Map<Long, Long> removedTask   = new ConcurrentHashMap<>();

    @Override
    public void removeTask(Long id) {
        Task modelToDelete = tasks.get(id);

        if(modelToDelete != null) {
            tasks.remove(id);
            removedTask.put(id, modelToDelete.getTopicStarterId());
        }
        else logger.warn("[FEED] " + ErrorMessageResolver.REMOVE_MODEL_NULLABLE + " [ID] : " + id);
    }

    @Override
    public void removeGeo(Long id) {
        GeoPoint modelToDelete = points.get(id);

        if(modelToDelete != null) {
            points.remove(id);
            removedPoints.put(id, modelToDelete.getUserId());
        }
        else logger.warn("[FEED] " + ErrorMessageResolver.REMOVE_MODEL_NULLABLE + " [ID] : " + id);
    }

    @Override
    public void removeRoute(Long id) {
        Route modelToDelete = routes.get(id);

        if(modelToDelete != null) {
            routes.remove(id);
            removedRoutes.put(id, modelToDelete.getUserId());
        }
        else logger.warn("[FEED] " + ErrorMessageResolver.REMOVE_MODEL_NULLABLE + " [ID] : " + id);
    }
    //</editor-fold>

    //<editor-fold desc="Rate Chart Tasks">

    public void upTask(Long taskId) {
        Long hotMark = taskChart.putIfAbsent(taskId, 1L);

        if(hotMark != null)
            taskChart.replace(taskId, ++hotMark);
    }

    public void downTask(Long taskId) {
        Long hotMark = taskChart.get(taskId);

        if(hotMark != null)
            taskChart.replace(taskId, --hotMark);
    }

    //</editor-fold>



    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void update() {

        //<editor-fold desc="Preventive Clear">

        // Remove unnecessary models, where there is no need to proceed them
        pointsToProceed.removeAll(pointsToProceed.stream()
                .filter(removedPoints::containsKey)
                .collect(Collectors.toSet()));

        routesToProceed.removeAll(routesToProceed.stream()
                .filter(removedRoutes::containsKey)
                .collect(Collectors.toSet()));

        tasksToProceed.removeAll(tasksToProceed.stream()
                .filter(removedTask::containsKey)
                .collect(Collectors.toSet()));

        //</editor-fold>

        //<editor-fold desc="Proceed">

        // Geo Points
        if(!pointsToProceed.isEmpty()) {
            calculateGeos();
            pointsToProceed.clear();
        }

        // Routes
        if(!routesToProceed.isEmpty()) {
            calculateRoutes();
            routesToProceed.clear();
        }

        // Tasks
        if(!tasksToProceed.isEmpty()) {
            calculateTasks();
            tasksToProceed.clear();
        }

        //</editor-fold>

        //<editor-fold desc="Clear">

        // Geos
        if(!removedPoints.isEmpty()) {
            clearGeos();
            removedPoints.clear();
        }

        // Routes
        if(!removedRoutes.isEmpty()) {
            clearRoutes();
            removedRoutes.clear();
        }

        // Tasks
        if(!removedTask.isEmpty()) {
            clearTasks();
            removedTask.clear();
        }

        //</editor-fold>

    }



    //<editor-fold desc="Calculate">

    /**
     * Calculate Models to match user desired locations
     */
    private void calculateGeos() {
        for (GeoPoint geo : points.entrySet().stream()
                .filter(entry -> pointsToProceed.contains(entry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList())) {
            for (Map.Entry<Long, Task> task : tasks.entrySet()) {
                if (!geo.getUserId().equals(task.getValue().getTopicStarterId())
                        && GeoComputer.geoInRadius(geo, task.getValue().getGeo())) {
                    Map<Long, GeoLocations> taskLocations = localizedTasks.putIfAbsent(geo.getUserId(), createTaskNode(task.getKey()));

                    if(taskLocations == null)
                        taskLocations = localizedTasks.get(geo.getUserId());

                    GeoLocations geoLocations = taskLocations.putIfAbsent(task.getKey(), new GeoLocations());

                    if(geoLocations == null)
                        geoLocations = taskLocations.get(task.getKey());

                    geoLocations.addPoint(geo);
                }
            }
        }
    }

    private void calculateRoutes() {
        for (Route route : routes.entrySet().stream()
                .filter(entry -> pointsToProceed.contains(entry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList())) {
            for (Map.Entry<Long, Task> task : tasks.entrySet()) {
                if (!route.getUserId().equals(task.getValue().getTopicStarterId())
                        && route.getGeoPoints().getGeos().stream().anyMatch(geo -> GeoComputer.geoInRadius(geo, task.getValue().getGeo()))) {
                    Map<Long, GeoLocations> taskLocations = localizedTasks.putIfAbsent(route.getUserId(), createTaskNode(task.getKey()));

                    if(taskLocations == null)
                        taskLocations = localizedTasks.get(route.getUserId());

                    GeoLocations geoLocations = taskLocations.putIfAbsent(task.getKey(), new GeoLocations());

                    if(geoLocations == null)
                        geoLocations = taskLocations.get(task.getKey());

                    geoLocations.addRoute(route);
                }
            }
        }
    }

    private void calculateTasks() {
        for (Task task : tasks.entrySet().stream().map(Map.Entry::getValue)
                .filter(entryTask -> tasksToProceed.contains(entryTask.getId()))
                .collect(Collectors.toList())) {
            for (Map.Entry<Long, GeoPoint> geo : points.entrySet()) {
                if (!geo.getValue().getUserId().equals(task.getTopicStarterId())
                        && GeoComputer.geoInRadius(geo.getValue(), task.getGeo())) {
                    Map<Long, GeoLocations> taskLocations = localizedTasks.putIfAbsent(geo.getValue().getUserId(), createTaskNode(task.getId()));

                    if(taskLocations == null)
                        taskLocations = localizedTasks.get(geo.getValue().getUserId());

                    GeoLocations geoLocations = taskLocations.putIfAbsent(task.getId(), new GeoLocations());

                    if(geoLocations == null)
                        geoLocations = taskLocations.get(task.getId());

                    geoLocations.addPoint(geo.getValue());
                }
            }
        }
    }

    // Support method
    private static Map<Long, GeoLocations> createTaskNode(Long taskId) {
        return new HashMap<Long, GeoLocations>() {{ put(taskId, new GeoLocations()); }};
    }

    //</editor-fold>

    //<editor-fold desc="Clear">

    /**
     * Clear user deleted models
     */
    private void clearGeos() {
        for (Map<Long, GeoLocations> taskMap : localizedTasks.entrySet()
                .stream().parallel().filter(entry -> removedPoints.containsValue(entry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toSet())) {
            for (GeoLocations locations : taskMap.entrySet()
                    .stream().parallel().filter(entry -> removedTask.containsKey(entry.getKey()))
                    .map(Map.Entry::getValue).collect(Collectors.toSet())) {
                locations.removePoint(removedPoints.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet()));
            }
        }
    }

    private void clearRoutes() {
        for (Map<Long, GeoLocations> taskMap : localizedTasks.entrySet()
                .stream().parallel().filter(entry -> removedRoutes.containsValue(entry.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toSet())) {
            for (GeoLocations locations : taskMap.entrySet()
                    .stream().parallel().filter(entry -> removedTask.containsKey(entry.getKey()))
                    .map(Map.Entry::getValue).collect(Collectors.toSet())) {
                locations.removeRoute(removedRoutes.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet()));
            }
        }
    }

    private void clearTasks() {
        for(Map.Entry<Long, Map<Long, GeoLocations>> taskMap : localizedTasks.entrySet()) {
            for(Map.Entry<Long, Long> taskIdToRemove : removedTask.entrySet()) {
                if (taskMap.getValue().containsKey(taskIdToRemove.getKey())) {
                    taskMap.getValue().remove(taskIdToRemove.getKey());
                }
            }
        }
    }

    //</editor-fold>



    @Override
    public Optional<List<TaskDTO>> chart(Long userId) {
        if(userId == null)
            return Optional.empty();

        final TreeMap<Long, TaskDTO> taskTree = new TreeMap<>();

        tasks.entrySet().stream()
                .filter(task -> taskChart.containsKey(task.getKey()))
                .map(Map.Entry::getValue)
                .forEach(t -> taskTree.put(taskChart.get(t.getId()), subsService.fillSubs(userId, ModelTranslator.toDTO(t))));

        return Optional.of(taskTree.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<TaskDTO>> recent(Long userId) {
        if(userId == null)
            return Optional.empty();

        return Optional.of(subsService.fillSubs(userId, ModelTranslator.tasksToDTO(tasks.entrySet().stream()
                .map(Map.Entry::getValue).filter(task -> !task.getTopicStarterId().equals(userId))
                .sorted(Comparator.comparing(Task::getLastModifyDate))
                .limit(RECENT_FEED_SIZE).collect(Collectors.toList()))));
    }

    @Override
    public Optional<List<TaskDTO>> local(Long userId) {
        if(userId == null)
            return Optional.empty();

        Map<Long, GeoLocations> userLocals = localizedTasks.get(userId);

        if(userLocals == null)
            return Optional.empty();

        List<Long> taskIds = userLocals.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());

        if(taskIds == null || taskIds.isEmpty())
            return Optional.empty();

        return Optional.of(subsService.fillSubs(userId, ModelTranslator.tasksToDTO(tasks.entrySet()
                .stream().filter(task -> taskIds.contains(task.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList()))));
    }

    @Override
    public Optional<List<TaskDTO>> mine(Long userId) {
        if(userId == null)
            return Optional.empty();

        return Optional.of(subsService.fillSubs(userId, ModelTranslator.tasksToDTO(tasks.entrySet()
                .stream().filter(task -> task.getValue().getTopicStarterId().equals(userId))
                .map(Map.Entry::getValue).collect(Collectors.toList()))));
    }

    @Override
    public Optional<List<TaskDTO>> all(Long userId) {
        if(userId == null)
            return Optional.empty();

        return Optional.of(subsService.fillSubs(userId, ModelTranslator.tasksToDTO(tasks.entrySet().stream()
                .map(Map.Entry::getValue).collect(Collectors.toList()))));
    }

    @Override
    public Optional<List<TaskDTO>> subscribed(Long userId) {
        Optional<Set<Long>> subs = subsService.getUserSubscriptions(userId);

        return subs.map(longs -> subsService.fillSubs(userId, ModelTranslator.tasksToDTO(tasks.entrySet()
                .stream().filter(t -> longs.contains(t.getKey()))
                .map(Map.Entry::getValue).collect(Collectors.toList()))));

    }

    @Override
    public Optional<List<TaskDTO>> searchByTheme(Long userId, String theme, FeedType feedType) {
        Optional<List<TaskDTO>>  tasksToProceed = getByFeedType(userId, feedType);

        if(Validator.isStrEmpty(theme))
            return tasksToProceed;

        return tasksToProceed.map(taskDTOS -> Optional.of(taskDTOS.stream()
                .filter(task -> satisfyThemeSearch(task.getTheme(), theme))
                .collect(Collectors.toList()))).orElse(tasksToProceed);
    }

    @Override
    public Optional<List<TaskDTO>> searchByTags(Long userId, List<String> tags, FeedType feedType) {
        Optional<List<TaskDTO>>  tasksToProceed = getByFeedType(userId, feedType);

        if(tags == null || tags.isEmpty())
            return tasksToProceed;

        final Set<String> tagSet = new HashSet<>(tags);
        return tasksToProceed.map(taskDTOS -> Optional.of(taskDTOS.stream()
                .filter(task -> satisfyTagSearch(task.getTagsAsString(), tagSet))
                .collect(Collectors.toList()))).orElse(tasksToProceed);
    }


    /**
     * Search private util/support methods
     */
    private Optional<List<TaskDTO>> getByFeedType(Long userId, FeedType type) {
        switch (type) {
            case SUBSCRIBED: return subscribed(userId);
            case RECENT:    return recent(userId);
            case OWNER:     return mine(userId);
            case LOCAL:     return local(userId);
            case CHART:     return chart(userId);
            case ALL:
            default:        return all(userId);
        }
    }

    private boolean satisfyThemeSearch(String target, String desired) {
        return FuzzySearch.partialRatio(target, desired) > 85;
    }

    private boolean satisfyTagSearch(Set<String> target, Set<String> desired) {
        return target.stream().anyMatch(desired::contains);
    }
}
