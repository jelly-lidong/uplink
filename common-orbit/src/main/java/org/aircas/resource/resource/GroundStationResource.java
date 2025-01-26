package org.aircas.resource.resource;

public interface Resource {
    String getId();
    boolean isAvailable(AbsoluteDate date);
    void reserve(TimeWindow window);
    void release(TimeWindow window);
}

// 示例：地面站资源
public class GroundStationResource implements Resource {
    private final GroundStation station;
    private final List<TimeWindow> reservedWindows;
    // ... 实现方法
} 