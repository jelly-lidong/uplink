package org.aircas.orbit.handler;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.utils.PVCoordinatesProvider;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 * 位置速度差异处理器
 *
 * <p>
 * 该类用于计算和记录航天器实际状态与预期状态之间的位置和速度差异。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>在轨道传播过程中记录每个时间步长的状态差异</li>
 * <li>计算实际轨道与参考轨道的偏差</li>
 * <li>提供位置和速度差异的时间序列数据</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>轨道确定和预报</li>
 * <li>轨道机动评估</li>
 * <li>轨道误差分析</li>
 * </ul>
 * </p>
 *
 * <p>
 * 技术细节:
 * <ul>
 * <li>实现了Orekit的OrekitFixedStepHandler接口</li>
 * <li>使用TimeStampedPVCoordinates存储差异数据</li>
 * <li>支持任意参考系下的位置速度计算</li>
 * </ul>
 * </p>
 */

public class DeltaPVHandler implements OrekitFixedStepHandler {

  /**
   * PV provider .
   */
  private final PVCoordinatesProvider provider;

  /**
   * PV differences at each step.
   */
  @Getter
  private final List<TimeStampedPVCoordinates> deltaPV;

  /**
   * Simple constructor.
   *
   * @param provider the {@link PVCoordinatesProvider} to get coordinates
   */
  public DeltaPVHandler(final PVCoordinatesProvider provider) {
    this.provider = provider;
    // prepare an empty list of vectors
    this.deltaPV = new ArrayList<TimeStampedPVCoordinates>();
  }

  /**
   * {@inheritDoc}
   */
  public void handleStep(final SpacecraftState s) {
    // Get the PV from the provider
    final TimeStampedPVCoordinates provided = provider.getPVCoordinates(s.getDate(), s.getFrame());
    // Get the PV from the state
    final TimeStampedPVCoordinates current = s.getPVCoordinates();
    // Get the deltaPV
    final TimeStampedPVCoordinates dPV = new TimeStampedPVCoordinates(s.getDate(),
        current.getPosition().subtract(provided.getPosition()),
        current.getVelocity().subtract(provided.getVelocity()));
    // add the current deltaPV
    deltaPV.add(dPV);

  }


}
