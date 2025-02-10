package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetectionSettings;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import java.io.File;
import java.util.Locale;

/**
 * 太阳排除事件检测器
 *
 * <p>
 * 该检测器用于检测卫星观测目标时是否受到太阳干扰。
 * 当卫星-目标连线与卫星-太阳连线之间的夹角小于设定阈值时,
 * 表示太阳可能会干扰卫星对目标的观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星观测目标时的太阳干扰</li>
 * <li>计算卫星-目标-太阳三者之间的角度关系</li>
 * <li>支持设置太阳排除角阈值</li>
 * <li>提供事件处理机制</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>光学成像任务规划</li>
 * <li>卫星对地观测任务</li>
 * <li>空间目标监视跟踪</li>
 * </ul>
 * </p>
 *
 * <p>
 * 技术细节:
 * <ul>
 * <li>基于Orekit事件检测框架实现</li>
 * <li>使用J2000惯性坐标系</li>
 * <li>支持TLE格式的目标轨道数据</li>
 * </ul>
 * </p>
 */

public class SolarExclusionEventDetector extends AbstractDetector<SolarExclusionEventDetector> {

    private final Frame inertialFrame;
    private final Propagator targetPropagator;
    private final double thresholdAngle;


    public SolarExclusionEventDetector(Frame inertialFrame, Propagator targetPropagator, double thresholdAngle,
                                       final int maxIter, AdaptableInterval maxCheck, double threshold, final EventHandler handler) {
        super(new EventDetectionSettings(maxCheck, threshold, maxIter), handler);
        this.inertialFrame = inertialFrame;
        this.targetPropagator = targetPropagator;
        this.thresholdAngle = thresholdAngle;
    }

    /**
     * 创建一个新的SolarExclusionEventDetector实例。
     * <p>
     * 该方法用于创建一个新的太阳排除事件检测器,
     * 并设置相关参数。
     *
     * @param newMaxCheck  最大检查间隔
     * @param newThreshold 检测阈值
     * @param newMaxIter   最大迭代次数
     * @param newHandler   事件处理器
     * @return 新的SolarExclusionEventDetector实例
     */
    @Override
    protected SolarExclusionEventDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        // 使用AdaptableInterval的getMaxCheck()方法获取最大检查间隔
        return new SolarExclusionEventDetector(inertialFrame, targetPropagator, thresholdAngle, newMaxIter, newMaxCheck, newThreshold, newHandler);
    }


    /**
     * 计算卫星、目标和太阳之间的角度差值。
     * <p>
     * 该函数计算卫星到太阳的向量与卫星到目标的向量之间的夹角,
     * 并与阈值角度比较,用于判断是否发生太阳排除事件。
     *
     * @param s 航天器状态
     * @return 实际角度与阈值角度的差值(弧度)
     * - 如果返回值为正,表示角度大于阈值
     * - 如果返回值为负,表示角度小于阈值
     * - 如果返回值为0,表示角度等于阈值
     */
    @Override
    public double g(SpacecraftState s) {
        Vector3D sunPosition = CelestialBodyFactory.getSun().getPVCoordinates(s.getDate(), inertialFrame).getPosition();
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();
        Vector3D targetPosition = targetPropagator.propagate(s.getDate()).getPVCoordinates(inertialFrame).getPosition();

        Vector3D satelliteToSun = sunPosition.subtract(satellitePosition);
        Vector3D satelliteToTarget = targetPosition.subtract(satellitePosition);
        double angle = Vector3D.angle(satelliteToSun, satelliteToTarget);
        //System.out.println("太阳遮蔽角: " + Math.toDegrees(angle));
        return angle - Math.toRadians(thresholdAngle);
    }

    public static void main(String[] args) {
        try {
            // 初始化Orekit数据
            final File home = new File(System.getProperty("user.home"));
            final File orekitData = new File(home, "orekit-data");
            if (!orekitData.exists()) {
                System.err.format(Locale.US, "未找到 %s 文件夹%n", orekitData.getAbsolutePath());
                System.exit(1);
            }
            final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
            manager.addProvider(new DirectoryCrawler(orekitData));

            // 定义卫星轨道
            double a = 7000e3; // 半长轴
            double e = 0.; // 偏心率
            double i = Math.toRadians(98.7); // 倾角
            double omega = Math.toRadians(0); // 近地点幅角
            double raan = Math.toRadians(0); // 升交点赤经
            double lM = Math.toRadians(0); // 平近点角
            AbsoluteDate startDate = new AbsoluteDate(2023, 10, 1, 0, 0, 0.0, TimeScalesFactory.getUTC());
            double mu = Constants.EGM96_EARTH_MU;

            KeplerianOrbit satelliteOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, PositionAngleType.MEAN, FramesFactory.getEME2000(), startDate, mu);

            // 定义目标TLE
            String line1 = "1 25544U 98067A   20344.52777778  .00001264  00000-0  29611-4 0  9993";
            String line2 = "2 25544  51.6460  21.4613 0007417  45.5487 314.5526 15.49112347256347";
            TLE targetTLE = new TLE(line1, line2);
            TLEPropagator targetPropagator = TLEPropagator.selectExtrapolator(targetTLE);

            // 设置传播器
            KeplerianPropagator satellitePropagator = new KeplerianPropagator(satelliteOrbit);

            // 最大迭代次数 - 用于事件检测器的迭代求解,防止无限循环
            // 当检测器在寻找事件发生的精确时间时使用
            // 通常设置为100-1000之间的值即可满足精度要求
            int maxIter = 100;
            // 太阳遮蔽角阈值(度) - 卫星-目标视线与卫星-太阳视线的最小夹角
            // 当实际角度小于此阈值时,表示太阳可能干扰对目标的观测
            // 典型值为10-30度,本例中设为15度
            double thresholdAngle = 5.0;
            // 最大检查间隔(秒) - 事件检测器搜索事件的时间步长
            // 较小的值会提高检测精度但增加计算量
            // 设为60秒表示每分钟检查一次太阳遮蔽情况
            double maxCheck = 60.0; // 每分钟检查一次
            // 检测阈值 - 用于确定事件是否发生的数值精度
            // 当g函数值小于此阈值时认为找到了事件
            // 值越小精度越高,但可能需要更多迭代次数
            double threshold = 0.001; // 精度
            // 定义事件检测器
            SolarExclusionEventDetector detector = new SolarExclusionEventDetector(FramesFactory.getEME2000(),
                    targetPropagator, thresholdAngle, maxIter, AdaptableInterval.of(maxCheck), threshold, new EventHandler() {
                @Override
                public Action eventOccurred(SpacecraftState s, EventDetector detector, boolean increasing) {
                    if (increasing) {
                        System.out.println("太阳遮蔽角大于15度开始时间: " + s.getDate());
                    } else {
                        System.out.println("太阳遮蔽角大于15度结束时间: " + s.getDate());
                    }
                    return Action.CONTINUE;
                }

            });

            // 将事件检测器添加到传播器
            satellitePropagator.addEventDetector(detector);

            // 传播轨道
            AbsoluteDate endDate = startDate.shiftedBy(86400); // 传播一天
            satellitePropagator.propagate(endDate);

        } catch (OrekitException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

}
