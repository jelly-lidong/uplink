package org.aircas.orbit.file.wold;


import lombok.Builder;
import lombok.Data;

/**
 * @author pdai
 */
@Builder
@Data
public class User {

  /**
   * user id.
   */
  private Long id;

  /**
   * username.
   */
  private String userName;

  /**
   * email.
   */
  private String email;

  /**
   * phoneNumber.
   */
  private long phoneNumber;

  /**
   * description.
   */
  private String description;


}
