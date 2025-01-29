package org.aircas.orbit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orekit.time.AbsoluteDate;

/**
 * 时间区间
 * 
 * <p>
 * 该类用于表示一个时间区间,包含开始时间和结束时间。
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeInterval {

    /**
     * 开始时间
     */
    private  AbsoluteDate startDate;

    /**
     * 结束时间
     */
    private  AbsoluteDate endDate;

}
