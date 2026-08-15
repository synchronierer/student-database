package de.igslandstuhl.database.api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.igslandstuhl.database.Registry;
import de.igslandstuhl.database.server.resources.ResourceLocation;
import de.igslandstuhl.database.utils.RegistryEnum;

/**
 * Represents different levels of difficulty for tasks.
 */
public class TaskLevel extends RegistryEnum<TaskLevel> implements APIObject {
    private static final ResourceLocation META =
        new ResourceLocation("meta", "api", "task_levels.json");

    private static boolean initialized;

    private final int number;
    private final String germanTranslation;
    private final double ratio;

    private TaskLevel(
            Registry<String, TaskLevel> registry,
            String key) {
        super(registry, key);
        this.number = 0;
        this.germanTranslation = key;
        this.ratio = Double.NaN;
    }

    private TaskLevel(
            Registry<String, TaskLevel> registry,
            String key,
            int number,
            String germanTranslation,
            double ratio) {
        super(registry, key);
        this.number = number;
        this.germanTranslation = germanTranslation;
        this.ratio = ratio;
    }

    public int getNumber() {
        return number;
    }

    public String getGermanTranslation() {
        return germanTranslation;
    }

    /**
     * Returns the task level corresponding to the given number.
     *
     * @param number the configured level number
     * @return the corresponding task level
     * @throws IllegalArgumentException if no such task level exists
     */
    public static TaskLevel get(int number) {
        ensureInitialized();

        TaskLevel result =
            RegistryEnum.valueOf(String.valueOf(number), TaskLevel.class);

        if (result == null) {
            throw new IllegalArgumentException(
                "No such task level: " + number
            );
        }

        return result;
    }

    /**
     * Returns the ratio associated with this level.
     *
     * @return the configured ratio
     * @throws IllegalStateException if this level has no ratio
     */
    public double getRatio() {
        if (Double.isNaN(ratio)) {
            throw new IllegalStateException(
                "No ratio configured for task level " + number
            );
        }

        return ratio;
    }

    @Override
    public String toString() {
        return number == -1 ? "Special" : String.valueOf(number);
    }

    @Override
    public String toJSON() {
        return number == -1 ? "\"Special\"" : String.valueOf(number);
    }

    @Override
    protected TaskLevel[] values(Registry<String, TaskLevel> registry) {
        List<TaskLevel> levels = registry.stream()
            .sorted(
                Comparator.comparingInt(
                    level -> level.getNumber() == -1
                        ? Integer.MAX_VALUE
                        : level.getNumber()
                )
            )
            .toList();

        return levels.toArray(new TaskLevel[0]);
    }

    @Override
    protected void initValues() {
        final Map<String, ?> config;

        try {
            config = GraduationLevel.RESOURCE_MANAGER
                .readJsonResourceAsMap(META);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to load task levels", e
            );
        }

        config.forEach((key, value) -> {
            if (!(value instanceof Map<?, ?> settings)) {
                throw new IllegalArgumentException(
                    "Invalid task level configuration for " + key
                );
            }

            int number = Integer.parseInt(key);

            Object nameValue = settings.get("name");
            if (nameValue == null) {
                throw new IllegalArgumentException(
                    "Missing name for task level " + key
                );
            }

            String translation = String.valueOf(nameValue);

            Object ratioValue = settings.get("ratio");
            double ratio = ratioValue instanceof Number numericRatio
                ? numericRatio.doubleValue()
                : Double.NaN;

            registry().register(
                key,
                new TaskLevel(
                    registry(),
                    key,
                    number,
                    translation,
                    ratio
                )
            );
        });
    }

    @Override
    protected TaskLevel initValue(
            Registry<String, TaskLevel> registry,
            String key) {
        return new TaskLevel(registry, key);
    }

    private static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }

        try {
            RegistryEnum.init(TaskLevel.class);
            initialized = true;
        } catch (InstantiationException |
                 IllegalAccessException |
                 IllegalArgumentException |
                 InvocationTargetException |
                 NoSuchMethodException |
                 SecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
