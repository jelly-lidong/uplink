package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.orekit.bodies.OneAxisEllipsoid;
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
import org.orekit.utils.IERSConventions;

import java.io.File;
import java.util.Locale;
/**
 * 地球大气层排除事件检测器
 * 
 * <p>
 * 该类用于检测目标是否在地球大气层背景中。当目标不在地球大气层背景中时,
 * 表示可以对目标进行观测。
 * </p>
 * 
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测目标是否在地球大气层背景中</li>
 * <li>计算目标与地球大气层的几何关系</li>
 * <li>生成目标可观测的时间区间</li>
 * </ul>
 * </p>
 * 
 * <p>
 * 使用场景:
 * <ul>
 * <li>空间目标观测任务规划</li>
 * <li>地基光学观测</li>
 * <li>空间态势感知</li>
 * </ul>
 * </p>
 * 
 * <p>
 * 技术细节:
 * <ul>
 * <li>基于Orekit事件检测框架实现</li>
 * <li>使用WGS84地球椭球体模型</li>
 * <li>考虑100km高度的大气层</li>
 * </ul>
 * </p>
 */
public class EarthAtmosphereExclusionDetector extends AbstractDetector<EarthAtmosphereExclusionDetector> {

    private final Frame inertialFrame;
    private final Propagator targetPropagator;
    private final OneAxisEllipsoid earth;

    public EarthAtmosphereExclusionDetector(Frame inertialFrame, Propagator targetPropagator, OneAxisEllipsoid earth, AdaptableInterval maxCheck, double threshold,
                                            int newMaxIter, EventHandler newHandler) {
        super(new EventDetectionSettings(maxCheck, threshold, newMaxIter), newHandler);
        this.inertialFrame = inertialFrame;
        this.targetPropagator = targetPropagator;
        this.earth = earth;
    }

    @Override
    protected EarthAtmosphereExclusionDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new EarthAtmosphereExclusionDetector(inertialFrame, targetPropagator, earth, newMaxCheck, newThreshold, newMaxIter, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();
        Vector3D targetPosition = targetPropagator.propagate(s.getDate()).getPVCoordinates(inertialFrame).getPosition();
        Vector3D earthPosition = earth.getBodyFrame().getTransformTo(inertialFrame, s.getDate()).transformPosition(Vector3D.ZERO);

        // 计算目标与卫星的向量
        Vector3D satelliteToTarget = targetPosition.subtract(satellitePosition);

        // 计算地球与卫星的向量
        Vector3D satelliteToEarth = earthPosition.subtract(satellitePosition);

        // 计算目标与地球的夹角
        double angle = Vector3D.angle(satelliteToTarget, satelliteToEarth);

        // 计算地球和大气层的视半径
        double earthAtmosphereRadius = earth.getEquatorialRadius() + 100000; // 100 km 大气层

        // 如果目标与地球的夹角小于地球和大气层的视半径，则目标在地球背景中
        return angle - Math.asin(earthAtmosphereRadius / satelliteToEarth.getNorm());
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
            double e = 0.001; // 偏心率
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

            // 定义地球模型
            OneAxisEllipsoid earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                    Constants.WGS84_EARTH_FLATTENING,
                    FramesFactory.getITRF(IERSConventions.IERS_2010, true));

            // 定义事件检测器
            // 最大迭代次数 - 用于事件检测器的迭代求解,防止无限循环
            // 当检测器在寻找事件发生的精确时间时使用
            // 通常设置为100-1000之间的值即可满足精度要求
            int maxIter = 100;
            // 最大检查间隔(秒) - 事件检测器搜索事件的时间步长
            // 较小的值会提高检测精度但增加计算量
            // 设为60秒表示每分钟检查一次太阳遮蔽情况
            double maxCheck = 60.0; // 每分钟检查一次
            // 检测阈值 - 用于确定事件是否发生的数值精度
            // 当g函数值小于此阈值时认为找到了事件
            // 值越小精度越高,但可能需要更多迭代次数
            double threshold = 0.001; // 精度

            EarthAtmosphereExclusionDetector detector = new EarthAtmosphereExclusionDetector(FramesFactory.getEME2000(),
                    targetPropagator, earth, AdaptableInterval.of(maxCheck), threshold, maxIter, new EventHandler() {
                @Override
                public Action eventOccurred(SpacecraftState s, EventDetector detector1, boolean increasing) {
                    if (increasing) {
                        System.out.println("目标不在地球和大气层背景中开始时间: " + s.getDate());
                    } else {
                        System.out.println("目标不在地球和大气层背景中结束时间: " + s.getDate());
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
